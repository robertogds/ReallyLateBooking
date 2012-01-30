package models;

import helper.DateHelper;
import helper.ImageHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import controllers.CRUD.Hidden;

import play.Logger;
import play.data.validation.Email;
import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.Required;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.Table;

@Table("deals")
public class Deal extends Model {
	public static final int MAXDEALS = 3;
	
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	@Required
	public String hotelName;	
	public String hotelCode;
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

	public static List<Deal> findActiveDealsByOwner(User owner){
		return all().filter("owner", owner).order("position").fetch();
	}
	

	public static List<Deal> findDealsFromHotUsa(){
		return all().filter("isHotUsa", Boolean.TRUE).fetch();
	}
	
	public static List<Deal> findDealsFromHotUsaByCity(City city){
		return all().filter("isHotUsa", Boolean.TRUE).filter("city", city).fetch();
	}
	
	public static Collection<Deal> findActiveDealsByCityV2(City city) {
		Integer hour = DateHelper.getCurrentHour();
		Logger.debug("Hour time right now is: " + hour);
		//If hour is between 6am and 12pm we return an empty list
		if (hour >= 6 && hour < 12){
			Logger.info("We are closed ");
			return new ArrayList<Deal>();
		}
		// between 12 and 23 all deals are open
		else{
			List<Deal> activeDeals = findAllActiveDealsByCityV2(city, MAXDEALS);
			if (hour >= 12 && hour < 24){
				Logger.info("We are all opened");
				return activeDeals;
			}
			return findActiveDealsByNight(activeDeals, hour);
		}
	}
	
	public static List<Deal> findActiveDealsByCity(City city){
		Integer hour = DateHelper.getCurrentHour();
		Logger.debug("Hour time right now is: " + hour);
		//If hour is between 6am and 12pm we return an empty list
		
		if (hour >= 6 && hour < 12){
			Logger.info("We are closed ");
			return new ArrayList<Deal>();
		}
		// between 12 and 23 all deals are open
		else if (hour >= 12 && hour < 24){
			Logger.info("We are all opened ");
			return findAllActiveDealsByCity(city, MAXDEALS);
		}
		
		List<Deal> activeDeals = all().filter("city", city).filter("active", Boolean.TRUE).order("position").order("-priceCents").fetch();
		return findActiveDealsByNight(activeDeals, hour);
	}
	
	private static List<Deal> findActiveDealsByNight(List<Deal> deals, Integer hour){
		
		List<Deal> active = new ArrayList<Deal>();
		Logger.debug("Is between 0am and 6am. Hour:  " + hour);
		// after 24 we check the limitHour
		for (Deal deal : deals){
			if (deal.limitHour != null && deal.limitHour > hour){
				active.add(deal);
				if (active.size() == MAXDEALS){
					return active;
				}
			}
		}
		return active;
	}
	
	/*
	 * Returns all deals by city independently of the time
	 * */
	public static List<Deal> findAllActiveDealsByCity(City city, Integer maxDeals){
		return all().filter("city", city).filter("active", Boolean.TRUE).order("position").order("-priceCents").fetch(maxDeals);
	}
	/*
	 * Returns all deals by city independently of the time
	 * V2 returns all the deals from all the locations in a city
	 * */
	public static List<Deal> findAllActiveDealsByCityV2(City city, Integer maxDeals){
		List<Deal> active = new ArrayList<Deal>();
		List<City> cities = City.findActiveCitiesByRoot(city.url);
		for (City location: cities) {
			active.addAll(all().filter("city", location).filter("active", Boolean.TRUE).order("position").order("-priceCents").fetch(maxDeals));
		}
		return active;
	}
	
	public static List<Deal> findByCity(City city) {
        return all().filter("city", city).order("-updated").order("position").fetch();
    }
	
	public static List<Deal> findAllActiveDealsByCityId(Long cityId){
		City city = City.findById(cityId);
		return findAllActiveDealsByCity(city,10);
	}
	
	
	public static Query<Deal> all() {
    	return Deal.all(Deal.class);
    }
	
	public String toString() {
		return hotelName;
	}
	
	public void fecthCity(){
		if (this.city != null){
			this.city = City.findById(this.city.id);
		}	
	}
		
	public void prepareImages(){
		ImageHelper.prepareImagesRoutes(this);
	}
	

	public static Deal findById(Long id) {
		return all().filter("id", id).get();
	}
	
	public static Deal findByHotelCode(String hotelCode) {
		return all().filter("hotelCode", hotelCode).get();
	}
	
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
	    Logger.debug("Updatind price for: " + deal.hotelName + " day: " + day );
	    deal.updated = Calendar.getInstance().getTime();
	    deal.update();
	}

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

	public void textToHtml() {
		this.detailText = parseToHtml(this.detailText);
		this.aroundText = parseToHtml(this.aroundText);
		this.foodDrinkText = parseToHtml(this.foodDrinkText);
		this.roomText = parseToHtml(this.roomText);
		this.hotelText = parseToHtml(this.hotelText);
	}
	
	private static String parseToHtml(String text){
		text = removeFirstBr(text, "*");
		text = removeFirstBr(text, "-");
		String html =  StringUtils.replace(text, "*", "<br>");
		html =  StringUtils.replace(html, "-", "<br>");
		return html;
	}
	
	private static String removeFirstBr(String text, String remove){
		if (StringUtils.startsWith(text, remove)){
			text = StringUtils.removeStart(text, remove);
		}
		return text;
	}
}


