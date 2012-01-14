package models.dto;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import play.Logger;

import models.Booking;
import models.City;
import models.Deal;
import models.Statistic;
import models.User;

public class StatisticDTO {
	
    public HashMap<City, CityData> cities;
    public HashMap<City, CityData> citiesB;
    public int registered;
    public int hotels;
    public int activeDirectHotels;
    public int bookings;
    public int dayStart;
    public int monthStart;
    public int yearStart;
    public int dayEnd;
    public int monthEnd;
    public int yearEnd;
    public int registeredB;
    public int hotelsB;
    public int activeDirectHotelsB;
    public int bookingsB;
    public int dayStartB;
    public int monthStartB;
    public int yearStartB;
    public int dayEndB;
    public int monthEndB;
    public int yearEndB;

	public StatisticDTO(Collection<City> cities, Calendar calStart, Calendar calEnd, Calendar calStartB, Calendar calEndB) {
		this(cities, calStart,  calEnd);
		Date startB = calStartB.getTime();
		Date endB = calEndB.getTime();
		Logger.debug("Statistics Start Time B: " + startB.toString());
		Logger.debug("Statistics End Time B: " + endB.toString());
		this.dayStartB = calStartB.get(Calendar.DAY_OF_MONTH);
		this.monthStartB = calStartB.get(Calendar.MONTH) + 1;
		this.yearStartB = calStartB.get(Calendar.YEAR);
		this.dayEndB = calEndB.get(Calendar.DAY_OF_MONTH);
		this.monthEndB =  calEndB.get(Calendar.MONTH) + 1;
		this.yearEndB = calEndB.get(Calendar.YEAR);
		this.registeredB = User.countNewUsersByDate(calStartB, calEndB);
		this.citiesB = new HashMap<City, CityData>();
		for (City city : cities){
			this.citiesB.put(city, new CityData(city, startB, endB));
		}
	}
	
	public StatisticDTO(Collection<City> cities, Calendar calStart, Calendar calEnd) {
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
		this.cities = new HashMap<City, CityData>();
		for (City city : cities){
			this.cities.put(city, new CityData(city, start, end));
		}
	}
}
