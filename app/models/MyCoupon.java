package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import notifiers.Mails;

import org.apache.commons.lang.StringUtils;

import controllers.admin.MyCoupons;

import play.Logger;
import play.Play;

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
	@DateTime
    public Date usedDate;
	public boolean isReferer;
	public boolean active;
	public boolean used;
	public String title;
	
	
	public MyCoupon() {
		super();
	}
	
	public MyCoupon(User user, String key, int credits) {
		super();
		this.user = this.assignToUser(user);
		this.expire = this.getExpirationDateByDays(EXPIRATION_DEFAULT);
		this.created = Calendar.getInstance().getTime();
		this.credits = credits;
		this.key = key;
		this.used = false;
		this.active = true;
	}
	
	
	public MyCoupon(User user, String key, int credits, int duration) {
		super();
		this.user = this.assignToUser(user);
		this.expire = this.getExpirationDateByDays(EXPIRATION_DEFAULT);
		this.created = Calendar.getInstance().getTime();
		this.credits = credits;
		this.key = key;
		this.used = false;
		this.active = true;
	}

	public MyCoupon(Coupon coupon, User user) {
		super();
		this.user = this.assignToUser(user);
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
	
    public static List<MyCoupon> findByKey(String key) {
    	if (key != null){
        	return MyCoupon.all().filter("key", key.trim().toUpperCase()).order("-created").fetch();
    	}
    	else{
    		return new ArrayList<MyCoupon>();
    	}
	}
    
    public static MyCoupon findById(Long id) {
        return all().filter("id", id).get();
    }
    
    public static List<MyCoupon> findActiveByUser(User user){
    	return MyCoupon.all().filter("user", user).filter("active", true).order("-created").fetch();
    }
    public static List<MyCoupon> findByUser(User user) {
    	return MyCoupon.all().filter("user", user).order("-created").fetch();
	}
    public static List<MyCoupon> findActiveByUserOrderByCredits(User user){
    	return MyCoupon.all().filter("user", user).filter("active", true).order("-credits").fetch();
    }
    
	public static MyCoupon findByKeyAndUser(String key, User user) {
		if (key != null && user!= null){
    		return MyCoupon.all().filter("key", key.trim().toUpperCase()).filter("user", user).order("-created").get();
    	}
		else{
			return null;
		}
	}
	
	public static Collection<MyCoupon> findAllByUsedDate(Date start, Date end) {
		return MyCoupon.all().filter("used", Boolean.TRUE)
			.filter("usedDate>", start).filter("usedDate<", end).fetch();
	}
	
	public static Collection<MyCoupon> findAllRefererByUsedDate(Date start, Date end) {
		return allRefererByUsedDate(start, end).fetch();
	}
	
	public static int countAllRefererByUsedDate(Date start, Date end) {
		return allRefererByUsedDate(start, end).count();
	}
	
	public static Query<MyCoupon> allRefererByUsedDate(Date start, Date end) {
		return MyCoupon.all().filter("used", Boolean.TRUE).filter("isReferer", Boolean.TRUE)
			.filter("usedDate>", start).filter("usedDate<", end);
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

	
	private void validateUsed() throws InvalidCouponException{
		if (this.used){
			throw new InvalidCouponException("No puedes usar un cupón dos veces pillin");
		}		
	}
	
	/**
	 * Mark coupon as used and returns the credits
	 * @return the number of credits remaining
	 * @throws InvalidCouponException
	 */
	public Integer use(Integer finalPrice) throws InvalidCouponException{
		validateExpiration();
		validateUsed();
		return this.useValidUnused(finalPrice);
	}
	
	public Integer useValidUnused(Integer finalPrice){
		this.used = true;
		this.usedDate = Calendar.getInstance().getTime();
		this.update();
		if (this.isReferer && finalPrice > new Integer(Play.configuration.getProperty("coupons.referal.price.min"))) {
			this.activateCouponToReferal();
		}
		return this.credits;
	}
	
	public void createRefererCoupon(User user) {
		User friend = user;
		User referer = User.findByRefererId(this.key);
		if (referer != null){
			//Update the user with the referer key
			friend.referer = referer.refererId;
			friend.update();
			Logger.info("A friend used his coupon so gives %s a new coupon %s ", referer.refererId,  friend.refererId);
			Coupon invitedCoupon = Coupon.findByKey(friend.refererId);
			MyCoupon refererCoupon = new MyCoupon(referer, friend.refererId, invitedCoupon.credits, invitedCoupon.duration);
			refererCoupon.title = "FRIEND";
			refererCoupon.isReferer = Boolean.TRUE;
			refererCoupon.active = Boolean.FALSE; //set to active at booking
			refererCoupon.insert();
			//When the first friend register we considerer the referer is not a new user anymore
			referer.update();
			Mails.friendRegistered(referer, friend, refererCoupon);
		}
		else{
			Logger.info("Referer key doesnt seem to be an active user: " + this.key);
		}
		
	}
	
	/**
	 * returns true if the expiration date is in the past
	 * or this coupon has already been used before by the user
	 * */
	public Boolean expiredOrUsed() {
		return  expired() || this.used;
	}
	
	/**
	 * returns true if the expiration date is in the past
	 * */
	private Boolean expired() {
		Calendar now = Calendar.getInstance();
		Calendar expire = Calendar.getInstance();
		expire.setTime(this.expire);
		//Logger.debug("EXPIRED: Now %s Expire: %s",now.getTime(), expire.getTime());
		return now.after(expire);
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
				Mails.friendFirstBooking(referer, user, coupon);
				//TODO notificaciones
			} catch (Exception e) {
				Logger.error("Can´t find coupon for referer:%s and user %s", user.refererId, referer.email);
			}
		}
	}
	
	/**
	 * Mark the user as not new once he gets the first coupon
	 * @param user
	 * @return
	 */
	private User assignToUser(User user) {
		user.get();
		user.isNew = false;
		user.update();
		return user;
	}
	
	private void validateExpiration() throws InvalidCouponException {
		if (this.expired()){
			throw new InvalidCouponException("El cupón está caducado");
		}
	}


}
