package controllers.admin;

import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(Statistics.class)
public class Statistics  extends controllers.CRUD {
}

