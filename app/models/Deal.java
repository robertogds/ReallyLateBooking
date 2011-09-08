package models;

import helper.DateHelper;
import helper.ImageHelper;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
	public boolean active;
	@Required
	@Index("city_index")
    public City city;
	@Required
	@Index("owner_index")
    public User owner;
	@Email
	public String contactEmail;
	@Required
	public Float salePriceCents;
	@Required
	@Min(0)
	public Integer priceCents;
	@Min(0)
	public Integer priceDay2;
	@Min(0)
	public Integer priceDay3;
	@Min(0)
	public Integer priceDay4;
	@Required
	@Min(0)
	public Integer quantity;
	public Integer position;
	@Min(0)
	@Max(23)
	public Integer limitHour;
	public Boolean breakfastIncluded;
	public String roomType;
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
	
	public static List<Deal> findActiveDealsByCity(City city){
		Integer hour = DateHelper.getCurrentHour();
		Logger.info("Hour time right now is: " + hour);
		//If hour is between 6am and 12pm we return an empty list
		
		if (hour >= 6 && hour < 12){
			Logger.info("We are closed ");
			return new ArrayList<Deal>();
		}
		// between 12 and 23 all deals are open
		else if (hour >= 12 && hour < 24){
			Logger.info("We are all opened ");
			return all().filter("city", city).filter("active", Boolean.TRUE).order("position").order("-priceCents").fetch(MAXDEALS);
		}
		
		List<Deal> activeDeals = all().filter("city", city).filter("active", Boolean.TRUE).order("position").order("-priceCents").fetch();
		return findActiveDealsByNight(activeDeals, hour);
	}
	
	private static List<Deal> findActiveDealsByNight(List<Deal> deals, Integer hour){
		
		List<Deal> active = new ArrayList<Deal>();
		Logger.info("Is between 0am and 6am. Hour:  " + hour);
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
	
	public static List<Deal> findByCity(City city) {
        return all().filter("city", city).order("position").order("-priceCents").fetch();
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
	
	public static void updateDealByCode(String hotelCode, int quantity, Float price, String lin){
	    Deal deal = Deal.findByHotelCode(hotelCode);
	    // Edit
	    deal.quantity = quantity;
	    if (price != null && price > 0){
	    	deal.salePriceCents = price;
	    }
	    deal.updated = Calendar.getInstance().getTime();
	    deal.bookingLine = lin;
	    deal.update();
	}
}


