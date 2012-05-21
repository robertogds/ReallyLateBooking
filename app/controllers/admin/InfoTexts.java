package controllers.admin;

import java.util.List;

import models.Deal;
import models.InfoText;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.CRUD.ObjectType;
import controllers.Security;

@Check(Security.EDITOR_ROLE)
@With(Secure.class)
@CRUD.For(InfoText.class)
public class InfoTexts  extends controllers.CRUD {
	
	
	public static void exportAll() {
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<InfoText> texts = InfoText.all().fetch();
        List objects = texts;
        render("admin/export.csv", objects, type);
    }
}

