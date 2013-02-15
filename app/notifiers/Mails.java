package notifiers;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import models.Company;
import models.Deal;
import models.InfoText;
import models.MyCoupon;
import models.Partner;
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
	private static final String SOPORTE_MAIL = "RLB <soporte@reallylatebooking.com>";

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

	public static void bookingSurvey(User user) {
		if (StringUtils.isNotBlank(user.email)){
			String lang = Lang.get(); 
			//We want survey email to be rendered in correct lang
			Lang.set(StringUtils.isNotBlank(user.locale) ? user.locale : "es");
	
			Message message = new Message();
			message.setSubject(Messages.get("mail.booking.survey.title"));
			message.setSender(HOLA_MAIL);
			message.setBcc(SOPORTE_MAIL);
			message.setTo(user.email);
			String template = "Mails/bookingSurvey";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("user", user);
			send(message, template, params);
			Lang.set(lang);
		}
	}

	public static void userBookingConfirmation(Booking booking) {
		if (StringUtils.isNotBlank(booking.userEmail)){
			booking.user.get();
			Message message = new Message();
			message.setSubject(Messages.get("mail.bookinguser.subject") + " "  + booking.hotelName);
			message.setSender(HOLA_MAIL);
			String template = "Mails/userBookingConfirmation";
			message.setTo(booking.userEmail);
			if (booking.bookingForFriend){
				message.setCc(booking.user.email);
			}
			message.setBcc(RESERVAS_MAIL);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("booking", booking);
			Deal deal = Deal.findById(booking.deal.id);
			params.put("deal", deal);
			if (booking.partner != null) {
				Partner partner = Partner.findById(booking.partner.id);
				params.put("partner", partner);
			}
			Logger.debug("Message to: " + message.getHtmlBody() + " to: " + message.getTo());
			send(message, template, params); 
		}
	}


	public static void hotelBookingConfirmation(Booking booking) {
		String lang = Lang.get(); 
		Company company = Company.findById(booking.company.id);
		//We want hotels email to be rendered in correct lang
		Lang.set(StringUtils.isNotBlank(company.lang) ? company.lang : "en");

		Message message = new Message();
		message.setSubject(Messages.get("mail.bookinghotel.subject") + " "  + booking.hotelName);
		message.setSender(HOLA_MAIL);
		message.setTo(getCollectionEmails(booking.hotelEmail));
		message.setBcc(RESERVAS_MAIL); //easy way to know when a new booking is made
		String template = "Mails/hotelBookingConfirmation";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("booking", booking);
		if (booking.partner != null) {
			Partner partner = Partner.findById(booking.partner.id);
			params.put("partner", partner);
		}
		send(message, template, params);

		//set language to client original language
		Lang.set(lang);
	}

	public static void rememberPrice(User owner) {
		String lang = Lang.get(); 
		//We want hotels email to be rendered in correct lang
		Lang.set(StringUtils.isNotBlank(owner.locale) ? owner.locale : "en");
		String text = InfoText.findByKeyAndLocale("PRICE_EMAIL", Lang.get()).content;
		String emails = StringUtils.isBlank(owner.contactEmail) ? owner.email : owner.email + ";" + owner.contactEmail;

		Message message = new Message();
		message.setSubject(Messages.get("mail.owner.remember.subject"));
		message.setSender(HOLA_MAIL);
		message.setTo(getCollectionEmails(emails));
		String template = "Mails/rememberPrice";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("owner", owner);
		params.put("text", text);
		send(message, template, params);
		//set language to original language
		Lang.set(lang);
	}


	private static Collection<String> getCollectionEmails(String hotelEmail) {
		Collection<String> emails = Arrays.asList(hotelEmail.split(";"));
		return emails;
	}


	public static void friendRegistered(User referer, User friend, MyCoupon refererCoupon) {
		Message message = new Message();
		message.setSubject(Messages.get("mail.friend.registered", friend.firstName));
		message.setSender(HOLA_MAIL);
		String template = "Mails/friendRegistered";
		message.setTo(referer.email);
		message.setBcc(HOLA_MAIL);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("referer", referer);
		params.put("friend", friend);
		params.put("coupon", refererCoupon);
		send(message, template, params); 
	}

	public static void friendFirstBooking(User referer, User user, MyCoupon coupon) {
		Message message = new Message();
		message.setSubject(Messages.get("mail.friend.firstbooking", user.firstName));
		message.setSender(HOLA_MAIL);
		String template = "Mails/friendFirstBooking";
		message.setTo(referer.email);
		message.setBcc(HOLA_MAIL);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("referer", referer);
		params.put("friend", user);
		params.put("coupon", coupon);
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

	public static void hotusaRisePrices(Deal deal, Float netPrice) {
		String lang = Lang.get(); 
		//We want hotels email to be rendered in Spanish
		Lang.set("es");
		City city = City.findById(deal.city.id);
		Message message = new Message();
		message.setSubject("Hotusa sube a "+ netPrice + " el " + deal.hotelName + " de "+ city.name);
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

	public static void errorMail(String subject, String content) {
		Message message = new Message();
		message.setSubject(subject);
		message.setSender(HOLA_MAIL);
		message.setTo(SOPORTE_MAIL);
		String template = "Mails/errorMail";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("message", content);
		send(message, template, params);
	}

	public static void bookingErrorMail(Booking booking) {
		Message message = new Message();
		message.setSubject(Messages.get("mails.booking.error.subject", booking.hotelName));
		message.setSender(HOLA_MAIL);
		message.setCc(SOPORTE_MAIL);
		User user = User.findById(booking.user.id);
		if (user != null && user.email != null){
			message.setTo(user.email);
			String template = "Mails/bookingErrorMail";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("user", user);
			params.put("booking", booking);
			send(message, template, params);
		}
		else{
			Logger.warn("We can´t send the error email because user %s have not an email", booking.user.id);
		}
	}

	public static void contactForm(String email, String name, String text) {
		Message message = new Message();
		message.setSubject("Email de contacto desde el club web de " + email);
		message.setSender(HOLA_MAIL);
		message.setTo(SOPORTE_MAIL);
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
		message.setSubject("Un hotel interesado desde la web: " + hotelName);
		message.setSender(HOLA_MAIL);
		message.setTo(SOPORTE_MAIL);
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
			Logger.warn("Template html email not found ", e);
		}

		try {
			Template templateText = TemplateLoader.load(template + ".txt");
			bodyText = templateText.render(params);
			message.setTextBody(bodyText);
		} catch (TemplateNotFoundException e) {
			Logger.warn("Template text email not found ", e);
		}

		if (message.getTo() != null && (StringUtils.isNotEmpty(bodyHtml) || StringUtils.isNotEmpty(bodyText))){
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

