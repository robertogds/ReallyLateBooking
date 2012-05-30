package models;

import java.util.Calendar;
import java.util.Date;

import models.exceptions.InvalidCouponException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import play.Logger;
import play.Play;
import play.data.validation.Validation;

import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;

@Table("coupons")
public class Coupon extends Model{
	public static final String CREDITS_REFERER_TYPE = "REFERER";
	public static final String CREDITS_WELCOME_TYPE = "WELCOME";
	
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	
	public String key;
	public String title;
	public String type;
	public int credits;
	public int duration;
	@DateTime
    public Date created;
	@DateTime
    public Date expire;
	
	public Coupon() {
		super();
	}
	public Coupon(String key, int credits, String type) {
		super();
		this.expire = this.getExpirationDateByMonths(100);
		this.created = Calendar.getInstance().getTime();
		this.credits = credits;
		this.type = type;
		this.key = key;
	}

	public static Query<Coupon> all() {
    	return Model.all(Coupon.class);
    }
	
    public static Coupon findByKey(String key){
    	return Coupon.all().filter("key", key.toUpperCase()).order("-created").get();
    }
    
    public static Coupon findById(Long id) {
        return all().filter("id", id).get();
    }
	
	public String toString() {
		return "key: " + this.key + " credits: " +  this.credits + " expire: " + this.expire;
	}
	
	
	/**
	 * returns true if the expiration date is in the future
	 * and this user has not been referred before
	 * */
	public void validateForUser(User user) throws InvalidCouponException{
		user.get();
		this.validateOwnCoupon(user);
		this.validateReferalValid(user);
		this.validateExpiration();
		this.validateisNew(user);
	}
	
	/**
	 * Returns a new MyCoupon instance for the given user if:
	 * 	- Coupon is valid (expiration date)
	 *  - Its not a referer coupon 
	 *  - its a referer coupon but user has never used another referer
	 * If coupon is REFERER type, then also adds the coupon to the referer.
	 **/
	public MyCoupon createMyCoupon(User user) throws InvalidCouponException {
		this.validateForUser(user);
		MyCoupon newCoupon = new MyCoupon(this, user);
		newCoupon.insert();
		Logger.debug("## Coupon.createMyCoupon(user): Coupon %s is valid for user with refererId %s ", newCoupon.key, user.refererId);
		
		if (this.type.equals(CREDITS_REFERER_TYPE)){
				newCoupon.createRefererCoupon(user);
		}
		return newCoupon;
	}
	
	private void validateisNew(User user) throws InvalidCouponException {
		Logger.debug(" User is new: %s" , user.isNew);
		if (!user.isNew && this.onlyForNews()){
			throw new InvalidCouponException("Ya no eres un user nuevo");
		}
	}
	private void validateOwnCoupon(User user) throws InvalidCouponException{
		if (this.key.equalsIgnoreCase(user.refererId)){
			throw new InvalidCouponException("No puedes usar tu propio cupón");
		}
	}
	
	private Boolean onlyForNews(){
		return this.type != null && (this.type.equalsIgnoreCase(CREDITS_WELCOME_TYPE) || this.type.equalsIgnoreCase(CREDITS_REFERER_TYPE));
	}
	
	/**
	 * throws exception if this user has been referred before
	 * */
	public void validateReferalValid(User user) throws InvalidCouponException{
		if (this.type.equals(Coupon.CREDITS_REFERER_TYPE) && referalCouponUsed(user)){
			throw new InvalidCouponException("No puedes usar un cupón de amigo más de una vez");
		}
	}
	
	private void validateExpiration() throws InvalidCouponException {
		if (this.expired()){
			throw new InvalidCouponException("El cupón está caducado");
		}
	}
	
	/**
	 * returns true if the expiration date is in the past
	 * */
	public Boolean expired() {
		Calendar now = Calendar.getInstance();
		Calendar expire = Calendar.getInstance();
		expire.setTime(this.expire);
		//Logger.debug("EXPIRED: Now %s Expire: %s",now.getTime(), expire.getTime());
		return now.after(expire);
	}
	
	private Boolean referalCouponUsed(User user){
		return StringUtils.isNotBlank(user.referer);
	}
	
	private Date getExpirationDateByMonths(int months){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}
}
