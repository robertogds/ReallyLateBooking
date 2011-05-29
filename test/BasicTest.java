import org.junit.*;

import com.google.gson.JsonObject;

import controllers.Deals;

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
		City madrid = new City("Madrid");
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
		City madrid = new City("Madrid");
		madrid.insert();
		City barcelona = new City("Barcelona");
		barcelona.insert();
	    // Create some new deals and save it
	    new Deal("Hotel Ritz", madrid, Boolean.TRUE).insert();
	    new Deal("Hotel Cibeles", madrid, Boolean.TRUE).insert();
	    new Deal("Hotel Sol", madrid, Boolean.FALSE).insert();
	    new Deal("Hotel Catalunya", barcelona, Boolean.TRUE).insert();
	    
	    // Retrieve all the hotel with city madrid
	    int count = Deal.findActiveDealsByCity(madrid).size();
	    // Test 
	    assertEquals(2, count);
	    
	    // Retrieve all the hotel with city Barcelona
	    int countBcn = Deal.findActiveDealsByCity(barcelona).size();
	    // Test 
	    assertEquals(1, countBcn);
	}
	
	

}
