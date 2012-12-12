package models.dto;

import helper.UtilsHelper;

import java.util.List;

import models.City;
import play.i18n.Lang;

public class DealsCityDTO {

    public Long id;
	public String name;
	public String url;
	public String latitude;
	public String longitude;
	public String root;
	public boolean hasZones;
	public boolean isRoot;
	public List<DealDTOV3> deals;
	
	public DealsCityDTO(City city, List<DealDTOV3> deals){
		this.id = city.id;
		this.name = city.nameEN;
		this.url = city.url;
		this.latitude = city.latitude;
		this.longitude = city.longitude;
		this.root = city.root;
		city.country.get();
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
