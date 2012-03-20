package controllers;

import helper.DateHelper;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
		List<Deal> deals = Deal.findDealsByOwner(user);
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
			if(deal.city != null){
				deal.city.get();
			}
			for(Booking booking: bookings){
				booking.user.get();
			}
			int hour = DateHelper.getCurrentHour(deal.city.utcOffset);
			boolean open = DateHelper.isActiveTime(hour);
			
			if (!open){
				Date countdown = DateHelper.getTimeToOpen(hour);
				flash.put("warning", Messages.get("web.extranet.hotel.closed", countdown.getHours(),  countdown.getMinutes()));
			}
			else{
				if (deal.dealIsPublished()){
					flash.success( Messages.get("web.extranet.hotel.open.congrats"));
				}
				else{
					flash.put("warning", Messages.get("web.extranet.hotel.open.sorry"));
				}
				
			}
			render(deal, user, bookings, totalBookings);
		}
		else{
			notFound();
		}
	}
	
	public static void save(Long id, @Required Integer quantity, @Required Integer salePriceCents, boolean breakfastIncluded,
			Integer priceDay2, Integer priceDay3, Integer priceDay4, Integer priceDay5) {
	    if(validation.hasErrors()) {
	    	flash.error(Messages.get("web.extranet.updatedeal.incorrect"));
            params.flash();
            validation.keep(); // keep the errors for the next request
	        Logger.debug("Errors " + validation.errorsMap().toString());
	    }
	    else{
	    	// Retrieve post
		     Deal deal = Deal.findById(id);
		    // Edit
		    deal.quantity = salePriceCents == 0 ? 0 : quantity;
		    deal.priceDay2 = priceDay2 == null || priceDay2 == 0 ? null : priceDay2;
		    deal.priceDay3 = priceDay3 == null || priceDay3 == 0 ? null : priceDay3;
		    deal.priceDay4 = priceDay4 == null || priceDay4 == 0 ? null : priceDay4;
		    deal.priceDay5 = priceDay5 == null || priceDay5 == 0 ? null : priceDay5;
		    deal.updated = Calendar.getInstance().getTime();
		    deal.breakfastIncluded = breakfastIncluded;
		    deal.city.get();
		    
		    //If quantity is 0 the deal cant be active
		    if (quantity > 0 ){
		    	if (deal.dealIsPublished() && deal.salePriceCents < salePriceCents){
		    		flash.error(Messages.get("web.extranet.updatedeal.incorrect.price"));
		    	}
		    	else{
		    		deal.salePriceCents = salePriceCents;
		    		deal.update();
				    //Notify RLB of the updated prices
				    Mails.ownerUpdatedDeal(deal);
				    flash.success(Messages.get("web.extranet.updatedeal.success"));
		    	}
		    }
		    else{
		    	deal.active = Boolean.FALSE;
		    	flash.success(Messages.get("web.extranet.updatedeal.success.solded"));
		    	deal.update();
			    //Notify RLB of the updated prices
			    Mails.ownerUpdatedDeal(deal);
		    }
	    }
	    edit(id);
	}

	
}
