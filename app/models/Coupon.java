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
import play.i18n.Messages;

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
    	return Coupon.all().filter("key", key.trim().toUpperCase()).order("-created").get();
    }
    
    public static Coupon findById(Long id) {
        return all().filter("id", id).get();
    }
	
	public String toString() {
		return "key: " + this.key + " credits: " +  this.credits + " expire: " + this.expire;
	}
	
	
	/**
	 * validates a coupon code for a user
	 * @param userId
	 * @param key
	 * @return
	 * @throws InvalidCouponException
	 */
	public static MyCoupon validateAndSave(Long userId, String key) throws InvalidCouponException{
		Logger.debug("Validating Coupon with key %s for user with id %s", key, userId);	
		User user= User.findById(userId);
		MyCoupon myCoupon = MyCoupon.findByKeyAndUser(key, user);
		if (myCoupon == null){
			Coupon coupon = Coupon.findByKey(key);
			if (coupon == null){
				Logger.debug("Couldn't find Coupon with key %s", key);
				throw new InvalidCouponException(Messages.get("coupon.create.nofound"));
			}
			else{
				myCoupon = coupon.createMyCoupon(user);
			}
		}
		else{
			Logger.debug("Can't use a MyCoupon twice: for user %s with key %s", user.email, key);
			throw new InvalidCouponException(Messages.get("coupon.create.twice"));
		}
		return myCoupon;
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
			throw new InvalidCouponException(Messages.get("coupon.create.welcome.notnew"));
		}
	}
	private void validateOwnCoupon(User user) throws InvalidCouponException{
		if (this.key.equalsIgnoreCase(user.refererId)){
			throw new InvalidCouponException(Messages.get("coupon.create.owncoupon"));
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
			throw new InvalidCouponException(Messages.get("coupon.create.referer.twice"));
		}
	}
	
	private void validateExpiration() throws InvalidCouponException {
		if (this.expired()){
			throw new InvalidCouponException(Messages.get("coupon.create.expired"));
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
