package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import models.City;
import models.Deal;
import models.dto.DealDTO;

import helper.DateHelper;
import helper.GeoHelper;
import play.Logger;
import play.data.binding.As;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import services.DealsService;
import siena.embed.Format;

/**
 * Controller for the methods required by the eDreams white label app
 * @author pablopr
 *
 */
public class Edreams extends Controller{

	@Before(only = {"findCityDealsByLatLong", "findCityDealsByCityName"})
	public static void setCORS() { 
		Http.Header hd = new Http.Header(); 
		hd.name = "Access-Control-Allow-Origin"; 
		hd.values = new ArrayList<String>(); 
		hd.values.add("*"); 
		Http.Response.current().headers.put("Access-Control-Allow-Origin",hd); 
	}
	
	/**
	 * Search for the nearest city and return the active deals
	 * @param lat
	 * @param lng
	 */
	public static void findCityDealsByLatLong(@Required  double lat, @Required  double lng, @Required @As("yyyy-MM-dd") Date checkin, @Required int nights){
		if(validation.hasErrors()){
			renderJSON(null);
		}
		else{
			List<City> cities = City.findActiveCities();
			City city = GeoHelper.getNearestCity(lat, lng, cities);
			Logger.debug("City find by coordenates %s and %s is %s", lat, lng, city);
			renderJSON(DealsService.findTonightDealsByCityV3(city));
		}
	}
	
	/**
	 * Search for a city by name and return the active deals
	 * @param name
	 */
	public static void findCityDealsByCityName(@Required String name, @Required @As("yyyy-MM-dd") Date checkin, @Required int nights){
		if(validation.hasErrors()){
			renderJSON(null);
		}
		else{
			City city = City.findByUrl(name.toLowerCase());
			Logger.debug("City find by name %s  is %s", name, city);
			renderJSON(DealsService.findTonightDealsByCityV3(city));	
		}
	}

	
	/****  JQuery Mobile Version -- DEPRECATED ****/
	@Deprecated
	public static void index() {
		render();
	}
	
	@Deprecated
	public static void searchDeals(double lat, double lng, String city, int nights, @As("yyyy-MM-dd") Date checkin){
		Logger.debug("search form with date: %s city: %s nights %s", checkin, city, nights);
		Collection<DealDTO> deals = DealsService.findDealsByGeoOrCity(lat, lng, city, nights, checkin);
		if (deals == null || deals.size() == 0){
			renderTemplate("Edreams/bookingWhiteLabel.html", lat, lng, city, nights, checkin);
		}
		else{
			render(deals, city);
		}
	}
	
	@Deprecated
	public static void deal(Long id){
		Deal hotelDeal = Deal.findById(id);
		if (hotelDeal == null){
			notFound();
		}
		City city = City.findById(hotelDeal.city.id);
		DealDTO deal = new DealDTO(hotelDeal, city.root);
		
		List<String> images = deal.images;
		render(deal, images);
	}
	
	@Deprecated
	public static void photos(String image, List<String> images, Long dealId){
		Deal deal = Deal.findById(dealId);
		Logger.debug("Image to show: %s for images list %s ", image, images);
		String prevImage = null;
		String nextImage = null;
		Boolean imageFounded = Boolean.FALSE;
		for (String photo : images){
			if (imageFounded){
				nextImage = photo;
				break;
			}
			if (!StringUtils.equalsIgnoreCase(photo, image)){
				prevImage = photo;
			}
			else{
				imageFounded = Boolean.TRUE;
			}
		}
		Logger.debug("Image prev: %s and next: %s", prevImage, nextImage);
		render(image, nextImage, prevImage, images, deal);
	}
	
	@Deprecated
	public static void map(Long dealId){
		Deal deal = Deal.findById(dealId);
		render(deal);
	}

}
