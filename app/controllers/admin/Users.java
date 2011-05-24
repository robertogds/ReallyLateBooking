package controllers.admin;

import play.mvc.With;
import models.User;
import controllers.*;

@Check("admin")
@With(Secure.class)
@CRUD.For(User.class)
public class Users extends controllers.CRUD  {

}
