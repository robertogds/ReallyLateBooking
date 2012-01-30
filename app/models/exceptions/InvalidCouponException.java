package models.exceptions;

public class InvalidCouponException extends Exception {

	public InvalidCouponException() {
		super();
	}

	public InvalidCouponException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCouponException(String message) {
		super(message);
	}

	public InvalidCouponException(Throwable cause) {
		super(cause);
	}
	
}
