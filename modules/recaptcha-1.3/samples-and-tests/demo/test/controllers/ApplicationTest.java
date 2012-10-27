package controllers;



import org.junit.Test;

import play.Play;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class ApplicationTest extends FunctionalTest {
    
	
	@Test 
	public void testBasicStuff() {		
		
		Response response = GET("/recaptcha");
		assertStatus(200, response);	
		//make sure recaptcha is rendered into page
		assertTrue(response.out.toString().contains("http://www.google.com/recaptcha/api/challenge"));
		
	}
    
}