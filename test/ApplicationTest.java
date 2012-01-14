
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
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
import sun.util.calendar.CalendarUtils;
import models.*;
import models.dto.BookingDTO;
import models.dto.BookingStatusMessage;
import models.dto.StatusMessage;
import models.dto.UserDTO;
import models.dto.UserStatusMessage;

public class ApplicationTest extends FunctionalTest {

	String userJson = "{\"email\":\"bob@gmail.com\",\"password\":\"5ebe2294ecd0e0f08eab7690d2a6ee69\"}";
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
    	City madrid = new City("Madrid", "madrid");
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
	    UserStatusMessage message = new Gson().fromJson(jsonResponse, UserStatusMessage.class);
	    UserDTO newUser =  message.content;
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
	    //Test correct update
	    Response response = PUT("/user/" + user.id, "application/json", json);
	    String jsonResponse = response.out.toString();
	    UserStatusMessage message = new Gson().fromJson(jsonResponse, UserStatusMessage.class);
	    assertIsOk(response);
	    assertContentType("application/json", response);
	    assertEquals((Integer)Http.StatusCode.OK,(Integer) message.status);
    }
    @Test
    public void createBookingFromJson(){
    	User user = new User("bob@gmail.com", "secret", "Bob", "Smith");
    	user.insert();
    	City madrid = new City("Madrid", "madrid");
    	madrid.insert();
		Deal deal = new Deal("Hotel Ritz", madrid, Boolean.TRUE);
		deal.mainImageBig = "mainImage.jpg";
		deal.mainImageSmall = "mainImageSmall.jpg";
		deal.quantity = 1 ; 
		deal.contactEmail = "pablo@iipir.com";
		deal.insert();
    	// Create a new booking and save it
	    Booking booking = new Booking(deal, user);
	    booking.creditCard = "4214730854508021";
	    booking.creditCardName = "Pablo Pazos";
	    booking.creditCardCVC= "564";
	    booking.creditCardExpiry ="01/01/2012";
	    booking.creditCardType = "visa";
	    booking.nights = 1 ; 
	    String json = new Gson().toJson(new BookingDTO(booking),BookingDTO.class);	    
	    String url = Router.reverse("Bookings.create").url;
	    //Test correct creation
	    Response response = POST(url+"?json=" + json);
	    String jsonResponse = response.out.toString();
	    Logger.debug("JSON received: " + jsonResponse);
	    BookingStatusMessage message = new Gson().fromJson(jsonResponse, BookingStatusMessage.class);
	    Booking newBooking = ((BookingDTO) message.content).toBooking();
	    assertIsOk(response);
	    assertNotNull(newBooking);
	    assertFalse(newBooking.code == null);
	    Deal dealAfterBooking = Deal.findById(deal.id);
	    assertEquals(dealAfterBooking.quantity.intValue(), deal.quantity - 1);
	    //Second 
	    response = POST(url+"?json=" + json);
	    jsonResponse = response.out.toString();
	    message = new Gson().fromJson(jsonResponse, BookingStatusMessage.class);
	    assertEquals(Http.StatusCode.INTERNAL_ERROR, message.status);
    }
    @Test
    public void createNotValidBookingFromJson(){
    	User user = new User("bob@gmail.com", "secret", "Bob", "Smith");
    	user.insert();
    	City madrid = new City("Madrid", "madrid");
    	madrid.insert();
		Deal deal = new Deal("Hotel Ritz", madrid, Boolean.TRUE);
		deal.mainImageBig = "mainImage.jpg";
		deal.mainImageSmall = "mainImageSmall.jpg";
		deal.quantity = 2 ;
		deal.insert();
    	// Create a new booking and try to save it
		// Its not valid because credit card is not correct
	    Booking booking = new Booking(deal, user);
	    booking.creditCardName = "Pablo Pazos";
	    booking.creditCard = "12345678912345679";
	    booking.creditCardCVC= "546";
	    booking.creditCardExpiry ="10/12";
	    booking.creditCardType = "visa";
	    booking.nights = 1 ; 
	    String json = new Gson().toJson(new BookingDTO(booking),BookingDTO.class);	    
	    String url = Router.reverse("Bookings.create").url;
	    //Test incorrect creation
	    Response response = POST(url+"?json=" + json);
	    String jsonResponse = response.out.toString();
	    Logger.debug("JSON received: " + jsonResponse);
	    BookingStatusMessage message = new Gson().fromJson(jsonResponse, BookingStatusMessage.class);
	    assertIsOk(response);
	    assertContentType("application/json", response);
	    assertEquals( Http.StatusCode.INTERNAL_ERROR, message.status);
    }
    @Test
    public void listBookingsByUser(){
    	User user = new User("bob@gmail.com", "secret", "Bob", "Smith");
    	user.insert();
    	City madrid = new City("Madrid", "madrid");
    	madrid.insert();
		Deal deal = new Deal("Hotel Ritz", madrid, Boolean.TRUE);
		deal.mainImageBig = "mainImage.jpg";
		deal.mainImageSmall = "mainImageSmall.jpg";
		deal.quantity = 1 ; 
		deal.contactEmail = "pablo@iipir.com";
		deal.insert();
    	// Create a new booking and save it
	    Booking booking = new Booking(deal, user);
	    booking.creditCard = "4214730854508021";
	    booking.creditCardName = "Pablo Pazos";
	    booking.creditCardCVC= "546";
	    booking.creditCardExpiry ="01/01/2012";
	    booking.creditCardType = "visa";
	    booking.nights = 1 ; 
	    String json = new Gson().toJson(new BookingDTO(booking),BookingDTO.class);	    
	    String url = Router.reverse("Bookings.create").url;
	    //Test correct creation
	    Response response = POST(url+"?json=" + json);
	    String jsonResponse = response.out.toString();
	    Logger.debug("JSON received: " + jsonResponse);
	    //Test correct creation
	    response = GET("/user/" + user.id + "/bookings");
	    jsonResponse = response.out.toString();
	    Logger.debug("Booking list JSON received: " + jsonResponse);
	    
	    
    	
    }
    
}