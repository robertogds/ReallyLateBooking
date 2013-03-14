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
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;
import services.CitiesService;
import services.DealsService;

@With({I18n.class, LogExceptions.class})
public class Cities  extends Controller {
	public static final String DEFAULT_COUNTRY = "spain";
	
	@Before(only = {"index"})
    static void checkConnected() {
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
		Collection<City> cities = CitiesService.getStaticCities();
		render(cities); 
	}
	
	/** Json API Methods **/
	public static void dealsByCityId(Long cityId) {
		City city = City.findById(cityId);
		Boolean noLimits = Boolean.FALSE;
		Boolean hideAppOnly = Boolean.FALSE;
		Collection<Deal> deals = DealsService.findActiveDealsByCityV2(city, noLimits, hideAppOnly);
        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
		for (Deal deal: deals){
			dealsDtos.add(new DealDTO(deal, city.url));
		}
        renderJSON(dealsDtos);
	}
	
	public static void cityListAll() {
        Collection<CityDTO> citiesDto = CitiesService.cityListAll();
        renderJSON(citiesDto);
	} 
	
	public static void cityList() {
		Country country = Country.findByName(DEFAULT_COUNTRY);
		Collection<CityDTO> citiesDto = CitiesService.cityListByCountry(country.url);
        renderJSON(citiesDto);
	}
	
	public static void cityListByCountry(String countryUrl) {
        Collection<CityDTO> citiesDto = CitiesService.cityListByCountry(countryUrl);
        renderJSON(citiesDto);
	}
	
	
}

