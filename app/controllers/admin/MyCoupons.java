package controllers.admin;

import models.MyCoupon;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;

@Check(Security.ADMIN_ROLE)
@With(Secure.class)
@CRUD.For(MyCoupon.class)
public class MyCoupons  extends controllers.CRUD {
}

