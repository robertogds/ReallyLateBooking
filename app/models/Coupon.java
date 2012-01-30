package models;

import java.util.Calendar;
import java.util.Date;

import models.exceptions.InvalidCouponException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import play.Logger;
import play.data.validation.Validation;

import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;

@Table("coupons")
public class Coupon extends Model{
	private static final int EXPIRATION_DEFAULT = 100;
	public static final int CREDITS_FIRST_DEFAULT = 12;
	public static final int CREDITS_REFERER_DEFAULT = 12;
	public static final String CREDITS_REFERER_TYPE = "REFERER";
	
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	
	public String key;
	public int credits;
	@DateTime
    public Date created;
	@DateTime
    public Date expire;
	
	public String type;
	
	public Coupon() {
		super();
	}
	public Coupon(String key, int credits, String type) {
		super();
		this.expire = this.getExpirationDateByMonths(EXPIRATION_DEFAULT);
		this.created = Calendar.getInstance().getTime();
		this.credits = credits;
		this.type = type;
		this.key = key;
	}

	public static Query<Coupon> all() {
    	return Model.all(Coupon.class);
    }
	
    public static Coupon findByKey(String key){
    	return Coupon.all().filter("key", key).order("-created").get();
    }
    
    public static Coupon findById(Long id) {
        return all().filter("id", id).get();
    }
	
	public String toString() {
		return "key: " + this.key + " credits: " +  this.credits + " expire: " + this.expire;
	}
	
	private Date getExpirationDateByMonths(int months){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}

	/**
	 * returns true if the expiration date is in the future
	 * */
	public boolean isValid() {
		Calendar now = Calendar.getInstance();
		Calendar expire = Calendar.getInstance();
		expire.setTime(this.expire);
		return now.before(expire);
	}
	
	/**
	 * Returns a new MyCoupon instance for the given user if:
	 * 	- Coupon is valid (expiration date)
	 *  - Its not a referer coupon 
	 *  - its a referer coupon but user has never used another referer
	 * If coupon is REFERER type, then also adds the coupon to the referer.
	 **/
	public MyCoupon createMyCoupon(User user) throws InvalidCouponException {
		if (isValid() && (StringUtils.isBlank(user.referer) || !this.type.equals(CREDITS_REFERER_TYPE))){
			MyCoupon newCoupon = new MyCoupon(this, user);
			newCoupon.insert();
			if (this.type.equals(CREDITS_REFERER_TYPE)){
				newCoupon.createRefererCoupon(user);
			}
			return newCoupon;
		}
		else{
			throw new InvalidCouponException();
		}
	}
}
