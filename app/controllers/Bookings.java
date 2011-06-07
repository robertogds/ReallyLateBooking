package controllers;

import helper.BookingStatusMessage;
import helper.StatusMessage;

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
			Booking booking = new Gson().fromJson(body, Booking.class);
			validateAndSave(booking);
		}
	}
	
	private static void validateAndSave(@Valid Booking booking){	
		if (booking.valid()){ 
			Logger.debug("Valid booking");
			booking.insert();
			Logger.debug("Deal id: " + booking.deal.id);
			
			Deal deal = Deal.findById(booking.deal.id);
			deal.quantity = deal.quantity - booking.getRooms();
			deal.update();
			
			
			renderJSON(new BookingStatusMessage(Http.StatusCode.CREATED, "CREATED", "booking created correctly", booking));
		}
		else{
			Logger.debug("Invalid booking: " + validation.error("creditCard"));
			renderJSON(new BookingStatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", "booking could not be made", booking));
		}
	}
	

	public static void show(Long id)  {
	    Booking booking = Booking.findById(id);
	    renderJSON(booking);
	}
	
}
