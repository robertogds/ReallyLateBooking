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
import play.data.validation.Valid;
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
	
    @Index("user_index")
    public User user;
    
    @Index("company_index")
    public Company company;
    
    @Index("invoice_index")
    public Invoice invoice;
    
    @Index("city_index")
    public City city;
    
    @Index("deal_index")
    public Deal deal;
    @DateTime
    public Date checkinDate;
    @Required
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
    @Valid
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
	public Integer totalSalePrice;
	public Integer finalPrice;
	public Integer credits;
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
	public boolean breakfastIncluded;
	public boolean fromWeb;

    public Booking(Deal deal, User user) {
        this.deal = deal;
        this.user = user;
    }
    
	@Override
	public void insert() {
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
    	this.salePriceCents = this.deal.salePriceCents;
    	this.totalSalePrice =  this.getTotal();
    	this.credits = this.getUserCredits();
    	this.finalPrice = this.totalSalePrice - this.credits;
    	this.invoiced = Boolean.FALSE;
    	this.canceled = Boolean.FALSE;
    	this.breakfastIncluded = this.deal.breakfastIncluded == null ? false : this.deal.breakfastIncluded;
    	super.insert();
	}
	
	/*
	 * Returns the total credits used for the current booking
	 * If the user has more credits than the booking total price they cant be used.
	 * If its hotusa we cant use credits
	 * */
	private Integer getUserCredits(){
		if (this.deal.isHotUsa){
			return 0;
		}
		else{
			this.user.get();
			int credits = this.user.credits <= this.totalSalePrice ? this.user.credits : this.totalSalePrice;
	    	return credits;
		}
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
    	if (this.nights >= 2){
    		price += this.deal.priceDay2;
    	}
    	if (this.nights >= 3){
    		price += this.deal.priceDay3;
    	}
    	if (this.nights >= 4){
    		price += this.deal.priceDay4;
    	}
    	if (this.nights == 5){
    		price += this.deal.priceDay5;
    	}
    	Logger.debug("Total Sale price: " + price);

        return price;
    }
    

	public void validate() {
		Logger.debug("validate?");
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
			creditCardExpiryValid();
		}
	}

    public void creditCardValid(){
    	Logger.debug("Credit card number: " + creditCard + " creditCardName: "+ creditCardName + " creditCardType: "+creditCardType + " creditCardCVC: " + creditCardCVC);
    	if (!CreditCardHelper.validCC(this.creditCard)){
    			Logger.debug("Credit card number algotrithm failed");
    			Validation.addError("booking.creditCard", Messages.get("booking.validation.creditcard"), creditCard);
		}
	}
    
	private void creditCardExpiryValid() {
		 Logger.debug("creditCardExpiryValid?");
		 String[] parsers = new String[] {"M/yyyy", "M'/'yyyy"};
		 Date date ;
		 try {
			 date = DateUtils.parseDate(this.creditCardExpiry, parsers);
			 Logger.debug("Validating expiration date " + date.toString());
			 Validation.future("booking.creditCardExpiry", date, Calendar.getInstance().getTime()).message("booking.validation.creditcardexpiry");	
		 } catch (ParseException e) {
			Logger.error("Error parsing expiration date ", e);
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
