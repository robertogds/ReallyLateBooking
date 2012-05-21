package helper.bidobido;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;

import play.Logger;
import play.Play;

/**
 * Singleton containing BidoBido virtual tpv configuration data
 * @author pablopr
 */
public class BidoBidoConfig {
	private String apiUrl; //https://www.bidobido.com/pagos/bidopay_oculto.php 
	private String urlPayment; 
	private String urlKO; 
	private String urlOK; 
	private String terminal; //1
	private String language; //1
	private String bidoUserId; // identificador_bidobido="39512669";
	private String bidoPassword; //contrasena_metodo_pago="eo930eus";
	private String merchant; //comercio
	private String testMode; //1 yes, 0 no
	private String currency; // EUR = 1 
	private String transactionType; // 0
	
	public static BidoBidoConfig instance = new BidoBidoConfig();
	
	private BidoBidoConfig(){
		this.apiUrl = Play.configuration.getProperty("bidobido.api.apiUrl");
		this.urlKO = Play.configuration.getProperty("bidobido.api.urlKO");
		this.urlOK = Play.configuration.getProperty("bidobido.api.urlOK");
		this.terminal = Play.configuration.getProperty("bidobido.api.terminal");
		this.urlPayment = Play.configuration.getProperty("bidobido.api.urlPayment");
		this.language = Play.configuration.getProperty("bidobido.api.language");
		this.bidoUserId = Play.configuration.getProperty("bidobido.api.bidoUserId");
		this.bidoPassword = Play.configuration.getProperty("bidobido.api.bidoPassword");
		this.merchant = Play.configuration.getProperty("bidobido.api.merchant");
		this.testMode = Play.configuration.getProperty("bidobido.api.testMode");
		this.currency = Play.configuration.getProperty("bidobido.api.currency");
		this.transactionType = Play.configuration.getProperty("bidobido.api.transactionType");
	}
	
	public String getPaymentParams(String bookingId, String price, String email) {
		String params;
		try {
			params = "referencia="+ bookingId
				+ "&cantidad=" + price 
				+ "&moneda=" + this.currency
				+ "&terminal=" + this.terminal 
				+ "&URL_respuesta=" + URLEncoder.encode( this.urlPayment, "UTF-8")
				+ "&UrlKO=" + URLEncoder.encode(this.urlKO, "UTF-8") 
				+ "&UrlOK=" + URLEncoder.encode(this.urlOK, "UTF-8") 
				+ "&idioma=" + this.language
				+ "&empresa_id=" + this.bidoUserId
				+ "&tipo_transaccion=" + this.transactionType
				+ "&comercio=" + this.merchant
				+ "&ref_descrip=" + this.bidoUserId
				+ "&test=" + this.testMode
				+ "&firma=" + this.getHashKey(bookingId, price)
				+ "&email=" + email;
		} catch (UnsupportedEncodingException e) {
			Logger.error("Error encoding BidoBido payment params: %s", e);
			return null;
		}
		return params;
	}

	public String getPaymentUrl() {
		return this.apiUrl;
	}
	private String getHashKey(String bookingId, String price) {
		String message=price+bookingId+this.currency+this.transactionType+this.bidoUserId+this.bidoPassword+this.testMode;
		return getHash(message);
	}
	
	public static String getHash(String message) {
		String hash = "";
    	try{
    		byte[] buffer, digest;
    		buffer = message.getBytes();
    		MessageDigest md = MessageDigest.getInstance("SHA1");
    		md.update(buffer);
    		digest = md.digest();

	        for(byte aux : digest) {
	            int b = aux & 0xff;
	            if (Integer.toHexString(b).length() == 1) hash += "0";
	            hash += Integer.toHexString(b);
	        }

	    	}catch( NoSuchAlgorithmException e){
	    		Logger.error("Error haciendo hash:"+e.getMessage());
	    	}
        return hash;
    }
	
}