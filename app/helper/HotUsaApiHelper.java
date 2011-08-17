package helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import models.Booking;
import models.Deal;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import play.Logger;
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
	
	private static Document prepareRequest(String wsReq){
		try {
			wsReq = URLEncoder.encode(wsReq, "UTF-8");
			String url = APIURL + "?codigousu="+ sCodigousu +"&clausu="+ sClausu +"&afiliacio="+ sAfiliacio +"&secacc="+ sSecacc+"&xml="+wsReq;
			URL parsedUrl = new URL(url);
			URLConnection urlConnection = parsedUrl.openConnection(); 
			urlConnection.setConnectTimeout(10000); 
			urlConnection.setReadTimeout(10000); 
			
			//InputSource source = new InputSource(urlConnection.getInputStream());
            //source.setEncoding("ISO-8859-1");
			 
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

/*	
	private static Document prepareRequest(String wsReq){
		WSRequest request = WS.url("http://xml.hotelresb2b.com/xml/listen_xml.jsp");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigousu", "RENG");
		parameters.put("clausu", "xml269009");
		parameters.put("afiliacio", "VE");
		parameters.put("secacc", "54269");
        parameters.put("xml", wsReq);
		request.parameters = parameters;
		request.setHeader("content-type", "text/xml");
		try {
			
            InputSource source = new InputSource(url.openStream());
            source.setEncoding("ISO-8859-1");
    		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source); 
    		return doc;

        } catch (Exception e) {
           Logger.error("Error receiving xml from hotusa: " + e);
        } 
        
		return null;
	}
*/
	private static String getAllHotelsByCity(String country, String prov, String city){
		String request =  "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> <!DOCTYPE peticion SYSTEM \"http://xml.hotelresb2b.com/xml/dtd/pet_disponibilidad_110.dtd\"> <peticion>" +
		"<tipo>110</tipo> <nombre>Disponibilidad Varias Habitaciones Regímenes</nombre> <agencia>ReallyLateBooking.com</agencia> <parametros>" +
		"<hotel></hotel> <pais>"+SPAIN+"</pais> <provincia>"+MADRID_PROV+"</provincia> <poblacion>"+MADRID+"</poblacion> <categoria>0</categoria> <radio>9</radio> " +
		"<fechaentrada>08/15/2011</fechaentrada> <fechasalida>08/16/2011</fechasalida> <marca></marca> <afiliacion>VE</afiliacion> " +
		"<usuario>939201</usuario> <numhab1>1</numhab1> <paxes1>2-0</paxes1> <numhab2>0</numhab2>" +
		"<paxes2>0</paxes2> <numhab3>0</numhab3> <paxes3>0</paxes3> <restricciones>1</restricciones>" +
		" <idioma>1</idioma> <duplicidad>1</duplicidad> <comprimido>0</comprimido> <informacion_hotel>0</informacion_hotel></parametros> </peticion>";
		
		return request;
	}
	
	
	private static String getPriceByHotelRequest(List<Deal> deals){
		String hotelCodeList = "";
		for (Deal deal: deals){
			hotelCodeList += deal.hotelCode + "#";
		}
		
		Calendar today = Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
		Calendar tomorrow = Calendar.getInstance(); 
		tomorrow.add(Calendar.DAY_OF_MONTH, 1 ); 
		
 		String request = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> <!DOCTYPE peticion SYSTEM \"http://xml.hotelresb2b.com/xml/dtd/pet_disponibilidad_5.dtd\">" +
 		"<peticion> <tipo>105</tipo> <nombre>Petición de Disponibilidad</nombre> <agencia>ReallyLateBooking.com</agencia> <parametros>" +
 		"<codishotel>"+ hotelCodeList +"</codishotel> <regimen>OB</regimen> <numhab1>2</numhab1> <paxes1>2-0</paxes1> <numhab2>0</numhab2> " +
 		"<paxes2>0-0</paxes2> <numhab3>0</numhab3> <paxes3>0-0</paxes3> <usuario>939201</usuario> <afiliacion>VE</afiliacion> " +
 		"<fechaentrada>"+sdf.format(today.getTime())+"</fechaentrada> <fechasalida>"+ sdf.format(tomorrow.getTime()) +"</fechasalida> <idioma>1</idioma> <duplicidad>0</duplicidad>" +
 		"<marca/> </parametros></peticion>";
 		
		return request;
	}
	
	private static String reservationRequest(Booking booking){
 		String request = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> <!DOCTYPE peticion SYSTEM \"http://xml.hotelresb2b.com/xml/dtd/pet_reserva_3.dtd\">" +
 				"<peticion> <nombre>Peticion de Reserva</nombre> <agencia>ReallyLateBooking.com</agencia> <tipo>202</tipo> <parametros>" +
 				"<codigo_hotel>"+ booking.deal.hotelCode +"</codigo_hotel> <nombre_cliente>"+ booking.creditCardName +"</nombre_cliente> <observaciones></observaciones>" +
 				" <num_mensaje /> <num_expediente>AB1245</num_expediente> <forma_pago>12</forma_pago> <tipo_targeta>VisaCard</tipo_targeta>" +
 				" <num_targeta>"+ booking.creditCard +"</num_targeta> <mes_expiracion_targeta>03</mes_expiracion_targeta> <ano_expiracion_targeta>13</ano_expiracion_targeta>" +
 				" <titular_targeta>"+ booking.creditCardName +"</titular_targeta> <res>" +
 				"<lin>"+ booking.deal.bookingLine +"</lin> " +
 				"</res> </parametros></peticion>";
 		
 		return request;
	}
	
	public static void getHotelPrices(List<Deal> deals){
		String wsReq = getPriceByHotelRequest(deals);
		Logger.debug("WSRequest: " + wsReq);
		Document xml = prepareRequest(wsReq);
		if (xml != null){
			if (xml.getElementsByTagName("hot") != null){
				int hotels = xml.getElementsByTagName("hot").getLength();
				Logger.debug("Hotels number: " + hotels);
				for (int i=0; i < hotels; i++){
					String hotelCode = xml.getElementsByTagName("cod").item(i).getTextContent();
					String priceString = xml.getElementsByTagName("prr").item(i).getTextContent();
					String status = xml.getElementsByTagName("esr").item(i).getTextContent();
					String regime = xml.getElementsByTagName("reg").item(i).getTextContent();
					/* lin son Valores compactados de la disponibilidad, 
					 * que servirán para realizar la reserva. Una lin por noche, por ahora una siempre */
					String lin = xml.getElementsByTagName("lin").item(i).getTextContent();
					//Si se acepta el pago directo
					String pdr = xml.getElementsByTagName("pdr").item(i).getTextContent();
					
					if (status.equals("OK") && regime.equals("OK") && pdr.equals("S")){
						Logger.debug("Hotel is Ok, code: " + hotelCode + " price: " + priceString);
						Float price = Float.parseFloat(priceString);
						int quantity = 1; //always 1 by now
						Deal.updateDealByCode(hotelCode, quantity, price, lin);
					}
					else{
						Logger.debug("Hotel is sold out for tonight");
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
		Document xml = prepareRequest(wsReq);
		if (xml != null){
			String status = xml.getElementsByTagName("estado").item(0).getTextContent();
			Logger.debug("Reservation status: " + status);
			
			if (status.equals("00")){
				String localizador = xml.getElementsByTagName("N_localizador").item(0).getTextContent();
				Logger.debug("Reservation is Ok, localizador: " + localizador);
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
