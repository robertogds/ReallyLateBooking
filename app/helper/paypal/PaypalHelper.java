package helper.paypal;

import helper.UrlConnectionHelper;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import models.Booking;
import play.Logger;
import play.Play;
import play.data.parsing.UrlEncodedParser;
import play.libs.WS;

public final class PaypalHelper {
	public static final String PAYMENTACTION_SALE = "Sale";
	public static final String PAYMENTACTION_AUTHORIZATION = "Authorization";
	
	private static PaypalConfig config = PaypalConfig.instance;
	
	public static String setCheckout(Booking booking, String cancelUrl){
		Map<String, String> nvp = callSetExpressCheckout(booking, cancelUrl);
        String strAck = nvp.get("ACK").toString();
        if(strAck !=null && strAck.equalsIgnoreCase("Success")){
        	String token = nvp.get("TOKEN").toString();
        	booking.paypalToken = token;
        	booking.update();
            //' return Redirect url  to paypal.com
            return config.paypalUrl + token;
        }
        else{
            // Display a user friendly Error on the page using any of the following error information returned by PayPal
            String ErrorCode = nvp.get("L_ERRORCODE0").toString();
            String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
            String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
            String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
            Logger.error("Paypal Errors: %s Code: %s", ErrorLongMsg, ErrorCode);
            return null;
        }
	}
	
	public static Boolean confirmPayment(Booking booking, String token, String payerID){
		Map<String, String> nvp = doExpressCheckOutPayment(token, payerID, booking.totalSalePrice.toString());
		String strAck = nvp.get("ACK").toString();
        if(strAck !=null && strAck.equalsIgnoreCase("Success") ||strAck.equalsIgnoreCase("SuccessWithWarning")){
        	booking.transactionId =nvp.get("TRANSACTIONID").toString();
        	booking.orderTime = nvp.get("PAYMENTINFO_0_ORDERTIME").toString();
        	booking.amt= nvp.get("PAYMENTINFO_0_AMT").toString();
        	booking.currencyCode =  nvp.get("PAYMENTINFO_0_CURRENCYCODE").toString();
        	booking.feeAmt = nvp.get("FEEAMT").toString();
        	booking.taxAmt = nvp.get("PAYMENTINFO_0_TAXAMT").toString();
        	booking.paymentStatus = nvp.get("PAYMENTINFO_0_PAYMENTSTATUS").toString();
        	booking.payed = true;
        	booking.canceled = false;
        	booking.pending = false;
        	booking.update();
        	return Boolean.TRUE;

        }
        else{
            // Display a user friendly Error on the page using any of the following error information returned by PayPal
            String ErrorCode = nvp.get("L_ERRORCODE0").toString();
            String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
            String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
            String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
            Logger.error("Paypal Errors: %s Code: %s", ErrorLongMsg, ErrorCode);
            return Boolean.FALSE;
        }
	}
	
	/**
	 * Function to perform the SetExpressCheckout API call
	 * @param booking
	 * @return Returns a HashMap object containing the response from the server.
	 * Inputs:
	 *		paymentAmount:  	Total value of the shopping cart
	 *		currencyCodeType: 	Currency code value the PayPal API
	 *		paymentType: 		paymentType has to be one of the following values: Sale or Order or Authorization
	 */
	public static Map<String, String> callSetExpressCheckout( Booking booking, String cancelUrl){
		Map<String, Object> params = new HashMap<String, Object>(); 
		// Add login infos 
		params.put("USER", config.user); 
		params.put("PWD", config.password); 
		params.put("SIGNATURE", config.signature); 
		params.put("METHOD", "SetExpressCheckout"); 
		params.put("VERSION", config.VERSION); 
		params.put("PAYMENTACTION", PAYMENTACTION_SALE);
		params.put("AMT", booking.totalSalePrice); 
		params.put("ReturnUrl", config.returnUrl); 
		params.put("CANCELURL", cancelUrl); 
		params.put("CURRENCYCODE", "EUR"); 
		params.put("NOSHIPPING", "1"); // No shipping address 
		params.put("ADDROVERRIDE", "0");
		params.put("HDRIMG", "http://d2f5bmx5jz1oq8.cloudfront.net/web/logo_black_300.png");
		params.put("HDRBACKCOLOR", "000000");
		params.put("PAYFLOWCOLOR", "000000");
		params.put("BRANDNAME", "Club ReallyLateBooking");
		params.put("CUSTOMERSERVICENUMBER", "911 86 30 81");
		params.put("L_PAYMENTREQUEST_n_ITEMCATEGORY0", "Digital");
		params.put("SOLUTIONTYPE", "Sole"); // No paypal account needed 
		params.put("LANDINGPAGE", "Billing"); // Non login page 
		params.put("CHANNELTYPE", "Merchant"); 
		params.put("PAYMENTREQUEST_0_AMT", booking.totalSalePrice); 
		params.put("PAYMENTREQUEST_0_CURRENCYCODE", "EUR"); 
		params.put("PAYMENTREQUEST_0_CUSTOM", booking.hotelName); 
		params.put("PAYMENTREQUEST_0_PAYMENTACTION", "Sale"); 
		params.put("GIFTMESSAGEENABLE", "0"); 
		params.put("GIFTRECEIPTENABLE", "0"); 
		params.put("GIFTWRAPENABLE", "0"); 
		params.put("L_PAYMENTREQUEST_0_NAME0",  booking.hotelName); 
		params.put("L_PAYMENTREQUEST_0_AMT0", booking.totalSalePrice); 
		params.put("L_PAYMENTREQUEST_0_QTY0", "1"); 
		
		String back = UrlConnectionHelper.prepareRequest(config.apiEndpoint, createQueryString(params), "application/x-www-form-urlencoded");

		// Parse result 
		Map<String,String[]> r = UrlEncodedParser.parse(back); 
		Map<String,String> result = new HashMap<String,String>(); 
		for(String k : r.keySet()) { 
		    result.put(k, r.get(k)[0]); 
		} 
		Logger.debug("Response SetExpressCheckout: %s", result.toString());
		
		return result;
	}

	
	/*********************************************************************************
	  * confirmPayment: Function to perform the DoExpressCheckoutPayment API call
	  * Inputs:  token , payerId, amount
	  * Output: Returns a HashMap object containing the response from the server.
	*********************************************************************************/
	public static Map<String, String> doExpressCheckOutPayment( String token, String payerID, String finalPaymentAmount){
		Map<String, Object> params = new HashMap<String, Object>(); 
		// Add login infos 
		params.put("USER", Play.configuration.get("paypal.api.username").toString()); 
		params.put("PWD", Play.configuration.get("paypal.api.password").toString()); 
		params.put("SIGNATURE", Play.configuration.get("paypal.api.signature").toString()); 
		params.put("METHOD", "DoExpressCheckoutPayment"); 
		params.put("VERSION", "86"); 
		params.put("PAYMENTACTION", PAYMENTACTION_SALE);
		params.put("AMT", finalPaymentAmount); 
		params.put("TOKEN", token); 
		params.put("PAYERID", payerID); 
		params.put("CURRENCYCODE", "EUR"); 
		
		
		// Call paypal 
		String back = UrlConnectionHelper.prepareRequest(config.apiEndpoint, createQueryString(params), "application/x-www-form-urlencoded");

		// Parse result 
		Map<String,String[]> r = UrlEncodedParser.parse(back); 
		Map<String,String> result = new HashMap<String,String>(); 
		for(String k : r.keySet()) { 
		    result.put(k, r.get(k)[0]); 
		} 
		Logger.debug("Response DoExpressCheckoutPayment: %s", result.toString());
		
	    return result;
	    
	}

	
	private static String createQueryString(Map<String, Object> parameters) {
        StringBuilder sb = new StringBuilder();
        for (String key : parameters.keySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            Object value = parameters.get(key);

            if (value != null) {
                if (value instanceof Collection<?> || value.getClass().isArray()) {
                    Collection<?> values = value.getClass().isArray() ? Arrays.asList((Object[]) value) : (Collection<?>) value;
                    boolean first = true;
                    for (Object v : values) {
                        if (!first) {
                            sb.append("&");
                        }
                        first = false;
                        sb.append(encode(key)).append("=").append(encode(v.toString()));
                    }
                } else {
                    sb.append(encode(key)).append("=").append(encode(parameters.get(key).toString()));
                }
            }
        }
        return sb.toString();
    }
	
	private static String encode(String part) {
         try {
             return URLEncoder.encode(part, Play.defaultWebEncoding);
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
     }
}


