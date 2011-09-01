package models;

import helper.CreditCardHelper;
import helper.DateHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import play.Logger;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.data.validation.Validation.Validator;
import play.i18n.Messages;
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
    @MaxSize(value=4)
    public Integer creditCardCVC;
    public String code;
	public Float salePriceCents;
	public Integer priceCents;
	public Integer priceDay2;
	public Integer priceDay3;
	public Integer priceDay4;
	public String hotelName;
	public Boolean needConfirmation;

    public Booking(Deal deal, User user) {
        this.deal = deal;
        this.user = user;
    }
    
	@Override
	public void insert() {
    	this.checkinDate = DateHelper.getTodayDate();
    	this.code = RandomStringUtils.randomAlphanumeric(8);
    	this.deal = Deal.findById(this.deal.id); //fetch deal from datastore
    	this.priceCents = this.deal.priceCents; //save actual deal price
    	this.salePriceCents = this.deal.salePriceCents;
    	this.priceDay2 = this.deal.priceDay2;
    	this.priceDay3 = this.deal.priceDay3;
    	this.priceDay4 = this.deal.priceDay4;
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
    
    public static List<Booking> findConfirmationRequiredDeals(){
    	return Booking.all().filter("needConfirmation", Boolean.TRUE).fetch();
    }
    
    public static List<Booking> findByUser(User user){
    	return Booking.all().filter("user", user).fetch();
    }
   
    public Float getTotal() {
        return salePriceCents + priceDay2 + priceDay3 + priceDay4;
    }
    
    public void creditCardValid(){
    	if (Validation.valid("creditCard", this).message("booking.validation.creditcard").ok &&
    			Validation.valid("creditCardName", this).message("booking.validation.creditcardname").ok &&
    			Validation.valid("creditCardType", this).message("booking.validation.creditcardtype").ok&&
    			Validation.valid("creditCardCVC", this).message("booking.validation.cvc").ok){
    		if (!CreditCardHelper.validCC(this.creditCard)){
    			Validation.addError("creditCard", Messages.get("booking.validation.creditcard"), creditCard);
    		}
    		else{
    			creditCardExpiryValid();
    		}
    	}
	}
    
	private void creditCardExpiryValid() {
		 String[] parsers = new String[] {"M/yyyy", "M'/'yyyy"};
		 Date date ;
		 try {
			 date = DateUtils.parseDate(this.creditCardExpiry, parsers);
			 Logger.debug("Validating expiration date " + date.toString());
			 Validation.future("creditCardExpiry", date, Calendar.getInstance().getTime()).message("booking.validation.creditcardexpiry");	
		} catch (ParseException e) {
			Logger.error("Error parsing expiration date ", e);
		}
	}

	public void validate() {
		Deal deal = Deal.findById(this.deal.id);
		Logger.debug("Validating booking, we have: " + deal.quantity  + " and we book: " + this.getRooms());
		//can't book if there are no enough rooms available
		if (deal == null || !deal.active){
			Validation.addError("deal", Messages.get("booking.validation.expired"));
		} 
		else if (this.getRooms() > deal.quantity){
			Validation.addError("rooms", Messages.get("booking.validation.over"));
			Logger.error("##AGOTADA: someone tried to make a reservation but there were no left rooms");
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
