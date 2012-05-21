package models.dto;

import models.MyCoupon;

import com.google.gson.annotations.Expose;

public class CouponStatusMessage extends StatusMessage {
	
	@Expose
	public MyCouponDTO content;
	
	public CouponStatusMessage(int status, String message, String detail, MyCouponDTO content) {
		super(status, message, detail);
		this.content = content;
	}

}
