import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import models.City;
import models.Deal;
import models.User;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import controllers.oauth.ApiSecurer;

import play.Logger;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.libs.Crypto;
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
	public void rememberPassword() {
	    // Create a new user and save it
	    User user = new User("bob@gmail.com", "secret", "Bob", "Smith");
	    user.insert();
	    Logger.debug("User password: " + user.password);
	    Logger.debug("Crypto password: " + DigestUtils.md5Hex("secret"));

	    // Retrieve the user with e-mail address bob@gmail.com and pass smith
	    User bob = User.findByEmailAndPassword("bob@gmail.com", DigestUtils.md5Hex("secret"));
	    // Test 
	    assertNotNull(bob);
	    
	    bob.setPasswordResetCode();
	    Logger.debug("User password code after reset: " + bob.resetCode);
	    // Retrieve the user by reset code
	    bob = User.findByResetCode(bob.resetCode);
	    assertNotNull(bob);
	    assertEquals("Bob", bob.firstName);
	}
	
	@Test
	public void createIdenticalUsers() {
	    // Create a new user and save it
	    new User("bob@gmail.com", "secret", "Bob", "Smith").insert();
	    User user = new User("bob@gmail.com", "secret", "Bob", "Smith");
	    user.validate();
	    if (!Validation.hasErrors()){
	    	user.insert();
	    }
	    // Retrieve number of users with e-mail address bob@gmail.com
	    int count = User.all().filter("email", "bob@gmail.com").count();
	   
	    // Test 
	    assertEquals(1, count);
	}
	
	
}
