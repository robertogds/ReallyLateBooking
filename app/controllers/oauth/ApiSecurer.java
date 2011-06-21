package controllers.oauth;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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
	
    /**
     * Check that the given message with the given secret is valid.
     * 
     * @throws IOException
     *             the message couldn't be read.
     * @throws URISyntaxException
     *             the message URL is invalid.
     */
    public static Boolean validateMessage(String message, String signedMessage, long timestamp, String key, String secret)
            throws IOException, URISyntaxException{
    	
    	if (!checkValidTimestamp(timestamp)){
    		return Boolean.FALSE;
    	}
    	
    	String signed = calculateMD5(message, key, timestamp, secret);
    	
    	return signedMessage.equals(signed);
    }
    
    
    public static Boolean checkValidTimestamp(long timestamp){
    	long miliseconds = Calendar.getInstance().getTimeInMillis();
    	long seconds = miliseconds / 1000;
    	if (seconds + 10 > timestamp){
    		return Boolean.TRUE;
    	}
    	return Boolean.FALSE;
    }
    
    
    /**
     * Computes MD5 signature.
     * 
     * @param data
     *     The data to be signed.
     * @param key
     *     The signing key.
     * @param timestamp
     * 		The timestamp
     * @param  secret
     * 		The private secret
     * @return
     *      The base64-encoded MD5 signature.
     * @throws
     *     java.security.SignatureException when signature generation fails
     */
    public static String calculateMD5(String data, String key, long timestamp, String secret){
        	
        	String signature =  data  + key  + timestamp + secret;

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
