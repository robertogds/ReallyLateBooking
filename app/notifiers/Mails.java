package notifiers;

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
 
}

