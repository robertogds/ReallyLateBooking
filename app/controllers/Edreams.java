package controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import notifiers.Mails;

import org.apache.commons.lang.StringUtils;

import models.Booking;
import models.City;
import models.Deal;
import models.User;
import models.dto.BookingStatusMessage;
import models.dto.DealDTO;
import models.dto.StatusMessage;
import models.exceptions.InvalidBookingCodeException;

import helper.DateHelper;
import helper.GeoHelper;
import helper.JsonHelper;
import helper.paypal.PaypalHelper;
import play.Logger;
import play.data.binding.As;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import services.BookingServices;
import services.DealsService;
import siena.embed.Format;

/**
 * Controller for the methods required by the eDreams white label app
 * @author pablopr
 *
 */
public class Edreams extends Controller{

	
	@Before(only = {"findCityDealsByLatLong", "findCityDealsByCityName", "createBooking"})
	public static void setCORS() { 
		Http.Header hd = new Http.Header(); 
		hd.name = "Access-Control-Allow-Origin"; 
		hd.values = new ArrayList<String>(); 
		hd.values.add("*"); 
		Http.Response.current().headers.put("Access-Control-Allow-Origin",hd); 
	}
	
	/**
	 * Search for the nearest city and return the active deals
	 * @param lat
	 * @param lng
	 */
	public static void findCityDealsByLatLong(@Required  double lat, @Required  double lng, @Required @As("yyyy-MM-dd") Date checkin, @Required int nights){
		if(validation.hasErrors()){
			renderJSON(null);
		}
		else{
			List<City> cities = City.findActiveCities();
			City city = GeoHelper.getNearestCity(lat, lng, cities);
			Logger.debug("City find by coordenates %s and %s is %s", lat, lng, city);
			renderJSON(DealsService.findDealsByCityAndDateV3(city, checkin, nights));
		}
	}
	
	/**
	 * Search for a city by name and return the active deals
	 * @param name
	 */
	public static void findCityDealsByCityName(@Required String name, @Required @As("yyyy-MM-dd") Date checkin, @Required int nights){
		if(validation.hasErrors()){
			renderJSON(null);
		}
		else{
			City city = City.findByUrl(name.toLowerCase());
			Logger.debug("City find by name %s  is %s", name, city);
			renderJSON(DealsService.findDealsByCityAndDateV3(city, checkin, nights));	
		}
	}
	
	
	public static void createBooking(@Required String email, @Required String firstName, @Required String lastName, String phone, @Required Long dealId,
			@Required int nights){
		if (!validation.hasErrors()){ 
			User user = User.findOrCreateUserForWhiteLabel(email, firstName, lastName, phone);
			Booking booking = Booking.createBookingForWhiteLabel(dealId, user, nights);
			booking.validateNoCreditCart(); //Custom validation
			if (!validation.hasErrors()){ 
				booking.insert();
		    	booking.get();
				try {
					BookingServices.doCompleteReservation(booking, validation);
				} catch (InvalidBookingCodeException e) {
					Mails.bookingErrorMail(booking);
					renderBookingError(booking);
				}
		    	User.updateJustBooked(booking.user.id, booking.checkinDate);
				Mails.hotelBookingConfirmation(booking);
				
				String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
						new BookingStatusMessage(Http.StatusCode.CREATED, "CREATED", 
								Messages.get("booking.create.correct"), booking));
				renderJSON(json);
			}
		}
		Logger.debug("Invalid booking: " + validation.errors().toString());
		String messageJson = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
				new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", validation.errors().toString()));
		renderJSON(messageJson);
	}
	
	private static void renderBookingError(Booking booking){
		Logger.debug("Invalid booking: " + validation.errors().toString());
		String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
				new BookingStatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", validation.errors().toString(), booking));
		renderJSON(json);
	}
	
}
