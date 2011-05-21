package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import jobs.Bootstrap;


import models.*;

public class Application extends Controller {
	
	public static void index() { 
		//Workaround needed because jobs dont work on gae
		Bootstrap job = new Bootstrap();
		job.doJob();
		render(); 
	} 
	
	public static void inline() { 
		render(); 
	}

	public static void include() {
		render();
	}

	public static void includeError() {
		render();
	}
	
	public static void inlineError() {
		render();
	}
	
	public static void prueba() {
		render();
	}
	
}