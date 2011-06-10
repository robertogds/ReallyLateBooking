package notifiers;

import models.Booking;
import models.User;
import play.mvc.Mailer;

public class Mails extends Mailer {
	 
   public static void welcome(User user) {
      setSubject("Welcome %s", user.firstName);
      addRecipient(user.email);
      setFrom("Me <pablo@iipir.com>");
      send(user);
   }
   
   public static void validate(User user) {
	      setSubject("Welcome %s", user.firstName);
	      addRecipient(user.email);
	      setFrom("Me <pablo@iipir.com>");
	      send(user);
   }
 
   public static void lostPassword(User user, String newPassword) {
      setFrom("Robot <hola@reallylatebooking.com>");
      setSubject("Your password has been reset");
      addRecipient(user.email);
      send(user, newPassword);
   }
   
   public static void userBookingConfirmation(Booking booking) {
	      setSubject("Booking comfirmation at %s", booking.deal.hotelName);
	      addRecipient(booking.user.email);
	      setFrom("Me <pablo@iipir.com>");
	      send(booking.user, booking);
   }
   
   public static void hotelBookingConfirmation(Booking booking) {
	      setSubject("Booking comfirmation ats %s", booking.deal.hotelName);
	      addRecipient(booking.deal.contactEmail);
	      setFrom("Me <pablo@iipir.com>");
	      send(booking.user, booking);
   }
 
}

