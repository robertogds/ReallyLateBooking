package controllers.admin;

import models.Setting;
import models.User;
import play.mvc.Controller;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;

@Check(Security.ADMIN_ROLE)
@With(Secure.class)
@CRUD.For(Setting.class)
public class Settings extends controllers.CRUD {

}
