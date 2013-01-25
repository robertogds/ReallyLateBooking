package controllers;

import helper.DateHelper;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import notifiers.Mails;

import jobs.Bootstrap;
import models.Booking;
import models.City;
import models.Company;
import models.Deal;
import models.User;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.With;

@Check("owner")
@With({LogExceptions.class, Secure.class})
public class Owners extends Controller{
	
	private static final Logger log = Logger.getLogger(Owners.class.getName());
	
	@Catch(Exception.class)
    public static void logIllegalState(Throwable throwable) {
        log.severe("Internal error …" + throwable);
        throwable.printStackTrace();
        User user = User.findByEmail(Security.connected());
        Mails.errorMail("#EXTRANET ERROR# User:" + user.firstName + " email: "+ user.email, throwable.toString());
        flash.put("error", Messages.get("web.extranet.hotel.notconfigured"));
        index();
    }
	
	public static void index() { 
		User user = User.findByEmail(Security.connected());
		List<Deal> deals = Deal.findDealsByOwner(user);
		render(deals, user); 
	} 
	
	public static void edit(Long id) {
		log.info("Update deal " + id);
		User user = User.findByEmail(Security.connected());
		Deal deal = Deal.findById(id);
		if (deal != null && deal.owner != null && deal.owner.equals(user)){
			List<Booking> bookings = Booking.findByDeal(deal);
			int totalBookings = bookings.size();
			deal.company.get();
			deal.city.get();
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
	
	public static void save(Long id, @Required int quantity, @Required int salePriceCents, @Required int bestPrice, boolean breakfastIncluded,
			int priceDay2, int priceDay3, int priceDay4, int priceDay5, int quantityDay2, int quantityDay3, int quantityDay4, int quantityDay5) {
	    if(validation.hasErrors()) {
	    	flash.error(Messages.get("web.extranet.updatedeal.incorrect"));
            params.flash();
            validation.keep(); // keep the errors for the next request
	        log.info("Errors " + validation.errorsMap().toString());
	    }
	    else{
		     Deal deal = Deal.findById(id);
		    // Edit
		    deal.quantity = salePriceCents == 0 ? 0 : quantity;
		    deal.priceDay2 = priceDay2 == 0 || quantityDay2 == 0 ? null : priceDay2;
		    deal.priceDay3 = priceDay3 == 0 || quantityDay3 == 0 ? null : priceDay3;
		    deal.priceDay4 = priceDay4 == 0 || quantityDay4 == 0 ? null : priceDay4;
		    deal.priceDay5 = priceDay5 == 0 || quantityDay5 == 0 ? null : priceDay5;
		    
		    if (checkQuantityDay(deal.priceDay2, deal.quantity, quantityDay2) &&
		    	checkQuantityDay(deal.priceDay3, deal.quantity, quantityDay3) &&
		    	checkQuantityDay(deal.priceDay4, deal.quantity, quantityDay4) &&
		    	checkQuantityDay(deal.priceDay5, deal.quantity, quantityDay5)){
		    	deal.quantityDay2 = quantityDay2 > 0 ? quantityDay2 : null;
		    	deal.quantityDay3 = quantityDay3 > 0 ? quantityDay3 : null;
			    deal.quantityDay4 = quantityDay4 > 0 ? quantityDay4 : null;
			    deal.quantityDay5 = quantityDay5 > 0 ? quantityDay5 : null;
		    }
		    else{
		    	flash.error(Messages.get("web.extranet.updatedeal.incorrect.quantity"));
	    		edit(id);
		    }
		    
		    //If quantity is 0 the deal cant be active
		    deal.city.get();
		    if (quantity > 0 ){
		    	if (deal.dealIsPublished() && deal.salePriceCents!=null && deal.salePriceCents < salePriceCents){
		    		flash.error(Messages.get("web.extranet.updatedeal.incorrect.price"));
		    		edit(id);
		    	}
		    	else{
				    flash.success(Messages.get("web.extranet.updatedeal.success"));
		    	}
		    }
		    else{
		    	deal.active = Boolean.FALSE;
			    flash.success(Messages.get("web.extranet.updatedeal.success.solded"));
		    }
		    
		    deal.updated = Calendar.getInstance().getTime();
		    deal.breakfastIncluded = breakfastIncluded;
		    deal.salePriceCents = salePriceCents;
    		deal.bestPrice = bestPrice;
    		deal.calculateNetPrices();
    		deal.calculateDiscount();
    		deal.update();
    		//Notify RLB of the updated prices
		    Mails.ownerUpdatedDeal(deal);
	    }
	    edit(id);
	}

	/**
	 * Checks if quantity is correct for the given day
	 * It must be greater than quantity for tonight
	 * @param priceDay
	 * @param quantity
	 * @param quantityDay
	 * @return
	 */
	private static boolean checkQuantityDay(Integer priceDay,
			int quantity, int quantityDay) {
		if (priceDay != null){
	    	if (quantity > 0 && quantityDay < quantity){
	    		return false;
	    	}
	    }
		return true;
	}

	
}
