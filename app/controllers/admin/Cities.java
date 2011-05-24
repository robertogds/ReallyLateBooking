package controllers.admin;

import java.util.*;

import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import models.*;
import models.crudsiena.*;
import play.*;
import play.modules.crudsiena.CrudUnique;
import play.mvc.*;
import siena.*;

@Check("admin")
@With(Secure.class)
@CRUD.For(City.class)
public class Cities extends controllers.CRUD {
	
	
}

