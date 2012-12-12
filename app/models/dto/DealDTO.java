package models.dto;

import helper.DateHelper;
import helper.ImageHelper;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

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

public class DealDTO extends AbstractDealDTO{
   
    public CityDTO city;
    public Boolean active;
    public Boolean isHotUsa;
	

	public DealDTO(Deal deal, String cityRoot) {
		super(deal);
		this.city = this.findCurrentCity(deal, cityRoot);
		this.active = deal.active;
		this.isHotUsa = deal.isHotUsa;
		
	}

	private CityDTO findCurrentCity(Deal deal, String cityRoot) {
		deal.city.get();
		//If deal has a second city and the current city url is not the main city, then assign the second city to the deal DTO
		if (deal.secondCity != null && !deal.city.url.equalsIgnoreCase(cityRoot)){
			deal.secondCity.get();
			return new CityDTO(deal.secondCity);
		}
		else{
			return new CityDTO(deal.city);
		}
	}
}
