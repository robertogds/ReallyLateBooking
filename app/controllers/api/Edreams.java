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
import services.BookingServices;
import services.DealsService;
import siena.embed.Format;

/**
 * Controller for the methods required by the eDreams white label app
 * @author pablopr
 *
 */
public class Edreams extends Controller{
	
	private static final Logger log = Logger.getLogger(Edreams.class.getName());
	
	@Before
	public static void setCORS() { 
		Http.Header hd = new Http.Header(); 
		hd.name = "Access-Control-Allow-Origin"; 
		hd.values = new ArrayList<String>(); 
		hd.values.add("*"); 
		Http.Response.current().headers.put("Access-Control-Allow-Origin",hd); 
	}

	@Before
	public static void checkSignature(){
		Boolean correct = ApiSecurer.checkPartnerSignature(request);
		if (!correct){
			log.warning("Invalid signature ");
			Mails.errorMail("##WARNING: Invalid api signature", request.toString());
			ApiResponse response = new ApiResponse(Http.StatusCode.BAD_REQUEST, "ERROR", "Invalid api signature. Contact soporte@reallylatebooking.com");
			renderJSON(response.json);
		}
	}
	
	@Catch(Exception.class)
    public static void renderInternalError(Throwable throwable) {
        log.severe("Internal error at Edreams Controller… " + throwable.getStackTrace().toString());
        Mails.errorMail("##IMPORTANT: Error en White Label Api", throwable.getMessage());
        ApiResponse response = new ApiResponse(Http.StatusCode.INTERNAL_ERROR, "error", "Error interno. Contacte con atención al cliente, por favor. ");
		renderJSON(response.json);
    }
	
	public static void bookingWhiteLabel(String bookingUrl){
		String city = "ciudad";
		log.warning(bookingUrl);
		render(bookingUrl, city);
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
	
	public static void openTransaction(@Required String amount, @Required String firstName, 
			@Required String lastName, @Required String phone, @Required @Email String email, @Required Long dealId, 
			@Required int nights, @Required String partnerId){
		log.info("openTransaction: " + email + " amount:" + amount);
		if(validation.hasErrors()){
			log.severe("openTransaction: all parameters are required");
			renderError(Http.StatusCode.BAD_REQUEST,"Reserva cancelada: todos los campos son obligatorios");
		}
		else{
			try {
				User user = User.findOrCreateUserForWhiteLabel(email, firstName, lastName, phone);
				Booking booking = Booking.createBookingForWhiteLabel(dealId, user, nights, partnerId);
				booking.pending = Boolean.TRUE;
				booking.insert();
				String token = ZoozHelper.openTransaction(Double.parseDouble(amount), user);
				ApiResponse response = new ApiResponse("token", token);
				renderJSON(response.json);
			} catch (IOException e) {
				log.severe("IOException at openTransaction: " + e);
				Mails.errorMail("##IMPORTANT: Error en White Label Api: openTransaction", e.getMessage());
				renderError(Http.StatusCode.INTERNAL_ERROR, "No hemos podido realizar su reserva.");
			}
		}
		
	}
	
	public static void createBooking(@Required String email, @Required String firstName, @Required String lastName, 
			String phone, @Required Long dealId, @Required int nights, @Required String partnerId){
		
		if (!validation.hasErrors()){ 
			User user = User.findOrCreateUserForWhiteLabel(email, firstName, lastName, phone);
			Deal deal = Deal.findById(dealId);
			Partner partner = Partner.findByPartnerId(partnerId);
			Booking booking = Booking.findPendingBooking(deal, user, partner);
			if (booking != null){
				booking.validateNoCreditCart(); //Custom validation
				if (!validation.hasErrors()){ 
					booking.pending = Boolean.FALSE;
			    	booking.update();
					try {
						BookingServices.doCompleteReservation(booking, validation);
					} catch (InvalidBookingCodeException e) {
						Mails.bookingErrorMail(booking);
						renderError(Http.StatusCode.INTERNAL_ERROR,validation.errors().toString());
					}
			    	User.updateJustBooked(booking.user.id, booking.checkinDate);
					Mails.hotelBookingConfirmation(booking);
					Mails.userBookingConfirmation(booking);
					
					String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
							new BookingStatusMessage(Http.StatusCode.CREATED, "CREATED", 
									Messages.get("booking.create.correct"), booking));
					renderJSON(json);
				}
			}
			else{
				log.warning("couldn't find booking with for user " + user.email);
			}
		}
		
		log.warning("Invalid booking: " + validation.errors().toString());
		renderError(Http.StatusCode.INTERNAL_ERROR, validation.errors().toString());
	}
	
	private static void renderError(int status, String error){
		log.warning("Invalid booking: " + validation.errors().toString());
        ApiResponse response = new ApiResponse(status, "error", error);
		renderJSON(response.json);
	}
	
}
