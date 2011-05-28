package models;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import models.crudsiena.SienaSupport;

public class Booking extends SienaSupport {
    
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	
    @Required
    @Index("user_index")
    public User user;
    
    @Required
    @Index("deal_index")
    public Deal deal;
    
    @Required
    public Date checkinDate;
    
    @Required
    public Date checkoutDate;
    
    @Required(message="Credit card number is required")
    @Match(value="^\\d{16}$", message="Credit card number must be numeric and 16 digits long")
    public String creditCard;
    
    @Required(message="Credit card name is required")
    @MinSize(value=3, message="Credit card name is required")
    @MaxSize(value=70, message="Credit card name is required")
    public String creditCardName;
    public int creditCardExpiryMonth;
    public int creditCardExpiryYear;
    public boolean smoking;
    public int beds;

    public Booking(Deal deal, User user) {
        this.deal = deal;
        this.user = user;
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
        return deal.salePriceCents * getNights();
    }

    public int getNights() {
        return (int) ( checkoutDate.getTime() - checkinDate.getTime() ) / 1000 / 60 / 60 / 24;
    }

    public String getDescription() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return deal==null ? null : deal.hotelName + 
            ", " + df.format( checkinDate ) + 
            " to " + df.format( checkoutDate );
    }

    public String toString() {
        return "Booking(" + user + ","+ deal.hotelName + ")";
    }

}
