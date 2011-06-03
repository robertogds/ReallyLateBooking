import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import models.City;
import models.Deal;
import models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import controllers.oauth.ApiSecurer;

import play.Logger;
import play.test.UnitTest;


public class UsersTest  extends UnitTest {
	@Before @After
	public void setup() {
		for(User user: User.all(User.class).fetch() ){
			user.delete();
		}
	}
	
	@Test
	public void createAndRetrieveUser() {
	    // Create a new user and save it
	    new User("bob@gmail.com", "secret", "Bob", "Smith").insert();
	    
	    // Retrieve the user with e-mail address bob@gmail.com
	    User bob = User.findByEmail("bob@gmail.com");
	   
	    // Test 
	    assertNotNull(bob);
	    assertEquals("Bob", bob.firstName);
	}
	
	@Test
	public void testTimestamp(){
	    long now = Calendar.getInstance().getTimeInMillis() / 1000;
	    long seconds = 11 * 1000;
	    long after = now + seconds; 

		assertTrue(ApiSecurer.checkValidTimestamp(now));
		assertFalse(ApiSecurer.checkValidTimestamp(after));
    	
	}
	
}
