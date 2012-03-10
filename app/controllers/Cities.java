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
	
	@Before(only = {"index"})
    static void verifySSL(){
        if (request.secure == true ){
            redirect("http://" + request.host + request.url); 
        }
    }
	
	public static void index() {
		Collection<City> cities = staticCities();
		render(cities); 
	}
	
	private static Collection<City> staticCities() {
		Collection<City> cities = new ArrayList<City>();
		cities.add(new City("A Coruña", "a_coruna"));
		cities.add(new City("Barcelona", "barcelona"));
		cities.add(new City("Berlin", "berlin"));
		cities.add(new City("Bilbao", "bilbao"));
		cities.add(new City("Lisboa", "lisbon"));
		cities.add(new City("Londres", "london"));
		cities.add(new City("Madrid", "madrid"));
		cities.add(new City("Málaga", "malaga"));
		cities.add(new City("Mallorca", "mallorca"));
		cities.add(new City("Milan", "milan"));
		cities.add(new City("Paris", "paris"));
		cities.add(new City("Santiago", "santiago_de_compostela"));
		cities.add(new City("Sevilla", "sevilla"));
		cities.add(new City("Valencia", "valencia"));
		cities.add(new City("Valladolid", "valladolid"));
		return cities;
	}

	/** Json API Methods **/
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

