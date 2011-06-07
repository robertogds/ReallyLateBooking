import helper.StatusMessage;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import org.junit.After;
import org.junit.Before;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import controllers.Deals;
import play.Logger;
import play.test.*;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
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
    
    @Test
    public void validateUser(){
    	// Create a new user and save it
	    User user = new User("pablopr@gmail.com", "secret", "Bob", "Smith");
	    user.insert();	
	    user = User.findById(user.id);
	    //assertFalse(user.validated); Not now because we've disabled validation
	    String activateUrl = Router.reverse("Users.activate").url;
	    GET(activateUrl+"/" +  user.validationCode);
	    Logger.debug("### Validating: " + activateUrl+"/" +  user.validationCode);
	    //Test correct validation
	    User userValidated = User.findById(user.id);
	    assertTrue(userValidated.validated);
    }
    
    @Test
	public void loginUser(){    	
    	// Create a new user and save it
	    User user = new User("bob@gmail.com", "secret", "Bob", "Smith");
	    user.insert();	
	    //We need to validate email before login
	    String activateUrl = Router.reverse("Users.activate").url;
	    GET(activateUrl+"/" +  user.validationCode);
	    Logger.debug("### Validating: " + activateUrl+"/" +  user.validationCode);
	    //Test correct login
	    String url = Router.reverse("Application.login").url;
	    Response response = POST(url+"?json=" + userJson);
	    String json = response.out.toString();
	    StatusMessage message = new Gson().fromJson(json, StatusMessage.class);
	    assertIsOk(response);
	    assertContentType("application/json", response);
	    assertEquals((Integer)Http.StatusCode.OK,(Integer) message.status);
	    //Test incorrect login
	    response = POST(url+"?json=" + userJsonB);
	    json = response.out.toString();
	    message = new Gson().fromJson(json, StatusMessage.class);
	    assertIsOk(response);
	    assertContentType("application/json", response);
	    assertEquals((Integer)Http.StatusCode.NOT_FOUND,(Integer) message.status);
	}
    
    @Test
    public void createUserFromJson(){
    	// Create a new user and save it
	    User user = new User("pablopr@gmail.com", "secret", "Bob", "Smith");
	    String json = new Gson().toJson(user,User.class);	    
	    String url = Router.reverse("Users.create").url;
	    //Test correct creation
	    Response response = POST(url+"?json=" + json);
	    String jsonResponse = response.out.toString();
	    StatusMessage message = new Gson().fromJson(jsonResponse, StatusMessage.class);
	    User newUser = (User) message.content;
	    assertIsOk(response);
	    assertContentType("application/json", response);
	    assertNotNull(newUser.id);
	    assertEquals("pablopr@gmail.com", newUser.email);
    }
    
    @Test
    public void updateUserFromJson(){
    	// Create a new user and save it
	    new User("bob@gmail.com", "secret", "Bob", "Smith").insert();
	    User user = User.findByEmail("bob@gmail.com");
	    user.firstName = "Pablo";
	    String json = new Gson().toJson(user);	    
	    String url = Router.reverse("Users.update").url;
	    //Test correct update
	    Response response = POST(url+"?json=" + json);
	    String jsonResponse = response.out.toString();
	    User newUser = new Gson().fromJson(jsonResponse, User.class);
	    assertIsOk(response);
	    assertContentType("application/json", response);
	    assertEquals("Pablo", newUser.firstName);
    }
    
}