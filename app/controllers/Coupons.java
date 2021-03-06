package controllers;

import helper.JsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import notifiers.Mails;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import controllers.oauth.ApiSecurer;

import models.Booking;
import models.City;
import models.Deal;
import models.InfoText;
import models.Coupon;
import models.MyCoupon;
import models.User;
import models.dto.BookingDTO;
import models.dto.BookingStatusMessage;
import models.dto.CouponStatusMessage;
import models.dto.MyCouponDTO;
import models.dto.StatusMessage;
import models.exceptions.InvalidCouponException;
import play.data.validation.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;

@With({I18n.class, LogExceptions.class})
public class Coupons extends Controller {
	
	private static final Logger log = Logger.getLogger(Coupons.class.getName());
	
	@Before(only = {"validate","validateAjax"})
    static void checkConnected() {
		Security.checkConnected();
    }
	
	@Before(unless = {"validate","validateAjax","create"})
	static void checkSignature(){
		Boolean correct = ApiSecurer.checkApiSignature(request);
		if (!correct){
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", "Invalid api signature. Contact soporte@reallylatebooking.com"));
			renderJSON(json);
		}
	}
	
	/**
	 * Validates a coupon from its key
	 * If its a referal creates a coupon for the referal friend in an inactive state
	 * Puts the couponReferalId in the params map so the coupon can be active when the booking
	 * was finished.
	 * Return to the given returnUrl
	 * */
	public static void validate(@Required String key, String returnUrl){
		try {
			Coupon.validateAndSave(Long.valueOf(session.get("userId")), key);
		} catch (InvalidCouponException e) {
			validation.addError("key", e.getMessage());
		}
		redirect(returnUrl);
	}
	
	public static void validateAjax(@Required String key) {
		MyCoupon myCoupon;
		try {
			myCoupon = Coupon.validateAndSave(Long.valueOf(session.get("userId")), key);
			renderCouponCreated(myCoupon);
		} catch (InvalidCouponException e) {
			validation.addError("key", e.getMessage());
			renderCouponError(e.getMessage());
		}
	}
	
	public static void couponsExpiringWarning(){
		//List<MyCoupon> counpons = MyCoupon.
	}
	
	/*** Json API methods ****/
	public static void listByUser(Long userId){
		User user = User.findById(userId);
		List<MyCoupon> couponList = MyCoupon.findActiveByUser(user);
		List<MyCouponDTO> couponsDtos = new ArrayList<MyCouponDTO>();
		for (MyCoupon coupon: couponList){
			couponsDtos.add(new MyCouponDTO(coupon));
		}
		renderJSON(couponsDtos);
	}
	
	public static void create(String json) {
		String body = json != null ? json : params.get("body");
		log.info("Create Coupon " + body);	
		if (body != null){
			MyCoupon myCoupon;
			try {
				myCoupon = new Gson().fromJson(body, MyCoupon.class);
				try {
					myCoupon = Coupon.validateAndSave(myCoupon.user.id, myCoupon.key);
					renderCouponCreated(myCoupon);
				} catch (InvalidCouponException e) {
					renderCouponError(e.getMessage());
				}
			} catch (JsonParseException e) {
				log.severe("Error parsing coupon json..." + e);
				renderCouponError(Messages.get("coupon.create.internalerror"));
			} 
		}
		renderCouponError(Messages.get("coupon.create.internalerror"));
	}
	
	
	private static void renderCouponCreated(MyCoupon myCoupon){
		String response = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
				new CouponStatusMessage(Http.StatusCode.CREATED, "CREATED", 
						Messages.get("coupon.create.correct"), new MyCouponDTO(myCoupon)));
		renderJSON(response);
	}
	
	private static void renderCouponError(String message){
		String messageJson = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
				new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", message));
		renderJSON(messageJson);
	}
}
