package controllers;

import helper.HotUsaApiHelper;
import helper.JsonHelper;

import java.util.ArrayList;
import java.util.List;

import models.Booking;
import models.City;
import models.Deal;
import models.MyCoupon;
import models.User;
import models.dto.BookingDTO;
import models.dto.BookingStatusMessage;
import models.dto.StatusMessage;
import notifiers.Mails;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import controllers.oauth.ApiSecurer;

@With(I18n.class)
public class Bookings extends Controller{
	
	@Before(only = {"doBooking"})
    static void checkConnected() {
		Security.checkConnected();
    }
	
	@Before(unless = {"doBooking"})
	static void checkSignature(){
		Boolean correct = ApiSecurer.checkApiSignature(request);
		if (!correct){
			Logger.debug("Invalid signature ");
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", "Invalid api signature. Contact hola@reallylatebooking.com"));
			renderJSON(json);
		}
	}
	
	public static void doBooking(@Valid Booking booking, @Required Long dealId){
		//Assign booking user to the current session user
		User user = User.findById(Long.valueOf(session.get("userId")));
		booking.user = user;
		Deal deal = Deal.findById(dealId);
		booking.deal = deal;
		booking.rooms = 1; //we dont allow more rooms by now
		if (!validation.hasErrors()){ 
			booking.validate(); //Custom validation
		}
		if (!validation.hasErrors()){ 
			tryBooking(booking, user);
		}
		else{
			params.flash(); // add http parameters to the flash scope
	        validation.keep(); // keep the errors for the next request
	        Logger.debug("Errors " + validation.errorsMap().toString());
	        Deals.bookingForm(dealId);
		}
	}
	
	private static void tryBooking(Booking booking, User user){
		Logger.debug("Valid booking from web");
		//we need to fetch all the info form user and deal 
		booking.city = City.findById(booking.deal.city.id);
		//if is an old object from datastore without the boolean set
		booking.deal.isHotUsa = booking.deal.isHotUsa != null ? booking.deal.isHotUsa : Boolean.FALSE;
		booking.deal.isFake = booking.deal.isFake != null ? booking.deal.isFake : Boolean.FALSE;
		booking.fromWeb = true;
		booking.insert();
		if (booking.deal.isHotUsa && !booking.deal.isFake ){
			String localizador = HotUsaApiHelper.reservation(booking);
			if (localizador != null){
				saveUnconfirmedBooking(booking, localizador);
			}
			else{
				Logger.error("It's not an error, Hotusa didnt give us the booking identifier. Why?");
		        flash.error(Messages.get("booking.validation.over"));
		        String cityUrl = booking.city.isRootCity() ? booking.city.url : booking.city.root;
		        //If booking is not complete, delete booking
				booking.delete();
				Deals.list(cityUrl);
			}
		}
		else{
			updateDealRooms(booking.deal.id, booking.rooms, booking.nights);
			updateUserCredits(booking, user);
			activateCouponToReferal(user);
			Mails.hotelBookingConfirmation(booking);
		}
		//inform user by mail 
		Mails.userBookingConfirmation(booking);
		//inform user at the web
		flash.success(Messages.get("web.bookingForm.success"));
		//send user to his bookings list
		Users.dashboard();
	}
	
	private static void updateUserCredits(Booking booking, User user) {
		user.credits = user.credits - booking.credits;
		user.update();
	}
	private static void activateCouponToReferal(User user) {
		if (StringUtils.isNotBlank(user.referer)){
			User referer = User.findByRefererId(user.referer);
			MyCoupon coupon = MyCoupon.findByKeyAndUser(user.refererId, referer);
			coupon.active = Boolean.TRUE;
			coupon.update();
			//TODO send mail to referal?
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
		booking.validate(); //validate object and fill errors map if exist
		if (!validation.hasErrors()){ 
			Logger.debug("Valid booking");
			//we need to fetch all the info form user and deal 
			booking.deal = Deal.findById(booking.deal.id);
			booking.user = User.findById(booking.user.id);
			booking.city = City.findById(booking.deal.city.id);
			//if is an old object from datastore without the boolean set
			booking.deal.isHotUsa = booking.deal.isHotUsa != null ? booking.deal.isHotUsa : Boolean.FALSE;
			booking.deal.isFake = booking.deal.isFake != null ? booking.deal.isFake : Boolean.FALSE;
			booking.insert();
			if (booking.deal.isHotUsa && !booking.deal.isFake ){
				doHotUsaReservation(booking);
			}
			else{
				updateDealRooms(booking.deal.id, booking.rooms, booking.nights);
				Mails.hotelBookingConfirmation(booking);
			}

			Mails.userBookingConfirmation(booking);
			
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new BookingStatusMessage(Http.StatusCode.CREATED, "CREATED", 
							Messages.get("booking.create.correct"), booking));
			renderJSON(json);
		}
		else{
			renderBookingError(booking);
		}
	}
	
	private static void doHotUsaReservation(Booking booking){
		String localizador = HotUsaApiHelper.reservation(booking);
		if (localizador != null){
			saveUnconfirmedBooking(booking, localizador);
		}
		else{
			Logger.error("It's not an error, Hotusa didnt give us the booking identifier. Why?");
			validation.addError("rooms", Messages.get("booking.validation.over"));
			booking.delete();
			renderBookingError(booking);
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
			deal.quantityDay2 = deal.quantityDay2 - rooms;
			if (nights > 2){
				deal.quantityDay3 = deal.quantityDay3 - rooms;
				if (nights > 3){
					deal.quantityDay4 = deal.quantityDay4 - rooms;
					if (nights > 4){
						deal.quantityDay5 = deal.quantityDay5 - rooms;
					}
				}
			}
		}
		deal.update();
	}
	
	
}
