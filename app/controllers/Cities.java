package controllers;

import java.util.*;
import models.*;
import play.*;
import play.mvc.Controller;


public class Cities  extends Controller {
	
	public static void cityList() {
	        Collection<City> cities = City.all().fetch();
	        
	        renderJSON(cities);
	}
	
	
}

