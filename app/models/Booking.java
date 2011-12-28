package models;

import helper.CreditCardHelper;
import helper.DateHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.PreUpdate;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.h2.util.MathUtils;

import play.Logger;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.data.validation.Validation.Validator;
import play.i18n.Messages;
import siena.DateTime;
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
    
    @Index("company_index")
    public Company company;
    
    @Index("invoice_index")
    public Invoice invoice;
    
    @Index("city_index")
    public City city;
    
    @Required
    @Index("deal_index")
    public Deal deal;
    @DateTime
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
    public String creditCardCVC;
    public String code;
	public Integer salePriceCents;
	public Integer priceCents;
	public Integer priceDay2;
	public Integer priceDay3;
	public Integer priceDay4;
	public Integer priceDay5;
	public String hotelName;
	public Boolean needConfirmation;
	public Boolean canceled;
	public Boolean invoiced;
    public String bookingForFirstName;
	public String bookingForLastName;
	public String bookingForEmail;
	public Float fee;
	@MaxSize(10000)
	public String comment;

    public Booking(Deal deal, User user) {
        this.deal = deal;
        this.user = user;
    }
    
	@Override
	public void insert() {
		Logger.debug("get nights: " + this.nights);
		this.nights = this.getTotalNights();
    	this.checkinDate = DateHelper.getTodayDate();
    	this.code = RandomStringUtils.randomAlphanumeric(8);
    	this.deal = Deal.findById(this.deal.id); //fetch deal from datastore
    	this.priceDay2 = this.deal.priceDay2;
    	this.priceDay3 = this.deal.priceDay3;
    	this.priceDay4 = this.deal.priceDay4;
    	this.priceDay5 = this.deal.priceDay5;
    	this.hotelName = this.deal.hotelName;
    	this.priceCents = this.getTotalPrice(); //save actual deal price
    	this.salePriceCents = this.getTotal();
    	this.invoiced = Boolean.FALSE;
    	this.canceled = Boolean.FALSE;
    	
    	super.insert();
	}
	
	private Integer getTotalNights(){
		if (this.nights == null || this.nights < 1){
			return 1;
		}
		else{
			return this.nights;
		}
	}
    
    private Integer getTotalPrice() {
		return this.deal.priceCents * this.nights;
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
   
    public Integer getTotal() {
    	Integer price = this.deal.salePriceCents;
    	if (nights >= 2){
    		price += priceDay2;
    	}
    	if (nights >= 3){
    		price += priceDay3;
    	}
    	if (nights >= 4){
    		price += priceDay4;
    	}
    	if (nights == 5){
    		price += priceDay5;
    	}
    	Logger.debug("Total price: " + price);

        return price;
    }
    
    public void creditCardValid(){
    	Logger.debug("Credit card number: " + creditCard + " creditCardName: "+ creditCardName + " creditCardType: "+creditCardType + " creditCardCVC: " + creditCardCVC);
    	if (Validation.valid("creditCard", this).message("booking.validation.creditcard").ok &&
    			Validation.valid("creditCardName", this).message("booking.validation.creditcardname").ok &&
    			Validation.valid("creditCardType", this).message("booking.validation.creditcardtype").ok&&
    			Validation.valid("creditCardCVC", this).message("booking.validation.cvc").ok){
    		if (!CreditCardHelper.validCC(this.creditCard)){
    			Logger.debug("Credit card number algotrithm failed");
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

	public static Collection<Booking> findByCompany(Company company) {
		return Booking.all().filter("company", company).fetch();
	}

	public static Collection<Booking> findUninvoicedByCompany(Company company) {
		return Booking.all().filter("company", company).filter("invoiced", Boolean.FALSE)
			.filter("canceled", Boolean.FALSE).fetch();
	}

	public static Collection<Booking> findByInvoice(Invoice invoice) {
		return Booking.all().filter("invoice", invoice).fetch();
	}

	public static int countBookingsByCity(City city, Date start, Date end) {
		int bookings = Booking.all().filter("city", city)
    		.filter("checkinDate>", start).filter("checkinDate<", end).count();
		return bookings;
	}
	

}
