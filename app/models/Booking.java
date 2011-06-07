package models;

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
    
    @Required(message="Credit card number is required")
    public String creditCard;
    
    @Required(message="Credit card name is required")
    @MinSize(value=3, message="Credit card name is required")
    @MaxSize(value=70, message="Credit card name is required")
    public String creditCardName;
    public int creditCardExpiryMonth;
    public int creditCardExpiryYear;
    public int creditCardSecureCode;

    public Booking(Deal deal, User user) {
        this.deal = deal;
        this.user = user;
    }
    
    @Override
	public void insert() {
    	this.checkinDate = Calendar.getInstance().getTime();
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
    
    public boolean creditCardValid(){
		return Validation.valid("creditCard", this).ok;
	}
    
	public boolean valid() {
		Deal deal = Deal.findById(this.deal.id);
		Logger.debug("Validating booking, we have: " + deal.quantity  + " and we book: " + this.getRooms());
		//can't book if there are no enough rooms available
		if (deal != null && this.getRooms() <= deal.quantity){
			return creditCardValid();
		}
		return false;
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
