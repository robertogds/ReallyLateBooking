package helper.getaroom;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import models.Deal;

import play.Play;

/**
 * Singleton containing GetARoom configuration data
 * @author pablopr
 *
 */
public class GetaroomConfig {
	public String apiUrl; 
	public String apiKey; 
	public String authToken; 
	public String sAfiliacio; 
	public String sSecacc; 
	public String usuario;
	public String payType;
	public String currency;
	public int bookingDays;
	
	
	public static GetaroomConfig instance = new GetaroomConfig();
	
	private GetaroomConfig(){
		this.apiUrl = Play.configuration.getProperty("getaroom.api.apiUrl");
		this.apiKey = Play.configuration.getProperty("getaroom.api.apiKey");
		this.authToken = Play.configuration.getProperty("getaroom.api.authToken");
		this.bookingDays = new Integer(Play.configuration.getProperty("getaroom.api.bookingDays"));
		this.currency = "EUR";
	}
	
	public String getPropertyByIdUrl(String uuid) throws UnsupportedEncodingException{
		return this.apiUrl + "/api/properties/" + uuid + ".xml?api_key="+ this.apiKey + "&auth_token=" + this.authToken;
	}
	
	public String getHotelAvailabilityByLocation(String location, Date checkin, Date checkout, int rooms, int adults) throws UnsupportedEncodingException{
		String locationEncoded = URLEncoder.encode(location, "UTF-8");
		return this.apiUrl + "/searches/hotel_availability?" +
				"destination=" + locationEncoded +
				"&transaction_id=test" +
				"&check_in=" + formatDate(checkin) + 
				"&check_out=" + formatDate(checkout) + 
				"&rooms=" + rooms + 
				"&adults=" + adults +
				"&currency="+ this.currency +
				"&api_key="+ this.apiKey + 
				"&auth_token=" + this.authToken;
	}
	
	private String formatDate(Date date){
		Format formatter = new SimpleDateFormat("MM/dd/yyyy");
		return formatter.format(date);
	}

	public String getHotelAvailabilityByHotelId(String uuid, Date checkin,
			Date checkout, int rooms, int adults) {
		
		return this.apiUrl + "/properties/" + uuid + "/room_availability?" +
		"&transaction_id=test" +
		"&check_in=" + formatDate(checkin) + 
		"&check_out=" + formatDate(checkout) + 
		"&rooms=" + rooms + 
		"&adults=" + adults +
		"&currency="+ this.currency +
		"&api_key="+ this.apiKey + 
		"&auth_token=" + this.authToken;

	}

	public String getHotelsAvailabilityByHotelId(Collection<Deal> deals,
			Date checkin, Date checkout, int rooms, int adults) {
		// TODO Auto-generated method stub
		//URL="$GAR_DOMAIN/room_availabilities?transaction_id=$GAR_TRANS_ID&check_in=$GAR_CHECK_IN_DATE&check_out=$GAR_CHECK_OUT_DATE&rooms=$GAR_ROOM_COUNT&adults=$GAR_ADULTS&cancellation_rules=$GAR_CANCELLATION_RULES&api_key=$GAR_API_KEY&auth_token=$GAR_AUTH_TOKEN"

		return null;
	}
}
