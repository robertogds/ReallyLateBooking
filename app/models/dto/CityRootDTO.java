package models.dto;

import helper.UtilsHelper;

import java.util.List;

import models.City;
import play.i18n.Lang;

public class CityRootDTO {
 	public Long id;
	public String name;
	public List<CityZoneDTO> zones;
	
	public CityRootDTO(City city, List<CityZoneDTO> zones){
		this.id = city.id;
		this.name = city.nameEN;
		this.zones = zones;
		
		String lang = Lang.get();
		if (UtilsHelper.langIsSpanish(lang)){
			this.name = city.name;
		}
		else if (UtilsHelper.langIsFrench(lang)){
			this.name = city.nameFR;
		}
	}

}
