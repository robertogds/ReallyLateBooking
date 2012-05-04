package controllers;

import helper.DateHelper;
import helper.hotusa.HotUsaApiHelper;

import java.util.*;

import com.google.appengine.api.urlfetch.HTTPHeader;

import models.*;
import models.dto.CityDTO;
import models.dto.DealDTO;
import models.dto.StatusMessage;
import play.*;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;

@With(Analytics.class)
public class Deals extends Controller {
	
	@Before(only = {"show","list","bookingForm"})
    static void checkConnected() {
		Logger.debug("## Accept-languages: " + request.acceptLanguage().toString());
		if (params._contains("email")){
			//If user is already logged in we dont care about coming from an email
			if (Security.connectedUserExists()){
				Security.checkConnected();
			}
			else{
				renderArgs.put("fromEmail", true);
			}
		}
		else{
			Security.checkConnected();
		}
    }
	
	/**
	 * Renders the booking form for the given deal
	 **/
	public static void bookingForm(Long id) {
		Deal deal = Deal.findById(id);
		if (deal != null){
			prepareDeal(deal);
			render();
		}
		else{
			notFound();
		}
	}
	
	/**
	 * Renders the deal detail page
	 **/
	public static void show(String cityUrl, Long id) {
		Deal deal = Deal.findById(id);
		if (deal != null){
			prepareDeal(deal);
			render();
		}
		else{
			notFound();
		}
	}
	
	/**
	 * Prepare image deals, i18n text and add the dto to the render params
	 * @param deal
	 */
	private static void prepareDeal(Deal deal){
		DealDTO dealDto = new DealDTO(deal);
		dealDto.textToHtml();
		renderArgs.put("deal", dealDto);
	}
	
	/**
	 * Retrieves all the active deals from all the active locations
	 * in the required city.
	 * Renders html page 
	 * @param cityUrl
	 */
	public static void list(String cityUrl) {
		City cityOrig = City.findByUrl(cityUrl);
		if (cityOrig != null){
			Collection<Deal> deals = Deal.findActiveDealsByCityV2(cityOrig);
	        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
			for (Deal deal: deals){
				dealsDtos.add(new DealDTO(deal));
			}
			
			int hour = DateHelper.getCurrentHour(cityOrig.utcOffset);
			boolean open = DateHelper.isActiveTime(hour);
			if (!open){
				Date countdown = DateHelper.getTimeToOpen(hour);
				renderArgs.put("countdown", countdown);
			}
			CityDTO city = new CityDTO(cityOrig);
			
			if (city.showHint){
				flash.error(city.hint);
			}
	        render(city, dealsDtos, open);
		}
		else{
			notFound();
		}
	}
	
	
	/**
	 * Retrieves all the active deals from all the active locations
	 * in the required city.
	 * Renders JSON 
	 * @param cityUrl
	 */
	public static void listV2(String cityUrl) {
		City city = City.findByUrl(cityUrl);
		if (city != null){
			Collection<Deal> deals = Deal.findActiveDealsByCityV2(city);
	        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
			for (Deal deal: deals){
				dealsDtos.add(new DealDTO(deal));
			}
	        renderJSON(dealsDtos);
		}
		else{
			renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", Messages.get("city.notfound")));
		}
	}
	
	/**
	 * Retrieves all the active deals from all the active locations
	 * in the required city.
	 * Renders JSON 
	 * @param cityUrl
	 */
	@Deprecated
	public static void iPhoneList(String cityUrl) {
		City city = City.findByUrl(cityUrl);
		if (city != null){
			//odd workaround to maintain backwards compatibility with the city zones
			if (city.isRootCity()){
				city = city.mainZone;
				city.get();
			}
			Collection<Deal> deals = Deal.findActiveDealsByCity(city);
	        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
			for (Deal deal: deals){
				deal.fecthCity(); //retrieves city object to not to send just the city id
				dealsDtos.add(new DealDTO(deal));
			}
	        renderJSON(dealsDtos);
		}
		else{
			renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", Messages.get("city.notfound")));
		}
	}
	
	/*
	 * Refresh prices and availability from hotUsa hotels
	 * Used by cron GAE
	 */
	public static void refreshHotUsaPrices(){
		List<City> cities = City.findActiveCities();
		HotUsaApiHelper.getHotelPricesByCityList(cities);
	}
	
	public static void showMovePrices(){
		render();
	}
	
	public static void movePrices(){
		List<Deal> deals =  Deal.findDealsNotFromHotUsa();
		for (Deal deal : deals){
			movePriceByDeal(deal);
		}
		showMovePrices();
	}

	private static void movePriceByDeal(Deal deal) {
		if (deal.priceDay2 != null){
			deal.updated = Calendar.getInstance().getTime();
			deal.salePriceCents = deal.priceDay2;
			deal.netSalePriceCents = deal.netPriceDay2;
			deal.priceDay2 = null;
			deal.netPriceDay2 = null;
			deal.quantity = deal.quantityDay2;
			deal.quantityDay2 = 0;
		}
		else{
			deal.quantity = 0;
		}
		if (deal.priceDay3 != null){
			deal.priceDay2 = deal.priceDay3;
			deal.netPriceDay2 = deal.netPriceDay3;
			deal.netPriceDay3 = null;
			deal.priceDay3 = null;
			deal.quantityDay2 = deal.quantityDay3;
			deal.quantityDay3 = 0;
		}
		if (deal.priceDay4 != null){
			deal.priceDay3 = deal.priceDay4;
			deal.netPriceDay3 = deal.netPriceDay4;
			deal.netPriceDay4 = null;
			deal.priceDay4 = null;
			deal.quantityDay3 = deal.quantityDay4;
			deal.quantityDay4 = 0;
		}
		if (deal.priceDay5 != null){
			deal.priceDay4 = deal.priceDay5;
			deal.netPriceDay4 = deal.netPriceDay5;
			deal.netPriceDay5 = null;
			deal.priceDay5 = null;
			deal.quantityDay4 = deal.quantityDay5;
			deal.quantityDay5 = 0;
		}
		deal.update();
	}

}

