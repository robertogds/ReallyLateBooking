package controllers;

import play.*;
import play.exceptions.UnexpectedException;
import play.mvc.*;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.results.Ok;
import play.mvc.results.Result;


import helper.StatusMessage;

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
				renderJSON(new StatusMessage(Http.StatusCode.OK, "OK", "user is logged"));
			}
		}
		
		renderJSON(new StatusMessage(Http.StatusCode.NOT_FOUND, "NOT_FOUND", "user or password incorrect"));
	}
	
	public static void activate(String code){
		Logger.debug("validatind user with code: " + code);
		User user = User.all().filter("validationCode", code).get();
		if (user != null){
			Logger.debug("User is not null");
			user.validated = true;
			user.update();
			Mails.welcome(user);
		}
		render(user);
	}
	
	
	/*
	 * This is a workaround to solve an issue from siena. 
	 * Not necessary with the next version of siena-play
	 */
	public static class MyRenderJson extends Result {

	    String json;

	    public MyRenderJson(Object o, Class<?>... classesToIgnore) {

	        MyExclusionStrategy[] strats = new
	        MyExclusionStrategy[classesToIgnore.length];
	        for(int i=0; i<classesToIgnore.length;i++) {
	            strats[i] = new MyExclusionStrategy(classesToIgnore[i]);
	        }
	        Gson gson = new GsonBuilder()
	            .setExclusionStrategies(strats)
	            .create();
	        json = gson.toJson(o);
	    }

	    public MyRenderJson(String jsonString) {
	        json = jsonString;
	    }

	    public void apply(Request request, Response response) {
	        try {
	            setContentTypeIfNotSet(response, "application/json;charset=utf-8");
	            response.out.write(json.getBytes("utf-8"));
	        } catch (Exception e) {
	            throw new UnexpectedException(e);
	        }
	    }

	    //

	    static Method getMethod(Class<?> clazz, String name) {
	        for(Method m : clazz.getDeclaredMethods()) {
	            if(m.getName().equals(name)) {
	                return m;
	            }
	        }
	        return null;
	    }

	    public class MyExclusionStrategy implements ExclusionStrategy {
	        private final Class<?> typeToSkip;

	        private MyExclusionStrategy(Class<?> typeToSkip) {
	          this.typeToSkip = typeToSkip;
	        }

	        public boolean shouldSkipClass(Class<?> clazz) {
	          return (clazz == typeToSkip);
	        }

	        public boolean shouldSkipField(FieldAttributes f) {
	          return false;
	        }
	      }

	} 
	
}