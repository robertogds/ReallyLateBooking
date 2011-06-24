package controllers.oauth;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import models.User;

import org.apache.commons.codec.digest.DigestUtils;

import play.Logger;
import play.libs.Codec;

import siena.sdb.ws.Base64;


/**
 * An algorithm to determine whether a message has a valid signature, a correct
 * version number, a fresh timestamp, etc.
 *
 * @author Pablo Pazos
 */
public class ApiSecurer {
	
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	public static Boolean validateSignature(String token, String message, String timestamp){
		
		return Boolean.FALSE;
	}
	
    /**
     * Check that the given message with the given secret is valid.
     * 
     * @throws IOException
     *             the message couldn't be read.
     * @throws URISyntaxException
     *             the message URL is invalid.
     */
    public static Boolean validateMessage(String baseUrl, String signedMessage, long timestamp, String key){
    	
    	User user = User.findByToken(key);
    	if (user == null){
    		Logger.debug("User not found with token: " + key);
    		return Boolean.FALSE;
    	}
    	if (!checkValidTimestamp(timestamp)){
    		return Boolean.FALSE;
    	}
    	
    	Logger.debug("APISECURER validate url: " + baseUrl);
    	String signed = calculateMD5(baseUrl, user.secret);
    	
    	Logger.debug("URL SIGNED: " + signed);
    	
    	return signedMessage.equals(signed);
    }
    
    
    public static Boolean checkValidTimestamp(long timestamp){
    	long miliseconds = Calendar.getInstance().getTimeInMillis();
    	//we add 10 hours 
    	long seconds = miliseconds + 64000000;
    	
    	Logger.debug("Timestamp now: " + seconds + " timestamp iphone:" + timestamp);
    	if (seconds > timestamp){
    		return Boolean.TRUE;
    	}
    	return Boolean.FALSE;
    }
    
    
    /**
     * Computes MD5 signature.
     * 

     * @param url
     * 		The url
     * @param  secret
     * 		The private secret
     * @return
     *      The base64-encoded MD5 signature.
     * @throws
     *     java.security.SignatureException when signature generation fails
     */
    public static String calculateMD5(String url, String secret){
        	
        	String signature =  url + secret;

            // base64-encode the md5
            String result = DigestUtils.md5Hex(signature);

        return result;
    }
    
    /**
     * Computes RFC 2104-compliant HMAC signature.
     * 
     * @param data
     *     The data to be signed.
     * @param key
     *     The signing key.
     * @return
     *      The base64-encoded RFC 2104-compliant HMAC signature.
     * @throws
     *     java.security.SignatureException when signature generation fails
     */
    public static String calculateRFC2104HMAC(String data, String key)
        throws java.security.SignatureException{
        String result;
        try {
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), 
                                                         HMAC_SHA1_ALGORITHM);
            
            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            
            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());
            
            // base64-encode the hmac
            result = Base64.encodeBytes(rawHmac);
        } 
        catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

}
