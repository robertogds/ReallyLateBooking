package controllers.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import notifiers.Mails;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.google.gson.Gson;

import controllers.ThreeScale;
import controllers.oauth.ApiSecurer;

import models.Booking;
import models.City;
import models.Deal;
import models.Partner;
import models.User;
import models.dto.ApiResponse;
import models.dto.BookingStatusMessage;
import models.dto.DealDTO;
import models.dto.StatusMessage;
import models.exceptions.InvalidBookingCodeException;

import helper.DateHelper;
import helper.GeoHelper;
import helper.JsonHelper;
import helper.paypal.PaypalHelper;
import helper.zooz.ZoozHelper;
import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;
import services.BookingServices;
import services.DealsService;
import siena.embed.Format;

/**
 * Controller for the methods required by the API 
 * @author pablopr
 *
 */
@With(ThreeScale.class)
public class Deals extends Controller{
	
	private static final Logger log = Logger.getLogger(Deals.class.getName());
	
	
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
			log.info("City find by coordenates " + lat + ", " + lng + " is " + city);
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
			log.info("City find by name " + name + " is " + city);
			renderJSON(DealsService.findDealsByCityAndDateV3(city, checkin, nights));	
		}
	}
}
