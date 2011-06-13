package helper.dto;

import java.security.InvalidParameterException;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.Booking;
import models.Deal;
import models.User;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Index;
import java.lang.reflect.Modifier;

public class BookingDTO {
	
	public  Long dealId;
	public  Long userId;
    public Long id;
    public Integer nights;
    public Integer rooms;
    public  String creditCard;
    public  String creditCardName;
    public  String creditCardExpiry;
    public  Integer creditCardCVC;
    public  String creditCardType;
	public Integer salePriceCents;
	public Integer priceCents;
    public String code;
    public String hotelName;
    
    
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
		this.salePriceCents = booking.salePriceCents;
		this.priceCents = booking.priceCents;
		this.hotelName = booking.hotelName;
	}
    
	public BookingDTO() {
	}

	private void validateBooking(Booking booking) {
		if (booking == null){
			throw new InvalidParameterException("Object booking cannot be null");
		}
	}
    
	public Booking toBooking(){
		Deal deal = new Deal(dealId);
		User user = new User(userId);
		Booking booking = new Booking(deal, user);
		booking.nights = this.nights;
		booking.rooms =  this.rooms;
		booking.creditCard = this.creditCard;
		booking.creditCardName = this.creditCardName;
		booking.creditCardExpiry = this.creditCardExpiry;
		booking.creditCardCVC = this.creditCardCVC;
		booking.creditCardType = this.creditCardType;
		booking.code = this.code;
		booking.priceCents = this.priceCents;
		booking.salePriceCents = this.priceCents;
		booking.hotelName = this.hotelName;
		return booking;
	}
    
}


