package controllers;

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
		Logger.debug("Create user " + json);
		User user = new Gson().fromJson(json, User.class);
		user.insert();
		//This is a workaround to solve issue from siena. Not necessary with the next version	    
		throw new MyRenderJson(user, PersistenceManager.class); 
	}

	public static void update(String json) {
		Logger.debug("Update user " + json);
		User user = new Gson().fromJson(json, User.class);
	    User dbUser = User.findById(user.id);
	    dbUser.updateDetails(user);
	    dbUser.update();
	    //This is a workaround to solve issue from siena. Not necessary with the next version	    
		throw new MyRenderJson(dbUser, PersistenceManager.class);
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
