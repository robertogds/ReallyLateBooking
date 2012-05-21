package controllers.admin;

import java.util.List;

import models.Booking;
import models.City;
import models.Company;
import models.Deal;
import models.User;
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
	
	public static void exportAll() {
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<Booking> bookings = Booking.all().fetch();
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
        List objects = bookings;

        render("admin/export.csv", objects, type);
    }
	
}

