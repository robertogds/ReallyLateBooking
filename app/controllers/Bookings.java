package controllers;

import groovy.lang.IncorrectClosureArgumentsException;
import helper.AffiliateHelper;
import helper.JsonHelper;
import helper.UAgentInfo;
import helper.bidobido.BidoBidoHelper;
import helper.hotusa.HotUsaApiHelper;
import helper.paypal.PaypalHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Booking;
import models.City;
import models.Deal;
import models.MyCoupon;
import models.User;
import models.dto.BookingDTO;
import models.dto.BookingStatusMessage;
import models.dto.StatusMessage;
import models.exceptions.InvalidBookingCodeException;
import models.exceptions.InvalidCouponException;
import notifiers.Mails;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import controllers.oauth.ApiSecurer;

@With({I18n.class, LogExceptions.class})
public class Bookings extends Controller{
	
	
	@Before(only = {"doBooking","showConfirmation"})
    static void checkConnected() {
		Security.checkConnected();
    }
	
	@Before(unless = {"doBooking","completeBooking","showConfirmation"})
	static void checkSignature(){
		Boolean correct = ApiSecurer.checkApiSignature(request);
		if (!correct){
			Logger.debug("Invalid signature ");
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", "Invalid api signature. Contact hola@reallylatebooking.com"));
			renderJSON(json);
		}
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
	        Logger.debug("Errors " + validation.errorsMap().toString());
	        Deals.bookingForm(dealId);
		}
	}
	
	private static void tryBooking(Booking booking, String cancelUrl) throws UnsupportedEncodingException{
		Logger.debug("Valid booking from web");
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
			Logger.debug("We have more credits than the room cost, so no need to go to paypal");
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
		User user = User.findById(Long.valueOf(session.get("userId")));
		if (booking == null || user == null){
			Logger.error("Can´t find Booking or User from Paypal with token %s for payerId %s", token, PayerID);
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
				Logger.debug("Can´t make the charge to the credit card");
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
			doCompleteReservation(booking);
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
		Logger.debug("Bookings for user: " + userId + " are : " + bookingList.size());
		List<BookingDTO> bookingDtoList = new ArrayList<BookingDTO>();
		for(Booking booking : bookingList){
			bookingDtoList.add(new BookingDTO(booking));
		}
		String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(bookingDtoList);
		renderJSON(json);
	}
	
	
	public static void create(String json) {
		String body = json != null ? json : params.get("body");
		Logger.debug("Create booking " + body);	
		if (body != null){
			BookingDTO bookingDto;
			try {
				bookingDto = new Gson().fromJson(body, BookingDTO.class);
				validateAndSave(bookingDto.toBooking());
			} catch (JsonParseException e) {
				Logger.error("Error parsing booking json", e);
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
				doCompleteReservation(booking);
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
		//set user first booking if needed
		User user = User.findById(booking.user.id);
		user.firstBookingDate = user.firstBookingDate == null ? booking.checkinDate : user.firstBookingDate;
		user.pendingSurvey = true;
		user.update();
		//We mark all the coupons needed as used
		booking.user =  User.findById(booking.user.id);
		booking.user.markMyCouponsAsUsed(booking.credits, booking.finalPrice);
		//inform user by mail 
		booking.code = booking.isHotusa ? Booking.RESTEL + "-"+booking.code : booking.code;
		//Send email to user and hotel
		Mails.hotelBookingConfirmation(booking);
		Mails.userBookingConfirmation(booking);
	}

	private static void doCompleteReservation(Booking booking) throws InvalidBookingCodeException{
		if ((booking.deal.isHotUsa != null && booking.deal.isHotUsa) && (booking.deal.isFake == null||!booking.deal.isFake )){ 
			//If we are booking for more than one nights we need to refresh de lin codes 
			if (booking.nights > 1){
				HotUsaApiHelper.refreshAvailability(booking);
			}
			String localizador = HotUsaApiHelper.reservation(booking);
			if (localizador != null){
				saveUnconfirmedBooking(booking, localizador);
			}
			else{
				validation.addError("rooms", Messages.get("booking.validation.problem"));
				booking.pending = Boolean.TRUE;
				booking.update();
				throw new InvalidBookingCodeException("Localizador from hotusa is null");
			}
		}
		else{
			updateDealRooms(booking.deal.id, booking.rooms, booking.nights);
		}
	}
	
	private static void saveUnconfirmedBooking(Booking booking, String localizador){
		Logger.debug("Correct booking: " + localizador);
		booking.code = localizador;
		booking.needConfirmation = Boolean.TRUE;
		booking.update();
	}
	
	private static void renderBookingError(Booking booking){
		Logger.debug("Invalid booking: " + validation.errors().toString());
		String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
				new BookingStatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", validation.errors().toString(), booking));
		renderJSON(json);
	}
	
	private static void updateDealRooms(Long dealId, Integer rooms, int nights){
		Logger.debug("Deal id: %s ## Rooms: %s ## Nights: %s", dealId, rooms, nights);
		Deal deal = Deal.findById(dealId);
		deal.quantity = deal.quantity - rooms;
		if (deal.quantity == 0){
			deal.active = Boolean.FALSE;
		}
		if (nights > 1){
			deal.quantityDay2 = deal.quantityDay2 == null ? deal.quantity :deal.quantityDay2 - rooms;
			if (nights > 2){
				deal.quantityDay3 =  deal.quantityDay3 == null ? deal.quantity : deal.quantityDay3 - rooms;
				if (nights > 3){
					deal.quantityDay4 =  deal.quantityDay4 == null ? deal.quantity : deal.quantityDay4 - rooms;
					if (nights > 4){
						deal.quantityDay5 =  deal.quantityDay5 == null ? deal.quantity : deal.quantityDay5 - rooms;
					}
				}
			}
		}
		deal.update();
	}
}
