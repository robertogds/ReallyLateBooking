package helper.dto;

import java.security.InvalidParameterException;
import java.util.Date;

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

public class BookingDTO {

	public Long dealId;
	public Long UserId;
    public Long id;
    public Integer nights;
    public Integer rooms;
    public String creditCard;
    public String creditCardName;
    public String creditCardExpiry;
    public int creditCardCVC;
    public String creditCardType;
    public String code;
    
    
	public BookingDTO(Booking booking) {
		validateBooking(booking);
		this.id= booking.id;
		this.dealId = booking.deal.id;
		this.UserId = booking.user.id;
		this.nights = booking.nights;
		this.rooms = booking.rooms;
		this.creditCard = booking.creditCard;
		this.creditCardName = booking.creditCardName;
		this.creditCardExpiry = booking.creditCardExpiry;
		this.creditCardCVC = booking.creditCardCVC;
		this.creditCardType = booking.creditCardType;
		this.code = booking.code;
	}
    
	private void validateBooking(Booking booking) {
		if (booking == null){
			throw new InvalidParameterException("Object booking cannot be null");
		}
	}
    
	public Booking toBooking(){
		Deal deal = new Deal(dealId);
		User user = new User(UserId);
		Booking booking = new Booking(deal, user);
		booking.nights = this.nights;
		booking.rooms =  this.rooms;
		booking.creditCard = this.creditCard;
		booking.creditCardName = this.creditCardName;
		booking.creditCardExpiry = this.creditCardExpiry;
		booking.creditCardCVC = this.creditCardCVC;
		booking.creditCardType = this.creditCardType;
		booking.code = this.code;
		return booking;
	}
    
}


