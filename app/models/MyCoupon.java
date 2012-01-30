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
	
	
	public MyCoupon() {
		super();
	}
	public MyCoupon(User user, String key, int credits) {
		super();
		this.user = user;
		this.expire = this.getExpirationDateByMonths(EXPIRATION_DEFAULT);
		this.created = Calendar.getInstance().getTime();
		this.credits = credits;
		this.key = key;
		this.used = false;
		this.active = true;
	}

	public MyCoupon(Coupon coupon, User user) {
		super();
		this.user = user;
		this.expire = coupon.expire;
		this.created = Calendar.getInstance().getTime();
		this.key = coupon.key;
		this.credits = coupon.credits;
		this.used = false;
		this.isReferer = coupon.type.equals(Coupon.CREDITS_REFERER_TYPE);
		this.active = true;
	}
	
	public static Query<MyCoupon> all() {
    	return Model.all(MyCoupon.class);
    }
	
    public static MyCoupon findByKey(String key){
    	return MyCoupon.all().filter("key", key).filter("active", true).order("-created").get();
    }
    
    public static MyCoupon findById(Long id) {
        return all().filter("id", id).get();
    }
    
    public static List<MyCoupon> findByUser(User user){
    	return MyCoupon.all().filter("user", user).filter("active", true).order("-created").fetch();
    }
    
	public static MyCoupon findByKeyAndUser(String key, User user) {
		return MyCoupon.all().filter("key", key).filter("user", user).filter("active", true).order("-created").get();
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
	 * and this user has not been referred before
	 * */
	public Boolean isValid() {
		if (isReferalValid()){
			Calendar now = Calendar.getInstance();
			Calendar expire = Calendar.getInstance();
			expire.setTime(this.expire);
			return now.before(expire);
		}
		return Boolean.FALSE;
	}
	
	private Boolean isReferalValid(){
		this.user.get();
		if(this.isReferer && StringUtils.isNotBlank(this.user.referer)){
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	public void use() throws InvalidCouponException{
		if (!this.used && isValid()){
			this.used = true;
			this.user.get();
			this.user.credits += this.credits;
			this.user.referer = this.isReferer ? this.key : null;
			this.user.update();
			this.update();
		}
		else{
			throw new InvalidCouponException();
		}
	}
	public void createRefererCoupon(User user) {
		User referer = User.findByRefererId(this.key);
		if (referer != null){
			Logger.info("A friend used his coupon so gives him a new coupon: " + this.key);
			MyCoupon invitedCoupon = MyCoupon.findByKeyAndUser(user.refererId, user);
			MyCoupon refererCoupon = new MyCoupon(referer, user.refererId, invitedCoupon.credits);
			refererCoupon.isReferer = Boolean.TRUE;
			refererCoupon.active = Boolean.FALSE; //set to active at booking
			refererCoupon.insert();
		}
		else{
			Logger.info("Referer key doesnt seem to be an active user: " + this.key);
		}
		
	}
	
}
