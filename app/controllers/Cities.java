package controllers;

import java.util.*;
import models.*;
import models.crudsiena.*;
import play.*;
import play.mvc.*;
import siena.*;

@CRUD.For(City.class)
public class Cities extends controllers.CRUD {
	
	public static void cityList() {
	        Collection<City> cities = City.all().fetch();
	        
	        renderJSON(cities);
	}
	
	
}

