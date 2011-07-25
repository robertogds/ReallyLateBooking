package notifiers;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.apphosting.api.ApiProxy.ApiDeadlineExceededException;

import models.Booking;
import models.Deal;
import models.User;
import play.Logger;
import play.exceptions.MailException;
import play.exceptions.TemplateNotFoundException;
import play.exceptions.UnexpectedException;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.Mail;
import play.mvc.Mailer;
import play.templates.Template;
import play.templates.TemplateLoader;

public class Mails extends MailServiceFactory {
	 
   public static void welcome(User user) {
	   	  Message message = new Message();
	   	  message.setSubject(Messages.get("mail.welcome.subject") + " " + user.firstName);
	   	  message.setSender("RLB <hola@reallylatebooking.com>");
	   	  message.setTo(user.email);
	   	  message.setBcc("hola@reallylatebooking.com"); //easy way to know when a new user registers. delete after some weeks
	   	  String template = "Mails/welcome";
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("user", user);
	   	  send(message, template, params);
   }
   
   
   public static void freeNightMadrid(User user) {
	      String lang = Lang.get(); 
	   	  Lang.set("es");
	   	  Message message = new Message();
	   	  message.setSubject( user.firstName +", "+ Messages.get("mail.freenight.subject"));
	   	  message.setSender("RLB <hola@reallylatebooking.com>");
	   	  message.setTo(user.email);
	   	  String template = "Mails/freeNightMadrid";
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("user", user);
	   	  send(message, template, params);
	   	  //set language to client original language
	   	  Lang.set(lang);
   }

//   public static void validate(User user) {
//	      setSubject("Welcome %s", user.firstName);
//	      addRecipient(user.email);
//	      setFrom("Me <pablo@iipir.com>");
//	      send(user);
//   }
// 
   public static void lostPassword(User user) {
	      Message message = new Message();
	   	  message.setSubject(Messages.get("mail.remember.subject") + " "  + user.firstName);
	   	  message.setSender("RLB <hola@reallylatebooking.com>");
	   	  message.setTo(user.email);
	   	  String template = "Mails/lostPassword";
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("user", user);
	   	  send(message, template, params);
   }
   
   public static void userBookingConfirmation(Booking booking) {
	   	  Message message = new Message();
	   	  message.setSubject(Messages.get("mail.bookinguser.subject") + " "  + booking.deal.hotelName);
	   	  message.setSender("RLB <hola@reallylatebooking.com>");
	   	  message.setTo(booking.user.email);
	   	  String template = "Mails/userBookingConfirmation";
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("user", booking.user);
	   	  params.put("booking", booking);
	   	  send(message, template, params);
   }
   
   public static void hotelBookingConfirmation(Booking booking) {
	      String lang = Lang.get(); 
	   	  //We want hotels email to be rendered in Spanish
	   	  Lang.set("es");
	   	  
	   	  Message message = new Message();
	   	  message.setSubject(Messages.get("mail.bookinghotel.subject") + " "  + booking.deal.hotelName);
	   	  message.setSender("RLB <hola@reallylatebooking.com>");
	   	  message.setTo(booking.deal.contactEmail);
	   	  message.setBcc("hola@reallylatebooking.com"); //easy way to know when a new booking is made
	   	  String template = "Mails/hotelBookingConfirmation";
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("user", booking.user);
	   	  params.put("booking", booking);
	   	  
	   	  send(message, template, params);
	   	  //set language to client original language
	   	  Lang.set(lang);
   }
   
   public static void ownerUpdatedDeal(Deal deal) {
	      String lang = Lang.get(); 
	   	  //We want hotels email to be rendered in Spanish
	   	  Lang.set("es");
	   	  
	   	  Message message = new Message();
	   	  message.setSubject("Precios de hoy actualizados para el  "  + deal.hotelName);
	   	  message.setSender("RLB <hola@reallylatebooking.com>");
	   	  message.setTo("precios@reallylatebooking.com");
	   	  String template = "Mails/ownerUpdatedDeal";
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("deal", deal);
	   	  
	   	  send(message, template, params);
	   	  //set language to client original language
	   	  Lang.set(lang);
   }
   
   protected static void send(Message message, String template, Map<String, Object> params) {

	   // The rule is as follow: If we ask for text/plain, we don't care about the HTML
       // If we ask for HTML and there is a text/plain we add it as an alternative.
       // If contentType is not specified look at the template available:
       // - .txt only -> text/plain
       // else
       // - -> text/html
       String bodyHtml = null;
       String bodyText = "";
       try {
           Template templateHtml = TemplateLoader.load(template + ".html");
           bodyHtml = templateHtml.render(params);
           message.setHtmlBody(bodyHtml);
       } catch (TemplateNotFoundException e) {
    	   Logger.debug("Template html email not found ", e);
       }

       try {
           Template templateText = TemplateLoader.load(template + ".txt");
           bodyText = templateText.render(params);
           message.setTextBody(bodyText);
       } catch (TemplateNotFoundException e) {
    	   Logger.debug("Template text email not found ", e);
       }
       
       if (StringUtils.isNotEmpty(bodyHtml) || StringUtils.isNotEmpty(bodyText)){
    	Logger.debug("Sending email...");
    	try {
   			getMailService().send(message);
   		} catch (IOException e) {
   			Logger.error("Error Sending email", e);
   		} catch (ApiDeadlineExceededException e){
   			Logger.error("Error Sending email took too long. usually the mail is sent anyway", e);
   		}
       }
	   
   }
   
}

