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
	public static final String RESTEL = "RESTEL";
    
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	public String code;
    @Index("user_index")
    public User user;
    @Index("partner_index")
    public Partner partner;
    public String hotelName;
    public String hotelEmail;
    public String userFirstName;
	public String userLastName;
    public String userEmail;
	public String userPhone;
    @DateTime
    public Date checkinDate;
	@Required
    public Integer nights;
    public Integer rooms;
    public Integer finalPrice;
	public Integer credits;
    public Integer totalSalePrice;
	public Float netTotalSalePrice;
	public Float fee;
	public boolean payed;
	public boolean pending;
    public boolean bookingForFriend;
    public String bookingForFirstName;
	public String bookingForLastName;
	public String bookingForEmail;
	public String bookingForPhone;
    public boolean breakfastIncluded;
	public boolean fromWeb;
	public boolean isHotusa;
	public Boolean needConfirmation;
	public Boolean canceled;
	public Boolean invoiced;
	
    @Index("company_index")
    public Company company;
    @Index("invoice_index")
    public Invoice invoice;
    @Index("city_index")
    public City city;
    @Index("deal_index")
    public Deal deal;
	public String dealAddress;
	@MaxSize(10000)
	public String comment;
	public Integer salePriceCents;
	public Float netSalePriceCents;
	public Integer priceCents;
	public Integer priceDay2;
	public Integer priceDay3;
	public Integer priceDay4;
	public Integer priceDay5;
	public Float netPriceDay2;
	public Float netPriceDay3;
	public Float netPriceDay4;
	public Float netPriceDay5;
    public String creditCardType;
    public String creditCard;
    @MinSize(value=3)
    @MaxSize(value=70)
    public String creditCardName;
    @Valid
    public String creditCardExpiry;
    @MinSize(value=3)
    @MaxSize(value=4)
    public String creditCardCVC;
	public String paypalToken;
	public String paypalPayerId;
	public String transactionId;
	public String orderTime;
	public String amt;
	public String currencyCode;
	public String feeAmt;
	public String settleAmt;
	public String taxAmt;
	public String exchangeRate;
	public String paymentStatus;

	public boolean fromWhiteLabel;



	

    public Booking(Deal deal, User user) {
        this.deal = deal;
        this.user = user;
    }
    
    public Booking(Deal deal, User user, Integer nights) {
        this(deal, user);
        this.nights = nights;
    }
    
	@Override
	public void insert() {
		this.nights = this.getTotalNights();
    	this.checkinDate = DateHelper.getTodayDate();
    	this.code = RandomStringUtils.randomAlphanumeric(8);
    	this.deal = Deal.findById(this.deal.id); //fetch deal from datastore
    	//if is an old object from datastore without the boolean set
    	this.deal.isHotUsa = this.deal.isHotUsa != null ? this.deal.isHotUsa : Boolean.FALSE;
    	this.deal.isFake = this.deal.isFake != null ? this.deal.isFake : Boolean.FALSE;
    	this.city = City.findById(this.deal.city.id);
    	this.user = User.findById(this.user.id);
    	this.company = this.deal.company;
    	this.hotelName = this.deal.hotelName;
    	this.priceDay2 = this.deal.priceDay2;
    	this.priceDay3 = this.deal.priceDay3;
    	this.priceDay4 = this.deal.priceDay4;
    	this.priceDay5 = this.deal.priceDay5;
    	this.netPriceDay2 = this.deal.netPriceDay2;
    	this.netPriceDay3 = this.deal.netPriceDay3;
    	this.netPriceDay4 = this.deal.netPriceDay4;
    	this.netPriceDay5 = this.deal.netPriceDay5;
    	this.priceCents = this.calculateTotalPrice(); //save actual deal price
    	this.salePriceCents = this.deal.salePriceCents;
    	this.netSalePriceCents = this.deal.netSalePriceCents;
    	this.totalSalePrice =  this.calculateTotalSalePrice();
    	this.netTotalSalePrice =  this.calculateTotalNetSalePrice();
    	this.credits = this.getUserCredits();
    	this.finalPrice = this.totalSalePrice > this.credits ? this.totalSalePrice - this.credits : 0;
    	this.fee = this.totalSalePrice - this.netTotalSalePrice;
    	this.invoiced = Boolean.FALSE;
    	this.payed = Boolean.FALSE;
    	this.canceled = Boolean.FALSE; 
    	this.isHotusa = this.deal.isHotUsa;
    	this.breakfastIncluded = this.deal.breakfastIncluded == null ? false : this.deal.breakfastIncluded;
    	this.dealAddress = this.deal.address;
    	this.hotelEmail = this.deal.contactEmail;
    	this.bookingForFriend = (this.bookingForEmail != null && !this.user.email.equalsIgnoreCase(this.bookingForEmail))
    						|| (this.bookingForLastName != null && !this.user.lastName.equalsIgnoreCase(this.bookingForLastName));
    	this.userEmail = this.bookingForFriend ? this.bookingForEmail : this.user.email;
    	this.userFirstName = this.bookingForFriend ? this.bookingForFirstName :this.user.firstName;
    	this.userLastName =this.bookingForFriend ? this.bookingForLastName : this.user.lastName;
    	this.userPhone = this.bookingForFriend ? this.bookingForPhone : this.user.phone;
     	super.insert();
	}
	
	/*
	 * Returns the total credits used for the current booking
	 * If the user has more credits than the booking total price they cant all be used.
	 * */
	private Integer getUserCredits(){
		this.user.get();
		int credits = this.user.calculateTotalCreditsFromMyCoupons();
		credits = credits <= this.totalSalePrice ? credits : this.totalSalePrice;
	    return credits;
	}
	
	private Integer getTotalNights(){
		if (this.nights == null || this.nights < 1){
			return 1;
		}
		else{
			return this.nights;
		}
	}
    
    private Integer calculateTotalPrice() {
		return this.deal.priceCents * this.nights;
	}

	public static Booking findById(Long id) {
        return all().filter("id", id).get();
    }
    
    public static Query<Booking> all() {
    	return Model.all(Booking.class);
    }
    
    public static List<Booking> findByDeal(Deal deal){
    	return Booking.all().filter("deal", deal).filter("pending", Boolean.FALSE).filter("canceled", Boolean.FALSE).fetch();
    }
    
    public static List<Booking> findConfirmationRequiredDeals(){
    	return Booking.all().filter("needConfirmation", Boolean.TRUE).fetch();
    }
    
    public static List<Booking> findAllByUser(User user){
    	return Booking.all().filter("user", user).fetch();
    }
    
    public static List<Booking> findByUser(User user){
    	return Booking.all().filter("user", user).filter("canceled", Boolean.FALSE).filter("pending", Boolean.FALSE).fetch();
    }
   
    private Integer calculateTotalSalePrice() {
    	Integer price = this.deal.salePriceCents;
    	if (this.nights >= 2){
    		Logger.debug("Total price day 2: %s , total: %s", this.deal.priceDay2, price);
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
    
    private Float calculateTotalNetSalePrice() {
    	Float price = this.deal.netSalePriceCents;
    	if (this.nights >= 2){
    		price += this.deal.netPriceDay2;
    	}
    	if (this.nights >= 3){
    		price += this.deal.netPriceDay3;
    	}
    	if (this.nights >= 4){
    		price += this.deal.netPriceDay4;
    	}
    	if (this.nights == 5){
    		price += this.deal.netPriceDay5;
    	}
    	Logger.debug("Total Net Sale price: " + price);

        return price;
    }
    

	public void validateNoCreditCart() {
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
	}
	
	public void validate() {
		this.validateNoCreditCart();
		creditCardValid();
		creditCardExpiryValid();
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
		 String[] parsers = new String[] {"M/yy", "M'/'yy"};
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
		int bookings = Booking.all().filter("city", city).filter("canceled", Boolean.FALSE)
    		.filter("checkinDate>", start).filter("checkinDate<", end).count();
		return bookings;
	}
	
	public static int countBookingsByUser(User user, Date start, Date end) {
		int bookings = Booking.all().filter("user", user).filter("canceled", Boolean.FALSE)
    		.filter("checkinDate>", start).filter("checkinDate<", end).count();
		return bookings;
	}
	
	public static Collection<Booking> findAllBookingsByDate(Date start, Date end) {
		return Booking.all().filter("canceled", Boolean.FALSE)
			.filter("checkinDate>", start).filter("checkinDate<", end).fetch();
	}
	
	public static Collection<Booking> findAllNonPendingBookingsByDate(Date start, Date end) {
		return Booking.all().filter("canceled", Boolean.FALSE).filter("pending", Boolean.FALSE)
			.filter("checkinDate>", start).filter("checkinDate<", end).fetch();
	}
	
	public static Collection<Booking> findAllBookingsByDateAndCity(City city, Date start, Date end) {
		return Booking.all().filter("city", city).filter("canceled", Boolean.FALSE)
			.filter("checkinDate>", start).filter("checkinDate<", end).fetch();
	}
	
	public static Integer countAllBookingsByDateAndDeal(Deal deal, Date start, Date end) {
		return Booking.all().filter("deal", deal).filter("canceled", Boolean.FALSE).filter("pending", Boolean.FALSE)
			.filter("checkinDate>", start).filter("checkinDate<", end).count();
	}
	
	public static Integer findLastBookingsByDeal(Deal deal, int days) {
		Calendar now = Calendar.getInstance();
		Calendar before = Calendar.getInstance();
		before.add(Calendar.DAY_OF_YEAR, -days);
		return countAllBookingsByDateAndDeal(deal,  before.getTime(), now.getTime());
	}
	
	
	public static Collection<Booking> findAllNonPendindBookingsByDateAndCity(City city, Date start, Date end) {
		return Booking.all().filter("city", city).filter("canceled", Boolean.FALSE).filter("pending", Boolean.FALSE)
			.filter("checkinDate>", start).filter("checkinDate<", end).fetch();
	}

	public static Booking findByPaypalToken(String token) {
		 return all().filter("paypalToken", token).get();
	}

	public void saveUnconfirmedBooking(String localizador) {
		Logger.debug("Correct booking: " + localizador);
		this.code = localizador;
		this.needConfirmation = Boolean.TRUE;
		this.update();
	}

	public static Booking createBookingForWhiteLabel(Long dealId, User user,
			int nights, String partnerId) {
		Deal deal = new Deal();
		deal.id = dealId;
		Booking booking = new Booking(deal, user, nights);
		booking.bookingForEmail = user.email;
		booking.bookingForFirstName = user.firstName;
		booking.bookingForLastName = user.lastName;
		booking.bookingForPhone = user.phone;
		booking.rooms = 1; //we dont allow more rooms by now
		booking.fromWhiteLabel = true;
		Partner partner = Partner.findByPartnerId(partnerId);
		booking.partner = partner;
		return booking;
	}

	public static Collection<Booking> findAllBookingsByPartner(Partner partner) {
		return Booking.all().filter("partner", partner).order("checkinDate").fetch();
	}

	public static Booking findByIdAndUser(Long bookingId, User user) {
		if (user.isAdmin){
			return Booking.all().filter("id", bookingId).get();
		}
		else{
			return Booking.all().filter("id", bookingId).filter("user", user).get();
		}
	}

	public static Booking findPendingBooking(Deal deal, User user,
			Partner partner) {
		return Booking.all().filter("deal", deal).filter("pending", Boolean.TRUE)
			.filter("user", user).filter("partner", partner).get();
	}

}
