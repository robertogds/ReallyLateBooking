package controllers.admin;

import java.util.List;

import models.Booking;
import models.City;
import models.Company;
import models.Deal;
import models.User;
import play.data.validation.Email;
import play.data.validation.Required;
import play.exceptions.TemplateNotFoundException;
import play.modules.siena.SienaModelUtils;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.CRUD.ObjectType;
import controllers.Security;

@Check(Security.ADMIN_ROLE)
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
	
	public static void exportAll(int page) {
		int limit = 1000;
        int offset = limit * page;
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<Booking> bookings = Booking.all().offset(offset).fetch(limit);
        prepareBookings(bookings);
        List objects = bookings;
        render("admin/export.csv", objects, type);
    }
	
	public static void exportCompleted(int page) {
		int limit = 1000;
        int offset = limit * page;
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<Booking> bookings = Booking.all().filter("canceled", false).filter("pending", false).order("checkinDate").offset(offset).fetch(limit);
        prepareBookings(bookings);
        List objects = bookings;
        render("admin/export.csv", objects, type);
    }
	
	private static void prepareBookings( List<Booking> bookings){
		for (Booking booking: bookings){
			booking.user = User.findById(booking.user.id);
			if (booking.company != null){
				booking.company = Company.findById(booking.company.id);
			}
			if (booking.city != null){
				booking.city = City.findById(booking.city.id);
			}
			if (booking.deal != null){
				booking.deal = Deal.findById(booking.deal.id);
			}
		}
	}
	
	
	public static void assignToUser(@Required @Email String email, Booking booking){
		if (!validation.hasErrors()){
			booking = Booking.findById(booking.id);
			User user = User.findByEmail(email.trim().toLowerCase());
			if (user == null){
				validation.addError("email", "User not found");
			}
			if (!validation.hasErrors()){
				booking.user = user;
				booking.update();
				
				flash.success("Booking reassigned to %s", user.email);
				redirect("admin.Bookings.list");
			}
		}
		params.flash(); // add http parameters to the flash scope
        flash.error(validation.errors().toString());
		redirect("admin.Bookings.show", booking.id);
	}
	
}

