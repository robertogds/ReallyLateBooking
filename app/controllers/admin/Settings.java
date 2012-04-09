package controllers.admin;

import models.Setting;
import models.User;
import play.mvc.Controller;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(Setting.class)
public class Settings extends controllers.CRUD {

}
