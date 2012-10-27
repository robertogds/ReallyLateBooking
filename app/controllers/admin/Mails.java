package controllers.admin;

import helper.AffiliateHelper;
import models.Booking;
import models.Deal;
import models.InfoText;
import models.MyCoupon;
import models.User;
import play.i18n.Lang;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import controllers.Check;
import controllers.Secure;
import controllers.Security;


@Check(Security.ADMIN_ROLE)
@With(Secure.class)
public class Mails extends Controller {

	public static void list(){
		render();
	}
	
	public static void welcome(String lang){
		User user= User.findById(Long.valueOf(session.get("userId")));
		Lang.set(lang);
		renderTemplate("Mails/welcome.html", user);
	}
	
	public static void rememberPass(String lang){
		User user= User.findById(Long.valueOf(session.get("userId")));
		Lang.set(lang);
		renderTemplate("Mails/lostPassword.html", user);
	}
	
	public static void bookingSurvey(String lang){
		User user= User.findById(Long.valueOf(session.get("userId")));
		Lang.set(lang);
		renderTemplate("Mails/bookingSurvey.txt", user);
	}
	
	public static void rememberPrice(String lang){
		User owner = User.findById(Long.valueOf(session.get("userId")));
		Lang.set(lang);
		String text = InfoText.findByKeyAndLocale("PRICE_EMAIL", Lang.get()).content;

		renderTemplate("Mails/rememberPrice.html", owner, text);
	}
	
	public static void friendFirstBooking(String lang){
		User friend= User.findById(Long.valueOf(session.get("userId")));
		User referer = friend;
		MyCoupon coupon = MyCoupon.all().get();
		Lang.set(lang);
		renderTemplate("Mails/friendFirstBooking.html", friend, referer, coupon);
	}
	
	public static void friendRegistered(String lang){
		User friend= User.findById(Long.valueOf(session.get("userId")));
		User referer = friend;
		MyCoupon coupon = MyCoupon.all().get();
		Lang.set(lang);
		renderTemplate("Mails/friendRegistered.html", friend, referer, coupon);
	}
	
	
	public static void hotelBookingConfirmation(String lang){
		Booking booking = Booking.all().get();
		Lang.set(lang);
		renderTemplate("Mails/hotelBookingConfirmation.html", booking);
	}
	
	public static void userBookingConfirmation(String lang){
		Booking booking = Booking.all().get();
		Deal deal = Deal.findById(booking.deal.id);
		Lang.set(lang);
		renderTemplate("Mails/userBookingConfirmation.html", booking, deal);
	}
	
}

