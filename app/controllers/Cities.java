package controllers;


import java.util.ArrayList;
import java.util.Collection;

import models.City;
import models.Country;
import models.User;
import models.dto.CityDTO;
import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Analytics.class)
public class Cities  extends Controller {
	public static final String DEFAULT_COUNTRY = "spain";
	
	@Before(only = {"index"})
    static void checkConnected() {
		Logger.debug("## Accept-languages: " + request.acceptLanguage().toString());
		Security.checkConnected();
    }
	
	public static void index() {
		Collection<City> cities = City.findActiveCities();
		render(cities); 
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

