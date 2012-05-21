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
	
	@Before(unless = {"validate","validateAjax"})
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
		validateAndSave(Long.valueOf(session.get("userId")), key);
		redirect(returnUrl);
	}
	
	public static void validateAjax(@Required String key) {
		Logger.debug("Create Coupon from code " + key);	
		MyCoupon myCoupon = validateAndSave(Long.valueOf(session.get("userId")), key);
		if (!validation.hasErrors()){ 
			renderCouponCreated(myCoupon);
		}
		else{
			renderCouponError();
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
		Logger.debug("Create Coupon " + body);	
		if (json != null){
			MyCoupon myCoupon;
			try {
				myCoupon = new Gson().fromJson(json, MyCoupon.class);
				myCoupon = validateAndSave(myCoupon.user.id, myCoupon.key);
				if (!validation.hasErrors()){ 
					renderCouponCreated(myCoupon);
				}
			} catch (JsonParseException e) {
				Logger.error("Error parsing coupon json", e);
			} finally{
				renderCouponError();
			}
		}
		renderCouponError();
	}
	
	
	private static MyCoupon validateAndSave(Long userId, String key){
		User user= User.findById(userId);
		MyCoupon myCoupon = MyCoupon.findByKeyAndUser(key, user);
		if (myCoupon == null){
			Coupon coupon = Coupon.findByKey(key);
			if (coupon == null){
				Logger.debug("Couldn't find Coupon with key %s", key);
				validation.addError("key",  Messages.get("coupon.create.error"));
			}
			else{
				myCoupon = createMyCoupon(coupon, user);
			}
		}
		else{
			Logger.debug("Couldn't find MyCoupon for user %s with key %s", user.email, key);
			validation.addError("key",  Messages.get("coupon.create.error"));
		}
		return myCoupon;
	}
	
	
	/**
	 * Use a code to get the user coupon and the referer if necessary 
	 * */
	private static MyCoupon createMyCoupon(Coupon coupon, User user){
		try {
			return coupon.createMyCoupon(user);
		} catch (InvalidCouponException e) {
			validation.addError("key",  Messages.get("coupon.create.error"));
			return null;
		}
	}
	
	private static void renderCouponCreated(MyCoupon myCoupon){
		Logger.debug("Valid Coupon %s", myCoupon.key);
		String response = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
				new CouponStatusMessage(Http.StatusCode.CREATED, "CREATED", 
						Messages.get("coupon.create.correct"), new MyCouponDTO(myCoupon)));
		renderJSON(response);
	}
	
	private static void renderCouponError(){
		String messageJson = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
				new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", Messages.get("coupon.create.error")));
		renderJSON(messageJson);
	}
}
