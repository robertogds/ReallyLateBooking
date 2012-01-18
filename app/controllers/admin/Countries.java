package controllers.admin;

import models.Country;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(Country.class)
public class Countries extends controllers.CRUD {
	
	
}

