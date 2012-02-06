package controllers.admin;

import models.Invitation;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(Invitation.class)
public class Invitations  extends controllers.CRUD {
}

