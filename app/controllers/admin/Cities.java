package controllers.admin;

import helper.JsonHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.City;
import models.Deal;
import models.User;
import models.dto.CouponStatusMessage;
import models.dto.MyCouponDTO;
import java.util.logging.Logger;
import play.data.binding.As;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Http;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;
import controllers.CRUD.ObjectType;

@Check(Security.EDITOR_ROLE)
@With(Secure.class)
@CRUD.For(City.class)
public class Cities extends controllers.CRUD {
	
	private static final Logger log = Logger.getLogger(Cities.class.getName());
	
	/*
	 * Override list method from CRUD 
	 * 
	 * */
	public static void list(int page, String search, String searchFields, String orderBy, String order, String[] fields) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<City> objects = City.findAllMainCities(orderBy, order);
        int count = objects.size();
        int totalCount = count;
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }
	
	public static void zones(Long cityId) {
         City root =  City.findById(cityId);
         List<City> cities = City.findCitiesByRoot(root.url);
		 render(cities, root);
    }
	
	public static void editCityDeals(Long cityId) {
		City city = City.findById(cityId);
		//List<City> cities = City.findCitiesByRoot(city.url);
		Collection<Deal> deals = Deal.findByCity(city);
        render(city, deals);
	}
	
	
	public static void autoOrderDeals(Long cityId) {
		City city = City.findById(cityId);
        Collection<Deal> deals = Deal.findActiveByCityOrderDiscountAndPrice(city);
        int position = 1;
        for (Deal deal: deals){
        	deal.position = position;
        	deal.update();
        	position++;
        }
        editCityDeals(city.id);
	}
	
	public static String order(String list) {
		return "Lista de deals actualizada correctamente";
	}
	
	public static void updateDeal(Long id, Integer quantity, Integer priceCents, Integer bestPrice, Integer salePriceCents, 
			Integer priceDay2, Integer priceDay3, Integer priceDay4, Integer priceDay5, 
			Integer position, Boolean active, Boolean isHotUsa, Boolean isFake, Integer limitHour,
			Long cityId, String trivagoCode, Integer points) {
	    Deal deal;
	    // Retrieve post
	    deal = Deal.findById(id);
	    // Edit
	    deal.quantity = quantity;
	    deal.bestPrice = bestPrice;
	    deal.priceCents = priceCents;
	    deal.salePriceCents = salePriceCents;
	    deal.priceDay2 = priceDay2;
	    deal.priceDay3 = priceDay3;
	    deal.priceDay4 = priceDay4;
	    deal.priceDay5 = priceDay5;
	    deal.position = position;
	    deal.active = active != null ? active : Boolean.FALSE;
	    deal.isHotUsa = isHotUsa != null ? isHotUsa : Boolean.FALSE;
	    deal.isFake = isFake != null ? isFake : Boolean.FALSE;
	    deal.limitHour = limitHour;
	    deal.trivagoCode = trivagoCode;
	    deal.points = points;
	    deal.calculateDiscount();
	    //Actualize city updated date
	    City city = City.findById(cityId);
	    city.updated = Calendar.getInstance().getTime();
	    city.update();
	    if (!city.isSimpleCity()){
	    	City root = City.findByUrl(city.root);
	    	root.updated = city.updated;
	    	root.update();
	    	//if city is a zone we want to redirect to the root
	    	cityId = root.id;
	    }
	    
	    // Validate
	    //validation.valid(deal);

	    // Save
	    deal.update();

	    renderJSON(deal);
	}
	
	
	public static void updateAll(Long cityId){
		//Workaround due to a Play binding problem
		String[] items = params.getAll("dealIds");
	    for (int position = 0; position < items.length; position++) {
	    	Long dealId = Long.parseLong(items[position]);
	    	Deal deal = Deal.findById(dealId);
	    	deal.position= position;
	    	deal.update();
	    }
	}
	
	public static void exportAll() {
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<City> cities = City.all().fetch();
		for (City city: cities){
			if(city.country != null){
				city.country.get();
			}
		}
        List objects = cities;

        render("admin/export.csv", objects, type);
    }
	
}

