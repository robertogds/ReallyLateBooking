package controllers.admin;

import helper.HotUsaApiHelper;

import java.util.List;

import models.City;
import models.Deal;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(Deal.class)
public class Deals extends controllers.CRUD {
	
	public static void refreshHotUsaPrices(){
		List<City> cities = City.findActiveCities();
		HotUsaApiHelper.getHotelPricesByCityList(cities);
		redirect("/admin/deals");
	}
	
}


