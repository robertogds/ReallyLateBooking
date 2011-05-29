import models.City;
import models.Deal;
import models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
	
}
