package controllers.admin;

import java.util.Calendar;
import java.util.Collection;

import models.City;
import models.Deal;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(City.class)
public class Cities extends controllers.CRUD {
	
	public static void editCityDeals(Long cityId) {
		City city = City.findById(cityId);
        Collection<Deal> deals = Deal.findByCity(city);
        render(city, deals);
	}
	
	public static void updateDeal(Long id, Integer quantity, Integer priceCents, Integer bestPrice, Integer salePriceCents, 
			Integer priceDay2, Integer priceDay3, Integer priceDay4, Integer priceDay5, 
			Integer position, Boolean active, Boolean isHotUsa, Boolean isFake, Integer limitHour,
			Long cityId) {
	    Deal deal;
	    // Retrieve post
	    deal = Deal.findById(id);
	    // Edit
	    deal.quantity = quantity;
	    deal.bestPrice = bestPrice;
	    deal.priceCents = priceCents;
	    if (deal.bestPrice != null ){
	    	int dif = (deal.bestPrice - deal.salePriceCents) * 100;
	    	deal.discount = dif != 0 ? dif / deal.bestPrice : 0;
	    }
	    else{
	    	deal.discount = 0;
	    }
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

