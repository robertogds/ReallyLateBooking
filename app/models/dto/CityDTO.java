package models.dto;

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
	
	public CityDTO(City city){
		this.id = city.id;
		this.name = city.nameEN;
		this.url = city.url;
		this.latitude = city.latitude;
		this.longitude = city.longitude;
		this.root = city.root;
		
		String lang = Lang.get();
		if (lang != null){
			Logger.debug("Filling city info with locale: " + lang);
			if (lang.equals("es") || lang.equals("es_ES")){
				this.name = city.name;
				Logger.debug("Filling city info with Spanish : " + this.name);
			}
			else if (lang.equals("fr")){
				Logger.debug("Filling city info with French ");
				this.name = city.nameFR;
			}
			else{
				Logger.debug("Lang is not spanish nor french: " + lang);
			}
		}
	}
}
