import org.junit.*;

import com.google.gson.JsonObject;

import controllers.Deals;

import helper.CreditCardHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import play.Logger;
import play.db.Model;
import play.libs.WS;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

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
	public void createAndRetrieveDeal() {
		City madrid = new City("Madrid", "madrid");
		madrid.insert();
	    // Create a new deal and save it
	    new Deal("Hotel Ritz", madrid).insert();
	    
	    // Retrieve the first hotel with city madrid
	    Deal hotel = Deal.all().filter("city", madrid).get();
	    
	    // Test 
	    assertNotNull(hotel);
	    assertEquals("Hotel Ritz", hotel.hotelName);
	}
	
	
	@Test
	public void findDealsFromCity() {
		City madrid = new City("Madrid", "madrid");
		madrid.insert();
		City barcelona = new City("Barcelona", "barcelona");
		barcelona.insert();
	    // Create some new deals and save it
	    new Deal("Hotel Ritz", madrid, Boolean.TRUE).insert();
	    new Deal("Hotel Cibeles", madrid, Boolean.TRUE).insert();
	    new Deal("Hotel Sol", madrid, Boolean.FALSE).insert();
	    new Deal("Hotel Catalunya", barcelona, Boolean.TRUE).insert();
	    
	    // Retrieve all the hotel with city madrid
	    int count = Deal.findActiveDealsByCity(madrid, false, false).size();
	    // Test 
	    assertEquals(2, count);
	    
	    // Retrieve all the hotel with city Barcelona
	    int countBcn = Deal.findActiveDealsByCity(barcelona, false, false).size();
	    // Test 
	    assertEquals(1, countBcn);
	}
	
	@Test
	public void validateCreditCard(){
	    String aCard = "4214730854508021";
	    assertTrue(CreditCardHelper.getCardID(aCard) > -1);
	    Logger.debug("This a " + CreditCardHelper.getCardName(CreditCardHelper.getCardID(aCard)));
	    try {
			boolean isValid = CreditCardHelper.validCC(aCard);
			assertTrue(isValid);
		} catch (Exception e) {
			Logger.error("Exception validating credit card ", e);
		}
	    
	}
	
	@Test
	public void validateInvalidCreditCard(){
	    String aCard = "4211720854408021";
	    assertTrue(CreditCardHelper.getCardID(aCard) > -1);
	    Logger.debug("This a " + CreditCardHelper.getCardName(CreditCardHelper.getCardID(aCard)));
	    try {
			boolean isValid = CreditCardHelper.validCC(aCard);
			assertFalse(isValid);
		} catch (Exception e) {
			Logger.error("Exception validating credit card ", e);
		}
	    
	}

}
