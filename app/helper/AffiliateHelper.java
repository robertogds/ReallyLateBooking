package helper;

import models.Booking;
import play.mvc.Before;
import play.mvc.Scope.Params;
import play.mvc.Scope.Session;

public class AffiliateHelper {
	public static final String AFFILIATE_KEY = "user_id";
	
    public static void checkorigin(Session session, Params params) throws Throwable {
        // Needed for inspiring benefits
        if(params._contains(AFFILIATE_KEY)) {
        	session.put(AFFILIATE_KEY, params.get(AFFILIATE_KEY));
        }
	}
	
	public static Boolean fromAffiliate(Session session){
		return session.contains(AFFILIATE_KEY);
	}
	
	public static String getAffiliateUser(Session session){
		return session.get(AFFILIATE_KEY);
	}
	
	public static void sendAffiliateBooking(Booking booking , Session session){
		String url = "http://api.fidelizanet.com/aff_tracking/pixel?" +
				"aff_program=rlb&aff_event=sale&status=A" +
				"&order_num=" + booking.code +
				"&amount=" + booking.totalSalePrice +
				"&user_id=" + getAffiliateUser(session);
		
		UrlConnectionHelper.doGet(url);
	}
	
}
