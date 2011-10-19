package controllers;

import helper.dto.CityDTO;
import helper.dto.CountryDTO;

import java.util.*;

import models.*;
import play.*;
import play.i18n.Lang;
import play.mvc.Controller;


public class Cities  extends Controller {
	
	public static void cityList() {
		String lang = Lang.get();
		Cities.cityListByCountry("spain", lang);
	}
	
	public static void cityListByCountry(String countryUrl, String lang) {
		Country country = Country.findByName(countryUrl);
        Collection<City> cities = City.findActiveCitiesByCountry(country);
        if (Security.isConnected() && Security.check("admin")){
        	City.addTestCity(cities);
        }
        Collection<CityDTO> citiesDto = new ArrayList<CityDTO>();
		for (City city: cities){
			citiesDto.add(new CityDTO(city, lang));
		}
        renderJSON(citiesDto);
	}
}

