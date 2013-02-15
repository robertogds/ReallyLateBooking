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

@Check(Security.ADMIN_ROLE)
@With({LogExceptions.class, Secure.class})
public class Application extends Controller{
	
	private static final Logger log = Logger.getLogger(Application.class.getName());
	
	public static void index() { 
		User user = User.findById(Long.valueOf(session.get("userId")));
		render(user);
	} 
	
}