package controllers;



import org.junit.Test;

import play.Play;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class ApplicationHttpsTest extends FunctionalTest {
    
	
	@Test 
	public void testBasicStuff() {		
		
		Response response = GET("/recaptcha_https");
		assertStatus(200, response);	
		//make sure recaptcha is rendered into page with HTTPS
		assertTrue(response.out.toString().contains("https://www.google.com/recaptcha/api/challenge"));
		
	}
    
}