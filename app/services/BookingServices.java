package services;

import helper.hotusa.HotUsaApiHelper;
import models.Booking;
import models.Deal;
import models.exceptions.InvalidBookingCodeException;
import play.Logger;
import play.data.validation.Validation;
import play.i18n.Messages;

public final class BookingServices {
	
	public static void doCompleteReservation(Booking booking, Validation validation) throws InvalidBookingCodeException{
		if ((booking.deal.isHotUsa != null && booking.deal.isHotUsa) && (booking.deal.isFake == null||!booking.deal.isFake )){ 
			//If we are booking for more than one nights we need to refresh de lin codes 
			if (booking.nights > 1){
				HotUsaApiHelper.refreshAvailability(booking);
			}
			String localizador = HotUsaApiHelper.reservation(booking);
			if (localizador != null){
				booking.saveUnconfirmedBooking(localizador);
			}
			else{
				validation.addError("rooms", Messages.get("booking.validation.problem"));
				booking.pending = Boolean.TRUE;
				booking.update();
				throw new InvalidBookingCodeException("Localizador from hotusa is null");
			}
		}
		else{
			Deal.updateDealRooms(booking.deal.id, booking.rooms, booking.nights);
		}
	}
}
