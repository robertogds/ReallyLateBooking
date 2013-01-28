package models.dto;

import helper.UtilsHelper;

import java.util.List;

import models.City;
import play.i18n.Lang;

public class CityZoneDTO {

    public Long id;
	public String name;
	public String url;
	public String latitude;
	public String longitude;
	public boolean hasZones;
	public boolean isRoot;
	public List<DealDTOV3> deals;
	public boolean isGetaway;
	
	public CityZoneDTO(City city, List<DealDTOV3> deals){
		this.id = city.id;
		this.name = city.nameEN;
		this.url = city.url;
		this.latitude = city.latitude;
		this.longitude = city.longitude;
		this.isGetaway = city.isGetaway;
		this.isRoot = city.isRootCity();
		this.hasZones = city.isRootCityWithZones();
		this.deals = deals;
		
		String lang = Lang.get();
		if (UtilsHelper.langIsSpanish(lang)){
			this.name = city.name;
		}
		else if (UtilsHelper.langIsFrench(lang)){
			this.name = city.nameFR;
		}
	}
}
