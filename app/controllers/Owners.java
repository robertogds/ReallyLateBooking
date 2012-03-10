package controllers;

import helper.DateHelper;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import notifiers.Mails;

import jobs.Bootstrap;
import models.Booking;
import models.City;
import models.Deal;
import models.User;
import play.Logger;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@Check("owner")
@With(Secure.class)
public class Owners extends Controller{
	
	public static void index() { 
		User user = User.findByEmail(Security.connected());
		List<Deal> deals = Deal.findActiveDealsByOwner(user);
		render(deals, user); 
	} 
	
	public static void edit(Long id) {
		Logger.debug("Update deal " + id);
		User user = User.findByEmail(Security.connected());
		Deal deal = Deal.findById(id);
		if (deal != null && deal.owner != null && deal.owner.equals(user)){
			List<Booking> bookings = Booking.findByDeal(deal);
			int totalBookings = bookings.size();
			if(deal.company != null){
				deal.company.get();
			}
			for(Booking booking: bookings){
				booking.user.get();
			}
			render(deal, user, bookings, totalBookings);
		}
		else{
			notFound();
		}
	}
	
	public static void save(Long id, @Required Integer quantity, @Required Integer salePriceCents, boolean breakfastIncluded,
			Integer priceDay2, Integer priceDay3, Integer priceDay4, Integer priceDay5) {
	    // Retrieve post
	     Deal deal = Deal.findById(id);
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
	    	flash.error(Messages.get("web.extranet.updatedeal.incorrect"));
            params.flash();
	        edit(deal.id);
	    }
	    else{
	    	flash.success(Messages.get("web.extranet.updatedeal.success"));
		    deal.update();
		    //Notify RLB of the updated prices
		    Mails.ownerUpdatedDeal(deal);
		    index();
	    }
	}

	
}
