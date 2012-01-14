package models.dto;

import com.google.gson.annotations.Expose;

import models.Booking;

public class BookingStatusMessage extends StatusMessage {
	
	@Expose
	public BookingDTO content;
	
	public BookingStatusMessage(int status, String message, String detail, Booking content) {
		super(status, message, detail);
		this.content = new BookingDTO(content);
	}

}
