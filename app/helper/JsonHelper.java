package helper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

import play.exceptions.UnexpectedException;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.results.Result;

/** 
 * Use the com.google.gson.annotations.Expose annotation to mark each field you want to serialize.
 *
 * @author pablopr
 *
 */
public class JsonHelper {

	public static String jsonExcludeFieldsWithoutExposeAnnotation(Object content){
	    Gson gson = new GsonBuilder()
	        .excludeFieldsWithoutExposeAnnotation()  
	        .create();
	    return gson.toJson(content); 
	}


} 