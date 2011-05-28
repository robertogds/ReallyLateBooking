import helper.StatusMessage;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import org.junit.After;
import org.junit.Before;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import controllers.Deals;
import play.Logger;
import play.test.*;
import play.libs.WS;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class ApplicationTest extends FunctionalTest {

	String userJson = "{\"email\":\"bob@gmail.com\",\"password\":\"secret\"}";
	String userJsonB = "{\"email\":\"bob@gmaasil.com\",\"password\":\"incorrect\"}";
	
	@Before @After
	public void setup() {
		for(Deal deal : Deal.all(Deal.class).fetch()){
			deal.delete();
		}
		for(User user: User.all(User.class).fetch() ){
			user.delete();
		}
		for(City city : City.all(City.class).fetch()){
			city.delete();
		}
	}
	
    @Test
    public void testThatIndexPageWorks() {
        Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset("utf-8", response);
    }
    
    @Test
	public void loginUser(){    	
    	// Create a new user and save it
	    User user = new User("bob@gmail.com", "secret", "Bob", "Smith");
	    user.insert();	    
	    String url = Router.reverse("Application.login").url;
	    //Test correct login
	    Response response = POST(url+"?json=" + userJson);
	    String json = response.out.toString();
	    StatusMessage message = new Gson().fromJson(json, StatusMessage.class);
	    assertEquals((Integer)Http.StatusCode.OK,(Integer) response.status);
	    assertEquals((Integer)Http.StatusCode.OK,(Integer) message.status);
	    //Test incorrect login
	    response = POST(url+"?json=" + userJsonB);
	    json = response.out.toString();
	    message = new Gson().fromJson(json, StatusMessage.class);
	    assertEquals((Integer)Http.StatusCode.OK,(Integer) response.status);
	    assertEquals((Integer)Http.StatusCode.NOT_FOUND,(Integer) message.status);
	}
    
    @Test
	public void getDealListForIphone(){
    	City madrid = new City("Madrid");
    	madrid.insert();
		Deal ritz = new Deal("Hotel Ritz", madrid, Boolean.TRUE);
		ritz.address = "castellana 43";
		ritz.mainImageBig = "mainImage.jpg";
		ritz.mainImageSmall = "mainImageSmall.jpg";
		ritz.image1 = "image1.jpg";
		ritz.image2 = "image2.jpg";
		ritz.image3 = "image3.jpg";
		ritz.image4 = "image4.jpg";
		ritz.insert();
		
		Response response = GET("/deals/madrid");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
	}
    
}