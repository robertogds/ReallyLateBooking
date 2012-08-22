package controllers.admin;

import models.MyCoupon;
import models.User;
import play.data.validation.Email;
import play.data.validation.Required;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;

@Check(Security.ADMIN_ROLE)
@With(Secure.class)
@CRUD.For(MyCoupon.class)
public class MyCoupons  extends controllers.CRUD {
	
	
	public static void assignToUser(@Required @Email String email, MyCoupon myCoupon){
		if (!validation.hasErrors()){
			myCoupon = MyCoupon.findById(myCoupon.id);
			User user = User.findByEmail(email.trim().toLowerCase());
			if (user == null){
				validation.addError("email", "User not found");
			}
			if (!validation.hasErrors()){
				myCoupon.user = user;
				myCoupon.update();
				
				flash.success("Coupon reassigned to %s", user.email);
				redirect("admin.MyCoupons.list");
			}
		}
		params.flash(); // add http parameters to the flash scope
        flash.error(validation.errors().toString());
		redirect("admin.MyCoupons.show", myCoupon.id);
	}
}

