package controllers;

import helper.AffiliateHelper;
import helper.DateHelper;
import helper.hotusa.HotUsaApiHelper;

import java.util.*;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.*;
import models.dto.CityDTO;
import models.dto.DealDTO;
import models.dto.StatusMessage;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;
import services.DealsService;

@With({I18n.class, LogExceptions.class})
public class Deals extends Controller {
	
	@Before(only = {"show","list","bookingForm"})
    static void checkConnected() {
		if (params._contains("email")){
			//If user is already logged in we dont care about coming from an email
			if (Security.connectedUserExists()){
				Security.checkConnected();
			}
			else{
				renderArgs.put("fromEmail", true);
			}
		}
		else{
			Security.checkConnected();
		}
    }
	
	@Before(only = {"bookingForm", "show", "list"})
	static void checkorigin() throws Throwable{
		AffiliateHelper.checkorigin(session, params);
	}
	
	@Before(only = {"listV2"})
	public static void setCORS() { 
		Http.Header hd = new Http.Header(); 
		hd.name = "Access-Control-Allow-Origin"; 
		hd.values = new ArrayList<String>(); 
		hd.values.add("*"); 
		Http.Response.current().headers.put("Access-Control-Allow-Origin",hd); 
	}
	
	/**
	 * Renders the booking form for the given deal
	 **/
	public static void bookingForm(Long id) {
		Deal deal = Deal.findById(id);
		if (deal != null){
			prepareDeal(deal);
			render();
		}
		else{
			notFound();
		}
	}
	
	/**
	 * Renders the deal detail page
	 **/
	public static void show(String cityUrl, Long id) {
		Deal deal = Deal.findById(id);
		if (deal != null){
			prepareDeal(deal);
			render();
		}
		else{
			notFound();
		}
	}
	
	/**
	 * Prepare image deals, i18n text and add the dto to the render params
	 * @param deal
	 */
	private static void prepareDeal(Deal deal){
		DealDTO dealDto = new DealDTO(deal, deal.city.url);
		dealDto.textToHtml();
		renderArgs.put("deal", dealDto);
	}
	
	/**
	 * Retrieves all the active deals from all the active locations
	 * in the required city.
	 * Renders html page 
	 * @param cityUrl
	 */
	public static void list(String cityUrl) {
		City cityOrig = City.findByUrl(cityUrl);
		if (cityOrig != null){
			CityDTO city = new CityDTO(cityOrig);
			int hour = DateHelper.getCurrentHour(cityOrig.utcOffset);
			boolean open = DateHelper.isActiveTime(hour);
			Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
			
			if (open){
				User user = (User)renderArgs.get("user");
				user = user!= null ? User.findById(user.id) : null;
				Boolean noLimits = user!= null && user.isPartner;
				Boolean hideAppOnly = Boolean.TRUE;
				Collection<Deal> deals = DealsService.findActiveDealsByCityV2(cityOrig, noLimits, hideAppOnly);
				for (Deal deal: deals){
					dealsDtos.add(new DealDTO(deal, cityOrig.url));
				}
				if (city.showHint){
					flash.error(city.hint);
				}
			}
			else{
				Date countdown = DateHelper.getTimeToOpen(hour);
				renderArgs.put("countdown", countdown);
			}
			
	        render(city, dealsDtos, open);
		}
		else{
			notFound();
		}
	}

	public static void transloadit(String dealId, String city, String hotel, String transloadit, String redirectUrl){
		Deal deal = Deal.findById(new Long(dealId));
		deal.autoImageUrl = Boolean.TRUE;	
		//Logger.debug("Transloadit params: %s",  params.get("transloadit").toString());
		JsonParser parser = new JsonParser();
		JsonObject response = (JsonObject)parser.parse(params.get("transloadit"));
		JsonObject results = response.getAsJsonObject("results");
		//Logger.debug("Transloadit Results: %s",  results.toString());
		JsonArray images = results.getAsJsonArray("compress");
		for (JsonElement image: images){
			JsonObject imageObject = image.getAsJsonObject();
			String name = imageObject.get("name").getAsString();
			deal.addImage(city +"/"+ hotel +"/" + name);
		} 
		deal.update();
		redirect(redirectUrl);
	}
	
	/**
	 * Retrieves all the active deals from all the active locations
	 * in the required city.
	 * Renders JSON 
	 * @param cityUrl
	 */
	public static void listV2(String cityUrl) {
		City city = City.findByUrl(cityUrl);
		if (city != null){
			int hour = DateHelper.getCurrentHour(city.utcOffset);
			boolean open = DateHelper.isActiveTime(hour);
			if (!open){
				renderJSON(new StatusMessage(DateHelper.CITY_CLOSED, DateHelper.getTimeToOpenString(hour), "Not open yet"));
				//renderJSON(new ArrayList<DealDTO>());
			}
			else{
				Boolean noLimits = Boolean.FALSE;
				Boolean hideAppOnly = Boolean.FALSE;
				Collection<Deal> deals = DealsService.findActiveDealsByCityV2(city, noLimits, hideAppOnly);
		        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
				for (Deal deal: deals){
					dealsDtos.add(new DealDTO(deal, city.url));
				}
		        renderJSON(dealsDtos);
			}
		}
		else{
			renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", Messages.get("city.notfound")));
		}
	}
	
	/**
	 * Retrieves all the active deals from all the active locations
	 * in the required city.
	 * Renders JSON 
	 * @param cityUrl
	 */
	@Deprecated
	public static void iPhoneList(String cityUrl) {
		City city = City.findByUrl(cityUrl);
		if (city != null){
			//odd workaround to maintain backwards compatibility with the city zones
			if (city.isRootCityWithZones()){
				city = city.mainZone;
				city.get();
			}
			Boolean noLimits = Boolean.FALSE;
			Boolean hideAppOnly = Boolean.FALSE;
			Collection<Deal> deals = DealsService.findActiveDealsByCity(city, noLimits, hideAppOnly);
	        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
			for (Deal deal: deals){
				deal.fecthCity(); //retrieves city object to not to send just the city id
				dealsDtos.add(new DealDTO(deal, city.url));
			}
	        renderJSON(dealsDtos);
		}
		else{
			renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", Messages.get("city.notfound")));
		}
	}
	
	/*
	 * Refresh prices and availability from hotUsa hotels
	 * Used by cron GAE
	 */
	public static void refreshHotUsaPrices(){
		List<City> cities = City.findActiveCities();
		HotUsaApiHelper.getHotelPricesByCityList(cities);
	}
	
	public static void showMovePrices(){
		render();
	}
	
	public static void movePrices(){
		List<Deal> deals =  Deal.findDealsNotFromHotUsa();
		for (Deal deal : deals){
			DealsService.movePriceByDeal(deal);
		}
	}


}

