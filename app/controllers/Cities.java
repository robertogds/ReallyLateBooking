package controllers;

import helper.dto.CityDTO;
import helper.dto.CountryDTO;

import java.util.*;

import models.*;
import play.*;
import play.i18n.Lang;
import play.mvc.Before;
import play.mvc.Controller;


public class Cities  extends Controller {
	public static final String DEFAULT_COUNTRY = "spain";
	
	@Before
	static void analytics(){
		Statistic.saveVisit(request.path);
	}
	
	public static void cityListAll() {
        Collection<City> cities = City.findActiveCities();
        Collection<CityDTO> citiesDto = prepareCityDto(cities);
        renderJSON(citiesDto);
	} 
	
	public static void cityList() {
		Country country = Country.findByName(DEFAULT_COUNTRY);
        Collection<City> cities = City.findActiveCitiesByCountry(country);
        Collection<CityDTO> citiesDto = prepareCityDto(cities);
        renderJSON(citiesDto);
	}
	
	public static void cityListByCountry(String countryUrl) {
		Country country = Country.findByName(countryUrl);
        Collection<City> cities = City.findActiveCitiesByCountry(country);
        Collection<CityDTO> citiesDto = prepareCityDto(cities);
        renderJSON(citiesDto);
	}
	
	private static Collection<CityDTO> prepareCityDto( Collection<City> cities){
		Collection<CityDTO> citiesDto = new ArrayList<CityDTO>();
		for (City city: cities){
			CityDTO cityDto = new CityDTO(city);
			citiesDto.add(cityDto);
			Logger.debug("City name with locale is: " + cityDto.name);
		}
        return citiesDto;
	}
	
}

