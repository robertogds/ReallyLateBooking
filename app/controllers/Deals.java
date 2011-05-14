package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import siena.*;

import models.crudsiena.*;
import models.*;

@CRUD.For(Deal.class)
public class Deals extends controllers.CRUD {
	public static void listado() {
	        Collection<Deal> deals = Deal.all()
				.filter("active", true)
				.fetch(3);
	        renderJSON(deals);
	}
	
    
}

