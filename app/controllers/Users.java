package controllers;

import models.User;
import play.mvc.Controller;

public class Users extends Controller {

	public static void list(){
	  renderJSON(User.all().fetch());
	}
	
	public static void create(User newUser) {
	    newUser.insert();
	    show(newUser.id);
	}

	public static void update(Long id, User user) {
	    User dbUser = User.findById(id);
	    dbUser.updateDetails(user);
	    dbUser.insert();
	    show(id);
	}

	public static void delete(Long id) {
	    User.findById(id).delete();
	    renderText("success");
	}

	public static void show(Long id)  {
	    User user = User.findById(id);
	    renderJSON(user);
	}
	
	
}
