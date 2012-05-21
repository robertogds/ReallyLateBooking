package controllers.admin;

import java.util.List;

import models.Company;
import models.Country;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.CRUD.ObjectType;
import controllers.Security;

@Check(Security.EDITOR_ROLE)
@With(Secure.class)
@CRUD.For(Country.class)
public class Countries extends controllers.CRUD {
	
	public static void exportAll() {
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<Country> countries = Country.all().fetch();
        List objects = countries;
        render("admin/export.csv", objects, type);
    }
}

