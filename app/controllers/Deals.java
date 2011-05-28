package controllers;

import java.util.*;
import models.*;
import play.*;
import play.mvc.Controller;


public class Deals extends Controller {
	
	public static void iPhoneList(String city) {
	        Collection<Deal> deals = fetchDeals(city);	        
			for (Deal deal: deals){
				deal.prepareImages();//just for iphone now, make configurable in the future
				deal.fecthCity(); //retrieves city object to not to send just the city id
			}
	        renderJSON(deals);
	}
	
	private static Collection<Deal> fetchDeals(String cityName) {
			City city = City.findByName(cityName);
	        Collection<Deal> deals = Deal.findActiveDealsByCity(city);
	        return deals;
	}

}

