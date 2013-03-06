package helper.getaroom;

import helper.UrlConnectionHelper;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.Deal;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import play.Logger;
import play.libs.XML;

public final class GetaroomApi {
	
	private static GetaroomConfig config = GetaroomConfig.instance;
	
	public static List<Deal> getHotelsByCity(String city, Date checkin, Date checkout) throws UnsupportedEncodingException{
		List<Deal> deals = new ArrayList<Deal>();
		int rooms = 1;
		int adults = 2;
		String url = config.getHotelAvailabilityByLocation(city, checkin, checkout, rooms, adults);
		String response = prepareRequest(url);
		response = StringUtils.trim(response);
		Document xml = XML.getDocument(response);
		if (xml != null){
			if (xml.getElementsByTagName("success") != null){
				int hotels = xml.getElementsByTagName("hotel_stay").getLength();
				Logger.debug("Hotels number: " + hotels);
				int i = 0;
				for (int hotel=0; hotel < hotels; hotel++){
					Deal deal = new Deal();
					deal.hotelName = xml.getElementsByTagName("title").item(hotel).getTextContent();
					deal.hotelCode = xml.getElementsByTagName("uuid").item(hotel).getTextContent();
					deal.bookingHotelCode = xml.getElementsByTagName("lowest-average").item(hotel) != null ? xml.getElementsByTagName("lowest-average").item(hotel).getTextContent() : "";
					deal.bookingLine = xml.getElementsByTagName("url").item(hotel).getTextContent();
					deals.add(deal);
					i++;
				}
			}
		}
		return deals;
	}
	
	public static Integer getBestPriceByUuid(String uuid, Date checkin,
			Date checkout) {
		int rooms = 1;
		int adults = 2;
		String url = config.getHotelAvailabilityByHotelId(uuid, checkin, checkout, rooms, adults);
		Logger.debug("getBestPriceByUuid %s", url);
		String response = prepareRequest(url);
		response = StringUtils.trim(response);
		Document xml = XML.getDocument(response);
		if (xml != null){
			if (xml.getElementsByTagName("success") != null){
				String bar = xml.getElementsByTagName("original-average").item(0).getTextContent();
				if (StringUtils.isEmpty(bar)){
					bar = xml.getElementsByTagName("lowest-average").item(0).getTextContent();
				}
				Logger.debug("Hotels number: " + bar);
				if (StringUtils.isNotEmpty(bar)){
					Float priceBar = Float.parseFloat(bar);
					return priceBar.intValue();
				}
			}
		}
		return null;
	}
	
	
	public static HashMap<String, Integer> getBestPriceByUuids(
			Collection<Deal> deals, Date checkin, Date checkout) {
		int rooms = 1;
		int adults = 2;
		String url = config.getHotelsAvailabilityByHotelId(deals, checkin, checkout, rooms, adults);
		Logger.debug("getBestPriceByUuid %s", url);
		String response = prepareRequest(url);
		response = StringUtils.trim(response);
		Document xml = XML.getDocument(response);
		if (xml != null){
			if (xml.getElementsByTagName("success") != null){
				String bar = xml.getElementsByTagName("original-average").item(0).getTextContent();
				Logger.debug("Hotels number: " + bar);
				Float priceBar = Float.parseFloat(bar);
				//return priceBar.intValue();
			}
		}
		return null;
	}
	
	/******* PRIVATE HELPER METHODS *********/

	private static String prepareRequest(String url) {
		return UrlConnectionHelper.doSecureGetIgnoreCertificate(url);
	}

	
}


