package controllers;

import helper.HotUsaApiHelper;
import helper.dto.DealDTO;

import java.util.*;

import models.*;
import play.*;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

public class Deals extends Controller {
	
	@Before
	public static void checkLanguage(){
		Logger.debug("### HEADERS : " + request.headers.toString());
	}
	
	public static void list(String city) {
        render(city);
	}
	
	public static void iPhoneList(String city) {
	        Collection<Deal> deals = fetchDeals(city);	
	        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
			for (Deal deal: deals){
				deal.prepareImages();//just for iphone now, make configurable in the future
				deal.fecthCity(); //retrieves city object to not to send just the city id
				dealsDtos.add(new DealDTO(deal));
			}
	        renderJSON(dealsDtos);
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
	 * Used to avoid the lazy load. do we really need this?
	 */
	private static Collection<Deal> fetchDeals(String cityName) {
			City city = City.findByName(cityName);
	        Collection<Deal> deals = Deal.findActiveDealsByCity(city);
	        return deals;
	}
	
	/*
	 * Refresh prices and availability from hotUsa hotels
	 * Used by cron GAE
	 */
	public static void refreshHotUsaPrices(){
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

