package controllers.admin;

import models.Partner;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;

@Check(Security.ADMIN_ROLE)
@With(Secure.class)
@CRUD.For(Partner.class)
public class Partners extends controllers.CRUD {

}
