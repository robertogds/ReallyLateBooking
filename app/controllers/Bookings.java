package controllers;

import models.Booking;
import models.Deal;
import models.User;
import play.mvc.Controller;

public class Bookings extends Controller{
	
	public static void list(User user){
		renderJSON(Booking.findByUser(user));
	}
	
	public static void list(Deal deal){
		renderJSON(Booking.findByDeal(deal));
	}
		
	public static void create(Booking newBooking) {
		newBooking.insert();
	    show(newBooking.id);
	}

	public static void show(Long id)  {
	    Booking booking = Booking.findById(id);
	    renderJSON(booking);
	}
	
}
