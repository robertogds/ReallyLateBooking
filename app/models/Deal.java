package models;

import helper.DateHelper;
import helper.ImageHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import notifiers.Mails;

import org.apache.commons.lang.StringUtils;

import controllers.CRUD.Hidden;

import play.Logger;
import play.data.validation.Email;
import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.Required;
import play.libs.Mail;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.Table;

/**
 * @author pablopr
 *
 */
@Table("deals")
public class Deal extends Model {
	public static final int MAXDEALS = 3;
	
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	@Required
	public String hotelName;	
	public String hotelCode;
	public String trivagoCode;
	public Boolean isHotUsa;
	public Boolean isFake;
	public Boolean active;
	@Required
	@Index("city_index")
    public City city;
	@Required
	@Index("owner_index")
    public User owner;
	@Index("company_index")
	public Company company;
	@Email
	public String contactEmail;
	public double discount;
	public Integer bestPrice;
	@Required
	public Integer salePriceCents;
	@Required
	@Min(0)
	public Integer priceCents;
	@Min(0)
	public Integer priceDay2;
	@Min(0)
	public Integer priceDay3;
	@Min(0)
	public Integer priceDay4;
	@Min(0)
	public Integer priceDay5;
	@Required
	@Min(0)
	public Integer quantity;
	@Min(0)
	public Integer quantityDay2;
	@Min(0)
	public Integer quantityDay3;
	@Min(0)
	public Integer quantityDay4;
	@Min(0)
	public Integer quantityDay5;
	public Integer position;
	@Min(0)
	@Max(23)
	public Integer limitHour;
	public Boolean breakfastIncluded;
	public String roomType;
	public String roomTypeText;
	public Integer hotelCategory;
	public String address;
	public String latitude;
	public String longitude;
	@DateTime
	public Date updated;
	
	public boolean customDetailText;
	@MaxSize(10000)
	public String detailText;
	@MaxSize(10000)
	public String hotelText;
	@MaxSize(10000)
	public String roomText;
	@MaxSize(10000)
	public String foodDrinkText;
	@MaxSize(10000)
	public String aroundText;
	@MaxSize(10000)
	public String detailTextEN;
	@MaxSize(10000)
	public String hotelTextEN;
	@MaxSize(10000)
	public String roomTextEN;
	@MaxSize(10000)
	public String foodDrinkTextEN;
	@MaxSize(10000)
	public String aroundTextEN;
    @MaxSize(10000)
	public String detailTextFR;
	@MaxSize(10000)
	public String hotelTextFR;
	@MaxSize(10000)
	public String roomTextFR;
	@MaxSize(10000)
	public String foodDrinkTextFR;
	@MaxSize(10000)
	public String aroundTextFR;

	@Required
    public String mainImageBig;
	@Required
    public String mainImageSmall;
	public String listImage;
	public String image1;
	public String image2;
	public String image3;
	public String image4;
	public String image5;
	public String image6;
	public String image7;
	public String image8;
	public String image9;
	public String image10;
	public String bookingLine;
	public String bookingLine2;
	public String bookingLine3;
	public String bookingLine4;
	public String bookingLine5;
	
	public Deal(String hotelName, City city) {
		this.hotelName = hotelName;
		this.city = city;
	}
	
	public Deal(String hotelName, City city, Boolean active) {
		this.hotelName = hotelName;
		this.city = city;
		this.active = active;
	}
	
	public Deal(Long id) {
		this.id = id;
	}

	public Deal() {
		// TODO Auto-generated constructor stub
	}

	public static List<Deal> findDealsByOwner(User owner){
		return all().filter("owner", owner).order("position").fetch();
	}
	

	public static List<Deal> findDealsFromHotUsa(){
		return all().filter("isHotUsa", Boolean.TRUE).fetch();
	}
	
	public static List<Deal> findDealsFromHotUsaByCity(City city){
		return all().filter("isHotUsa", Boolean.TRUE).filter("city", city).fetch();
	}
	
	public static List<Deal> findByCity(City city) {
        return all().filter("city", city).order("-updated").order("position").fetch();
    }
	
	public static List<Deal> findActiveByCityOrderDiscountAndPrice(City city) {
        return all().filter("city", city).filter("active", Boolean.TRUE).order("-salePriceCents").order("-discount").fetch();
    }
	
	public static List<Deal> findAllActiveDealsByCityId(Long cityId){
		City city = City.findById(cityId);
		return findAllActiveDealsByCity(city).fetch();
	}
	
	public static Deal findById(Long id) {
		return all().filter("id", id).get();
	}
	
	public static Deal findByHotelCode(String hotelCode) {
		return all().filter("hotelCode", hotelCode).get();
	}
	
	public static Query<Deal> all() {
    	return Deal.all(Deal.class);
    }
	
	public String toString() {
		return hotelName;
	}
	
	/**
	 * Version 2. Cities can have root cities and zones.
	 * @param city
	 * @return all the active deals by city base on current time
	 */
	public static Collection<Deal> findActiveDealsByCityV2(City city) {
		if (!city.isRootCity()){
			String root = city.root;
			city = City.findByUrl(root);
			Logger.info("City is not root, we search the root: %s and found $s",root, city.name);
		}
		switch (DateHelper.getCurrentStateByCityHour(city.utcOffset)) {
			case (DateHelper.CITY_CLOSED):
				Logger.info("V2. We are closed");
				return new ArrayList<Deal>();
			case (DateHelper.CITY_OPEN_DAY):
				HashMap<City, List<Deal>> dealsMap = findAllActiveDealsByCityV2(city);
				Logger.info("V2. We are all opened");
				return dealsMapToListWithMax(dealsMap);
			case (DateHelper.CITY_OPEN_NIGHT):
				HashMap<City, List<Deal>> dealsMapAll = findAllActiveDealsByCityV2(city);
				Integer hour = DateHelper.getCurrentHour(city.utcOffset);
				Logger.debug("V2. Is between 0am and 6am. Hour:  " + hour);
				return findActiveDealsByNight(dealsMapAll, hour);
			default:
				Logger.error("This never should happen. What hour is it?");
				return new ArrayList<Deal>();
		 }
	}

	/**
	 * Version 1. Theres no zones, just cities
	 * @param city
	 * @return all the active deals by city base on current time
	 */
	@Deprecated
	public static List<Deal> findActiveDealsByCity(City city){
	
		switch (DateHelper.getCurrentStateByCityHour(city.utcOffset)) {
			case (DateHelper.CITY_CLOSED):
				//If hour is between 6am and 12pm we return an empty list
				Logger.info("We are closed ");
				return new ArrayList<Deal>();
			case (DateHelper.CITY_OPEN_DAY):
				// between 12 and 23 all deals are open
				Logger.info("We are all opened ");
				return findAllActiveDealsByCity(city).fetch(MAXDEALS);
			case (DateHelper.CITY_OPEN_NIGHT):
				//Is between 0am and 6am. Fetch all active deals and select the first MAXDEALS active
				Integer hour = DateHelper.getCurrentHour(city.utcOffset);
				Logger.debug("Is between 0am and 6am. Hour:  " + hour);
				List<Deal> activeDeals = findAllActiveDealsByCity(city).fetch();
				return selectMaxDealsByHour(activeDeals, hour);
			default:
				Logger.error("This never should happen. What hour is it?");
				return new ArrayList<Deal>();
		 }
	}	

	/**
	 * @param city
	 * @return Returns Query<Deal> with all deals by city independently of the time
	 */
	@Deprecated
	public static Query<Deal> findAllActiveDealsByCity(City city){
		return all().filter("city", city).filter("active", Boolean.TRUE)
			.order("position").order("-priceCents");
	}
	

	/**
	 * V2 returns all the deals from all the locations in a city
	 * @param city
	 * @return Returns all deals by city independently of the time
	 */
	public static HashMap<City, List<Deal>> findAllActiveDealsByCityV2(City city){
		HashMap<City, List<Deal>> dealsMap = new HashMap<City, List<Deal>>();
		List<City> cities = City.findActiveCitiesByRoot(city.url);
		for (City location: cities) {
			List<Deal> active = new ArrayList<Deal>();
			active.addAll(all().filter("city", location).filter("active", Boolean.TRUE).order("position").order("-priceCents").fetch());
			dealsMap.put(location, active);
		}
		return dealsMap;
	}
	
	public void fecthCity(){
		if (this.city != null){
			this.city.get();
		}	
	}
		
	/**
	 * Updates the deal hotel price, quantity and breakfast for a given day based on the hotusa API 
	 * @param hotelCode
	 * @param quantity
	 * @param price
	 * @param breakfastIncluded
	 * @param lin
	 * @param day
	 */
	public static void updateDealByCode(String hotelCode, int quantity, Integer price, Boolean breakfastIncluded, String lin, int day){
	    Deal deal = Deal.findByHotelCode(hotelCode);
	    //Some objects from datastore could come null so we check it
	    deal.isFake = deal.isFake != null ? deal.isFake : Boolean.FALSE;
	    deal.quantity = deal.quantity != null ? deal.quantity : 0;
	    deal.active = deal.active != null ? deal.active : Boolean.FALSE;
	    
	    // If isFake but active and dispo 0 we dont want to update dispo
	    if (!(deal.isFake && deal.quantity == 0 && deal.active)){
	    	deal.quantity = quantity;
	    }
	    
	  //If is active time and price is bigger than before we want to desactivate the deal
	    City city = City.findById(deal.city.id);
	    if (city != null && DateHelper.isActiveTime(DateHelper.getCurrentHour(city.utcOffset))
	    		&& deal.active && deal.salePriceCents != null 
	    		&& price != null && deal.salePriceCents < price){
	    	deal.active = false;
	    	Mails.hotusaRisePrices(deal);
	    }
	    
	    switch (day) {
			case 0:
				 //If isFake we dont want to change the price automatically by the cron task
			    if (price != null && price > 0 && !deal.isFake){
			    	deal.salePriceCents = price;
			    }
			    //Set breakfast included
			    deal.breakfastIncluded = breakfastIncluded;
			    deal.bookingLine = lin;
			    break;
			case 1:
				deal.priceDay2 = price;
			    deal.bookingLine2 = lin;
			    break;
			case 2:
				deal.priceDay3 = price;
			    deal.bookingLine3 = lin;
			    break;
			case 3:
				deal.priceDay4 = price;
			    deal.bookingLine4 = lin;
			    break;
			case 4:
				deal.priceDay5 = price;
			    deal.bookingLine5 = lin;
			    break;    
			default:
				break;
		}
	    Logger.debug("Updatind deal: " + deal.hotelName + " price: " + deal.salePriceCents + " quantity: " + quantity);
	    deal.updated = Calendar.getInstance().getTime();
	    
	    deal.update();
	}
	
	/**
	 * Updates the deal hotel price for a given day based on the hotusa API 
	 * @param hotelCode
	 * @param price
	 * @param lin
	 * @param day
	 */
	public static void updatePriceByCode(String hotelCode, Integer price, String lin, int day){
	    Deal deal = Deal.findByHotelCode(hotelCode);	    
	    switch (day) {
			case 0:
				 //If isFake we dont want to change the price automatically by the cron task
			    if (price != null && price > 0 && !deal.isFake){
			    	deal.salePriceCents = price;
			    }
			    //Set breakfast included
			    deal.bookingLine = lin;
			    break;
			case 1:
				deal.priceDay2 = price;
			    deal.bookingLine2 = lin;
			    break;
			case 2:
				deal.priceDay3 = price;
			    deal.bookingLine3 = lin;
			    break;
			case 3:
				deal.priceDay4 = price;
			    deal.bookingLine4 = lin;
			    break;
			case 4:
				deal.priceDay5 = price;
			    deal.bookingLine5 = lin;
			    break;    
			default:
				break;
		}
	    Logger.debug("Updating price for: " + deal.hotelName + " day: " + day );
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
				deal.bookingLine = null;
				deal.bookingLine2 = null;
				deal.bookingLine3 = null;
				deal.bookingLine4 = null;
				deal.bookingLine5 = null;
			    break;
			case 1:
				deal.priceDay3 = null;
				deal.priceDay4 = null;
				deal.priceDay5 = null;
				deal.bookingLine2 = null;
				deal.bookingLine3 = null;
				deal.bookingLine4 = null;
				deal.bookingLine5 = null;
			    break;
			case 2:
				deal.priceDay4 = null;
				deal.priceDay5 = null;
				deal.bookingLine3 = null;
				deal.bookingLine4 = null;
				deal.bookingLine5 = null;
			    break;
			case 3:
				deal.priceDay5 = null;
				deal.bookingLine4 = null;
				deal.bookingLine5 = null;
			    break;   
			case 4:
				deal.bookingLine5 = null;
			    break;  
			default:
				break;
		}
	    deal.update();
	}
	
	/*
	 * Count methods needed for the control panel.
	 */
	public static int countHotelsByCity(City city, Date start, Date end) {
		int hotels = Deal.all().filter("city", city).filter("active", Boolean.TRUE).count();
		return hotels;
	}

	public static int countActiveDirectHotelsByCity(City city, Date start, Date end) {
		int hotels = Deal.all().filter("city", city).filter("active", Boolean.TRUE).filter("isHotUsa", Boolean.FALSE).count();
		return hotels;
	}

	public static double findMaxDiscountByCity(City city, Date start, Date end) {
		Deal deal = Deal.all().filter("city", city).filter("active", Boolean.TRUE).order("-discount").get();
		return deal != null ? deal.discount : 0;
	}

	public static double findMinDiscountByCity(City city, Date start, Date end) {
		Deal deal = Deal.all().filter("city", city).filter("active", Boolean.TRUE).order("discount").get();
		return deal != null ? deal.discount : 0;
	}
	
	
	public Boolean dealIsPublished(){
		City city = City.findByUrl(this.city.root);
		Collection<Deal> deals = findActiveDealsByCityV2(city);
		for (Deal deal: deals){
			if (deal.id.equals(this.id)) return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	
	/**
	 * Limits the deals number per city zone to the MAXDEALS
	 * @param dealsMap
	 * @return Returns all the active deals in all the zones of a city.
	 */
	private static List<Deal> dealsMapToListWithMax(HashMap<City, List<Deal>> dealsMap) {
		List<Deal> deals = new ArrayList<Deal>();
		for(City city: dealsMap.keySet()){
			List<Deal> zoneDeals = selectMaxDeals(dealsMap.get(city));
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
	private static List<Deal> findActiveDealsByNight(HashMap<City, List<Deal>> dealsMap, Integer hour){
		List<Deal> activeDeals = new ArrayList<Deal>();
		Logger.debug("Is between 0am and 6am. Hour:  " + hour);
		// iterate over zones to limit deals list to MAXDEALS
		for(City city: dealsMap.keySet()){
			List<Deal> zoneDeals = selectMaxDealsByHour(dealsMap.get(city), hour);
			activeDeals.addAll(zoneDeals);
		}
		return activeDeals;
	}
	
	/**
	 * Limits the deals number per city zone to the MAXDEALS
	 * @param list
	 * @return
	 */
	private static List<Deal> selectMaxDeals(List<Deal> list) {
		List<Deal> zoneDeals = new ArrayList<Deal>();
		for (Deal deal : list){
			zoneDeals.add(deal);
			if (zoneDeals.size() == MAXDEALS){
				break;
			}
		}
		return zoneDeals;
	}
	
	/**
	 * Also limit the deals number per city zone to the MAXDEALS
	 * @param list
	 * @param hour
	 * @return
	 */
	private static List<Deal> selectMaxDealsByHour(List<Deal> list, Integer hour) {
		List<Deal> zoneDeals = new ArrayList<Deal>();
		for (Deal deal : list){
			// after 24 we check the limitHour
			if (deal.limitHour != null && deal.limitHour > hour){
				zoneDeals.add(deal);
				if (zoneDeals.size() == MAXDEALS){
					break;
				}
			}
		}
		return zoneDeals;
	}

	@Override
	public boolean equals(Object deal) {
		return true;
	}

	public void calculateDiscount() {
		if (this.bestPrice != null && this.bestPrice > 0){
	    	int dif = (this.bestPrice - this.salePriceCents) * 100;
	    	this.discount = dif != 0 ? dif / this.bestPrice : 0;
	    }
	    else{
	    	this.discount = 0;
	    }
	}

}


