package controllers.admin;

import helper.HotUsaApiHelper;

import java.util.*;

import models.*;
import controllers.*;
import play.*;
import play.mvc.*;
import siena.*;

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


