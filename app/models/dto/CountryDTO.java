package models.dto;

import helper.UtilsHelper;

import java.util.Collection;
import java.util.Date;

import models.Country;

import play.Logger;
import play.data.validation.Required;
import play.i18n.Lang;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.Table;

public class CountryDTO {

    public Long id;
	public String name;
	public String url;
	public String latitude;
	public String longitude;
	
	public CountryDTO(Country country){
		this.id = country.id;
		this.name = country.nameEN;
		this.url = country.url;
		this.latitude = country.latitude;
		this.longitude = country.longitude;
		
		String lang = Lang.get();
		//Logger.debug("Filling country info with locale: " + lang);
		if (UtilsHelper.langIsSpanish(lang)){
			//Logger.debug("Filling country info with Spanish ");
			this.name = country.name;
		}
		else if (UtilsHelper.langIsFrench(lang)){
			//Logger.debug("Filling country info with French ");
			this.name = country.nameFR;
		}
	}
	
}
