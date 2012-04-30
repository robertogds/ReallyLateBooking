package helper.hotusa;

import play.Play;

/**
 * Singleton containing hotusa or restel configuration data
 * @author pablopr
 *
 */
public class HotusaConfig {
	public String apiUrl; 
	public String sCodigousu; 
	public String sClausu; 
	public String sAfiliacio; 
	public String sSecacc; 
	public String usuario;
	public String payType;
	public int bookingDays;
	
	public static HotusaConfig instance = new HotusaConfig();
	
	private HotusaConfig(){
		this.apiUrl = Play.configuration.getProperty("hotusa.api.apiUrl");
		this.sCodigousu = Play.configuration.getProperty("hotusa.api.sCodigousu");
		this.sClausu = Play.configuration.getProperty("hotusa.api.sClausu");
		this.sAfiliacio = Play.configuration.getProperty("hotusa.api.sAfiliacio");
		this.sSecacc = Play.configuration.getProperty("hotusa.api.sSecacc");
		this.usuario = Play.configuration.getProperty("hotusa.api.usuario");
		this.payType = Play.configuration.getProperty("hotusa.api.paytype");
		this.bookingDays = new Integer(Play.configuration.getProperty("hotusa.api.bookingDays"));
	}
	
	public String getRequestUrl(String wsReq){
		return this.apiUrl + "?codigousu="+ this.sCodigousu +"&clausu=" 
		+ this.sClausu +"&afiliacio="+ this.sAfiliacio +"&secacc="
		+ this.sSecacc+"&xml="+wsReq;
	}
	
}
