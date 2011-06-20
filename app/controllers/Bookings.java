package controllers;

import java.util.ArrayList;
import java.util.List;

import notifiers.Mails;
import helper.JsonHelper;
import helper.dto.BookingDTO;
import helper.dto.BookingStatusMessage;
import helper.dto.StatusMessage;

import com.google.gson.Gson;

import models.Booking;
import models.Deal;
import models.User;
import play.Logger;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;

public class Bookings extends Controller{
	
	@Before
	public static void checkLanguage(){
		Logger.debug("### HEADERS : " + request.headers.toString());
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
			BookingDTO bookingDto = new Gson().fromJson(body, BookingDTO.class);
			validateAndSave(bookingDto.toBooking());
		}
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
			Mails.hotelBookingConfirmation(booking);
			Mails.userBookingConfirmation(booking);
			
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
		deal.update();
	}
	

	
}
