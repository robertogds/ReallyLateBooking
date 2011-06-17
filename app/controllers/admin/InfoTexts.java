package controllers.admin;

import models.InfoText;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(InfoText.class)
public class InfoTexts  extends controllers.CRUD {
}

