package controllers;

import helper.AffiliateHelper;
import helper.JsonHelper;
import helper.paypal.PaypalHelper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import models.Booking;
import models.Deal;
import models.User;
import models.dto.BookingDTO;
import models.dto.BookingStatusMessage;
import models.dto.StatusMessage;
import models.exceptions.InvalidBookingCodeException;
import notifiers.Mails;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;
import services.BookingServices;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import controllers.oauth.ApiSecurer;

@With({I18n.class, LogExceptions.class})
public class Bookings extends Controller{
	
	private static final Logger log = Logger.getLogger(Bookings.class.getName());
	
	@Before(only = {"doBooking","showConfirmation"})
    static void checkConnected() {
		Security.checkConnected();
    }
	
	@Before(unless = {"doBooking","completeBooking","showConfirmation"})
	static void checkSignature(){
		Boolean correct = ApiSecurer.checkApiSignature(request);
		if (!correct){
			log.warning("Invalid signature ");
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", "Invalid api signature. Contact hola@reallylatebooking.com"));
			renderJSON(json);
		}
	}
	
	@Before(only = {"create"})
	public static void setCORS() { 
		Http.Header hd = new Http.Header(); 
		hd.name = "Access-Control-Allow-Origin"; 
		hd.values = new ArrayList<String>(); 
		hd.values.add("*"); 
		Http.Response.current().headers.put("Access-Control-Allow-Origin",hd); 
	}
	
	public static void doBooking(@Valid Booking booking, @Required Long dealId, @Required String cancelUrl) throws UnsupportedEncodingException{
		//Assign booking user to the current session user
		User user = User.findById(Long.valueOf(session.get("userId")));
		booking.user = user;
		Deal deal = Deal.findById(dealId);
		booking.deal = deal;
		booking.rooms = 1; //we dont allow more rooms by now
		if (!validation.hasErrors()){ 
			booking.validateNoCreditCart(); //Custom validation
		}
		if (!validation.hasErrors()){ 
			tryBooking(booking, cancelUrl);
		}
		else{
			params.flash(); // add http parameters to the flash scope
	        validation.keep(); // keep the errors for the next request
	        Deals.bookingForm(dealId);
		}
	}
	
	private static void tryBooking(Booking booking, String cancelUrl) throws UnsupportedEncodingException{
		booking.fromWeb = true;
		booking.pending = true;
		booking.insert();
		booking.get();
		if (booking.finalPrice > 0){
			//String paymentUrl = BidoBidoHelper.getPaymentUrl(booking);
			String paypalUrl = PaypalHelper.setCheckout(booking, cancelUrl);
			redirect(paypalUrl);
		}
		else {
			booking.payed = true;
        	booking.canceled = false;
        	booking.pending = false;
        	booking.update();
			User user = User.findById(Long.valueOf(session.get("userId")));
			paymentFromWebCompleted(booking, user, "User had credits so payed 0", "User had credits so payed 0");
		}
	}
	
	public static void completeBooking(String token, String PayerID){
		Booking booking = Booking.findByPaypalToken(token);
		User user = null;
		try {
			user = User.findById(Long.valueOf(session.get("userId")));
		} catch (NumberFormatException e) {
			log.warning("Can´t format userid from session to Long id... " + e);
		}
		if (booking == null || user == null){
			log.severe("Can´t find Booking or User from Paypal with token " + token + " for payerId " + PayerID);
			String subject = "#WARNING#  Can´t find Booking or User from Paypal with token " + token + " for payerId " + PayerID;
			String content = " No se le ha hecho cargo en la tarjeta ni se ha reservado el hotel ";
			Mails.errorMail(subject, content);
			error();
		}
		else{
			booking.user = user;
			Boolean paymentCompleted = PaypalHelper.confirmPayment(booking, token, PayerID);
			if (paymentCompleted){
				paymentFromWebCompleted(booking, user, token, PayerID);
			}
			else{
				log.warning("Can´t make the charge to the credit card");
				String subject = "#WARNING#  Intento de compra cancelado de la reserva  " + booking.code;
				String content = "Puede que lo cancelase el usuario o que fallase. user: "+ user.email + " Token: " + token + " for payerId " + PayerID ;
				Mails.errorMail(subject, content);
				Deals.bookingForm(booking.deal.id);
			}
		}
	}
	
	private static void paymentFromWebCompleted(Booking booking, User user, String token, String payerID) {
		booking.deal = Deal.findById(booking.deal.id);
		try {
			BookingServices.doCompleteReservation(booking, validation);
		} catch (InvalidBookingCodeException e) {
			Mails.bookingErrorMail(booking);
	        flash.error(Messages.get("booking.validation.problem"));
	        Users.dashboard();
		}
		updateAndNotifyUserBooking(booking);
		
		//inform affiliate of booking if neccessary
		if (AffiliateHelper.fromAffiliate(session)) AffiliateHelper.sendAffiliateBooking(booking, session);
		//inform user at the web
		flash.success(Messages.get("web.bookingForm.success"));
		
		showConfirmation(booking.id);
	}
	
	public static void showConfirmation(Long id){
		Booking booking = Booking.findById(id);
		if (booking != null){
			Deal deal = booking.deal;
			//Send user to the confirmation booking page
			render(booking, deal);
		}
		else{
			notFound();
		}
	}

	/*** Json API methods ****/
	public static void listByUser(Long userId){
		User user = User.findById(userId);
		List<Booking> bookingList = Booking.findByUser(user);
		List<BookingDTO> bookingDtoList = new ArrayList<BookingDTO>();
		for(Booking booking : bookingList){
			bookingDtoList.add(new BookingDTO(booking));
		}
		String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(bookingDtoList);
		renderJSON(json);
	}
	
	
	public static void create(String json) {
		String body = json != null ? json : params.get("body");
		if (body != null){
			BookingDTO bookingDto;
			try {
				bookingDto = new Gson().fromJson(body, BookingDTO.class);
				validateAndSave(bookingDto.toBooking());
			} catch (JsonParseException e) {
				log.warning("Error parsing booking json..." + e);
				String messageJson = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
						new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", Messages.get("booking.validation.all.required")));
				renderJSON(messageJson);
			}
		}
	}
	
	
	private static void validateAndSave(@Valid Booking booking){
		booking.insert();
		booking.validateNoCreditCart(); //validate object and fill errors map if exist
		if (!validation.hasErrors()){ 
			try {
				BookingServices.doCompleteReservation(booking, validation);
			} catch (InvalidBookingCodeException e) {
				Mails.bookingErrorMail(booking);
				renderBookingError(booking);
			}
			updateAndNotifyUserBooking(booking);
			
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new BookingStatusMessage(Http.StatusCode.CREATED, "CREATED", 
							Messages.get("booking.create.correct"), booking));
			renderJSON(json);
		}
		else{
			renderBookingError(booking);
		}
	}
	

	private static void updateAndNotifyUserBooking(Booking booking) {
		User.updateJustBooked(booking.user.id, booking.checkinDate);
		markCouponsAsUsed(booking);
		//HACK to inform user and hotel tha we booked throught RESTEL
		booking.code = booking.isHotusa ? Booking.RESTEL + "-"+booking.code : booking.code;
		Mails.hotelBookingConfirmation(booking);
		Mails.userBookingConfirmation(booking);
	}
	
	private static void markCouponsAsUsed(Booking booking){
		//We mark all the coupons needed as used
		booking.user =  User.findById(booking.user.id);
		booking.user.markMyCouponsAsUsed(booking.credits, booking.finalPrice);
		
	}

	
	private static void renderBookingError(Booking booking){
		log.warning("Invalid booking: " + validation.errors().toString());
		String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
				new BookingStatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", validation.errors().toString(), booking));
		renderJSON(json);
	}
}
