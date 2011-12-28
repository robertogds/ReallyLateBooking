package controllers.admin;

import java.util.List;

import models.Booking;
import models.User;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(Booking.class)
public class Bookings extends controllers.CRUD{

	public static void exportAllCSV(){
		List<Booking> bookings = Booking.all().fetch();
		for (Booking booking: bookings){
			booking.user = User.findById(booking.user.id);
		}
		renderTemplate("admin/Bookings/bookings.csv",bookings);
	}
	
}

