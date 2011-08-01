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
		List<User> users = User.all().filter("isOwner", Boolean.FALSE).order("email").fetch();
		for (User user : users){
			Logger.debug("Sending promo email to: " + user.email);
			Mails.freeNightMadrid(user);
		}
	}
	
	public static void exportClientsCSV(){
		List<User> users = User.all().filter("isOwner", Boolean.FALSE).fetch();
		renderTemplate("admin/Users/users.csv",users);
	}
	
	public static void exportAllCSV(){
		List<User> users = User.all().fetch();
		renderTemplate("admin/Users/users.csv",users);
	}
}
