package controllers;

import helper.HotUsaApiHelper;

import java.util.Collection;
import java.util.List;

import jobs.Bootstrap;
import models.Booking;
import models.City;
import models.Deal;
import models.User;
import notifiers.Mails;
import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.libs.Crypto;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;

public class Application extends Controller {
	
	public static void index() {
        if(session.contains("user") && User.findById(Long.valueOf(session.get("userId"))) != null) {
            Cities.index();
        }
        render();
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
    
    
    public static void authenticate(@Required String username, String password) throws Throwable {
        // Check tokens
        Boolean allowed = (Boolean)Security.authenticate(username, password);
         
        if(validation.hasErrors() || !allowed) {
            flash.keep("url");
            flash.error("Nombre de usuario o contraseña incorrecta");
            params.flash();
        }
        else{
        	// Mark user as connected
            User user = User.findByEmail(username);
            Logger.debug("User founded by email : "+ username + " user: " + user.email);
        	if (user!= null){
        		user.createUserSession();
        	}
        }
        
        // Redirect to the original URL (or /)
        Secure.redirectToOriginalURL();
    }
	
	public static void mobile(){
		if (Play.mode.isDev()){
			//Workaround needed because jobs dont work on gae
			Bootstrap job = new Bootstrap();
			job.doJob();
			//End of workaround
		}
		
		
		Collection<City> cities = City.findActiveCities();
		Logger.debug("Number of cities: " + cities.size());
		render(cities); 
	}
	
	
	public static void activate(String code){
		Logger.debug("##### Validatind user with code: " + code);
		User user = User.all().filter("validationCode", code).get();
		if (user != null){
			Logger.debug("User is not null");
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
				Logger.debug("Confirmation correctly received: " + localizador);
				booking.code = localizador;
				booking.needConfirmation = Boolean.FALSE;
				booking.update();
				booking.deal = Deal.findById(booking.deal.id);
				booking.user = User.findById(booking.user.id);
				Mails.userBookingConfirmation(booking);
			}
			else{
				Logger.error("DANGER: Confirmation not received but User thinks its confirmed");
			}
		}
	}
	
}