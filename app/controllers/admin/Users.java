package controllers.admin;

import java.util.List;

import notifiers.Mails;
import play.Logger;
import play.mvc.With;
import models.User;
import controllers.*;

@Check("admin")
@With(Secure.class)
@CRUD.For(User.class)
public class Users extends controllers.CRUD  {
	
	public static void freeNightEmails(){
		List<User> users = User.all().filter("isOwner", false).order("email").fetch();
		for (User user : users){
			Logger.debug("Sending promo email to: " + user.email);
			Mails.freeNightMadrid(user);
		}
	}
}
