package controllers;

import helper.HotUsaApiHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import jobs.Bootstrap;
import models.Booking;
import models.City;
import models.Deal;
import models.User;
import notifiers.Mails;
import play.Logger;
import play.Play;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {
	
	@Before
	public static void checkLanguage(){
		Logger.debug("## Accept-languages: " + request.acceptLanguage().toString());
	}
	
	public static void index() { 
		//redirect to the extranet
		Owners.index();
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