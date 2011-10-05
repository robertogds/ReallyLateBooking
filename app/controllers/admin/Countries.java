package controllers.admin;

import java.util.*;

import notifiers.Mails;

import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;
import models.*;
import play.*;
import play.modules.crudsiena.CrudUnique;
import play.mvc.*;
import siena.*;

@Check("admin")
@With(Secure.class)
@CRUD.For(Country.class)
public class Countries extends controllers.CRUD {
	
	
}

