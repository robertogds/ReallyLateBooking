package controllers;

import helper.JsonHelper;

import java.util.List;

import models.Booking;
import models.City;
import models.Deal;
import models.MyCoupon;
import models.User;
import models.dto.StatusMessage;
import models.dto.UserDTO;
import models.dto.UserStatusMessage;
import notifiers.Mails;

import org.apache.commons.codec.digest.DigestUtils;

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import controllers.oauth.ApiSecurer;

@With(I18n.class)
public class Users extends Controller {
	
	@Before(only = {"dashboard", "updateAccount"})
    static void checkConnected() {
		Logger.debug("## Accept-languages: " + request.acceptLanguage().toString());
		Security.checkConnected();
    }
	
	@Before(unless = {"create", "resetPasswordForm","saveNewPassword","rememberPassword","login","loginJson","dashboard","updateAccount","register","loginUser","rememberPasswordEmail"})
	public static void checkSignature(){
		Boolean correct = ApiSecurer.checkApiSignature(request);
		if (!correct){
			Logger.debug("Invalid signature ");
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", "Invalid api signature. Contact hola@reallylatebooking.com"));
			renderJSON(json);
		}
	}
	
	/** RLB Web Methods **/

	public static void dashboard(){
		Logger.debug("SESION: " + session);
		User user= User.findById(Long.valueOf(session.get("userId")));
		List<Booking> bookings = Booking.findByUser(user);
		Integer totalBookings = bookings.size();
		for (Booking booking: bookings){
			booking.city.get(); 
			booking.deal.get();
		}
		List<MyCoupon> coupons = MyCoupon.findByUser(user);
		render(user, bookings, totalBookings, coupons);
	}
	
	public static void updateAccount(User user, String returnUrl) {
		validation.email("user.email",user.email);
		validation.required("user.firstName",user.firstName);
		validation.required("user.lastName",user.lastName);
		validation.required("user.password",user.password);
		Logger.debug("Update user " + user.email);
	    User dbUser = User.findById(Long.valueOf(session.get("userId")));
	    //encryt password
	    user.password = DigestUtils.md5Hex(user.password);
	    dbUser.updateDetails(user);
	    if (!validation.hasErrors()){
		    dbUser.update();
		    flash.success(Messages.get("web.users.updateaccount.success"));
	    }
	    else{
	    	flash.error(Messages.get("web.users.updateaccount.error"));
	    	params.flash(); // add http parameters to the flash scope
	        validation.keep(); // keep the errors for the next request
	        Logger.debug("Errors " + validation.errorsMap().toString());
	    }
	    redirect(returnUrl);
	}
	
	public static void rememberPasswordEmail(@Required String email, String returnUrl){
		Logger.debug("Remember password " + email);	
		 if (!validation.hasErrors()){
			 	User user = User.findByEmail(email);
				if (user != null){
					user.setPasswordResetCode();
					Mails.lostPassword(user);
					flash.success(Messages.get("user.remember.correct"));
				}
				else{
					flash.error(Messages.get("user.remember.incorrect"));
				}
	    }
	    else{
	    	flash.error(Messages.get("user.remember.incorrect"));
	    	params.flash(); // add http parameters to the flash scope
	        validation.keep(); // keep the errors for the next request
	    }
		redirect(returnUrl);
	}
	
	public static void resetPasswordForm(String code){
		User user = User.findByResetCode(code);
		render(user, code);
	}
	
	public static void saveNewPassword(@Required String code, @Required String password){
		Logger.debug("User recovery password code " + code);	
		if( validation.hasErrors()){
			flash.error(Messages.get("user.remember.password.incorrect"));
		}
		else{
			User user = User.findByResetCode(code);
			if (user != null){
				user.password = DigestUtils.md5Hex(password);
				user.update();
				flash.success(Messages.get("user.remember.password.success"));
			}
			else{
		         flash.error(Messages.get("user.remember.account.notfound"));
			}
		}
		resetPasswordForm(code);
	}
    
	/** RLB API Methods **/
	
	
	/**
	 * Login a user from a Json received in the body
	 */
	public static void login() { 
		User user = User.loginJson( params.get("body"));
		if (user != null){
			renderJSON(new UserStatusMessage(Http.StatusCode.OK, "OK", Messages.get("user.login.correct"), user));
		}
		else{
			renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", Messages.get("user.login.incorrect")));
		}
	}
	
	
	/**
	 * Create a new user based on the json received. 
	 * Check if users already exists and returns the existent user
	 * @param json
	 */
	public static void create() {
		String body = params.get("body");
		Logger.debug("Create user " + body);	
		if (body != null){
			User user = new Gson().fromJson(body, User.class);
			if (user.checkFacebookUserExists()){
				user = User.login(user);
				if (user != null){
					renderJSON(new UserStatusMessage(Http.StatusCode.OK, "OK", Messages.get("user.login.correct"), user));
				}
				else{
					renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", Messages.get("user.login.incorrect")));
				}
			}
			else{
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
	
	public static void rememberPassword(){
		String body = params.get("body");
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
}
