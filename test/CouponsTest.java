import models.Coupon;
import models.MyCoupon;
import models.User;
import models.exceptions.InvalidCouponException;

import org.junit.Before;
import org.junit.Test;

import controllers.Coupons;

import play.Logger;
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
	    MyCoupon myCoupon = MyCoupon.findByKeyAndUser(bob.refererId, bob);
	    assertNotNull(myCoupon);
	    Logger.debug("coupon: " + myCoupon + " is valid: " + myCoupon.isValid());
	    assertEquals(12, myCoupon.credits);
	    assertTrue(myCoupon.isValid());
	    //bob dont have credits until he uses his own coupon
	    assertEquals(0, bob.credits);
	    //Check if general coupon for his friends exists
	    Coupon couponFriends = Coupon.findByKey(bob.refererId);
	    assertNotNull(couponFriends);
	    assertEquals(12, couponFriends.credits);
	}
	
	@Test
	public void useOwnCoupon() throws InvalidCouponException{
		User bob =  new User("bob@gmail.com", "secret", "Bob", "Smith");
	    bob.insert();
	    //Bob use its own coupon
	    MyCoupon myCoupon = MyCoupon.findByKeyAndUser(bob.refererId, bob);
	    myCoupon.use();
	    bob.get(); //refresh bob
	    assertEquals(myCoupon.credits, bob.credits);
	    assertTrue(myCoupon.used);
	    InvalidCouponException error = null;
	    try {
			myCoupon.use();
		} catch (InvalidCouponException e) {
			error = e;
		}
		assertNotNull(error);
	}
	
	@Test
	public void useFriendCoupon() throws InvalidCouponException {
		User bob =  new User("bob@gmail.com", "secret", "Bob", "Smith");
	    bob.insert();
		User pepe =  new User("pepe@gmail.com", "secret", "Pepe", "Garcia");
	    pepe.insert();
	    //Pepe uses bob coupon so gives him 12€
	    Coupon couponBob = Coupon.findByKey(bob.refererId);
	    MyCoupon myCouponPepe = couponBob.createMyCoupon(pepe);
	    myCouponPepe.use();
	    assertEquals(12, couponBob.credits);
	    assertEquals(couponBob.credits, pepe.credits);
	    assertEquals(couponBob.key, pepe.referer);
	    myCouponPepe = MyCoupon.findByKeyAndUser(couponBob.key, pepe);
	    assertNotNull(myCouponPepe);
	    assertTrue(myCouponPepe.used);
	    MyCoupon myCouponReferal = MyCoupon.findByKeyAndUser(pepe.refererId, bob);
	    assertNull(myCouponReferal);
	}
}
