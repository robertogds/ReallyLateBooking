package controllers;

import play.*;
import play.mvc.*;

import helper.StatusMessage;

import java.util.*;

import com.google.gson.Gson;

import jobs.Bootstrap;


import models.*;

public class Application extends Controller {
	
	public static void index() { 
		//Workaround needed because jobs dont work on gae
		Bootstrap job = new Bootstrap();
		job.doJob();
		render(); 
	} 
	
	public static void login(String json) { 
		Logger.debug(json);
		User user = new Gson().fromJson(json, User.class);
		
		Logger.debug("Authenticate result: " + Security.authenticate(user.email, user.password));
		
		if (Security.authenticate(user.email, user.password)){
			renderJSON(new StatusMessage(Http.StatusCode.OK, "OK", "user is logged"));
		}
		else{
			renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", "user or password incorrect"));
		}
	}
	
}