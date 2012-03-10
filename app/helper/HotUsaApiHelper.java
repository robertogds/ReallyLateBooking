package helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Booking;
import models.City;
import models.Country;
import models.Deal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import play.Logger;
import play.data.validation.Validation;
import play.libs.XML;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public final class HotUsaApiHelper {
	
	private static final String APIURL = "http://xml.hotelresb2b.com/xml/listen_xml.jsp"; 
	private static final String sCodigousu = "RENG"; 
	private static final String sClausu = "xml269009"; 
	private static final String sAfiliacio = "VE"; 
	private static final String sSecacc = "54269"; 
	public static final String SPAIN = "ES";
	public static final String MADRID_PROV = "ESMAD";
	public static final String MADRID = "MADRID";
	public static final String ENCODING =  "ISO-8859-1";
	
	private static final String VISA = "Visa";
	private static final String MASTERCARD = "Mastercard";
	private static final String	AMEX = "American Express";
	
	private static final String HU_VISA = "VisaCard";
	private static final String HU_MASTERCARD = "MasterCard";
	private static final String	HU_AMEX = "AmExCard";
	
	private static final int DAYS = 1;

	private static Document prepareRequest(String wsReq){
		try {
			wsReq = URLEncoder.encode(wsReq, "UTF-8");
			String url = APIURL + "?codigousu="+ sCodigousu +"&clausu="+ sClausu +"&afiliacio="+ sAfiliacio +"&secacc="+ sSecacc+"&xml="+wsReq;
			URL parsedUrl = new URL(url);
			URLConnection urlConnection = parsedUrl.openConnection(); 
			urlConnection.setConnectTimeout(15000); 
			urlConnection.setReadTimeout(15000); 
			
			String xml = convertStreamToString(urlConnection.getInputStream());
			xml = StringUtils.trim(xml);
			Logger.debug("response code: " + xml);
			
    		Document doc = XML.getDocument(xml);
    		return doc;

        } catch (Exception e) {
           Logger.error("Error receiving xml from hotusa: " + e);
        } 
        
		return null;
	
	}
	
	public static String convertStreamToString(InputStream is)
	    throws IOException {
		/*
		 * To convert the InputStream to String we use the
		 * Reader.read(char[] buffer) method. We iterate until the
		 * Reader return -1 which means there's no more data to
		 * read. We use the StringWriter class to produce the string.
		 */
		if (is != null) {
		    Writer writer = new StringWriter();
		
		    char[] buffer = new char[1024];
		    try {
		        Reader reader = new BufferedReader(
		                new InputStreamReader(is, "ISO-8859-1"));
		        int n;
		        while ((n = reader.read(buffer)) != -1) {
		            writer.write(buffer, 0, n);
		        }
		    } finally {
		        is.close();
		    }
		    return writer.toString();
		} else {        
		    return "";
		}
	}

	private static String getAllHotelsByCityRequest(City city, Integer days){
		List<Deal> deals = Deal.findDealsFromHotUsaByCity(city);
		if (deals.isEmpty()){
			return null;
		}
		else{
			Country country = Country.findById(city.country.id);
			String hotelCodeList = "";
			for (Deal deal: deals){
				hotelCodeList += deal.hotelCode + "#";
			}
			
			Date today = DateHelper.getTodayDate();
			SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
			Calendar checkoutDate = Calendar.getInstance(); 
			checkoutDate.setTime(DateHelper.getFutureDay(days));
			
			String request =  "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> <!DOCTYPE peticion SYSTEM \"http://xml.hotelresb2b.com/xml/dtd/pet_disponibilidad_110.dtd\"> <peticion>" +
			"<tipo>110</tipo> <nombre>Disponibilidad Varias Habitaciones Regímenes</nombre> <agencia>ReallyLateBooking.com</agencia> <parametros>" +
			"<hotel>"+ hotelCodeList +"</hotel> <pais>"+ country.hotusaCode +"</pais> <provincia>"+city.hotusaProvCode+"</provincia> <poblacion>"+city.hotusaCode+"</poblacion> <categoria>0</categoria> <radio>9</radio> " +
			"<fechaentrada>"+sdf.format(today)+"</fechaentrada> <fechasalida>"+ sdf.format(checkoutDate.getTime()) +"</fechasalida> <marca></marca> <afiliacion>VE</afiliacion> " +
			"<usuario>939201</usuario> <numhab1>1</numhab1> <paxes1>2-0</paxes1> <numhab2>0</numhab2>" +
			"<paxes2>0</paxes2> <numhab3>0</numhab3> <paxes3>0</paxes3> <restricciones>0</restricciones>" +
			" <idioma>1</idioma> <duplicidad>1</duplicidad> <comprimido>0</comprimido> <informacion_hotel>0</informacion_hotel></parametros> </peticion>";
			
			return request;
		}
	}
	
	
	private static String getPriceByHotelRequest(List<Deal> deals, Integer days){
		String hotelCodeList = "";
		for (Deal deal: deals){
			hotelCodeList += deal.hotelCode + "#";
		}
		
		Date today = DateHelper.getTodayDate();
		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
		Calendar checkoutDate = Calendar.getInstance(); 
		checkoutDate.setTime(DateHelper.getFutureDay(days));
		
 		String request = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> <!DOCTYPE peticion SYSTEM \"http://xml.hotelresb2b.com/xml/dtd/pet_disponibilidad_5.dtd\">" +
 		"<peticion> <tipo>105</tipo> <nombre>Petición de Disponibilidad</nombre> <agencia>ReallyLateBooking.com</agencia> <parametros>" +
 		"<codishotel>"+ hotelCodeList +"</codishotel> <regimen>OB</regimen> <numhab1>1</numhab1> <paxes1>2-0</paxes1> <numhab2>0</numhab2> " +
 		"<paxes2>0-0</paxes2> <numhab3>0</numhab3> <paxes3>0-0</paxes3> <usuario>939201</usuario> <afiliacion>VE</afiliacion> " +
 		"<fechaentrada>"+sdf.format(today)+"</fechaentrada> <fechasalida>"+ sdf.format(checkoutDate.getTime()) +"</fechasalida> <idioma>1</idioma> <duplicidad>0</duplicidad>" +
 		"<marca/> </parametros></peticion>";
 		
		return request;
	}
	
	private static String reservationRequest(Booking booking){
		Date date = getDateFromString(booking.creditCardExpiry);
		SimpleDateFormat sdf = new SimpleDateFormat("mm-yy");
		String dateText = sdf.format(date);
		String [] splitArr = StringUtils.split(dateText, "-");
		String year = splitArr[1];
		String month = splitArr[0];
		String card = convertCardToHotUsa(booking.creditCardType);
		
 		String request = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <!DOCTYPE peticion SYSTEM \"http://xml.hotelresb2b.com/xml/dtd/pet_reserva_3.dtd\">" +
 				"<peticion> <nombre>Peticion de Reserva</nombre> <agencia>ReallyLateBooking.com</agencia> <tipo>202</tipo> <parametros>" +
 				"<codigo_hotel>"+ booking.deal.hotelCode +"</codigo_hotel> <nombre_cliente>"+ booking.creditCardName +"</nombre_cliente> <observaciones></observaciones>" +
 				" <num_mensaje /><forma_pago>12</forma_pago> <tipo_targeta>"+card+"</tipo_targeta>" +
 				" <num_targeta>"+ booking.creditCard +"</num_targeta> <mes_expiracion_targeta>"+ month +"</mes_expiracion_targeta> <ano_expiracion_targeta>"+ year +"</ano_expiracion_targeta>" +
 				" <titular_targeta>"+ booking.creditCardName +"</titular_targeta> <res>";
		request = request + "<lin>"+ booking.deal.bookingLine +"</lin> ";

 		if (booking.nights >= 2){
 			request = request + "<lin>"+ booking.deal.bookingLine2 +"</lin> ";
    	}
    	if (booking.nights >= 3){
    		request = request + "<lin>"+ booking.deal.bookingLine3 +"</lin> ";    	
    	}
    	if (booking.nights >= 4){
    		request = request + "<lin>"+ booking.deal.bookingLine4 +"</lin> ";    	
    	}
    	if (booking.nights == 5){
    		request = request + "<lin>"+ booking.deal.bookingLine5 +"</lin> ";    	
    	}
    	request = request + "</res> </parametros></peticion>";
 		
 		return request;
	}
	
	private static String confirmationRequest(String localizador){
 		String request = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> <!DOCTYPE peticion SYSTEM \" http://xml.hotelresb2b.com/xml/dtd/pet_reserva.dtd\">" +
 				" <peticion><nombre>Anulación/Confirmación de Reserva</nombre> <agencia>ReallyLateBooking.com</agencia> <tipo>3</tipo> <parametros>" +
 				"<localizador>"+ localizador +"</localizador><accion>AE</accion> </parametros></peticion>";
 		
 		return request;
	}
	
	private static String convertCardToHotUsa(String card){
		if (card.equals(VISA)){
			return HU_VISA;
		}
		if (card.equals(MASTERCARD)){
			return HU_MASTERCARD;
		}
		if (card.equals(AMEX)){
			return HU_AMEX;
		}
		return HU_VISA;
	}
	
	private static Date getDateFromString(String dateString){
		 String[] parsers = new String[] {"m/yyyy", "m'/'yyyy"};
		 Date date ;
		 try {
			 date = DateUtils.parseDate(dateString, parsers);
			 return date;
		} catch (ParseException e) {
			Logger.error("Error parsing expiration date ", e);
		} 
		return null;
	}
	
	public static void getHotelPricesByCityList(List<City> cities){
		for (City city : cities){
			String wsReq = getAllHotelsByCityRequest(city, DAYS);
			parseHotelPricesResponse110(wsReq, DAYS);
		}
	}
	
	public static void getHotelPrices(List<Deal> deals){
		String wsReq = getPriceByHotelRequest(deals, DAYS);
		parseHotelPricesResponse(wsReq, DAYS);
	}
	
	private static void parseHotelPricesResponse110(String wsReq, int bookingDays){
		Logger.debug("WSRequest: " + wsReq);
		Document xml = prepareRequest(wsReq);
		if (xml != null){
			if (xml.getElementsByTagName("hot") != null){
				int hotels = xml.getElementsByTagName("hot").getLength();
				Element hotelNode;
				Logger.debug("Hotels number: " + hotels);
				
				for (int hotel=0; hotel < hotels; hotel++){
					hotelNode = (Element)xml.getElementsByTagName("hot").item(hotel);
					String hotelCode = hotelNode.getElementsByTagName("cod").item(0).getTextContent();
					//Si se acepta el pago directo
					String pdr = hotelNode.getElementsByTagName("pdr").item(0).getTextContent();
					for (int day=0; day < bookingDays ; day++){
						Logger.debug("### Hotel day: " + day);
						if (hotelNode.getElementsByTagName("lin").item(day) != null){
							/* lin son Valores compactados de la disponibilidad, 
							 * que servirán para realizar la reserva. Una lin por noche*/
							String lin = hotelNode.getElementsByTagName("lin").item(day).getTextContent();
							
							String[] linArray = StringUtils.split(lin, "#");
							String status = linArray[6];
							String regime = linArray[5];
							String priceString = linArray[3];
							Logger.debug("PDR: " + pdr + " status: " + status + " priceString: " +  priceString + " regime:" + regime);	
							//We have dispo so we set price and dispo and continue with next day
							if (status.equals("OK") && (regime.equals("OB") || regime.equals("RO") || regime.equals("BB")) && pdr.equals("S")){
								Logger.debug("Hotel is Ok, code: " + hotelCode + " price: " + priceString + " breakfast included: " +  regime.equals("BB"));
								Float price = Float.parseFloat(priceString);
								BigDecimal priceRounded = new BigDecimal(price);
								priceRounded = priceRounded.setScale(0, RoundingMode.DOWN);
								int quantity = 1; //always 1 by now
								Boolean breakfastIncluded = regime.equals("BB") ;
								Deal.updateDealByCode(hotelCode, quantity, price.intValue(), breakfastIncluded, lin, day);
							}
							// we dont have dispo for current day
							else {
								//if current day is the first one, the hotel is marked as sold out 
								if (day == 0) {
									int quantity = 0; 
									Deal.updateDealByCode(hotelCode, quantity, null, null, lin, day);
									Logger.debug("Hotel is sold out for tonight: " + hotelCode);
								}
								//if is not the first day, we just update price
								else{
									Deal.updatePriceByCode(hotelCode, null, lin, day);
									Logger.debug("Hotel is sold out for tonight: " + hotelCode);
									
								}
								//we dont continue retrieving prices for next day
								Deal.cleanNextDays(hotelCode, day);
								break;
							}
						}
						else{
							Logger.debug("### No hotel for day: " + day);
						}
					}
				}
			}
		}
		else{
			//TODO
			Logger.error("Didnt receive a correct answer from HotUsa Api");
		}
	}
	private static void parseHotelPricesResponse(String wsReq, int bookingDays){
		Logger.debug("WSRequest: " + wsReq);
		Document xml = prepareRequest(wsReq);
		if (xml != null){
			if (xml.getElementsByTagName("hot") != null){
				int hotels = xml.getElementsByTagName("hot").getLength();
				Logger.debug("Hotels number: " + hotels);
				int i = 0;
				for (int hotel=0; hotel < hotels; hotel++){
					String hotelCode = xml.getElementsByTagName("cod").item(hotel).getTextContent();
					//Si se acepta el pago directo
					String pdr = xml.getElementsByTagName("pdr").item(hotel).getTextContent();
					for (int day=0; day < bookingDays ; day++){
						
						Logger.debug("### Hotel index : " + i + " day: " + day);
						
						if (xml.getElementsByTagName("lin").item(i) != null){
							/* lin son Valores compactados de la disponibilidad, 
							 * que servirán para realizar la reserva. Una lin por noche*/
							String lin = xml.getElementsByTagName("lin").item(i).getTextContent();
							
							String[] linArray = StringUtils.split(lin, "#");
							String status = linArray[6];
							String regime = linArray[5];
							String priceString = linArray[3];
							Logger.debug("PDR: " + pdr + " status: " + status + " priceString: " +  priceString + " regime:" + regime);	
							//We have dispo so we set price and dispo and continue with next day
							if (status.equals("OK") && (regime.equals("OB") || regime.equals("RO") || regime.equals("BB")) && pdr.equals("S")){
								Logger.debug("Hotel is Ok, code: " + hotelCode + " price: " + priceString + " breakfast included: " +  regime.equals("BB"));
								Float price = Float.parseFloat(priceString);
								BigDecimal priceRounded = new BigDecimal(price);
								priceRounded = priceRounded.setScale(0, RoundingMode.DOWN);
								int quantity = 1; //always 1 by now
								Boolean breakfastIncluded = regime.equals("BB") ;
								Deal.updateDealByCode(hotelCode, quantity, price.intValue(), breakfastIncluded, lin, day);
							}
							// we dont have dispo for current day
							else {
								//if current day is the first one, the hotel is marked as sold out 
								if (day == 0) {
									int quantity = 0; 
									Deal.updateDealByCode(hotelCode, quantity, null, null, lin, day);
									Logger.debug("Hotel is sold out for tonight: " + hotelCode);
								}
								//if is not the first day, we just update price
								else{
									Deal.updatePriceByCode(hotelCode, null, lin, day);
									Logger.debug("Hotel is sold out for tonight: " + hotelCode);
									
								}
								//we dont continue retrieving prices for next day
								Deal.cleanNextDays(hotelCode, day);
								i = i + (bookingDays - day);
								break;
							}
						}
						else{
							Logger.debug("### No hotel here: " + i + " day: " + day);
						}
						i++;
					}
				}
			}
		}
		else{
			//TODO
			Logger.error("Didnt receive a correct answer from HotUsa Api");
		}
	}
	
	public static String reservation(Booking booking){
		String wsReq = reservationRequest(booking);
		Logger.debug("WSRequest: " + wsReq);
		Document xml = prepareRequest(wsReq);
		if (xml != null){
			String status = xml.getElementsByTagName("estado").item(0).getTextContent();
			Logger.debug("Reservation status: " + status);
			if (status.equals("00")){
				String localizador = xml.getElementsByTagName("n_localizador").item(0).getTextContent();
				Logger.debug("Reservation is Ok, localizador: " + localizador);
				//PreReservation worked well, so we try confirmation
				return localizador;
			}
			else{
				Logger.error("Reservation couldnt be completed. Status received was not 00");
			}
		}
		else{
			Logger.error("Didnt receive a correct answer from HotUsa Api. Xml Response is null");
		}
		
		return null;
	}
	
	public static String confirmation(String localizador){
		String wsReq = confirmationRequest(localizador);
		Document xml = prepareRequest(wsReq);
		if (xml != null){
			String status = xml.getElementsByTagName("estado").item(0).getTextContent();
			Logger.debug("Confirmation status: " + status);
			
			if (status.equals("00")){
				localizador = xml.getElementsByTagName("localizador").item(0).getTextContent();
				Logger.debug("Confirmation is Ok, localizador: " + localizador);
				return localizador;
			}
			else{
				Logger.error("Reservation couldnt be completed");
			}
		}
		else{
			//TODO
			Logger.error("Didnt receive a correct answer from HotUsa Api");
		}
		return null;
	}
}


