import java.util.Calendar;

import models.Coupon;
import models.MyCoupon;
import models.User;
import models.exceptions.InvalidCouponException;

import org.junit.Before;
import org.junit.Test;

import controllers.Coupons;

import play.Logger;
import play.mvc.Http.Response;
import play.test.UnitTest;


public class CouponsTest  extends UnitTest {
	
	@Before 
	public void setup() {
		for(User user: User.all(User.class).fetch() ){
			user.delete();
		}
		for(Coupon coupon: Coupon.all().fetch() ){
			coupon.delete();
		}
		for(MyCoupon coupon: MyCoupon.all().fetch() ){
			coupon.delete();
		}
	}
	
	@Test
	public void createUserandRetrievecoupon() {
	    // Create a new user and save it
		User bob =  new User("bob@gmail.com", "secret", "Bob", "Smith");
	    bob.insert();
	    //Check if general coupon for his friends exists
	    Coupon couponFriends = Coupon.findByKey(bob.refererId);
	    assertNotNull(couponFriends);
	    assertEquals(20, couponFriends.credits);
	    Logger.debug("coupon: " + couponFriends + " is valid: " + !couponFriends.expired());
	    assertFalse(couponFriends.expired());
	    MyCoupon myCoupon = MyCoupon.findByKeyAndUser(bob.refererId, bob);
	    assertNull(myCoupon);
	    //bob  have 0 credits	
	    assertEquals(0, bob.calculateTotalCreditsFromMyCoupons());
	   
	}
	
	@Test
	public void useOwnCoupon() throws InvalidCouponException{
		Logger.debug("##### useOwnCoupon #####");
		User bob =  new User("bob@gmail.com", "secret", "Bob", "Smith");
	    bob.insert();
	    Coupon coupon = Coupon.findByKey(bob.refererId);
	    InvalidCouponException error = null;
	    try {
	    	//Bob use its own coupon
		    MyCoupon myCoupon = coupon.createMyCoupon(bob); 
		    myCoupon.use();
		} catch (InvalidCouponException e) {
			error = e;
			Logger.debug("Error: %s", e.getMessage());
		}
		assertNotNull(error);
		
		bob.get(); //refresh bob
	    assertEquals(0, bob.calculateTotalCreditsFromMyCoupons());
	}
	
	@Test
	public void useFirstCoupon() throws InvalidCouponException{
		Logger.debug("##### useFirstCoupon #####");
		User bob =  new User("bob@gmail.com", "secret", "Bob", "Smith");
	    bob.insert();
	    Coupon coupon = createWelcomeCoupon();
	    InvalidCouponException error = null;
	    try {
	    	//Bob use the welcome coupon
	    	Logger.debug("Bob is new: %s", bob.isNew);
		    MyCoupon myCoupon = coupon.createMyCoupon(bob); 
		    bob.get(); //refresh bob
		    assertEquals(10, bob.calculateTotalCreditsFromMyCoupons());
		    myCoupon.use();
		    assertEquals(0, bob.calculateTotalCreditsFromMyCoupons());
		} catch (InvalidCouponException e) {
			Logger.debug("Error: %s", e.getMessage());
			error = e;
		}
		assertNull(error);
	}
	
	@Test
	public void useFriendCoupon() throws InvalidCouponException {
		Logger.debug("##### useFriendCoupon #####");
		User bob =  new User("bob@gmail.com", "secret", "Bob", "BOB");
	    bob.insert();
		User pepe =  new User("pepe@gmail.com", "secret", "Pepe", "PEPE");
	    pepe.insert();
	    //Pepe uses bob coupon so gives him 20€
	    Coupon couponBob = Coupon.findByKey(bob.refererId);
	    MyCoupon myCouponPepe = couponBob.createMyCoupon(pepe);
	    assertEquals(20, couponBob.credits);
	    int creditsUsed =  myCouponPepe.use();
	    assertEquals(20,creditsUsed);
	    assertEquals(20, bob.calculateTotalCreditsFromMyCoupons());
	    assertEquals(couponBob.key, pepe.referer);
	    myCouponPepe = MyCoupon.findByKeyAndUser(couponBob.key, pepe);
	    assertNotNull(myCouponPepe);
	    assertTrue(myCouponPepe.used);
	}
	
	
	private Coupon createWelcomeCoupon() {
		Coupon coupon = new Coupon("WELCOME",10,Coupon.CREDITS_WELCOME_TYPE);
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.DAY_OF_YEAR, 5);
	    coupon.expire = calendar.getTime();
	    coupon.duration = 10;
	    coupon.insert();
	    return coupon;
	}
}
