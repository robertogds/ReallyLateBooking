package controllers.admin;

import helper.hotusa.HotUsaApiHelper;

import java.util.ArrayList;
import java.util.List;

import models.City;
import models.Company;
import models.Deal;
import models.User;
import models.dto.AbstractDealDTO;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.validation.Valid;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;

@Check(Security.EDITOR_ROLE)
@With(Secure.class)
@CRUD.For(Deal.class)
public class Deals extends controllers.CRUD {
	
	public static void panel(){
		List<Deal> deals = Deal.findLastCreatedDeals();
		render(deals);
	}
	
	public static void search(String hotelName, Long cityId, Long companyId){
		Logger.debug("Buscando: %s, %s, %s", hotelName, cityId, companyId);
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
		render(deals, cities, companies);
	}
	
	public static void searchForm(){
		List<City> cities = City.all().fetch();
		List<Company> companies = Company.all().fetch();
		List<Deal> deals = new ArrayList<Deal>();
		renderTemplate("admin/Deals/search.html", deals, cities, companies);
	}
	
	public static void create(@Valid Deal deal){
		Logger.debug("creando deal: %s", deal.hotelName);
		if (!validation.hasErrors()){
			if (deal.id != null){
				Deal dealDB = Deal.findById(deal.id);
				dealDB.updateFromDeal(deal);
				dealDB.update();
				createForm(deal.id);
			}
			else{
				deal.insert();
				createForm(deal.id);
			}
		}
		else{
			params.flash();
			render(deal);
		}
	}
	
	public static void createForm(Long id){
		if (id != null){
			Deal deal = Deal.findById(id);
			AbstractDealDTO dealDTO = new AbstractDealDTO(deal);
			renderTemplate("admin/Deals/create.html", deal, dealDTO);
		}
		else{
			renderTemplate("admin/Deals/create.html");
		}
	}
	
	public static void deleteImage(Long id, int image){
		Deal deal = Deal.findById(id);
		switch (image) {
			case 1:
				deal.image1 = null;
				break;
			case 2:
				deal.image2 = null;
				break;
			case 3:
				deal.image3 = null;
				break;
			case 4:
				deal.image4 = null;
				break;
			case 5:
				deal.image5 = null;
				break;
			case 6:
				deal.image6 = null;
				break;
			case 7:
				deal.image7 = null;
				break;
			case 8:
				deal.image8 = null;
				break;
			case 9:
				deal.image9 = null;
				break;
			case 10:
				deal.image10 = null;
				break;
		}
		deal.update();
		createForm(id);
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


