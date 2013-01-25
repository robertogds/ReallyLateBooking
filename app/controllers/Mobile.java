package controllers;

import play.exceptions.UnexpectedException;
import play.i18n.Messages;
import play.mvc.*;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.results.Ok;
import play.mvc.results.Result;



import java.lang.reflect.Method;
import java.util.*;

import notifiers.Mails;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jobs.Bootstrap;


import models.*;
import models.dto.StatusMessage;
import models.dto.UserStatusMessage;

public class Mobile extends Controller {
	
	public static void main() { 
		//Workaround needed because jobs dont work on gae
		Bootstrap job = new Bootstrap();
		job.doJob();
		//End of workaround
		Collection<City> cities = City.findActiveCities();
		render(cities);
	}
	
	
}