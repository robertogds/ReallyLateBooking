package controllers;

import notifiers.Mails;
import helper.StatusMessage;

import com.google.gson.Gson;

import models.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import siena.PersistenceManager;

public class Users extends Application {

	public static void list(){
	  renderJSON(User.all().fetch());
	}
	
	public static void create(String json) {
		String body = json != null ? json : params.get("body");
		Logger.debug("Create user " + body);
		
		if (body != null){
			User user = new Gson().fromJson(body, User.class);
			user.insert();
			Mails.validate(user);
			//This is a workaround to solve issue from siena. Not necessary with the next version	    
			throw new MyRenderJson(user, PersistenceManager.class); 
		}
	}

	public static void update(String json) {
		String body = json != null ? json : params.get("body");
		Logger.debug("Update user " + body);
		
		if (body != null){
			User user = new Gson().fromJson(body, User.class);
		    User dbUser = User.findById(user.id);
		    dbUser.updateDetails(user);
		    dbUser.update();
		    //This is a workaround to solve issue from siena. Not necessary with the next version	    
			throw new MyRenderJson(dbUser, PersistenceManager.class);
		}
	}

	public static void delete(Long id) {
	    User.findById(id).delete();
	    renderJSON(new StatusMessage(Http.StatusCode.OK, "OK", "User deleted correctly"));
	}

	public static void show(Long id)  {
	    User user = User.findById(id);
	    renderJSON(user);
	}
	
}
