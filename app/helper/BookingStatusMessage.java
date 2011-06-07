package helper;

import models.Booking;

public class BookingStatusMessage extends StatusMessage {
	
	public Booking content;
	
	public BookingStatusMessage(int status, String message, String detail, Booking content) {
		super(status, message, detail);
		this.content = content;
	}

}
