package helper.dto;

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
	
	public CityDTO(City city){
		this.id = city.id;
		this.name = city.nameEN;
		this.url = city.url;
		
		String lang = Lang.get();
		Logger.debug("Filling city info with locale: " + lang);
		if (lang.equals("es")){
			Logger.debug("Filling city info with Spanish ");
			this.name = city.name;
		}
		else if (lang.equals(Lang.getLocale().FRENCH.getLanguage())){
			Logger.debug("Filling city info with French ");
			this.name = city.nameFR;
		}
	}
	
}
