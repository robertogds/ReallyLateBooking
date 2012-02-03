package controllers;

import models.InfoText;
import models.Coupon;
import models.MyCoupon;
import models.User;
import models.exceptions.InvalidCouponException;
import play.Logger;
import play.data.validation.Required;
import play.i18n.Lang;
import play.mvc.Before;
import play.mvc.Controller;

public class Coupons extends Controller {
	
	@Before
    static void checkConnected() {
		Security.checkConnected();
    }
	
	/**
	 * Validates a coupon from its key and assign coupon credits to the user.
	 * If its a referal creates a coupon for the referal friend in an inactive state
	 * Puts the couponReferalId in the params map so the coupon can be active when the booking
	 * was finished.
	 * Return to the given returnUrl
	 * */
	public static void validate(@Required String key, String returnUrl){
		User user= User.findById(Long.valueOf(session.get("userId")));
		MyCoupon myCoupon = MyCoupon.findByKeyAndUser(key, user);
		if (myCoupon == null){
			Coupon coupon = Coupon.findByKey(key);
			if (coupon == null){
				validation.addError("key", "El código no corresponde a ningún cupón válido");
				saveErrors();
			}
			else{
				useCoupon(coupon, user);
			}
		}
		else{
			useMyCoupon(myCoupon);
		}
		redirect(returnUrl);
	}
	
	/**
	 * Uses a coupon giving its credits to the user
	 * */
	public static void useCoupon(Coupon coupon, User user){
		MyCoupon myCoupon;
		try {
			myCoupon = coupon.createMyCoupon(user);
			useMyCoupon(myCoupon);
		} catch (InvalidCouponException e) {
			validation.addError("key", "Este cupón ya no es válido o no es acumulable");
			saveErrors();
		}
	}
	
	private static void useMyCoupon(MyCoupon coupon){
		try {
			coupon.use();
			flash.put("success", "El cupón es válido y se te han aplicado los créditos de descuento.");
		} catch (InvalidCouponException e) {
			validation.addError("key", "Ya has usado este cupón o ha caducado");
			saveErrors();
		}
	}
	
	private static void saveErrors(){
		params.flash(); // add http parameters to the flash scope
	    validation.keep(); // keep the errors for the next request
	}
}
