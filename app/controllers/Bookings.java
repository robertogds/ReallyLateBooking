package controllers;

import notifiers.Mails;
import helper.dto.BookingDTO;
import helper.dto.BookingStatusMessage;
import helper.dto.StatusMessage;

import com.google.gson.Gson;

import models.Booking;
import models.Deal;
import models.User;
import play.Logger;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.mvc.Http;

public class Bookings extends Controller{
	
	public static void listByUser(Long userId){
		User user = User.findById(userId);
		renderJSON(Booking.findByUser(user));
	}
	
	public static void listByDeal(Long dealId){
		Deal deal = Deal.findById(dealId);
		renderJSON(Booking.findByDeal(deal));
	}
	
	
	public static void create(String json) {
		String body = json != null ? json : params.get("body");
		Logger.debug("Create booking " + body);	
		if (body != null){
			BookingDTO bookingDto = new Gson().fromJson(body, BookingDTO.class);
			validateAndSave(bookingDto.toBooking());
		}
	}
	
	private static void validateAndSave(@Valid Booking booking){
		booking.validate(); //validate object and fill errors map if exist
		if (!validation.hasErrors()){ 
			Logger.debug("Valid booking");
			booking.insert();
			updateDealRooms(booking.deal.id, booking.rooms);
			//we need to fetch all the info form user and deal 
			booking.deal = Deal.findById(booking.deal.id);
			booking.user = User.findById(booking.user.id);
			Mails.hotelBookingConfirmation(booking);
			Mails.userBookingConfirmation(booking);
			
			renderJSON(new BookingStatusMessage(Http.StatusCode.CREATED, "CREATED", "booking created correctly", booking));
		}
		else{
			Logger.debug("Invalid booking: " + validation.errors().toString());
			renderJSON(new BookingStatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", "booking could not be made", booking));
		}
	}
	
	private static void updateDealRooms(Long dealId, Integer rooms){
		Logger.debug("Deal id: " + dealId);
		Deal deal = Deal.findById(dealId);
		deal.quantity = deal.quantity - rooms;
		deal.update();
	}
	

	public static void show(Long id)  {
	    Booking booking = Booking.findById(id);
	    renderJSON(booking);
	}
	
}
