package models;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.Logger;

import models.exceptions.InvalidCouponException;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.Table;

@Table("mycoupons")
public class MyCoupon extends Model{
	private static final int EXPIRATION_DEFAULT = 100;
	
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	
	@Index("user_index")
	public User user;
	public String key;
	public int credits;
	@DateTime
    public Date created;
	@DateTime
    public Date expire;
	public boolean isReferer;
	public boolean active;
	public boolean used;
	public String title;
	
	
	public MyCoupon() {
		super();
	}
	public MyCoupon(User user, String key, int credits) {
		super();
		this.user = user;
		this.expire = this.getExpirationDateByDays(EXPIRATION_DEFAULT);
		this.created = Calendar.getInstance().getTime();
		this.credits = credits;
		this.key = key;
		this.used = false;
		this.active = true;
	}
	
	public MyCoupon(User user, String key, int credits, int duration) {
		super();
		this.user = user;
		this.expire = this.getExpirationDateByDays(EXPIRATION_DEFAULT);
		this.created = Calendar.getInstance().getTime();
		this.credits = credits;
		this.key = key;
		this.used = false;
		this.active = true;
	}

	public MyCoupon(Coupon coupon, User user) {
		super();
		this.user = user;
		this.expire = this.getExpirationDateByDays(coupon.duration);
		this.created = Calendar.getInstance().getTime();
		this.key = coupon.key;
		this.credits = coupon.credits;
		this.used = false;
		this.isReferer = coupon.type.equals(Coupon.CREDITS_REFERER_TYPE);
		this.active = true;
		this.title = coupon.title;
	}
	
	public static Query<MyCoupon> all() {
    	return Model.all(MyCoupon.class);
    }
	
    public static MyCoupon findActiveByKey(String key){
    	return MyCoupon.all().filter("key", key).filter("active", true).order("-created").get();
    }
    
    public static MyCoupon findById(Long id) {
        return all().filter("id", id).get();
    }
    
    public static List<MyCoupon> findActiveByUser(User user){
    	return MyCoupon.all().filter("user", user).filter("active", true).order("-created").fetch();
    }
    
	public static MyCoupon findByKeyAndUser(String key, User user) {
		return MyCoupon.all().filter("key", key).filter("user", user).order("-created").get();
	}
	
	public String toString() {
		return "key: " + this.key + " credits: " +  this.credits + " expire: " + this.expire;
	}
	
	private Date getExpirationDateByDays(int days){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, days);
		Logger.debug("getExpirationDateByDays: days %s Expire: %s",days, calendar.getTime());
		return calendar.getTime();
	}

	/**
	 * returns true if the expiration date is in the future
	 * and this user has not been referred before
	 * */
	public Boolean isValidForUser() {
		return this.isReferalValid() && this.isNotExpired();
	}
	
	/**
	 * returns true if the expiration date is in the future
	 * */
	public Boolean isNotExpired() {
		Calendar now = Calendar.getInstance();
		Calendar expire = Calendar.getInstance();
		expire.setTime(this.expire);
		Logger.debug("ISNOTEXPIRED: Now %s Expire: %s",now.getTime(), expire.getTime());
		return now.before(expire);
	}
	
	/**
	 * returns true if the expiration date is in the future
	 * and this coupon has not been used before by the user
	 * */
	public Boolean isNotExpiredNorUsed() {
		return  isNotExpired() && !this.used;
	}
	
	/**
	 * returns true this user has not been referred before
	 * */
	public Boolean isReferalValid(){
		this.user.get();
		Logger.debug("IsReferalValid= isUserOwnCoupon:%s isReferer:%s referalCouponUsed:%s", isUserOwnCoupon(), this.isReferer, referalCouponUsed());
		if( isUserOwnCoupon() || (this.isReferer && referalCouponUsed() ) ){
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	private Boolean referalCouponUsed(){
		return StringUtils.isNotBlank(this.user.referer);
	}
	
	private Boolean isUserOwnCoupon(){
		Boolean ownCoupon = this.key.equalsIgnoreCase(this.user.refererId) ;
		return ownCoupon;
	}
	
	/**
	 * Mark coupon as used and returns the credits
	 * @return the number of credits remaining
	 * @throws InvalidCouponException
	 */
	public Integer use() throws InvalidCouponException{
		if (isNotExpiredNorUsed()){
			return this.useValidUnused();
		}
		else{
			throw new InvalidCouponException();
		}
	}
	
	
	public Integer useValidUnused(){
		this.used = true;
		this.update();
		this.activateCouponToReferal();
		return this.credits;
	}
	
	private void activateCouponToReferal() {
		this.user.get();
		Logger.info("%s used a referal coupon so we activate the coupon for the referal %s ", user.email, user.referer);
		if (StringUtils.isNotBlank(user.referer)){
			User referer = User.findByRefererId(user.referer);
			Logger.info("Try to activate coupon to %s searching by key %s", referer.email, user.refererId);
			MyCoupon coupon = MyCoupon.findByKeyAndUser(user.refererId, referer);
			try {
				coupon.active = Boolean.TRUE;
				coupon.update();
				//TODO send mail to referal?
				//TODO notificaciones
			} catch (Exception e) {
				Logger.error("Can´t find coupon for referer:%s and user %s", user.refererId, referer.email);
			}
		}
	}
	
	public void createRefererCoupon(User user) {
		User referer = User.findByRefererId(this.key);
		if (referer != null){
			//Update the user with the referer key
			user.referer = referer.refererId;
			user.update();
			Logger.info("A friend used his coupon so gives %s a new coupon %s ", referer.refererId,  user.refererId);
			Coupon invitedCoupon = Coupon.findByKey(user.refererId);
			MyCoupon refererCoupon = new MyCoupon(referer, user.refererId, invitedCoupon.credits, invitedCoupon.duration);
			refererCoupon.isReferer = Boolean.TRUE;
			refererCoupon.active = Boolean.FALSE; //set to active at booking
			refererCoupon.insert();
		}
		else{
			Logger.info("Referer key doesnt seem to be an active user: " + this.key);
		}
		
	}
	
}
