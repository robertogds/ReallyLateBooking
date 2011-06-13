package models;

import helper.CreditCardHelper;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import play.Logger;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.data.validation.Validation.Validator;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;

public class Booking extends Model {
    
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	
    @Required
    @Index("user_index")
    public User user;
    
    @Required
    @Index("deal_index")
    public Deal deal;
    
    public Date checkinDate;
    public Integer nights;
    public Integer rooms;
    
    @Required
    public String creditCardType;
    @Required
    public String creditCard;
    @Required
    @MinSize(value=3)
    @MaxSize(value=70)
    public String creditCardName;
    @Required
    public String creditCardExpiry;
    @Required
    @MinSize(value=3)
    @MaxSize(value=3)
    public int creditCardCVC;
    
    public String code;
	public Integer salePriceCents;
	public Integer priceCents;
	public String hotelName;

    public Booking(Deal deal, User user) {
        this.deal = deal;
        this.user = user;
    }
    
	@Override
	public void insert() {
    	this.checkinDate = Calendar.getInstance().getTime();
    	this.code = RandomStringUtils.randomAlphanumeric(8);
    	this.deal = Deal.findById(this.deal.id); //fetch deal from datastore
    	this.priceCents = this.deal.priceCents; //save actual deal price
    	this.salePriceCents = this.deal.salePriceCents;
    	this.hotelName = this.deal.hotelName;
    	super.insert();
	}
    
    public static Booking findById(Long id) {
        return all().filter("id", id).get();
    }
    
    public static Query<Booking> all() {
    	return Model.all(Booking.class);
    }
    
    public static List<Booking> findByDeal(Deal deal){
    	return Booking.all().filter("deal", deal).fetch();
    }
    
    public static List<Booking> findByUser(User user){
    	return Booking.all().filter("user", user).fetch();
    }
   
    public Integer getTotal() {
        return deal.salePriceCents * nights;
    }
    
    public void creditCardValid(){
    	if (Validation.valid("creditCard", this).ok &&
    			Validation.valid("creditCardName", this).ok &&
    			Validation.valid("creditCardType", this).ok &&
    			Validation.valid("creditCardExpiry", this).ok){
    		if (!CreditCardHelper.validCC(this.creditCard)){
    			Validation.addError("creditCard", "Not a correct credit card number", creditCard);
    		}
    		else{
    			//TODO this validation is not working
    			//Validation.future("creditCardExpiry", this.creditCardExpiry);
    		}
    	}
	}
    
	@SuppressWarnings("unused")
	public void validate() {
		Deal deal = Deal.findById(this.deal.id);
		Logger.debug("Validating booking, we have: " + deal.quantity  + " and we book: " + this.getRooms());
		//can't book if there are no enough rooms available
		if (deal == null){
			Validation.addError("deal", "Deal cannot be null");
		} 
		else if (this.getRooms() > deal.quantity){
			Validation.addError("rooms", "not enough rooms fo tonight");
		} 
		else {
			creditCardValid();
		}
	}

    public String getDescription() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return deal==null ? null : deal.hotelName + 
            ", " + df.format( checkinDate ) ;
    }
    
    public Integer getRooms(){
    	//if we are not using number of rooms means is always just 1 per booking
    	return  this.rooms != null ? this.rooms : 1;
    }

}
