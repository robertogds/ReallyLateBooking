package controllers.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import models.Booking;
import models.Deal;
import models.Partner;
import models.User;
import notifiers.Mails;
import play.i18n.Messages;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.With;
import controllers.Check;
import controllers.LogExceptions;
import controllers.Owners;
import controllers.Secure;
import controllers.Security;

@Check(Security.PARTNER_ROLE)
@With({LogExceptions.class, Secure.class})
public class PartnerBookings extends Controller{
	
	private static final Logger log = Logger.getLogger(PartnerBookings.class.getName());
	
	@Catch(Exception.class)
    public static void logIllegalState(Throwable throwable) {
        log.severe("Internal error at Partners Dashboard…" + throwable);
        throwable.printStackTrace();
        User user = User.findById(Long.valueOf(session.get("userId")));
        Mails.errorMail("#Partners Dashboard ERROR# User:" + user.firstName + " email: "+ user.email, throwable.toString());
    }
	
	public static void index() { 
		User user = User.findById(Long.valueOf(session.get("userId")));
		if (user.isAdmin){
			List<Partner> partners = Partner.all().fetch(); 
			render(user, partners);
		} 
		else{
	        Partner partner = Partner.findById(user.partner.id);
			Collection<Booking> bookings = partner.findAllBookings();
			render(bookings, user, partner); 
		}

	} 
}