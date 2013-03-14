package services;

import java.util.ArrayList;
import java.util.Collection;

import play.i18n.Messages;

import models.City;
import models.Country;
import models.dto.CityDTO;

public final class CitiesService {
	
	
	public static Collection<CityDTO> cityListByCountry(String countryUrl) {
		Country country = Country.findByName(countryUrl);
        Collection<City> cities = City.findActiveCitiesByCountry(country);
        Collection<CityDTO> citiesDto = prepareCityDto(cities);
        return citiesDto;
	}
	
	public static Collection<CityDTO> cityListAll() {
		Collection<Country> countries = Country.findActiveCountries();
		Collection<City> cities =  new ArrayList<City>();
		for (Country country : countries){
			cities.addAll(City.findActiveCitiesByCountry(country));
		}
        Collection<CityDTO> citiesDto = prepareCityDto(cities);
        return citiesDto;
	} 
	

	public static Collection<City> getStaticCities() {
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

	private static Collection<CityDTO> prepareCityDto( Collection<City> cities){
		Collection<CityDTO> citiesDto = new ArrayList<CityDTO>();
		for (City city: cities){
			CityDTO cityDto = new CityDTO(city);
			citiesDto.add(cityDto);
		}
        return citiesDto;
	}
}
