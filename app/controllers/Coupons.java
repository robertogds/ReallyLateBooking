package controllers;

import helper.JsonHelper;

import java.util.ArrayList;
import java.util.List;

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
import play.Logger;
import play.data.validation.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;

@With({I18n.class, LogExceptions.class, Analytics.class})
public class Coupons extends Controller {
	
	@Before(only = {"validate","validateAjax"})
    static void checkConnected() {
		Security.checkConnected();
    }
	
	@Before(unless = {"validate","validateAjax","create"})
	static void checkSignature(){
		Boolean correct = ApiSecurer.checkApiSignature(request);
		if (!correct){
			Logger.debug("Invalid signature ");
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
			validateAndSave(Long.valueOf(session.get("userId")), key);
		} catch (InvalidCouponException e) {
			validation.addError("key", e.getMessage());
		}
		redirect(returnUrl);
	}
	
	public static void validateAjax(@Required String key) {
		Logger.debug("Create Coupon from code " + key);	
		MyCoupon myCoupon;
		try {
			myCoupon = validateAndSave(Long.valueOf(session.get("userId")), key);
			renderCouponCreated(myCoupon);
		} catch (InvalidCouponException e) {
			validation.addError("key", e.getMessage());
			renderCouponError(e.getMessage());
		}
	}
	
	/*** Json API methods ****/
	public static void listByUser(Long userId){
		User user = User.findById(userId);
		List<MyCoupon> couponList = MyCoupon.findActiveByUser(user);
		List<MyCouponDTO> couponsDtos = new ArrayList<MyCouponDTO>();
		for (MyCoupon coupon: couponList){
			couponsDtos.add(new MyCouponDTO(coupon));
		}
		Logger.debug("Coupons for user: " + userId + " are : " + couponList.size());
		renderJSON(couponsDtos);
	}
	
	public static void create(String json) {
		String body = json != null ? json : params.get("body");
		Logger.debug("Create Coupon %s", body);	
		if (body != null){
			MyCoupon myCoupon;
			try {
				myCoupon = new Gson().fromJson(body, MyCoupon.class);
				try {
					myCoupon = validateAndSave(myCoupon.user.id, myCoupon.key);
					renderCouponCreated(myCoupon);
				} catch (InvalidCouponException e) {
					renderCouponError(e.getMessage());
				}
			} catch (JsonParseException e) {
				Logger.error("Error parsing coupon json", e);
				renderCouponError("Error interno, no hemos podido validar tu cupón, contacta con soporte@reallylatebooking.com");
			} 
		}
		renderCouponError("Error interno, no hemos podido validar tu cupón, contacta con soporte@reallylatebooking.com");
	}
	
	
	private static MyCoupon validateAndSave(Long userId, String key) throws InvalidCouponException{
		Logger.debug("Validating Coupon with key %s for user with id %s", key, userId);	
		User user= User.findById(userId);
		MyCoupon myCoupon = MyCoupon.findByKeyAndUser(key, user);
		if (myCoupon == null){
			Coupon coupon = Coupon.findByKey(key);
			if (coupon == null){
				Logger.debug("Couldn't find Coupon with key %s", key);
				throw new InvalidCouponException("Codigo de cupón no encontrado");
			}
			else{
				myCoupon = coupon.createMyCoupon(user);
			}
		}
		else{
			Logger.debug("Can't use a MyCoupon twice: for user %s with key %s", user.email, key);
			throw new InvalidCouponException("Ya no eres un user nuevo");
		}
		return myCoupon;
	}
	
	
	private static void renderCouponCreated(MyCoupon myCoupon){
		Logger.debug("Valid Coupon %s", myCoupon.key);
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
