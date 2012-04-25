package models.dto;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import models.Booking;
import models.Deal;
import models.User;
import play.Logger;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.i18n.Lang;
import siena.Generator;
import siena.Id;
import siena.Index;
import java.lang.reflect.Modifier;

public class BookingDTO {
	
	public Long dealId;
	public Long userId;
	@Expose
    public Long id;
	@Expose
    public Integer nights;
	@Expose
    public Integer rooms;
    public String creditCard;
    public String creditCardName;
    public String creditCardExpiry;
    public String creditCardCVC;
    public String creditCardType;
    @Expose
	public Integer salePriceCents;
    @Expose
	public Integer priceCents;
    @Expose
	public Integer priceDay2;
    @Expose
	public Integer priceDay3;
    @Expose
	public Integer priceDay4;
    @Expose
	public Integer priceDay5;
    @Expose
    public String code;
    @Expose
    public String hotelName;
    @Expose
    public String checkinDate;
    @Expose
    public String bookingForFirstName;
    @Expose
	public String bookingForLastName;
    @Expose
	public String bookingForEmail;
    
    
	public BookingDTO(Booking booking) {
		validateBooking(booking);
		this.id= booking.id;
		this.dealId = booking.deal.id;
		this.userId = booking.user.id;
		this.nights = booking.nights;
		this.rooms = booking.rooms;
		this.creditCard = booking.creditCard;
		this.creditCardName = booking.creditCardName;
		this.creditCardExpiry = booking.creditCardExpiry;
		this.creditCardCVC = booking.creditCardCVC;
		this.creditCardType = booking.creditCardType;
		this.code = booking.code;
		this.salePriceCents = booking.totalSalePrice;
		this.priceCents = booking.priceCents;
		this.priceDay2 = booking.priceDay2;
		this.priceDay3 = booking.priceDay3;
		this.priceDay4 = booking.priceDay4;
		this.priceDay5 = booking.priceDay5;
		this.hotelName = booking.hotelName;
		this.bookingForEmail = booking.bookingForEmail;
		this.bookingForFirstName = booking.bookingForFirstName;
		this.bookingForLastName = booking.bookingForLastName;
		Logger.debug("Formating booking date with locale: " + Lang.getLocale());
		this.checkinDate = booking.checkinDate!= null ? 
					DateFormat.getDateInstance(DateFormat.MEDIUM, Lang.getLocale()).format(booking.checkinDate):
					"";
	}
    
	public BookingDTO() {
	}

	private void validateBooking(Booking booking) {
		if (booking == null){
			throw new InvalidParameterException("Object booking cannot be null");
		}
	}

	
	public Booking toBooking(){
		Deal deal = Deal.findById(dealId);
		User user = new User(userId);
		Booking booking = new Booking(deal, user);
		booking.company = deal.company;
		booking.nights = this.nights;
		booking.rooms =  this.rooms;
		booking.creditCard = this.creditCard;
		booking.creditCardName = this.creditCardName;
		booking.creditCardExpiry = this.creditCardExpiry;
		booking.creditCardCVC = this.creditCardCVC;
		booking.creditCardType = this.creditCardType;
		booking.code = this.code;
		booking.priceCents = this.priceCents;
		booking.salePriceCents = this.salePriceCents;
		booking.priceDay2 = this.priceDay2;
		booking.priceDay3 = this.priceDay3;
		booking.priceDay4 = this.priceDay4;
		booking.priceDay5 = this.priceDay5;
		booking.hotelName = this.hotelName;
		booking.bookingForEmail = this.bookingForEmail;
		booking.bookingForFirstName = this.bookingForFirstName;
		booking.bookingForLastName = this.bookingForLastName;
		return booking;
	}
    
}


