package controllers;


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

	public static void dealsByCityId(Long cityId) {
		City city = City.findById(cityId);
		Collection<Deal> deals = Deal.findActiveDealsByCityV2(city);
        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
		for (Deal deal: deals){
			dealsDtos.add(new DealDTO(deal));
		}
        renderJSON(dealsDtos);
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

