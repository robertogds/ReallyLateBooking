package controllers;

import helper.JsonHelper;
import helper.UAgentInfo;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import models.Booking;
import models.City;
import models.Company;
import models.Deal;
import models.InfoText;
import models.Invoice;
import models.MyCoupon;
import models.Setting;
import models.User;
import models.dto.StatusMessage;
import models.dto.UserDTO;
import models.dto.UserStatusMessage;
import notifiers.Mails;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

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

@With({I18n.class, LogExceptions.class})
public class Users extends Controller {
	
	private static final Logger log = Logger.getLogger(Users.class.getName());
	public static final String IPHONE_WRONG_VERSION = "5.0.1";
	
	@Before(only = {"dashboard", "updateAccount"})
    static void checkConnected() {
		log.info("## Accept-languages: " + request.acceptLanguage().toString());
		Security.checkConnected();
    }
	
	@Before(unless = {"create", "resetPasswordForm","saveNewPassword","rememberPassword","login",
			"loginJson","dashboard","updateAccount","register","loginUser",
			"rememberPasswordEmail","sendSurveyEmails","sendRememberPriceEmails",
			"showBookingInvoice"})
	public static void checkSignature(){
		Boolean correct = ApiSecurer.checkApiSignature(request);
		if (!correct){
			log.warning("Invalid signature ");
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", "Invalid api signature. Contact hola@reallylatebooking.com"));
			renderJSON(json);
		}
	}
	
	@Before(only= {"showBookingInvoice"})
	public static void loginFromEmail(){
		String token = params.get("token");
		User user = StringUtils.isBlank(token) ? null : User.findByToken(token);
		if (user!= null){
    		user.createUserSession();
    	}
		else{
			Security.checkConnected();
		}
	}
	
	@Before(only = {"login", "update","rememberPassword"})
	public static void mobileVersionDetectionStop(){
		//Http.Header header = Http.Request.current().headers.get("user-agent"); 
		String userAgent = request.headers.get("user-agent") != null ? request.headers.get("user-agent").value() : "";
		String accept = request.headers.get("accept")!= null ? request.headers.get("accept").value() : "";
		UAgentInfo agent = new UAgentInfo(userAgent, accept);
		log.info(userAgent);
		if (agent.isIphone && userAgent.contains(IPHONE_WRONG_VERSION)){
			log.warning("Es un iphone " + IPHONE_WRONG_VERSION);
			renderJSON(new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "UPDATE IPHONE", Messages.get("warning.update.iphone")));
		}
	}
	
	@Before(only = {"create"})
	public static void mobileVersionDetectionContinue(){
		//Http.Header header = Http.Request.current().headers.get("user-agent"); 
		String userAgent = request.headers.get("user-agent") != null ? request.headers.get("user-agent").value() : "";
		String accept = request.headers.get("accept")!= null ? request.headers.get("accept").value() : "";
		UAgentInfo agent = new UAgentInfo(userAgent, accept);
		log.info(userAgent);
		if (agent.isIphone && userAgent.contains(IPHONE_WRONG_VERSION)){
			log.warning("Es un iphone " + IPHONE_WRONG_VERSION);
			params.put("updateIphone", IPHONE_WRONG_VERSION);
		}
	}
	
	/** RLB Web Methods **/
	public static void dashboard(){
		User user= User.findById(Long.valueOf(session.get("userId")));
		List<Booking> bookings = Booking.findByUser(user);
		Integer totalBookings = bookings.size();
		for (Booking booking: bookings){
			booking.city.get(); 
			booking.deal.get();
		}
		List<MyCoupon> coupons = MyCoupon.findActiveByUser(user);
		Integer totalCoupons = coupons.size();
		log.warning("booking id 1:" + params.get("bookingId"));
		String bookingId = params.get("bookingId");
		render(user, bookings, totalBookings, coupons, totalCoupons, bookingId);
	}
	
	public static void showBookingInvoice(Long bookingId, String token){
		log.warning("booking id 0:" + params.get("bookingId"));
		User user= User.findById(Long.valueOf(session.get("userId")));
		Booking booking = Booking.findByIdAndUser(bookingId, user);
		if (booking == null){
			flash.error(Messages.get("web.users.bookings.showinvoice.notfound"));
			dashboard();
		}
		else{
			if (StringUtils.isBlank(user.nif)) {
				flash.error(Messages.get("web.users.bookings.showinvoice.updateinfo"));
				redirect("/dashboard?bookingId="+bookingId + "#profile");
			}
			Invoice invoice = Invoice.findOrCreateUserInvoice(booking, user);
			render(user, invoice);
		}
	}
	

	public static void updateAccount(User user, String returnUrl, Long bookingId) {
		validation.email("user.email",user.email);
		validation.required("user.firstName",user.firstName);
		validation.required("user.lastName",user.lastName);
		validation.required("user.password",user.password);
	    User dbUser = User.findById(Long.valueOf(session.get("userId")));
	    //encryt password if user changed it
	    user.password = StringUtils.equalsIgnoreCase(user.password, dbUser.password)? dbUser.password: DigestUtils.md5Hex(user.password) ;
	    dbUser.updateDetails(user);
	    if (!validation.hasErrors()){
		    dbUser.update();
		    if (bookingId == null){
		    	flash.success(Messages.get("web.users.updateaccount.success"));
		    }
		    else{
		    	//come from creating invoice, redirect
		    	showBookingInvoice(bookingId, user.token);
		    }
	    }
	    else{
	    	flash.error(Messages.get("web.users.updateaccount.error"));
	    	params.flash(); // add http parameters to the flash scope
	        validation.keep(); // keep the errors for the next request
	        log.info("Errors " + validation.errorsMap().toString());
	    }
	    redirect(returnUrl);
	}
	
	public static void rememberPasswordEmail(@Required String email, String returnUrl){
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
		redirect(returnUrl!= null ? returnUrl : "/");
	}
	
	public static void resetPasswordForm(String code){
		User user = User.findByResetCode(code);
		render(user, code);
	}
	
	public static void saveNewPassword(@Required String code, @Required String password){
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
		log.info("Create user " + body);	
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
					log.info("User correctly created " + new Gson().toJson(message));	
					if (!user.isFacebook){
						Mails.welcome(user);
					}
					if (params._contains("updateIphone") && params.get("updateIphone").equals(IPHONE_WRONG_VERSION)){
						Mails.errorMail("Se ha registrado un user con la versión 5.0.1", "User email " + user.email + " user.id: " + user.id);
						renderJSON(new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "UPDATE IPHONE", Messages.get("warning.update.iphone")));
					}
					else{
						renderJSON(message);
					}
				}
				else{
					UserStatusMessage message = new UserStatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", validation.errors().toString(), user);
					log.info("User couldnt be created " + new Gson().toJson(message));	
					renderJSON(message);
				}
			}
		}
	}
	

	public static void update(Long id) {
		String body = params.get("body");
		if (body != null){
			User user = new Gson().fromJson(body, User.class);
		    User dbUser = User.findById(id);
		    dbUser.updateDetails(user);
		    if (!validation.hasErrors()){
			    dbUser.update();
			    UserStatusMessage message = new UserStatusMessage(Http.StatusCode.OK, "OK", Messages.get("user.update.correct"), dbUser);
				renderJSON(message);
		    }
		    else{
				UserStatusMessage message = new UserStatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", validation.errors().toString(), dbUser);
				log.severe("User couldnt be updated " + new Gson().toJson(message));	
				renderJSON(message);
			}
		}
	}

	public static void show(Long id)  {
	    User user = User.findById(id);
	    if (user != null){
			renderJSON(new UserStatusMessage(Http.StatusCode.OK, "OK", "User found", user));
		}
		else{
			renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", "User not found"));
		}
	}
	
	public static void rememberPassword(){
		String body = params.get("body");
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
	
	public static void sendSurveyEmails(){
		List<User> users = User.findSurveyPending();
		for (User user : users){
			Mails.bookingSurvey(user);
			user.pendingSurvey = false;
			user.update();
		}
	}
	
	
	public static void sendRememberPriceEmails(){
		Collection<User> owners = User.getAllOwners();
		for (User owner : owners){
			Mails.rememberPrice(owner);
		}
	}
}
