package controllers.admin;

import java.util.*;
import models.*;
import models.crudsiena.*;
import controllers.*;
import play.*;
import play.mvc.*;
import siena.*;

@Check("admin")
@With(Secure.class)
@CRUD.For(Deal.class)
public class Deals extends controllers.CRUD {
	
}

