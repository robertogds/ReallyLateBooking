package controllers;

import models.Statistic;
import play.mvc.Before;
import play.mvc.Controller;

public class Analytics extends Controller{
	
	@Before
	static void analytics(){
		Statistic.saveVisit(request.path);
	}
	
}
