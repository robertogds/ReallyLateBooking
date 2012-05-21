package models.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import play.Logger;

import models.Booking;
import models.City;
import models.Deal;
import models.Statistic;
import models.User;

public class CityData {
	public int visits;
	public int hotels;
    public int activeDirectHotels;
    public int bookings = 0;
    public int bookingsMonday = 0;
    public int bookingsTuesday= 0;
    public int bookingsWednesday= 0;
    public int bookingsThursday= 0;
    public int bookingsFriday= 0;
    public int bookingsSaturday= 0;
    public int bookingsSunday= 0;
    public int bookingsWeb= 0;
    public int bookingsApps= 0;
    public int totalusers= 0;
    public float totalCommission = 0;
    public float totalPrice = 0;
    public float nightsPerBookingAvg=0;
    public float priceAvg=0;
    public float commisionAvg=0;
    public float bookingPerUserAvg=0;
    public Date startDate;

	
	public CityData(Collection<Booking> bookings){
		this.initializaData(bookings);
	}
	
	public void initialiceVisits(City city, Date start, Date end){
		this.visits = Statistic.countVisitsByCity(city, start, end);
		this.bookings = Booking.countBookingsByCity(city, start, end);
		this.hotels = Deal.countHotelsByCity(city,start, end);
		this.activeDirectHotels = Deal.countActiveDirectHotelsByCity(city, start, end);
	}
	
	private void initializaData(Collection<Booking> bookings){
			this.bookings = bookings.size();
			 float nights = 0;
			 this.totalPrice = 0;
			 List<User> users = new ArrayList<User>(); 
			 for (Booking booking: bookings){
				Logger.debug("## Initializing statistics data: %s", booking.code);
			    nights = nights + booking.nights;
			    this.totalPrice = this.totalPrice + booking.totalSalePrice;
			    if (booking.fromWeb){
			    	this.bookingsWeb++;
			    }
			    else{
			    	this.bookingsApps++;
			    }
			    this.totalCommission += booking.totalSalePrice - booking.netTotalSalePrice;
			    this.addBookingDayOfWeek(booking.checkinDate.getDay());
			    this.addDistinctUser(users, booking.user);
			 }
			 this.nightsPerBookingAvg = this.bookings > 0 ? nights / this.bookings : 0;
			 this.priceAvg = this.bookings > 0 ? this.totalPrice / this.bookings : 0;
			 this.commisionAvg = this.bookings > 0 ? this.totalCommission / this.bookings : 0;
			 this.totalusers = users.size();
			 this.bookingPerUserAvg = this.bookings > 0 &&  this.totalusers > 0 ? new Float(this.bookings) / this.totalusers : 0;
	}

	private void addDistinctUser(List<User> users, User user) {
		for (User u: users){
			if(u.id.equals(user.id)){
				return;
			}
		}
		users.add(user);
	}

	private void addBookingDayOfWeek(int dayOfWeek) {
		 switch (dayOfWeek) {
			case 0:
				this.bookingsSunday++;
				break;
			case 1:
				this.bookingsMonday++;
				break;
			case 2:
				this.bookingsTuesday++;
				break;
			case 3:
				this.bookingsWednesday++;
				break;
			case 4:
				this.bookingsThursday++;
				break;
			case 5:
				this.bookingsFriday++;
				break;
			case 6:
				this.bookingsSaturday++;
				break;
			default:
				break;
		}
	}
}