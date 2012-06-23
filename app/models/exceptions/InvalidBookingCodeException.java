package models.exceptions;

public class InvalidBookingCodeException extends Exception {

	public InvalidBookingCodeException() {
		super();
	}

	public InvalidBookingCodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidBookingCodeException(String message) {
		super(message);
	}

	public InvalidBookingCodeException(Throwable cause) {
		super(cause);
	}
	
}
