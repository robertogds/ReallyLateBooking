package models.dto;

import helper.mailchimp.MailChimpHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.Case;

import play.Logger;
import siena.Model;

import models.Booking;
import models.City;
import models.Deal;
import models.Statistic;
import models.User;

public class StatisticDTO {
	
    public LinkedHashMap<String, CityData> stats;
    public CityData allCities;
    public int registered;
    public int registeredWeb;
    public int registeredApp;
    public int dayStart;
    public int monthStart;
    public int yearStart;
    public int dayEnd;
    public int monthEnd;
    public int yearEnd;
	
	public StatisticDTO(Collection<City> cities, Calendar calStart, Calendar calEnd, boolean includePending) {
		Date start = calStart.getTime();
		Date end = calEnd.getTime();
		Logger.debug("Statistics Start Time : " + start.toString());
		Logger.debug("Statistics End Time : " + end.toString());
		this.dayStart = calStart.get(Calendar.DAY_OF_MONTH);
		this.monthStart = calStart.get(Calendar.MONTH) + 1;
		this.yearStart = calStart.get(Calendar.YEAR);
		this.dayEnd = calEnd.get(Calendar.DAY_OF_MONTH);
		this.monthEnd =  calEnd.get(Calendar.MONTH) + 1;
		this.yearEnd = calEnd.get(Calendar.YEAR);
	    this.registered = User.countNewUsersByDate(calStart, calEnd);
	    this.registeredWeb = User.countNewWebUsersByDate(calStart, calEnd);
	    this.registeredApp = this.registered - this.registeredWeb;
	    this.stats = new LinkedHashMap<String, CityData>(); 
	    
	    Collection<Booking> bookings = includePending ?  Booking.findAllBookingsByDate(start, end) : Booking.findAllNonPendingBookingsByDate(start, end);
	    this.allCities =  new CityData(bookings, start, end);
	    
		/*
		 * MAILCHIMP STATS
		 * 
		List<Booking> emailBookings = findBookingsFromSuscribers(bookings);
		this.allCities.bookingsEmail = emailBookings.size();
		for (Booking booking : emailBookings){
			this.allCities.totalCommissionEmail += booking.totalSalePrice - booking.netTotalSalePrice;
		}
	    */
		for (City city : cities){
			Collection<Booking> bookingsCity= new ArrayList<Booking>();
			Logger.debug("Adding bookings from city: %s", city.name);
			Collection<Booking> bookingsRoot = includePending ? Booking.findAllBookingsByDateAndCity(city,start, end) : Booking.findAllNonPendindBookingsByDateAndCity(city,start, end);
			bookingsCity.addAll(bookingsRoot);
			if (!city.isSimpleCity()){
				List<City> cityZones = City.findActiveCitiesByRoot(city.url);
				for (City location: cityZones) {
					Logger.debug("Adding bookings from city zone: %s", location.name);
					Collection<Booking> bookingsZone = includePending ? Booking.findAllBookingsByDateAndCity(location,start, end): Booking.findAllNonPendindBookingsByDateAndCity(location,start, end);
					bookingsCity.addAll(bookingsZone);
				}
			}
			stats.put(city.url, new CityData(bookingsCity, start, end));
		}
	}
	

	private List<Booking> findBookingsFromSuscribers(Collection<Booking> webBookings) {
		List<String> emailBookings = new ArrayList<String>();
		for (Booking booking : webBookings){
			String email = booking.userEmail != null ? booking.userEmail : booking.bookingForEmail;
			if (email!= null){
				emailBookings.add(email.toLowerCase().trim());
			}
		}
		List<String> bookers = MailChimpHelper.findBookingsFromSuscribers(emailBookings);
		List<Booking> bookingsFromEmail = new ArrayList<Booking>();
		List<String> bookersFound = new ArrayList<String>();
		for (String email : bookers){
			if (repeatedEmail(email, bookersFound)){
				Logger.debug("##Email booking already registered %s", email);
			}
			else{
				bookersFound.add(email);
				for (Booking booking : webBookings){
					String bookingEmail = booking.userEmail != null ? booking.userEmail : booking.bookingForEmail;
					Logger.debug("##Booking email is %s and suscriber email is %s", bookingEmail, email);
					if (email.trim().equalsIgnoreCase(bookingEmail.trim().toLowerCase())){
						bookingsFromEmail.add(booking);
					}
				}	
			}
		}
		
		
		return bookingsFromEmail;
	}

	private boolean repeatedEmail(String email, List<String> bookersFound) {
		for (String booking: bookersFound){
			if (booking.trim().toLowerCase().equalsIgnoreCase(email.trim())){
				return true;
			}
		}
		return false;
	}


	//TODO delete this
	private void createFakeBookings() {
		Booking booking = Booking.findById(new Long(47));
		int i = 0;
		while (i < 60){
			Booking newBooking  = booking;
			newBooking.id = null;
			newBooking.insert();
		}		
	}
	
}
