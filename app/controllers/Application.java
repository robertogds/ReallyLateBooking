package controllers;

import helper.AffiliateHelper;
import helper.UAgentInfo;
import helper.hotusa.HotUsaApiHelper;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import jobs.Bootstrap;
import models.Booking;
import models.Coupon;
import models.Deal;
import models.MyCoupon;
import models.User;
import notifiers.Mails;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.validation.Email;
import play.data.validation.Required;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

import com.google.appengine.repackaged.org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@With({I18n.class, LogExceptions.class})
public class Application extends Controller {
	
	@Before(only = {"login", "authenticate","register"})
    static void verifySSL(){
  //      if (request.secure == false && Play.mode.isProd()){
  //          redirect("https://" + request.host + request.url); 
   //     }
    }
	
	@Before(only = {"index"})
	static void checkorigin() throws Throwable{
		AffiliateHelper.checkorigin(session, params);
	}
	

	@Before(only={"index"})
	public static void mobileDetection(){
		//Http.Header header = Http.Request.current().headers.get("user-agent"); 
		String userAgent = request.headers.get("user-agent") != null ? request.headers.get("user-agent").value() : "";
		String accept = request.headers.get("accept")!= null ? request.headers.get("accept").value() : "";
		UAgentInfo agent = new UAgentInfo(userAgent, accept);
		Logger.debug("User-Agent: %s isIphone: %s isAndroid: %s", userAgent, agent.isIphone, agent.isIphone);
		if (agent.isIphone || agent.isAndroidPhone){
			mobile();
		}
	}
	
	public static void full() {
		if(Security.isConnected() && Security.connectedUserExists()){
			Cities.index();
		}
		else{
			renderTemplate("Application/index.html");
		}
    }
	
	public static void mobile() {
		render();
	}
	
	public static void index() {
		if(Security.isConnected() && Security.connectedUserExists()){
			Cities.index();
		}
		else{
			render();
		}
    }
    
    public static void register(@Required String username, @Required String password, String firstName, String lastName, String returnUrl) throws Throwable{
    	username = username  != null ? username.trim().toLowerCase() : username;
    	returnUrl = returnUrl != null ? returnUrl : "/";
		if (!validation.hasErrors() && validation.email(username).ok){
			Logger.debug("##  User register is correct %s", username); 
			boolean isAdmin = false;
			boolean validated = true;
			User user = new User(username, password, firstName, lastName,  isAdmin,  validated);
			user.fromWeb = true;
			user.validate();
			if (!validation.hasErrors()){
				user.insert();
				Mails.welcome(user);
				flash.success(Messages.get("web.register.correct"));
				login(username, password);
				Logger.debug("##  User has validate correctly and now redirect to %s", returnUrl); 
		        redirect(returnUrl);
			}
		}
		Logger.debug("##  User register hasErrors %s", username); 
		flash.error(Messages.get("web.register.incorrect"));
		redirect(returnUrl);
	}
    
    public static void login(@Required @Email String username, @Required String password, String returnUrl) throws Throwable{
    	login(username, password);
        redirect(returnUrl);
	}
    
    public static void authenticate(@Required String username, @Required String password) throws Throwable {
    	login(username, password);
        // Redirect to the original URL (or /)
        Secure.redirectToOriginalURL();
    }
    
    public static void logout() {
    	session.remove("username");
        session.remove("user");
        session.remove("firstName");
        session.remove("lastName");
        session.remove("uuid");
        session.remove("userId");
        redirect("Application.index");
    }
    
    public static void contactForm(@Required @Email String email, String name, @Required String message,
    		String returnUrl,  String code, String randomID){
    	//validation.equals(code, Cache.get(randomID));
    	if(validation.hasErrors()) {
            params.flash();
            flash.error(Messages.get("web.contact.incorrect"));
        }
        else{
        	Mails.contactForm(email, name, message);
        	flash.success(Messages.get("web.contact.correct"));
        }
    	redirect(returnUrl);
    }
    
    public static void hotelsForm(@Required @Email String email, @Required String hotelName, 
    		@Required String phone, String name, @Required String message, String returnUrl,
    		 String code, String randomID){
    	//validation.equals(code, Cache.get(randomID));
    	if(validation.hasErrors()) {
            params.flash();
            flash.error(Messages.get("web.contact.incorrect"));
        }
        else{
        	Mails.hotelForm(email, name, hotelName,  phone ,message);
        	flash.success(Messages.get("web.contact.correct"));
        }
    	redirect(returnUrl);
    }
    
	public static void bootstrap(){
		if (Play.mode.isDev()){
			//Workaround needed because jobs dont work on gae
			Bootstrap job = new Bootstrap();
			job.doJob();
			//End of workaround
		}
		index(); 
	}
	
	public static void landing(){
		//Http.Header header = Http.Request.current().headers.get("user-agent"); 
		String userAgent = request.headers.get("user-agent") != null ? request.headers.get("user-agent").value() : "";
		String accept = request.headers.get("accept")!= null ? request.headers.get("accept").value() : "";
		UAgentInfo agent = new UAgentInfo(userAgent, accept);
		Logger.debug("User-Agent: %s isIphone: %s isAndroid: %s", userAgent, agent.isIphone, agent.isIphone);
		if (agent.isIphone){
			redirect(Messages.get("itunes.url"));
		}
		else if (agent.isAndroidPhone){
			redirect(Messages.get("android.play.url"));
		}
		index();
	}
	
	public static void activate(String code){
		Logger.debug("##### Validatind user with code: " + code);
		User user = User.all().filter("validationCode", code).get();
		if (user != null){
			user.validated = true;
			user.update();
			Mails.welcome(user);
		}
		render(user);
	}
	
	
	public static void confirmReservations(){
		List<Booking> bookings = Booking.findConfirmationRequiredDeals();
		for(Booking booking : bookings){
			String localizador = HotUsaApiHelper.confirmation(booking.code);
			if (localizador != null){
				Logger.info("Confirmation correctly received: " + localizador);
				booking.code = localizador;
				booking.needConfirmation = Boolean.FALSE;
				booking.update();
				booking.deal = Deal.findById(booking.deal.id);
				booking.user = User.findById(booking.user.id);
			}
			else{
				String subject = "#ERROR# Hotusa dio error al confirmar la reserva " + booking.code;
				String content = "Hotel: " + booking.hotelName + " User: " + booking.userEmail;
				Mails.errorMail(subject, content);
				Logger.error("DANGER: Confirmation not received but User thinks its confirmed");
			}
		}
	}
	
	public static void pruebas(){
		//User user = User.all().fetch().get(0);
		Collection<Booking> bookings = Booking.all().order("checkinDate").fetch();
		Logger.debug("Total bookings %s", bookings.size());
		for(Booking booking: bookings){
			Logger.debug("Booking date %s booking code %s", booking.checkinDate, booking.code);
			User user =  User.findById(booking.user.id);
			
			if (user.firstBookingDate == null){
				user.firstBookingDate = booking.checkinDate;
				user.update();
			}
			else{
				Calendar firstBookingCal = Calendar.getInstance();
				firstBookingCal.setTime(user.firstBookingDate);
				
				Calendar bookingCal = Calendar.getInstance();
				bookingCal.setTime(booking.checkinDate);
				
				if (bookingCal.before(firstBookingCal)){
					Logger.debug("bookingCal %s is before first booking %s", bookingCal.getTime(), firstBookingCal.getTime());
					user.firstBookingDate = booking.checkinDate;
					user.update();
				}
				else{
					Logger.debug("bookingCal %s is NOT before first booking %s", bookingCal.getTime(), firstBookingCal.getTime());
				}
			}
			
			
		}
		//renderTemplate("Mails/welcomeV2.html", user);
	}
	
	
	private static void login(String username, String password){
        if(!validation.hasErrors() && 
        		Security.authenticate(username, password)) {
        	connectUser(username);
        }
        else{
        	flash.keep("url");
            flash.error(Messages.get("web.login.incorrect"));
            params.flash();
        }
    }
	
    /**
     * Find user by username and creates the user session
     * Username was previously checked and is allowed to enter
     * @param username
     */
    private static void connectUser(String username){
    	// Mark user as connected
        User user = User.findByEmail(username);
        Logger.debug("User founded by email : "+ username + " user: " + user.email);
    	if (user!= null){
    		user.createUserSession();
    	}
    }
    
    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#FF");
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }
}