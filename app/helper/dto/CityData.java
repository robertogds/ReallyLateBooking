package helper.dto;

import java.util.Calendar;
import java.util.Date;

import models.Booking;
import models.City;
import models.Deal;
import models.Statistic;

public class CityData {
	public int visits;
	public int bookings;
	public int hotels;
	public int activeDirectHotels;
	public double maxDiscount;
	public double minDiscount;
	
	public CityData(City city, Date start, Date end){
		this.visits = Statistic.countVisitsByCity(city, start, end);
		this.bookings = Booking.countBookingsByCity(city, start, end);
		this.hotels = Deal.countHotelsByCity(city,start, end);
		this.activeDirectHotels = Deal.countActiveDirectHotelsByCity(city, start, end);
		this.maxDiscount = Deal.findMaxDiscountByCity(city, start, end);
		this.minDiscount = Deal.findMinDiscountByCity(city, start, end);
	}
}