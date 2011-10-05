package controllers;

import java.util.*;
import models.*;
import play.*;
import play.mvc.Controller;


public class Cities  extends Controller {
	
	public static void cityList() {
		Cities.cityListByCountry("spain");
	}
	
	public static void cityListByCountry(String countryUrl) {
		Country country = Country.findByName(countryUrl);
        Collection<City> cities = City.findActiveCitiesByCountry(country);
        if (Security.isConnected() && Security.check("admin")){
        	City.addTestCity(cities);
        }
        renderJSON(cities);
	}
}

