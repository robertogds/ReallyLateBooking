package controllers.admin;

import helper.hotusa.HotUsaApiHelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
	
	public static void panel(){
		User user = User.findById(Long.valueOf(session.get("userId")));
		List<Deal> deals = Deal.findLastCreatedDeals();
		render(user, deals);
	}
	
	public static void search(String hotelName, Long cityId, Long companyId){
		Logger.debug("Buscando: %s, %s, %s", hotelName, cityId, companyId);
		User user = User.findById(Long.valueOf(session.get("userId")));
		List<City> cities = City.all().fetch();
		List<Company> companies = Company.all().fetch();
		List<Deal> deals = new ArrayList<Deal>();
		if (StringUtils.isNotBlank(hotelName)){
			deals = Deal.findByHotelName(hotelName, cityId, companyId);
		}
		else if (cityId != null){
			City city = new City(cityId);
			deals = Deal.findByCityOrderByName(city);
		}
		else if (companyId != null){
			Company company = new Company(companyId);
			deals = Deal.findByCompanyOrderByName(company);
		}
		render(user, deals, cities, companies);
	}
	
	public static void searchForm(){
		User user = User.findById(Long.valueOf(session.get("userId")));
		List<City> cities = City.all().fetch();
		List<Company> companies = Company.all().fetch();
		List<Deal> deals = new ArrayList<Deal>();
		renderTemplate("admin/Deals/search.html", user, deals, cities, companies);
	}
	
	public static void create(Deal deal){
		User user = User.findById(Long.valueOf(session.get("userId")));
		render(deal, user);
	}
	
	public static void createForm(){
		User user = User.findById(Long.valueOf(session.get("userId")));
		renderTemplate("admin/Deals/create.html", user);
	}
	
	public static void listCities(String orderBy, String order){
		List<City> cities = City.findAllMainCities(orderBy, order);
		render(cities);
	}
	
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


