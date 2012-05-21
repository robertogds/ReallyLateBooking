package controllers.admin;

import models.Coupon;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;

@Check(Security.ADMIN_ROLE)
@With(Secure.class)
@CRUD.For(Coupon.class)
public class Coupons  extends controllers.CRUD {
}

