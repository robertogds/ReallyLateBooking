package controllers;

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
	
	public static void save(Long id, Integer quantity, Integer salePriceCents, Boolean breakfastIncluded) {
	    Deal deal;
	    // Retrieve post
	    deal = Deal.findById(id);
	    // Edit
	    deal.quantity = quantity;
	    deal.salePriceCents = salePriceCents;
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
