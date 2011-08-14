package controllers;

import play.*;
import play.exceptions.UnexpectedException;
import play.i18n.Messages;
import play.mvc.*;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.results.Ok;
import play.mvc.results.Result;


import helper.dto.StatusMessage;
import helper.dto.UserStatusMessage;

import java.lang.reflect.Method;
import java.util.*;

import notifiers.Mails;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jobs.Bootstrap;


import models.*;

public class Application extends Controller {
	
	@Before
	public static void checkLanguage(){
		Logger.debug("AAAAAAAAB");
		Logger.debug("### Accept-language: " + request.acceptLanguage().toString());
	}
	
	public static void index() { 
		//redirect to the extranet
		Owners.index();
	} 
	
	public static void mobile(){
		if (Play.mode.isDev()){
			Logger.debug("POr aqui entra");
			//Workaround needed because jobs dont work on gae
			Bootstrap job = new Bootstrap();
			job.doJob();
			//End of workaround
			
			Bookings.doHotUsaBooking(null);
		}
		
		
		Collection<City> cities = City.findActiveCities();
		Logger.debug("Number of cities: " + cities.size());
		render(cities); 
	}
	
	
	public static void activate(String code){
		Logger.debug("##### Validatind user with code: " + code);
		User user = User.all().filter("validationCode", code).get();
		if (user != null){
			Logger.debug("User is not null");
			user.validated = true;
			user.update();
			Mails.welcome(user);
		}
		render(user);
	}
	
}