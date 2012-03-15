package models.dto;

import helper.DateHelper;

import java.security.InvalidParameterException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import controllers.InfoTexts;

import models.City;
import models.Deal;
import models.InfoText;
import models.User;
import play.Logger;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import siena.Generator;
import siena.Id;
import siena.Index;

public class DealDTO {
	
    public Long id;
	public String hotelName;	
    public CityDTO city;
	public Integer salePriceCents;
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
	public String detailText;
	public String hotelText;
	public String roomText;
	public String foodDrinkText;
	public String aroundText;
	public Date checkinDate;
	public Boolean breakfastIncluded;
	public String nights;
	public Boolean active;

	
	
	public DealDTO(Deal deal) {
		validateDeal(deal);
		this.id = deal.id;
		this.hotelName = deal.hotelName;
		deal.city.get();
		this.city = new CityDTO(deal.city);
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
		this.listImage = deal.listImage;
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
		this.nights = getNights();
		this.active = deal.active;
		
		String lang = Lang.get();
		if (lang.equals("es")){
			this.detailText = deal.detailText;
			this.hotelText = deal.hotelText;
			this.roomText = deal.roomText;
			this.aroundText = deal.aroundText;
			this.foodDrinkText = deal.foodDrinkText;
		}
		else if (lang.equals(Lang.getLocale().FRENCH.getLanguage())){
			this.detailText = deal.detailTextFR;
			this.hotelText = deal.hotelTextFR;
			this.roomText = deal.roomTextFR;
			this.aroundText = deal.aroundTextFR;
			this.foodDrinkText = deal.foodDrinkTextFR;
		}
		this.createDetailText(deal.customDetailText, lang);
	}

	private void createDetailText(boolean customText, String lang){
		if (!customText){
			InfoText text = InfoText.findByKey(InfoTexts.DEAL_DETAIL_TEXT);
			this.detailText = text != null ? text.content : this.detailText;
		}
		this.setBreakfastMode();
	}
	
	private void setBreakfastMode() {
		if(this.breakfastIncluded != null && this.breakfastIncluded){
			this.detailText = Messages.get("deal.detailText.breakast.yes") + this.detailText;
		}
		else{
			this.detailText = Messages.get("deal.detailText.breakast.no") + this.detailText;
		}
	}

	private String getNights(){
		String nights = "1";
		nights = this.priceDay2 != null ? "1-2" : nights;
		nights = this.priceDay3 != null ? "1-3" : nights;
		nights = this.priceDay4 != null ? "1-4" : nights;
		nights = this.priceDay5 != null ? "1-5" : nights;
		return nights;
	}
	
	private void validateDeal(Deal deal) {
		if (deal == null){
			throw new InvalidParameterException("Object Deal cannot be null");
		}
	}

	/**
	 * adapts the info text so we can display the text correctly at the web
	 * Just a workaround to avoid rewrite all the info
	 * */
	public void textToHtml() {
		this.detailText = parseToHtml(this.detailText);
		this.aroundText = parseToHtml(this.aroundText);
		this.foodDrinkText = parseToHtml(this.foodDrinkText);
		this.roomText = parseToHtml(this.roomText);
		this.hotelText = parseToHtml(this.hotelText);
	}
	
	/*
	 * Returns the html version of the text.
	 * just changes * and - by <br>
	 * */
	private static String parseToHtml(String text){
		text = removeFirstBr(text, "*");
		text = removeFirstBr(text, "- ");
		String html =  StringUtils.replace(text, "*", "<br>");
		html =  StringUtils.replace(html, "- ", "<br>");
		return html;
	}
	
	/*
	 * Removes the first BR  
	 * @param text
	 * @param remove
	 * @return
	 */
	private static String removeFirstBr(String text, String remove){
		if (StringUtils.startsWith(text, remove)){
			text = StringUtils.removeStart(text, remove);
		}
		return text;
	}
}
