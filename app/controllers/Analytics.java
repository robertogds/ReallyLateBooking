package controllers;

import models.Statistic;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

public class Analytics extends Controller{
	
	@Before
	static void analytics(){
		Statistic.saveVisit(request.path);
	}
	
}
