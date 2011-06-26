package controllers;

import org.apache.commons.codec.digest.DigestUtils;

import notifiers.Mails;
import helper.JsonHelper;
import helper.dto.StatusMessage;
import helper.dto.UserDTO;
import helper.dto.UserStatusMessage;

import com.google.gson.Gson;

import controllers.oauth.ApiSecurer;

import models.User;
import play.Logger;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import siena.PersistenceManager;

public class Users extends Controller {
	
	@Before(unless = {"create", "resetPasswordForm","saveNewPassword","rememberPassword"})
	public static void checkSignature(){
		Boolean correct = ApiSecurer.checkApiSignature(request);
		if (!correct){
			Logger.debug("Invalid signature ");
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", "Invalid api signature. Contact hola@reallylatebooking.com"));
			renderJSON(json);
		}
	}
	
	public static void rememberPassword(String json){
		String body = json != null ? json : params.get("body");
		Logger.debug("Remember password " + body);	
		if (body != null){
			User user = new Gson().fromJson(body, User.class);
			user = User.findByEmail(user.email);
			if (user != null){
				user.setPasswordResetCode();
				Mails.lostPassword(user);
				renderJSON(new StatusMessage(Http.StatusCode.OK, "OK", Messages.get("user.remember.correct")));
			}
			else{
				renderJSON(new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", Messages.get("user.remember.incorrect")));
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
			UserStatusMessage message = new UserStatusMessage(Http.StatusCode.OK, "CREATED", Messages.get("user.create.correct"), user);
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
		    if (!validation.hasErrors()){
			    dbUser.update();
			    UserStatusMessage message = new UserStatusMessage(Http.StatusCode.OK, "OK", Messages.get("user.update.correct"), dbUser);
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

	public static void show(Long id)  {
	    User user = User.findById(id);
	    renderJSON(new UserDTO(user));
	}
	
	public static void resetPasswordForm(String code){
		User user = User.findByResetCode(code);
		render(user, code);
	}
	
	public static void saveNewPassword(String code, String password){
		Logger.debug("User recovery password code " + code);	
		if( password == null || code==null){
			flash.error("password cannot be empty");
		}
		else{
			User user = User.findByResetCode(code);
			if (user != null){
				user.password = DigestUtils.md5Hex(password);
				user.update();
			}
			else{
		         flash.error("Account not found");
			}
		}
		render();
	}
	
}
