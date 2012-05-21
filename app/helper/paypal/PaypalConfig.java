package helper.paypal;

import helper.bidobido.BidoBidoConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import play.Logger;
import play.Play;

public class PaypalConfig {
	
	public String apiEndpoint; //https://www.bidobido.com/pagos/bidopay_oculto.php 
	public String paypalUrl; 
	public String cancelurl; 
	public String returnUrl; 
	public String user;
	public String password;
	public String signature;
	public final String VERSION = "86";
	
	public static PaypalConfig instance = new PaypalConfig();
	
	private PaypalConfig(){
		this.apiEndpoint = Play.configuration.getProperty("paypal.api.endpoint");
		this.paypalUrl = Play.configuration.getProperty("paypal.api.url");
		this.cancelurl = Play.configuration.getProperty("paypal.cancelPay");
		this.returnUrl = Play.configuration.getProperty("paypal.returnUrl");
		this.user =  Play.configuration.getProperty("paypal.api.username");
		this.password =  Play.configuration.getProperty("paypal.api.password");
		this.signature =  Play.configuration.getProperty("paypal.api.signature");

	}
	

}
