package controllers;


import helper.AffiliateHelper;

import java.util.ArrayList;
import java.util.Collection;

import models.City;
import models.Country;
import models.Deal;
import models.User;
import models.dto.CityDTO;
import models.dto.DealDTO;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;

@With({I18n.class, LogExceptions.class})
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
	
	@Before(only = {"index"})
	static void checkorigin() throws Throwable{
		AffiliateHelper.checkorigin(session, params);
	}
	
	@Before(only = {"cityListAll"})
	public static void setCORS() { 
		Http.Header hd = new Http.Header(); 
		hd.name = "Access-Control-Allow-Origin"; 
		hd.values = new ArrayList<String>(); 
		hd.values.add("*"); 
		Http.Response.current().headers.put("Access-Control-Allow-Origin",hd); 
	}
	
	public static void index() {
		Collection<City> cities = staticCities();
		render(cities); 
	}
	
	private static Collection<City> staticCities() {
		Collection<City> cities = new ArrayList<City>();
		cities.add(new City(Messages.get("web.cities.barcelona"), "barcelona"));
		cities.add(new City(Messages.get("web.cities.berlin"), "berlin"));
		cities.add(new City(Messages.get("web.cities.bruselas"), "bruselas"));
		cities.add(new City(Messages.get("web.cities.budapest"), "budapest"));
		cities.add(new City(Messages.get("web.cities.florencia"), "florencia"));
		cities.add(new City(Messages.get("web.cities.lisboa"), "lisbon"));
		cities.add(new City(Messages.get("web.cities.londres"), "london"));
		cities.add(new City(Messages.get("web.cities.madrid"), "madrid"));
		cities.add(new City(Messages.get("web.cities.milan"), "milan"));
		cities.add(new City(Messages.get("web.cities.paris"), "paris"));
		cities.add(new City(Messages.get("web.cities.praga"), "praga"));
		cities.add(new City(Messages.get("web.cities.roma"), "roma"));
		cities.add(new City(Messages.get("web.cities.valencia"), "valencia"));
		cities.add(new City(Messages.get("web.cities.venecia"), "venecia"));
		cities.add(new City(Messages.get("web.cities.viena"), "viena"));
		
		cities.add(new City(Messages.get("web.cities.coruna"), "a_coruna"));
		cities.add(new City(Messages.get("web.cities.bilbao"), "bilbao"));
		cities.add(new City(Messages.get("web.cities.granada"), "granada"));
		cities.add(new City(Messages.get("web.cities.malaga"), "malaga"));
		cities.add(new City(Messages.get("web.cities.mallorca"), "mallorca"));
		cities.add(new City(Messages.get("web.cities.salamanca"), "salamanca"));
		cities.add(new City(Messages.get("web.cities.compostela"), "santiago_de_compostela"));
		cities.add(new City(Messages.get("web.cities.sevilla"), "sevilla"));
		cities.add(new City(Messages.get("web.cities.vigo"), "vigo"));
		cities.add(new City(Messages.get("web.cities.valladolid"), "valladolid"));
		cities.add(new City(Messages.get("web.cities.zaragoza"), "zaragoza"));
		return cities;
	}

	/** Json API Methods **/
	public static void dealsByCityId(Long cityId) {
		City city = City.findById(cityId);
		Boolean noLimits = Boolean.FALSE;
		Boolean hideAppOnly = Boolean.FALSE;
		Collection<Deal> deals = Deal.findActiveDealsByCityV2(city, noLimits, hideAppOnly);
        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
		for (Deal deal: deals){
			dealsDtos.add(new DealDTO(deal, city.url));
		}
        renderJSON(dealsDtos);
	}
	
	public static void cityListAll() {
		Collection<Country> countries = Country.findActiveCountries();
		Collection<City> cities =  new ArrayList<City>();
		for (Country country : countries){
			cities.addAll(City.findActiveCitiesByCountry(country));
		}
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
		}
        return citiesDto;
	}
	
}

