package services;

import helper.DateHelper;
import helper.GeoHelper;
import helper.UtilsHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import controllers.Application;

import notifiers.Mails;
import siena.Query;

import models.City;
import models.Deal;
import models.dto.CityRootDTO;
import models.dto.DealDTO;
import models.dto.DealDTOV3;
import models.dto.CityZoneDTO;

public final class DealsService {
	private static final Logger log = Logger.getLogger(Application.class.getName());

	private static final int MAXDEALS = 3;
	private static final int MAX_NIGHTS = 5;
	
	/**
	 * Version 2. Cities can have root cities and zones.
	 * @param city
	 * @param noLimits 
	 * @return all the active deals by city base on current time
	 */
	public static Collection<Deal> findActiveDealsByCityV2(City city, Boolean noLimits, Boolean hideAppOnly) {
		if (city != null){
			if (!city.isRootCityWithZones()){
				String root = city.root;
				city = City.findByUrl(root);
			}
			switch (DateHelper.getCurrentStateByCityHour(city.utcOffset)) {
				case (DateHelper.CITY_CLOSED):
					log.info("V2. We are closed");
					return new ArrayList<Deal>();
				case (DateHelper.CITY_OPEN_DAY):
					LinkedHashMap<City, List<Deal>> dealsMap = findAllActiveDealsByCityV2(city);
					log.info("V2. We are all opened");
					return dealsMapToListWithMax(dealsMap, noLimits, hideAppOnly);
				case (DateHelper.CITY_OPEN_NIGHT):
					LinkedHashMap<City, List<Deal>> dealsMapAll = findAllActiveDealsByCityV2(city);
					Integer hour = DateHelper.getCurrentHour(city.utcOffset);
					return findActiveDealsByNight(dealsMapAll, hour, noLimits, hideAppOnly);
				default:
					log.severe("This never should happen. What hour is it?");
					return new ArrayList<Deal>();
			 }
		}
		else{
			log.severe("##findActiveDealsByCityV2: Trying to find deals but city is null");
			return null;
		}
	}

	/**
	 * Version 1. Theres no zones, just cities
	 * @param city
	 * @return all the active deals by city base on current time
	 */
	@Deprecated
	public static List<Deal> findActiveDealsByCity(City city, Boolean noLimits, Boolean hideAppOnly){
	
		switch (DateHelper.getCurrentStateByCityHour(city.utcOffset)) {
			case (DateHelper.CITY_CLOSED):
				//If hour is between 6am and 12pm we return an empty list
				log.info("We are closed ");
				return new ArrayList<Deal>();
			case (DateHelper.CITY_OPEN_DAY):
				// between 12 and 23 all deals are open
				log.info("We are all opened ");
				return Deal.findAllActiveDealsByCity(city).fetch(findMaxDeals(noLimits));
			case (DateHelper.CITY_OPEN_NIGHT):
				//Is between 0am and 6am. Fetch all active deals and select the first MAXDEALS active
				Integer hour = DateHelper.getCurrentHour(city.utcOffset);
				List<Deal> activeDeals = Deal.findAllActiveDealsByCity(city).fetch();
				return selectMaxDealsByHour(activeDeals, hour, noLimits, hideAppOnly);
			default:
				log.severe("This never should happen. What hour is it?");
				return new ArrayList<Deal>();
		 }
	}	

	

	/**
	 * V2 returns all the deals from all the locations in a city
	 * @param city
	 * @return Returns all deals by city independently of the time
	 */
	public static LinkedHashMap<City, List<Deal>> findAllActiveDealsByCityV2(City city){
		LinkedHashMap<City, List<Deal>> dealsMap = new LinkedHashMap<City, List<Deal>>();
		List<City> cities = City.findActiveCitiesByRoot(city.url);
		for (City location: cities) {
			List<Deal> active = new ArrayList<Deal>();
			active.addAll(Deal.findActiveByCityOrderPositionPrice(location));
			active.addAll(Deal.findSecondCityActiveByCityOrderPositionPrice(location));
			dealsMap.put(location, active);
		}
		return dealsMap;
	}
	
		
	/**
	 * Updates the deal hotel price, quantity and breakfast for a given day based on the hotusa API 
	 * @param hotelCode
	 * @param quantity
	 * @param price
	 * @param breakfastIncluded
	 * @param lin
	 * @param day
	 * @param removeIfPriceRaised 
	 */
	public static void updateDealByCode(String hotelCode, int quantity, Float price, Boolean breakfastIncluded, String lin, int day, Boolean removeIfPriceRaised){
	    Deal deal = Deal.findByHotelCode(hotelCode);
	    //Some objects from datastore could come null so we check it
	    deal.isFake = deal.isFake != null ? deal.isFake : Boolean.FALSE;
	    deal.quantity = deal.quantity != null ? deal.quantity : 0;
	    deal.active = deal.active != null ? deal.active : Boolean.FALSE;
	    
	    // If isFake but active and dispo 0 we dont want to update dispo
	    if (!(deal.isFake && deal.quantity == 0 && deal.active)){
	    	deal.quantity = quantity;
	    }
	    
	  //If is active time and price is bigger than before we want to deactivate the deal
	    City city = City.findById(deal.city.id);
	    Boolean dealInUse = city != null 
	    	&& (DateHelper.isActiveTime(DateHelper.getCurrentHour(city.utcOffset)) || DateHelper.isPricingTime(DateHelper.getCurrentHour(city.utcOffset)) )
	    	&& deal.active && deal.salePriceCents != null && price != null;
	    
	    if (removeIfPriceRaised && dealInUse && day == 0 && deal.bestPrice != null && deal.bestPrice.compareTo(price.intValue()) < 0 ){
	    	deal.active = false;
	    	Mails.hotusaRisePrices(deal, price);
	    }
	    
	    Boolean updatePublicPrices = !dealInUse;
	    deal.updateHotusaPrice(price, breakfastIncluded, lin, day, updatePublicPrices);
	    
	    log.info("Updatind deal: " + deal.hotelName + " price: " + deal.salePriceCents + " quantity: " + quantity);
	    deal.updated = Calendar.getInstance().getTime();
	    
	    deal.update();
	}
	
	public static void updateBookingHotelCode(String hotelCode) {
		Deal deal = Deal.findByHotelCode(hotelCode);
		deal.bookingHotelCode = hotelCode;
		deal.update();
	}
	
	public static void updatePricesAllDaysByCode(String hotelCode, Float price) {
		Deal deal = Deal.findByHotelCode(hotelCode);
		Integer roundedPrice = UtilsHelper.roundPrice(price);
		if (deal.netPriceDay2 != null && price.compareTo(deal.netPriceDay2) > 0){
			deal.priceDay2= roundedPrice;
		}
		if (deal.netPriceDay3 != null && price.compareTo(deal.netPriceDay3) > 0){
			deal.priceDay3= roundedPrice;
		}
		if (deal.netPriceDay4 != null && price.compareTo(deal.netPriceDay4) > 0){
			deal.priceDay4= roundedPrice;
		}
		if (deal.netPriceDay5 != null && price.compareTo(deal.netPriceDay5) > 0){
			deal.priceDay5= roundedPrice;
		}
		deal.update();
	}
	
	/**
	 * Updates the deal hotel price for a given day based on the hotusa API 
	 * @param hotelCode
	 * @param price
	 * @param lin
	 * @param day
	 */
	//FIXME this is not the correct name for this method. it's used only when the deal is solded out, right?
	public static void updatePriceByCode(String hotelCode, Float price, String lin, int day){
	    Deal deal = Deal.findByHotelCode(hotelCode);
	    Boolean updatePublicPrices = Boolean.TRUE;
	    deal.updateHotusaPrice(price, null, lin, day, updatePublicPrices);
	    log.info("Updating price for: " + deal.hotelName + " day: " + day );
	    deal.updated = Calendar.getInstance().getTime();
	    deal.update();
	}
	
	
	/**
	 * Hotusa API method
	 * @param hotelCode code to find the hotel
	 * @param day last day the deal has received prices
	 * Deletes deal prices for the days after the given day and updates the deal in BD
	 * */
	public static void cleanNextDays(String hotelCode, int day) {
		Deal deal = Deal.findByHotelCode(hotelCode);		
		switch (day) {
			case 0:
				deal.priceDay2 = null;
				deal.priceDay3 = null;
				deal.priceDay4 = null;
				deal.priceDay5 = null;
				deal.netPriceDay2 = null;
				deal.netPriceDay3 = null;
				deal.netPriceDay4 = null;
				deal.netPriceDay5 = null;
				//deal.bookingLine = null;
				deal.bookingLine2 = null;
				deal.bookingLine3 = null;
				deal.bookingLine4 = null;
				deal.bookingLine5 = null;
			    break;
			case 1:
				deal.priceDay3 = null;
				deal.priceDay4 = null;
				deal.priceDay5 = null;
				deal.netPriceDay3 = null;
				deal.netPriceDay4 = null;
				deal.netPriceDay5 = null;
				//deal.bookingLine2 = null;
				deal.bookingLine3 = null;
				deal.bookingLine4 = null;
				deal.bookingLine5 = null;
			    break;
			case 2:
				deal.priceDay4 = null;
				deal.priceDay5 = null;
				deal.netPriceDay4 = null;
				deal.netPriceDay5 = null;
				//deal.bookingLine3 = null;
				deal.bookingLine4 = null;
				deal.bookingLine5 = null;
			    break;
			case 3:
				deal.priceDay5 = null;
				deal.netPriceDay5 = null;
				//deal.bookingLine4 = null;
				deal.bookingLine5 = null;
			    break;   

			default:
				break;
		}
	    deal.update();
	}
	
	/**
	 * Limits the deals number per city zone to the MAXDEALS
	 * @param dealsMap
	 * @return Returns all the active deals in all the zones of a city.
	 */
	private static List<Deal> dealsMapToListWithMax(LinkedHashMap<City, List<Deal>> dealsMap, Boolean noLimits, Boolean hideAppOnly) {
		List<Deal> deals = new ArrayList<Deal>();
		for(City city: dealsMap.keySet()){
			List<Deal> zoneDeals = selectMaxDeals(dealsMap.get(city), noLimits, hideAppOnly);
			deals.addAll(zoneDeals);
		}
		return deals;
	}
	
	/**
	 * Check each deal limit hour to show it as active or not.
	 * Also limits the deals number per city zone to the MAXDEALS
	 * @param dealsMap
	 * @param hour
	 * @return Returns all the active deals in all the zones of a city at a given hour.
	 */
	private static List<Deal> findActiveDealsByNight(LinkedHashMap<City, List<Deal>> dealsMap, Integer hour, Boolean noLimits,  Boolean hideAppOnly){
		List<Deal> activeDeals = new ArrayList<Deal>();
		log.info("Is between 0am and 6am. Hour:  " + hour);
		// iterate over zones to limit deals list to MAXDEALS
		for(City city: dealsMap.keySet()){
			List<Deal> zoneDeals = selectMaxDealsByHour(dealsMap.get(city), hour, noLimits, hideAppOnly);
			activeDeals.addAll(zoneDeals);
		}
		return activeDeals;
	}
	
	
	/**
	 * V3
	 * Check each deal limit hour to show it as active or not.
	 * Also limits the deals number per city zone to the MAXDEALS
	 * @param dealsMap
	 * @param hour
	 * @return Returns a DealsCityDTO with all the active deals in all the zones of a city at a given hour.
	 */
	private static List<CityZoneDTO> findCityZonesWithActiveDealsByNight(LinkedHashMap<City, List<Deal>> dealsMap, Integer hour, Boolean noLimits,  Boolean hideAppOnly){
		List<CityZoneDTO> cityZones = new ArrayList<CityZoneDTO>();
		log.info("Is between 0am and 6am. Hour:  " + hour);
		// iterate over zones to limit deals list to MAXDEALS
		for(City city: dealsMap.keySet()){
			List<Deal> zoneDeals = selectMaxDealsByHour(dealsMap.get(city), hour, noLimits, hideAppOnly);
			if (zoneDeals.size() > 0){
				List<DealDTOV3> dealsDtos = new ArrayList<DealDTOV3>();
				for (Deal deal: zoneDeals){
					dealsDtos.add(new DealDTOV3(deal));
				}
				CityZoneDTO dealsCity = new CityZoneDTO(city, dealsDtos);
				cityZones.add(dealsCity);
			}
			
		}
		return cityZones;
	}
	
	/**
	 * V3
	 * Limits the deals number per city zone to the MAXDEALS
	 * @param dealsMap
	 * @return Returns a DealsCityDTO with all the active deals in all the zones of a city.
	 */
	private static List<CityZoneDTO> findCityZonesWithActiveDealsV3(LinkedHashMap<City, List<Deal>> dealsMap, Boolean noLimits, Boolean hideAppOnly) {
		List<CityZoneDTO> cityZones = new ArrayList<CityZoneDTO>();
		for(City city: dealsMap.keySet()){
			List<Deal> zoneDeals = selectMaxDeals(dealsMap.get(city), noLimits, hideAppOnly);
			if (zoneDeals.size() > 0){
				List<DealDTOV3> dealsDtos = new ArrayList<DealDTOV3>();
				for (Deal deal: zoneDeals){
					dealsDtos.add(new DealDTOV3(deal));
				}
				CityZoneDTO dealsCity = new CityZoneDTO(city, dealsDtos);
				cityZones.add(dealsCity);
			}
		}
		return cityZones;
	}
	
	/**
	 * Limits the deals number per city zone to the MAXDEALS
	 * @param list
	 * @return
	 */
	private static List<Deal> selectMaxDeals(List<Deal> list, Boolean noLimits, Boolean hideAppOnly) {
		int maxDeals = findMaxDeals(noLimits);
		List<Deal> zoneDeals = new ArrayList<Deal>();
		for (Deal deal : list){
			if (hideAppOnly && deal.onlyApp){
				log.info( deal.hotelName + " is app only so cant be shown here.");
			}
			else{
				zoneDeals.add(deal);
			}
			if (zoneDeals.size() == maxDeals){
				break;
			}
		}
		return zoneDeals;
	}
	
	private static int findMaxDeals(Boolean noLimits){
		return noLimits? 50 : MAXDEALS;
	}
	
	/**
	 * Also limit the deals number per city zone to the MAXDEALS
	 * @param list
	 * @param hour
	 * @return
	 */
	private static List<Deal> selectMaxDealsByHour(List<Deal> list, Integer hour, Boolean noLimits, Boolean hideAppOnly) {
		int maxDeals = findMaxDeals(noLimits);
		List<Deal> zoneDeals = new ArrayList<Deal>();
		for (Deal deal : list){
			// after 24 we check the limitHour
			if (deal.limitHour != null && deal.limitHour > hour){
				if (hideAppOnly && deal.onlyApp){
					log.info( deal.hotelName + " is app only so cant be shown here.");
				}
				else{
					zoneDeals.add(deal);
				}
				if (zoneDeals.size() == maxDeals){
					break;
				}
			}
		}
		return zoneDeals;
	}
	
	
	@Deprecated
	public static Collection<DealDTO> findTonightDealsByCity(City city) {
		Boolean noLimits = Boolean.TRUE;
		Boolean hideAppOnly = Boolean.TRUE;
		Collection<Deal> deals = findActiveDealsByCityV2(city, noLimits, hideAppOnly);
		Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
		for (Deal deal: deals){
			dealsDtos.add(new DealDTO(deal, city.url));
		}
		return dealsDtos;		
	}
	
	/**
	 * Move prices to the next day
	 * Used at night by the cron to get next day prices
	 * @param deal
	 */
	public static void movePriceByDeal(Deal deal) {
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
	
	
	public static CityRootDTO findDealsByCityAndDateV3(City city, Date checkin, int nights) {
		if (checkin != null && !DateHelper.isTodayDate(checkin)){
			log.info("Is not a search for today, " + checkin);
			return null;
		}
		else {
			return findTonightDealsByCityV3(city, nights);
		}
	}
	
	public static CityRootDTO findTonightDealsByCityV3(City city, int nights) {
		if (nights > MAX_NIGHTS){
			log.info("We don´t have offers for so many nights: " + nights);
			return null;
		}
		else{
			Boolean noLimits = Boolean.FALSE;
			Boolean hideAppOnly = Boolean.TRUE;
			return findCityZonesDealsV3(city, noLimits, hideAppOnly);
		}
	}
	
	
	/**
	 * Version 3. Cities can have root cities and zones.
	 * @param city
	 * @param noLimits 
	 * @return all the active deals by city base on current time
	 */
	public static CityRootDTO findCityZonesDealsV3(City city, Boolean noLimits, Boolean hideAppOnly) {
		if (city != null){
			if (!city.isRootCityWithZones()){
				String root = city.root;
				city = City.findByUrl(root);
			}
			switch (DateHelper.getCurrentStateByCityHour(city.utcOffset)) {
				case (DateHelper.CITY_CLOSED):
					log.info("V3. We are closed");
					return null;
				case (DateHelper.CITY_OPEN_DAY):
					LinkedHashMap<City, List<Deal>> dealsMap = findAllActiveDealsByCityV2(city);
					log.info("V3. We are all opened");
					List<CityZoneDTO> zones = findCityZonesWithActiveDealsV3(dealsMap, noLimits, hideAppOnly);
					return new CityRootDTO(city, zones);
				case (DateHelper.CITY_OPEN_NIGHT):
					LinkedHashMap<City, List<Deal>> dealsMapAll = findAllActiveDealsByCityV2(city);
					Integer hour = DateHelper.getCurrentHour(city.utcOffset);
					log.info("V3. Is between 0am and 6am. Hour:  " + hour);
					List<CityZoneDTO> zonesNight = findCityZonesWithActiveDealsByNight(dealsMapAll, hour, noLimits, hideAppOnly);
					return new CityRootDTO(city, zonesNight);
				default:
					log.severe("This never should happen. What hour is it?");
					return null;
			 }
		}
		else{
			log.severe("##V3.findCityZonesDeals: Trying to find deals but city is null");
			return null;
		}
	}
	
	

}
