package models;

import helper.DateHelper;
import helper.ImageHelper;
import helper.UtilsHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import models.dto.DealDTO;
import notifiers.Mails;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.search.ExpressionParser.num_return;

import controllers.CRUD.Hidden;

import play.Logger;
import play.Play;
import play.data.validation.Email;
import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.Required;
import play.libs.Mail;
import play.mvc.Scope.Flash;
import services.DealsService;
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
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	public String hotelName;	
	public String hotelCode;
	public String hotelCode2;
	public String trivagoCode;
	public Boolean isHotUsa;
	public Boolean isFake;
	public Boolean active;
	public boolean onlyApp;
	@Index("city_index")
    public City city;
	@Index("second_city_index")
    public City secondCity;
	@Required
	@Index("owner_index")
    public User owner;
	@Index("company_index")
	public Company company;
	public String contactEmail;
	public double discount;
	public Integer bestPrice;
	public Integer salePriceCents;
	public Float netSalePriceCents;
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
	@Min(0)
	public Float netPriceDay2;
	@Min(0)
	public Float netPriceDay3;
	@Min(0)
	public Float netPriceDay4;
	@Min(0)
	public Float netPriceDay5;
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

    public String mainImageBig;
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
	public String bookingHotelCode;
	public String bookingLine;
	public String bookingLine2;
	public String bookingLine3;
	public String bookingLine4;
	public String bookingLine5;
	public boolean autoImageUrl;
	public int points;
	public String uuid;
	
	
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
	}

	public static List<Deal> findDealsByOwner(User owner){
		return all().filter("owner", owner).order("position").fetch();
	}
	
	public static List<Deal> findDealsNotFromHotUsa(){
		return all().filter("isHotUsa", Boolean.FALSE).fetch();
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
	
	public static Collection<Deal> findByCityInGetARoom(City city) {
		Collection<Deal> deals = all().filter("city", city).fetch();
		Collection<Deal> dealsInGetARoom = new ArrayList<Deal>();
		for (Deal deal : deals) {
			if (StringUtils.isNotBlank(deal.uuid)) dealsInGetARoom.add(deal);
		}
		return dealsInGetARoom;
	}
	
	public static List<Deal> findActiveByCityOrderDiscountAndPrice(City city) {
        return all().filter("city", city).filter("active", Boolean.TRUE).order("-salePriceCents").order("-discount").fetch();
    }
	
	public static List<Deal> findActiveByCityOrderPositionPrice(City city) {
        return all().filter("city", city).filter("active", Boolean.TRUE).order("position").order("-priceCents").fetch();
    }
	
	public static List<Deal> findSecondCityActiveByCityOrderPositionPrice(City city) {
		return all().filter("secondCity", city).filter("active", Boolean.TRUE).order("position").order("-priceCents").fetch();
	}
	
	public static List<Deal> findLastCreatedDeals() {
		return Deal.all().order("-updated").fetch(10);	
	}
	
	public static List<Deal> findAllActiveDealsByCityId(Long cityId){
		City city = City.findById(cityId);
		return findAllActiveDealsByCity(city).fetch();
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
	
	public static Deal findById(Long id) {
		return all().filter("id", id).get();
	}
	
	public static Deal findByHotelCode(String hotelCode) {
		Deal deal = all().filter("hotelCode", hotelCode).get();
		if (deal == null){
			deal = all().filter("hotelCode2", hotelCode).get();
		}
		return deal;
	}
	
	public static Query<Deal> all() {
    	return Deal.all(Deal.class);
    }
	
	/*
	 * Count methods needed for the control panel.
	 */
	public static int countActiveHotelsByCity(City city) {
		int hotels = Deal.all().filter("city", city).filter("active", Boolean.TRUE).count();
		return hotels;
	}

	public static int countActiveDirectHotelsByCity(City city) {
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
	
	public String toString() {
		return hotelName;
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

	public void calculateNetPrices() {
		this.company = Company.findById(this.company.id);
		if (this.company.fee != null){
	    	this.netSalePriceCents = calculateNetPriceByFee(this.salePriceCents);
	    	this.netPriceDay2 = calculateNetPriceByFee(this.priceDay2);
	    	this.netPriceDay3 = calculateNetPriceByFee(this.priceDay3);
	    	this.netPriceDay4 = calculateNetPriceByFee(this.priceDay4);
	    	this.netPriceDay5 = calculateNetPriceByFee(this.priceDay5);
	    }
	    else{
	    	Logger.error("Owner updated deal but company has not the fee: %s ", this.hotelName);
	    }
	}
	
	private Float calculateNetPriceByFee(Integer price){
		if (price != null){
			return new Float(price - (price * this.company.fee / 100.0));
		}
		return null;
	}

	public void addImage(String image, int width, int height) {
		Logger.debug("deal.adImage: %s, %s, %s", image, width, height);
		if (width == 832 && height == 300){
			this.listImage = image;
		}
		else if (width == 320 && height == 120){
			this.mainImageBig = image;
		}
		else if (width == 140 && height == 110){
			this.mainImageSmall = image;
		}
		else if (width == 320 && height == 330){
			this.image10 = image;
		}
		else if (width == 320 && height == 400){
			if (StringUtils.isBlank(this.image1)){
				this.image1 = image;
			}
			else if (StringUtils.isBlank(this.image2)){
				this.image2 = image;
			}
			else if (StringUtils.isBlank(this.image3)){
				this.image3 = image;
			}
			else if (StringUtils.isBlank(this.image4)){
				this.image4 = image;
			}
			else if (StringUtils.isBlank(this.image5)){
				this.image5 = image;
			}
			else if (StringUtils.isBlank(this.image6)){
				this.image6 = image;
			}
			else if (StringUtils.isBlank(this.image7)){
				this.image7 = image;
			}
			else if (StringUtils.isBlank(this.image8)){
				this.image8 = image;
			}
			else if (StringUtils.isBlank(this.image9)){
				this.image9 = image;
			}
		}
	}
	
	public void fecthCity(){
		if (this.city != null){
			this.city.get();
		}	
	}
	
	public void updateHotusaPrice(Float price, Boolean breakfastIncluded, String lin, int day, Boolean updatePublicPrices){
		Integer roundPrice = 0;
		if (price != null){
			Float fee = price * new Float(0.1) ;
			roundPrice = UtilsHelper.roundPrice(price + fee);
		}
		switch (day) {
		case 0:
			 //If isFake we don't want to change the price automatically by the cron task
		    if (price != null && price > 0 && !this.isFake){
		    	this.salePriceCents = updatePublicPrices ? roundPrice : this.salePriceCents;
		    	this.netSalePriceCents = price;
		    }
		    //Set breakfast included
		    if (breakfastIncluded != null){
		    	this.breakfastIncluded = breakfastIncluded;
		    }
		    this.bookingLine = lin;
		    break;
		case 1:
			this.priceDay2 = updatePublicPrices ? roundPrice : this.priceDay2;
			this.netPriceDay2 = price;
			this.quantityDay2 = 1;
		    this.bookingLine2 = lin;
		    break;
		case 2:
			this.priceDay3 = updatePublicPrices ? roundPrice : this.priceDay3;
			this.netPriceDay3 = price;
			this.quantityDay3 = 1;
		    this.bookingLine3 = lin;
		    break;
		case 3:
			this.priceDay4 = updatePublicPrices ? roundPrice : this.priceDay4;
			this.netPriceDay4 = price;
			this.quantityDay4 = 1;
		    this.bookingLine4 = lin;
		    break;
		case 4:
			this.priceDay5 = updatePublicPrices ? roundPrice : this.priceDay5;
			this.netPriceDay5 = price;
			this.quantityDay5 = 1;
		    this.bookingLine5 = lin;
		    break;    
		default:
			break;
		}
	}
	
	public Boolean dealIsPublished(){
		City city = City.findByUrl(this.city.root);
		Boolean noLimits = Boolean.FALSE;
		Boolean onlyApp = Boolean.FALSE;
		Collection<Deal> deals = DealsService.findActiveDealsByCityV2(city, noLimits, onlyApp);
		for (Deal deal: deals){
			if (deal.id.equals(this.id)) return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public static void updateDealRooms(Long dealId, Integer rooms, int nights){
		Logger.debug("Deal id: %s ## Rooms: %s ## Nights: %s", dealId, rooms, nights);
		Deal deal = Deal.findById(dealId);
		deal.quantity = deal.quantity - rooms;
		if (deal.quantity == 0){
			deal.active = Boolean.FALSE;
		}
		if (nights > 1){
			deal.quantityDay2 = deal.quantityDay2 == null ? deal.quantity :deal.quantityDay2 - rooms;
			if (nights > 2){
				deal.quantityDay3 =  deal.quantityDay3 == null ? deal.quantity : deal.quantityDay3 - rooms;
				if (nights > 3){
					deal.quantityDay4 =  deal.quantityDay4 == null ? deal.quantity : deal.quantityDay4 - rooms;
					if (nights > 4){
						deal.quantityDay5 =  deal.quantityDay5 == null ? deal.quantity : deal.quantityDay5 - rooms;
					}
				}
			}
		}
		deal.update();
	}

	public static List<Deal> findByHotelName(String hotelName, Long cityId,
			Long companyId) {
		Logger.debug("findByHotelName: %s ", hotelName);
		Query<Deal> query = Deal.all().filter("hotelName", hotelName);
		if (cityId != null){
			Logger.debug("findByHotelName: city %s ", cityId);
			City city = new City(cityId);
			query = query.filter("city", city);
		}
		if (companyId != null){
			Logger.debug("findByHotelName: comnpany %s", companyId);
			Company company = new Company(companyId);
			query = query.filter("company", company);
		}
		return query.order("hotelName").fetch();
	}

	public static List<Deal> findByCompanyOrderByName(Company company) {
		return Deal.all().filter("company", company).order("hotelName").fetch();
	}

	public static List<Deal> findByCityOrderByName(City city) {
		return Deal.all().filter("city", city).order("hotelName").fetch();
	}
	
	public void updateFromDeal(Deal deal){
		this.hotelName = deal.hotelName;
		this.hotelCode = deal.hotelCode;
		this.hotelCode2 = deal.hotelCode2;
		this.trivagoCode = deal.trivagoCode;
		this.isHotUsa = deal.isHotUsa != null ? deal.isHotUsa : Boolean.FALSE;
		this.onlyApp = deal.onlyApp;
		Logger.debug("Deal is active %s", deal.active);
		this.isFake = deal.isFake != null ? deal.isFake : Boolean.FALSE;
		this.active = deal.active != null ? deal.active : Boolean.FALSE;
		this.breakfastIncluded = deal.breakfastIncluded != null ? deal.breakfastIncluded : Boolean.FALSE;
		this.city = deal.city;
		this.secondCity = deal.secondCity;
		this.owner = deal.owner;
		this.company = deal.company;
		this.contactEmail = deal.contactEmail;
		this.limitHour = deal.limitHour;
		this.hotelCategory = deal.hotelCategory;
		this.roomType = deal.roomType;
		this.roomTypeText = deal.roomTypeText;
		this.address = deal.address;
		this.latitude = deal.latitude;
		this.longitude = deal.longitude;
		this.detailText = deal.detailText;
		this.hotelText = deal.hotelText;
		this.roomText = deal.roomText;
		this.aroundText = deal.aroundText;
		this.foodDrinkText = deal.foodDrinkText;
		this.detailTextEN = deal.detailTextEN;
		this.hotelTextEN = deal.hotelTextEN;
		this.roomTextEN = deal.roomTextEN;
		this.aroundTextEN = deal.aroundTextEN;
		this.foodDrinkTextEN = deal.foodDrinkTextEN;
		this.detailTextFR = deal.detailTextFR;
		this.hotelTextFR = deal.hotelTextFR;
		this.roomTextFR = deal.roomTextFR;
		this.aroundTextFR = deal.aroundTextFR;
		this.foodDrinkTextFR = deal.foodDrinkTextFR;
		this.customDetailText = StringUtils.isNotBlank(this.detailText);
		this.updated = new Date();
	}
	
	
	/**
	 * Check if deal is ready to be published
	 * @return true if the updated time is today
	 */
	 public boolean updatedToday(){
		return this.updated!= null && DateHelper.isTodayDate(this.updated);
	}

}


