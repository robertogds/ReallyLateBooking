package helper.dto;

import helper.DateHelper;

import java.security.InvalidParameterException;
import java.util.Date;

import models.City;
import models.Deal;
import models.User;
import play.Logger;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.i18n.Lang;
import siena.Generator;
import siena.Id;
import siena.Index;

public class DealDTO {

    public Long id;
	public String hotelName;	
    public City city;
	public Float salePriceCents;
	public Integer priceCents;
	public Integer priceDay2;
	public Integer priceDay3;
	public Integer priceDay4;
	public Integer priceDay5;
	public Integer quantity;
	public String description;
	public String shortDescription;
	public String roomType;
	public String roomTypeText;
	public Integer hotelCategory;
	public String address;
	public String latitude;
	public String longitude;
    public String mainImageBig;
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
	public String detailText;
	public String hotelText;
	public String roomText;
	public String foodDrinkText;
	public String aroundText;
	public Date checkinDate;
	public Boolean breakfastIncluded;

	
	
	public DealDTO(Deal deal) {
		validateDeal(deal);
		this.id = deal.id;
		this.hotelName = deal.hotelName;
		this.city = deal.city;
		this.salePriceCents = deal.salePriceCents;
		this.priceCents = deal.priceCents;
		this.priceDay2 = deal.priceDay2;
		this.priceDay3 = deal.priceDay3;
		this.priceDay4 = deal.priceDay4;
		this.priceDay5 = deal.priceDay5;
		this.quantity = deal.quantity;
		this.hotelCategory = deal.hotelCategory;
		this.roomType = deal.roomType;
		this.roomTypeText = deal.roomTypeText;
		this.address = deal.address;
		this.latitude = deal.latitude;
		this.longitude = deal.longitude;
		this.mainImageBig = deal.mainImageBig;
		this.mainImageSmall = deal.mainImageSmall;
		this.image1 = deal.image1;
		this.image2 = deal.image2;
		this.image3 = deal.image3;
		this.image4 = deal.image4;
		this.image5 = deal.image5;
		this.image6 = deal.image6;
		this.image7 = deal.image7;
		this.image8 = deal.image8;
		this.image9 = deal.image9;
		this.image10 = deal.image10;
		this.detailText = deal.detailTextEN;
		this.hotelText = deal.hotelTextEN;
		this.roomText = deal.roomTextEN;
		this.aroundText = deal.aroundTextEN;
		this.foodDrinkText = deal.foodDrinkTextEN;
		this.checkinDate = DateHelper.getTodayDate();
		this.breakfastIncluded = deal.breakfastIncluded;
		
		String lang = Lang.get();
		Logger.debug("Filling deal info with locale: " + lang);
		if (lang.equals("es")){
			Logger.debug("Filling deal info with Spanish ");
			this.detailText = deal.detailText;
			this.hotelText = deal.hotelText;
			this.roomText = deal.roomText;
			this.aroundText = deal.aroundText;
			this.foodDrinkText = deal.foodDrinkText;
		}
		else if (lang.equals(Lang.getLocale().FRENCH.getLanguage())){
			Logger.debug("Filling deal info with French ");
			this.detailText = deal.detailTextFR;
			this.hotelText = deal.hotelTextFR;
			this.roomText = deal.roomTextFR;
			this.aroundText = deal.aroundTextFR;
			this.foodDrinkText = deal.foodDrinkTextFR;
		}

	}

	private void validateDeal(Deal deal) {
		if (deal == null){
			throw new InvalidParameterException("Object Deal cannot be null");
		}
	}

	
}
