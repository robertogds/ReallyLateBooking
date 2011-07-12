package controllers;

import helper.dto.DealDTO;

import java.util.*;

import models.*;
import play.*;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Deals extends Controller {
	
	@Before
	public static void checkLanguage(){
		Logger.debug("### HEADERS : " + request.headers.toString());
	}
	
	@Check("admin")
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
	
	private static Collection<Deal> fetchDeals(String cityName) {
			City city = City.findByName(cityName);
	        Collection<Deal> deals = Deal.findActiveDealsByCity(city);
	        return deals;
	}

}

