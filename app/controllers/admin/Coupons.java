package controllers.admin;

import models.Coupon;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(Coupon.class)
public class Coupons  extends controllers.CRUD {
}

