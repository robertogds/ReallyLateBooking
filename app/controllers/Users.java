package controllers;

import notifiers.Mails;
import helper.dto.StatusMessage;
import helper.dto.UserDTO;
import helper.dto.UserStatusMessage;

import com.google.gson.Gson;

import models.User;
import play.Logger;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.mvc.Http;
import siena.PersistenceManager;

public class Users extends Application {

	public static void list(){
	  renderJSON(User.all().fetch());
	}
	
	public static void rememberPassword(String json){
		String body = json != null ? json : params.get("body");
		Logger.debug("Remember password " + body);	
		if (body != null){
			User user = new Gson().fromJson(body, User.class);
			user = User.findByEmail(user.email);
			if (user != null){
				String newPassword = user.resetPassword();
				Mails.lostPassword(user, newPassword);
				renderJSON(new StatusMessage(Http.StatusCode.OK, "OK", "User password reseted correctly"));
			}
			else{
				renderJSON(new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", "Email not found"));
			}
		}
		
	}
	
	public static void create(String json) {
		String body = json != null ? json : params.get("body");
		Logger.debug("Create user " + body);	
		if (body != null){
			User user = new Gson().fromJson(body, User.class);
			validateAndSave(user);
		}
	}
	
	private static void validateAndSave(@Valid User user){
		user.validate();
		if (!validation.hasErrors()){
			user.insert();
			UserStatusMessage message = new UserStatusMessage(Http.StatusCode.CREATED, "CREATED", "user created correctly", user);
			Logger.debug("User correctly created " + new Gson().toJson(message));	
			Mails.welcome(user);
			//Mails.validate(user);
			renderJSON(message);
		}
		else{
			UserStatusMessage message = new UserStatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", validation.errors().toString(), user);
			Logger.debug("User couldnt be created " + new Gson().toJson(message));	
			renderJSON(message);
		}
	}

	public static void update(Long id) {
		String body = params.get("body");
		Logger.debug("Update user " + body);
		
		if (body != null){
			User user = new Gson().fromJson(body, User.class);
		    User dbUser = User.findById(id);
		    dbUser.updateDetails(user);
		    dbUser.validate();
		    if (!validation.hasErrors()){
			    dbUser.update();
			    UserStatusMessage message = new UserStatusMessage(Http.StatusCode.OK, "OK", "user updated correctly", dbUser);
				Logger.debug("User updated " + new Gson().toJson(message));	
				renderJSON(message);
		    }
		    else{
				UserStatusMessage message = new UserStatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", validation.errors().toString(), dbUser);
				Logger.debug("User couldnt be updated " + new Gson().toJson(message));	
				renderJSON(message);
			}
		}
	}

	public static void delete(Long id) {
	    User.findById(id).delete();
	    renderJSON(new StatusMessage(Http.StatusCode.OK, "OK", "User deleted correctly"));
	}

	public static void show(Long id)  {
	    User user = User.findById(id);
	    renderJSON(new UserDTO(user));
	}
	
}
