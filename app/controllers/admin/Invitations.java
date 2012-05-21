package controllers.admin;

import models.Invitation;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;

@Check(Security.ADMIN_ROLE)
@With(Secure.class)
@CRUD.For(Invitation.class)
public class Invitations  extends controllers.CRUD {
}

