package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import notifiers.Mails;
import helper.JsonHelper;
import helper.dto.BookingDTO;
import helper.dto.BookingStatusMessage;
import helper.dto.StatusMessage;
import helper.dto.UserStatusMessage;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import controllers.oauth.ApiSecurer;

import models.Booking;
import models.Deal;
import models.User;
import play.Logger;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;

public class Bookings extends Controller{
	
	@Before
	static void checkSignature(){
		Boolean correct = ApiSecurer.checkApiSignature(request);
		if (!correct){
			Logger.debug("Invalid signature ");
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", "Invalid api signature. Contact hola@reallylatebooking.com"));
			renderJSON(json);
		}
	}
	
	public static void listByUser(Long userId){
		User user = User.findById(userId);
		List<Booking> bookingList = Booking.findByUser(user);
		Logger.debug("Bookings for user: " + userId + " are : " + bookingList.size());
		List<BookingDTO> bookingDtoList = new ArrayList<BookingDTO>();
		for(Booking booking : bookingList){
			bookingDtoList.add(new BookingDTO(booking));
		}
		String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(bookingDtoList);
		renderJSON(json);
	}
	
	
	public static void create(String json) {
		String body = json != null ? json : params.get("body");
		Logger.debug("Create booking " + body);	
		if (body != null){
			BookingDTO bookingDto;
			try {
				bookingDto = new Gson().fromJson(body, BookingDTO.class);
				validateAndSave(bookingDto.toBooking());
			} catch (JsonParseException e) {
				Logger.error("Error parsing booking json", e);
				String messageJson = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
						new StatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", Messages.get("booking.validation.all.required")));
				renderJSON(messageJson);
			}
		}
	}
	
	public static void doHotUsaBooking(Booking booking){
		Logger.debug("Aqui estmos");
		WSRequest request = WS.url("http://xml.hotelresb2b.com/xml/listen_xml.jsp");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigousu", "RENG");
		parameters.put("clausu", "xml269009");
		parameters.put("afiliacio", "VE");
		parameters.put("secacc", "54269");
		
		String wsReq = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> <!DOCTYPE peticion SYSTEM \"http://xml.hotelresb2b.com/xml/dtd/pet_disponibilidad_110.dtd\"> <peticion>" +
		 		"<tipo>110</tipo> <nombre>Disponibilidad Varias Habitaciones Regímenes</nombre> <agencia>Agencia de Prueba</agencia> <parametros>" +
		 		"<hotel></hotel> <pais>ES</pais> <provincia>ESMAD</provincia> <poblacion>MADRID</poblacion> <categoria>4</categoria> <radio>9</radio> " +
		 		"<fechaentrada>08/13/2011</fechaentrada> <fechasalida>08/14/2011</fechasalida> <marca></marca> <afiliacion>VE</afiliacion> " +
		 		"<usuario>939201</usuario> <numhab1>1</numhab1> <paxes1>2-0</paxes1> <numhab2>0</numhab2>" +
		 		"<paxes2>0</paxes2> <numhab3>0</numhab3> <paxes3>0</paxes3> <restricciones>1</restricciones>" +
		 		" <idioma>1</idioma> <duplicidad>1</duplicidad> <comprimido>0</comprimido> <informacion_hotel>0</informacion_hotel></parametros> </peticion>";
		
		parameters.put("xml", wsReq);
		
		request.parameters = parameters;
		
		HttpResponse res = request.setHeader("content-type", "text/xml").get();
		int status = res.getStatus();
		Logger.debug("Response status: " + status);
		
		Document xml = res.getXml();
		Logger.debug("XML: " + xml.toString());
		
		//String rate = xml.getElementsByTagName("ConversionRateResult").item(0).getTextContent();
		
		
	}
	
	private static void validateAndSave(@Valid Booking booking){
		booking.validate(); //validate object and fill errors map if exist
		if (!validation.hasErrors()){ 
			Logger.debug("Valid booking");
			booking.insert();
			updateDealRooms(booking.deal.id, booking.rooms);
			//we need to fetch all the info form user and deal 
			booking.deal = Deal.findById(booking.deal.id);
			booking.user = User.findById(booking.user.id);
			Mails.userBookingConfirmation(booking);
			Mails.hotelBookingConfirmation(booking);
			
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new BookingStatusMessage(Http.StatusCode.CREATED, "CREATED", Messages.get("booking.create.correct"), booking));
			renderJSON(json);
		}
		else{
			Logger.debug("Invalid booking: " + validation.errors().toString());
			String json = JsonHelper.jsonExcludeFieldsWithoutExposeAnnotation(
					new BookingStatusMessage(Http.StatusCode.INTERNAL_ERROR, "ERROR", validation.errors().toString(), booking));
			renderJSON(json);
		}
	}
	
	private static void updateDealRooms(Long dealId, Integer rooms){
		Logger.debug("Deal id: " + dealId);
		Deal deal = Deal.findById(dealId);
		deal.quantity = deal.quantity - rooms;
		if (deal.quantity == 0){
			deal.active = Boolean.FALSE;
		}
		deal.update();
	}
	
	
}
