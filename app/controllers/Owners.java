package controllers;

import helper.DateHelper;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import notifiers.Mails;

import jobs.Bootstrap;
import models.City;
import models.Deal;
import models.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;

@Check("owner")
@With(Secure.class)
public class Owners extends Controller{
	
	public static void index() { 
		User owner = User.findByEmail(Security.connected());
		List<Deal> deals = Deal.findActiveDealsByOwner(owner);
		Logger.debug("Number of deals of this owner: " + deals.size());
		render(deals); 
	} 
	
	public static void edit(Long id) {
		Logger.debug("Update deal " + id);
		Deal deal = Deal.findById(id);
		render(deal);
	}
	
	public static void save(Long id, Integer quantity, Float salePriceCents, boolean breakfastIncluded,
			Integer priceDay2, Integer priceDay3, Integer priceDay4, Integer priceDay5) {
		Logger.debug("breakfast: " + breakfastIncluded);
	    Deal deal;
	    // Retrieve post
	    deal = Deal.findById(id);
	    // Edit
	    deal.quantity = quantity;
	    deal.salePriceCents = salePriceCents;
	    deal.priceDay2 = priceDay2;
	    deal.priceDay3 = priceDay3;
	    deal.priceDay4 = priceDay4;
	    deal.priceDay5 = priceDay5;
	    deal.updated = Calendar.getInstance().getTime();
	    deal.breakfastIncluded = breakfastIncluded;

	    // Validate
	    validation.valid(deal);
	    if(validation.hasErrors()) {
	        render("@form", deal);
	    }
	    // Save
	    deal.update();
	    
	    //Notify RLB of the updated prices
	    Mails.ownerUpdatedDeal(deal);
	    
	    index();
	}

	
}
