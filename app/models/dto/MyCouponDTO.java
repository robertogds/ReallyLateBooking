package models.dto;

import helper.DateHelper;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;

import models.MyCoupon;
import models.User;
import models.exceptions.InvalidCouponException;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.Table;

public class MyCouponDTO {
	
    public Long id;
	public Long userId;
	public String key;
	public int credits;
    public String created;
    public String expire;
	public boolean isReferer;
	public boolean active;
	public boolean used;
	public String title;
	
	
	public MyCouponDTO() {
		super();
	}
	public MyCouponDTO(MyCoupon coupon) {
		super();
		this.id = coupon.id;
		this.userId = coupon.user.id;
		this.expire = DateHelper.dateToString(coupon.expire);
		this.created =  DateHelper.dateToString(coupon.created);
		this.credits = coupon.expiredOrUsed() ? 0 : coupon.credits ;
		this.key = coupon.key;
		this.used = coupon.used;
		this.active = coupon.active;
		this.isReferer = coupon.isReferer;
		this.title = coupon.title;
	}
	
}
