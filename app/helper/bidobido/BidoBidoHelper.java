package helper.bidobido;

import helper.UrlConnectionHelper;
import models.Booking;

import org.w3c.dom.Document;

import play.Logger;
import play.libs.XML;

public final class BidoBidoHelper {

	private static BidoBidoConfig config = BidoBidoConfig.instance;
	
	
	/*
	 * Connects to bidobido and receives the payment url
	 * <respuesta><url>https://urlfacilidadparaelpago/continuacion</url></respuesta>
	 * <respuesta><error>el campo x da el error ...</error></respuesta>  
	 */
	public static String getPaymentUrl(Booking booking){
		String bidobidoUrl = config.getPaymentUrl();
		String params = config.getPaymentParams(booking.code, booking.totalSalePrice.toString(), "pablo@reallylatebooking.com");
		String response = UrlConnectionHelper.prepareRequest(bidobidoUrl, params, UrlConnectionHelper.CONTENT_XML);
		Document xml = XML.getDocument(response);
		if (xml != null){
			if (xml.getElementsByTagName("error").item(0) == null ){
				String url = xml.getElementsByTagName("url").item(0).getTextContent();
				Logger.debug("BidoBido payment url: " + url);
				//PreReservation worked well, so we try confirmation
				return url;
			}
			else{
				Logger.error("BidoBido error: %s", (xml.getElementsByTagName("error").item(0).getTextContent()));
			}
		}
		else{
			Logger.error("Didnt receive a correct answer from BidoBido Api. Xml Response is null");
		}
		
		return null;
	}
	
}