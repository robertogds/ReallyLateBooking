package helper.dto;

import models.Booking;

public class BookingStatusMessage extends StatusMessage {
	
	public BookingDTO content;
	
	public BookingStatusMessage(int status, String message, String detail, Booking content) {
		super(status, message, detail);
		this.content = new BookingDTO(content);
	}

}
