package controllers.admin;

import helper.hotusa.HotUsaApiHelper;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.Booking;
import models.City;
import models.Company;
import models.Deal;
import models.User;
import play.Logger;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;
import controllers.CRUD.ObjectType;

@Check(Security.EDITOR_ROLE)
@With(Secure.class)
@CRUD.For(Deal.class)
public class Deals extends controllers.CRUD {
	
	
	public static void refreshHotUsaPrices(){
		List<City> cities = City.findActiveCities();
		HotUsaApiHelper.getHotelPricesByCityList(cities);
		redirect("/admin/deals");
	}
	
	public static void exportAll() {
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<Deal> deals = Deal.all().fetch();
		for (Deal deal: deals){
			if (deal.company != null){
				deal.company = Company.findById(deal.company.id);
			}
			if (deal.owner != null){
				deal.owner = User.findById(deal.owner.id);
			}
			if (deal.city != null){
				deal.city = City.findById(deal.city.id);
			}
		}
        List objects = deals;

        render("admin/export.csv", objects, type);
    }
	
	public static void createHotusaHotel(String hotelCode){
		Deal deal = HotUsaApiHelper.getHotelInfo(hotelCode);
		redirect("/admin/deals/%s", deal.id);
	}
	
	
}


