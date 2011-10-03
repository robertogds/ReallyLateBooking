package controllers.admin;

import java.util.*;

import notifiers.Mails;

import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;
import models.*;
import play.*;
import play.modules.crudsiena.CrudUnique;
import play.mvc.*;
import siena.*;

@Check("admin")
@With(Secure.class)
@CRUD.For(City.class)
public class Cities extends controllers.CRUD {
	
	public static void editCityDeals(Long cityId) {
		City city = City.findById(cityId);
        Collection<Deal> deals = Deal.findByCity(city);
        render(city, deals);
	}
	
	public static void updateDeal(Long id, Integer quantity, Integer priceCents, Integer salePriceCents, 
			Integer priceDay2, Integer priceDay3, Integer priceDay4, Integer priceDay5, 
			Integer position, Boolean active, Boolean isHotUsa, Boolean isFake, Integer limitHour,
			Long cityId) {
	    Deal deal;
	    // Retrieve post
	    deal = Deal.findById(id);
	    // Edit
	    deal.quantity = quantity;
	    deal.priceCents = priceCents;
	    deal.salePriceCents = salePriceCents;
	    deal.priceDay2 = priceDay2;
	    deal.priceDay3 = priceDay3;
	    deal.priceDay4 = priceDay4;
	    deal.priceDay5 = priceDay5;
	    deal.position = position;
	    deal.active = active;
	    deal.isHotUsa = isHotUsa;
	    deal.isFake = isFake;
	    deal.limitHour = limitHour;
	    
	    //Actualize city updated date
	    City city = City.findById(cityId);
	    city.updated = Calendar.getInstance().getTime();
	    city.update();

	    // Validate
	    validation.valid(deal);

	    // Save
	    deal.update();

	    editCityDeals(cityId);
		
	}
	
}

