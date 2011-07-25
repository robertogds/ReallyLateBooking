package controllers;

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
				//send an email if deal is active
			}
			else{
				//send a different email if deal is inactive
			}
		}
	}
	
	private static Collection<Deal> fetchDeals(String cityName) {
			City city = City.findByName(cityName);
	        Collection<Deal> deals = Deal.findActiveDealsByCity(city);
	        return deals;
	}

}

