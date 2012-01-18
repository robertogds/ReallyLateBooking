package controllers;

import helper.DateHelper;
import helper.HotUsaApiHelper;

import java.util.*;

import com.google.appengine.api.urlfetch.HTTPHeader;

import models.*;
import models.dto.DealDTO;
import models.dto.StatusMessage;
import play.*;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;

@With(Analytics.class)
public class Deals extends Controller {
	
	@Before(only = {"show","list","bookingForm"})
    static void checkConnected() {
		Logger.debug("## Accept-languages: " + request.acceptLanguage().toString());
		Security.checkConnected();
    }
	
	/**
	 * Renders the booking form for the given deal
	 **/
	public static void bookingForm(Long id) {
		Deal deal = Deal.findById(id);
		if (deal != null){
			deal.prepareImages();//just for iphone now, make configurable in the future
			render(deal);
		}
		else{
			notFound();
		}
	}
	
	public static void show(String cityUrl, Long id) {
		Deal deal = Deal.findById(id);
		if (deal != null){
			deal.prepareImages();//just for iphone now, make configurable in the future
			render(deal);
		}
		else{
			notFound();
		}
	}
	
	public static void list(String cityUrl) {
		City city = City.findByName(cityUrl);
		if (city != null){
			Collection<Deal> deals = Deal.findActiveDealsByCityV2(city);
			
	        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
			for (Deal deal: deals){
				deal.prepareImages();//just for iphone now, make configurable in the future
				dealsDtos.add(new DealDTO(deal));
			}
	        render(city, dealsDtos);
		}
		else{
			notFound();
		}
	}
	
	/*
	 * Retrieves all the active deals from all the active locations
	 * in the required city.
	 * */
	public static void listV2(String cityUrl) {
		City city = City.findByName(cityUrl);
		
		if (city != null){
			Collection<Deal> deals = Deal.findActiveDealsByCityV2(city);
	        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
			for (Deal deal: deals){
				deal.prepareImages();//just for iphone now, make configurable in the future
				deal.fecthCity(); //retrieves city object to not to send just the city id
				dealsDtos.add(new DealDTO(deal));
			}
	        renderJSON(dealsDtos);
		}
		else{
			renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", Messages.get("city.notfound")));
		}
	}
	
	public static void iPhoneList(String cityUrl) {
		City city = City.findByName(cityUrl);
		if (city != null){
			Collection<Deal> deals = Deal.findActiveDealsByCity(city);
	        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
			for (Deal deal: deals){
				deal.prepareImages();//just for iphone now, make configurable in the future
				deal.fecthCity(); //retrieves city object to not to send just the city id
				dealsDtos.add(new DealDTO(deal));
			}
	        renderJSON(dealsDtos);
		}
		else{
			renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", Messages.get("city.notfound")));
		}
	}
	
	/*
	 * Send to hoteliers the current status of their hotels
	 * Not sure if we'll do it
	 */
	public static void sendStatusEmailToOwners(){
		Collection<City> cities = City.all().fetch();
		for (City city : cities){
			sendStatusEmailToOwnersByCity(city);
		}
	}
	
	private static void sendStatusEmailToOwnersByCity(City city){
		Collection<Deal> deals = Deal.findActiveDealsByCity(city);
		for (Deal deal: deals){
			if (deal.active){
				//TODO send an email if deal is active
			}
			else{
				//TODO send a different email if deal is inactive
			}
		}
	}
	
	
	/*
	 * Refresh prices and availability from hotUsa hotels
	 * Used by cron GAE
	 */
	public static void refreshHotUsaPrices(){
		if (DateHelper.isWorkingTime()){
			Logger.debug("### CRON TASK REFRESHING HOTELS AVAILABILITY START ###");
			List<Deal> deals = Deal.findDealsFromHotUsa();
			if (deals.size() > 0){
				HotUsaApiHelper.getHotelPrices(deals);
			}
			else{
				Logger.debug("We have no any hotusa hotel");
			}
			
			Logger.debug("### CRON TASK REFRESHING HOTELS AVAILABILITY END ###");
		}
		
	}

}

