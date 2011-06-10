package controllers;

import play.*;
import play.exceptions.UnexpectedException;
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
	
	public static void mail(){
		Logger.debug("##### testing mail: " );
		User user = User.all().filter("validated", true).filter("isAdmin",true).get();
		Mails.welcome(user);

	}
	
	
	public static void index() { 
		//Workaround needed because jobs dont work on gae
		Bootstrap job = new Bootstrap();
		job.doJob();
		//End of workaround
		
		Collection<City> cities = City.all().fetch();
		Logger.debug("Number of cities: " + cities.size());
		render(cities); 
	} 
	
	public static void login(String json) { 
		String body = json != null ? json : params.get("body");
		Logger.debug("JSON received: " + body);
		
		if (body != null){
			User user = new Gson().fromJson(body, User.class);
			
			Logger.debug("Authenticate result: " + Security.authenticate(user.email, user.password));
			
			if (Security.authenticate(user.email, user.password)){
				User dbUser = User.findByEmail(user.email);
				renderJSON(new UserStatusMessage(Http.StatusCode.OK, "OK", "user is logged", dbUser));
			}
		}
		
		renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", "user or password incorrect"));
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