package helper.dto;

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
		Logger.debug("Filling country info with locale: " + lang);
		if (lang.equals("es")){
			Logger.debug("Filling country info with Spanish ");
			this.name = country.name;
		}
		else if (lang.equals(Lang.getLocale().FRENCH.getLanguage())){
			Logger.debug("Filling country info with French ");
			this.name = country.nameFR;
		}
	}
	
}
