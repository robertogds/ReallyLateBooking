package models.dto;

import helper.UtilsHelper;

import java.util.Collection;
import java.util.Date;

import models.City;
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

public class CityDTO {

    public Long id;
	public String name;
	public String url;
	public String latitude;
	public String longitude;
	public String root;
	public boolean showHint;
	public String hint;
	public boolean hasZones;
	public boolean isRoot;
	public String country;
	
	public CityDTO(City city){
		this.id = city.id;
		this.name = city.nameEN;
		this.url = city.url;
		this.latitude = city.latitude;
		this.longitude = city.longitude;
		this.root = city.root;
		this.showHint = city.showHint;
		this.hint = city.hintEN;
		city.country.get();
		this.country = city.country.nameEN;
		this.isRoot = city.isRootCity();
		this.hasZones = city.isRootCityWithZones();
		
		String lang = Lang.get();
		if (lang != null){
			//Logger.debug("Filling city info with locale: " + lang);
			if (UtilsHelper.langIsSpanish(lang)){
				this.name = city.name;
				this.hint = city.hintES;
				this.country = city.country.name;
				//Logger.debug("Filling city info with Spanish : " + this.name);
			}
			else if (UtilsHelper.langIsFrench(lang)){
				//Logger.debug("Filling city info with French ");
				this.name = city.nameFR;
				this.hint = city.hintFR;
				this.country = city.country.nameFR;
			}
			else{
				//Logger.debug("Lang is not spanish nor french: " + lang);
			}
		}
	}
}
