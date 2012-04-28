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
import models.City;
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
   private static final String PRECIOS_MAIL = "precios@reallylatebooking.com";
   private static final String ALTAS_MAIL = "altas@reallylatebooking.com";
   private static final String RESERVAS_MAIL = "reservas@reallylatebooking.com";
   private static final String HOLA_MAIL = "RLB <hola@reallylatebooking.com>";
   
   public static void welcome(User user) {
	   	  Message message = new Message();
	   	  message.setSubject(Messages.get("mail.welcome.subject") + " " + user.firstName);
	   	  message.setSender(HOLA_MAIL);
	   	  message.setTo(user.email);
	   	  message.setBcc(ALTAS_MAIL); //easy way to know when a new user registers. delete after some weeks
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
	   	  message.setSender(HOLA_MAIL);
	   	  message.setTo(user.email);
	   	  String template = "Mails/freeNightMadrid";
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("user", user);
	   	  send(message, template, params);
	   	  //set language to client original language
	   	  Lang.set(lang);
   }

   public static void lostPassword(User user) {
	      Message message = new Message();
	   	  message.setSubject(Messages.get("mail.remember.subject") + " "  + user.firstName);
	   	  message.setSender(HOLA_MAIL);
	   	  message.setBcc(ALTAS_MAIL);
	   	  message.setTo(user.email);
	   	  String template = "Mails/lostPassword";
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("user", user);
	   	  send(message, template, params);
   }
   
   
   public static void userBookingConfirmation(Booking booking) {
	   if (StringUtils.isNotBlank(booking.bookingForEmail)){
		   userBookingConfirmationForFriend(booking);
	   }
	   else{
	   	  Message message = new Message();
	   	  message.setSubject(Messages.get("mail.bookinguser.subject") + " "  + booking.deal.hotelName);
	   	  message.setSender(HOLA_MAIL);
	   	  String template = "Mails/userBookingConfirmation";
	   	  message.setTo(booking.user.email);
	   	  message.setBcc(RESERVAS_MAIL);
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("user", booking.user);
	   	  params.put("booking", booking);
	   	  Deal deal = Deal.findById(booking.deal.id);
	   	  params.put("deal", deal);
	   	  Logger.debug("Message to: " + message.getHtmlBody() + " to: " + message.getTo());
	   	  send(message, template, params); 
	   }

   }
   
   private static void userBookingConfirmationForFriend(Booking booking) {
	   	  Message message = new Message();
	   	  message.setSubject(Messages.get("mail.bookinguser.subject") + " "  + booking.deal.hotelName);
	   	  message.setSender(HOLA_MAIL);
	   	  String template = "Mails/userBookingConfirmationForFriend";
	   	  User user = booking.user;
	   	  message.setTo(booking.bookingForEmail);
	   	  message.setCc(booking.user.email);
	   	  message.setBcc(RESERVAS_MAIL);
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("user", user);
	   	  params.put("booking", booking);
	   	  Deal deal = Deal.findById(booking.deal.id);
	   	  params.put("deal", deal);
	   	  send(message, template, params);
   }
   
   public static void hotelBookingConfirmation(Booking booking) {
	   String lang = Lang.get(); 
	   	  //We want hotels email to be rendered in Spanish
	   	  Lang.set("es");
	   if (StringUtils.isNotBlank(booking.bookingForEmail)){
		   hotelBookingConfirmationForFriend(booking);
	   }
	   else{
	   	  Message message = new Message();
	   	  message.setSubject(Messages.get("mail.bookinghotel.subject") + " "  + booking.deal.hotelName);
	   	  message.setSender(HOLA_MAIL);
	   	  message.setTo(booking.deal.contactEmail);
	   	  message.setBcc(RESERVAS_MAIL); //easy way to know when a new booking is made
	   	  String template = "Mails/hotelBookingConfirmation";
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("user", booking.user);
	   	  params.put("booking", booking);
	   	  
	   	  send(message, template, params);
	   	 
	   }
	   //set language to client original language
	   Lang.set(lang);
   }
   
   private static void hotelBookingConfirmationForFriend(Booking booking) {
	  Message message = new Message();
   	  message.setSubject(Messages.get("mail.bookinghotel.subject") + " "  + booking.deal.hotelName);
   	  message.setSender(HOLA_MAIL);
   	  message.setTo(booking.deal.contactEmail);
   	  message.setBcc(RESERVAS_MAIL); //easy way to know when a new booking is made
   	  String template = "Mails/hotelBookingConfirmationForFriend";
   	  Map<String, Object> params = new HashMap<String, Object>();
   	  params.put("user", booking.user);
   	  params.put("booking", booking);
   	  
   	  send(message, template, params);
   }


   public static void ownerUpdatedDeal(Deal deal) {
	      String lang = Lang.get(); 
	   	  //We want hotels email to be rendered in Spanish
	   	  Lang.set("es");
	   	  
	   	  City city = City.findById(deal.city.id);
	   	  Message message = new Message();
	   	  message.setSubject("Precios de hoy para el  "  + deal.hotelName + " de "+ city.name);
	   	  message.setSender(HOLA_MAIL);
	   	  message.setTo(PRECIOS_MAIL);
	   	  String template = "Mails/ownerUpdatedDeal";
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("deal", deal);
	      params.put("city", city);
	   	  
	   	  send(message, template, params);
	   	  //set language to client original language
	   	  Lang.set(lang);
    }
   
   	public static void hotusaRisePrices(Deal deal) {
	      String lang = Lang.get(); 
	   	  //We want hotels email to be rendered in Spanish
	   	  Lang.set("es");
	   	  City city = City.findById(deal.city.id);
	   	  Message message = new Message();
	   	  message.setSubject("Hotusa ha subido el precio del "  + deal.hotelName + " de "+ city.name);
	   	  message.setSender(HOLA_MAIL);
	   	  message.setTo(PRECIOS_MAIL);
	   	  String template = "Mails/ownerUpdatedDeal";
	   	  Map<String, Object> params = new HashMap<String, Object>();
	   	  params.put("deal", deal);
	      params.put("city", city);
	   	  
	   	  send(message, template, params);
	   	  //set language to client original language
	   	  Lang.set(lang);
    }

	public static void contactForm(String email, String name, String text) {
		Message message = new Message();
	 	message.setSubject("Email de contacto desde la web de " + email);
	 	message.setSender(HOLA_MAIL);
	 	message.setTo(HOLA_MAIL);
	 	String template = "Mails/contactForm";
	 	Map<String, Object> params = new HashMap<String, Object>();
	 	params.put("text", text);
	    params.put("email", email);
	    params.put("name", name);
	 	send(message, template, params);
	}
   
	public static void hotelForm(String email, String name, String hotelName,
			String phone, String text) {
		Message message = new Message();
	 	message.setSubject("Uno hotel interesado desde la web: " + hotelName);
	 	message.setSender(HOLA_MAIL);
	 	message.setTo(HOLA_MAIL);
	 	String template = "Mails/hotelForm";
	 	Map<String, Object> params = new HashMap<String, Object>();
	 	params.put("text", text);
	    params.put("email", email);
	    params.put("name", name);
	    params.put("hotelName", hotelName);
	    params.put("phone", phone);
	 	send(message, template, params);
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
    	   Logger.info("Template html email not found ", e);
       }

       try {
           Template templateText = TemplateLoader.load(template + ".txt");
           bodyText = templateText.render(params);
           message.setTextBody(bodyText);
       } catch (TemplateNotFoundException e) {
    	   Logger.error("Template text email not found ", e);
       }
       
       if (StringUtils.isNotEmpty(bodyHtml) || StringUtils.isNotEmpty(bodyText)){
    	try {
    		Logger.debug("Sending email...", bodyHtml);
   			getMailService().send(message);
   		} catch (IOException e) {
   			Logger.error("Error Sending email", e);
   		} catch (ApiDeadlineExceededException e){
   			Logger.error("Error Sending email took too long. usually the mail is sent anyway", e);
   		}
       }
	   
   }


}

