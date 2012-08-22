package models.dto;

import helper.DateHelper;
import helper.mailchimp.MailChimpHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import play.Logger;

import models.Booking;
import models.City;
import models.Deal;
import models.MyCoupon;
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
    public int bookingsWithCredits = 0;
    public int bookingsWithoutCredits = 0;
    public int bookingsMGM = 0;
    public int bookingsEmail= 0;
    public int totalusers= 0;
    public int firstBookingUsers = 0;
    public int repeatingUsers = 0;
    public int days = 0;
    public float totalCommission = 0;
    public float totalCommissionEmail = 0;
    public float totalPrice = 0;
    public int totalCredits = 0;
    public float nightsPerBookingAvg=0;
    public float priceAvg=0;
    public float commisionAvg=0;
    public float creditsAvg = 0;
    public float bookingPerUserAvg=0;
    public float bookingsPerDay = 0;
    public Date startDate;
    public Date endDate;
	private int bookingsUsersToday = 0;
	private int bookingsUsersLastWeek = 0;
	private int bookingsUsersLastMonth = 0;
	private int bookingsUsersTwoMonthsAgo = 0;
	private int bookingsUsersThreeMonthsAgo = 0;
	private int bookingsUsersSixMonthsAgo = 0;
	private int bookingsUsersNineMonthsAgo = 0;
	private int bookingsUsersOneYearAgo = 0;
	private int twoBookings = 0;
	private int threeBookings = 0;
	private int threeFiveBookings = 0;
	private int sixTenBookings = 0;
	private int moreTenBookings = 0;
	private List<User> usersSeveralBookings = new ArrayList<User>(); 
	
	public CityData(Collection<Booking> bookings, Date start, Date end){
		this.startDate = start;
		this.endDate = end;
		this.initializaData(bookings, start, end);
	}
	
	public void initialiceVisits(City city, Date start, Date end){
		this.visits = Statistic.countVisitsByCity(city, start, end);
		this.bookings = Booking.countBookingsByCity(city, start, end);
		this.hotels = Deal.countActiveHotelsByCity(city);
		this.activeDirectHotels = Deal.countActiveDirectHotelsByCity(city);
	}
	
	private void initializaData(Collection<Booking> bookings, Date start, Date end){
		this.bookings = bookings.size();
		this.days = DateHelper.daysBetween(start, end);
		float nights = 0;
		this.totalPrice = 0;
		List<User> users = new ArrayList<User>(); 
		for (Booking booking: bookings){
			nights = nights + booking.nights;
			this.totalPrice = this.totalPrice + booking.totalSalePrice;
			this.totalCredits += booking.credits != null ? booking.credits : 0;
			this.countBookingOrigin(booking);
			this.countBookingsWithCredits(booking);
			this.countBookingsByRegisteredTime(booking);
			this.totalCommission += booking.totalSalePrice - booking.netTotalSalePrice;
			this.addBookingDayOfWeek(booking.checkinDate.getDay());
			this.addDistinctUser(users, booking.user);
		}
		
		this.nightsPerBookingAvg = this.bookings > 0 ? nights / this.bookings : 0;
		this.priceAvg = this.bookings > 0 ? this.totalPrice / this.bookings : 0;
		this.commisionAvg = this.bookings > 0 ? this.totalCommission / this.bookings : 0;
		this.creditsAvg = this.bookings > 0 ? this.totalCredits / this.bookings : 0;
		this.totalusers = users.size();
		this.bookingsMGM = MyCoupon.countAllRefererByUsedDate(start, end);
		this.firstBookingUsers = User.countAllFirstBookingByDate(start, end);
		this.repeatingUsers = this.totalusers - this.firstBookingUsers;
		this.bookingPerUserAvg = this.bookings > 0 &&  this.totalusers > 0 ? new Float(this.bookings) / this.totalusers : 0;
		this.bookingsPerDay = this.bookings > 0 ? new Float(this.bookings) / this.days : 0;
	}


	private void countBookingsByRegisteredTime(Booking booking){
		User user = User.findById(booking.user.id);
		if (user.firstBookingDate != null && booking.checkinDate!= null){
			Calendar bookingCal = Calendar.getInstance();
			bookingCal.setTime(booking.checkinDate);
			Calendar firstBookingCal = Calendar.getInstance();
			firstBookingCal.setTime(user.firstBookingDate);
			this.resetSecondsToCalendar(bookingCal);
			this.resetSecondsToCalendar(firstBookingCal);
			
			if (bookingCal.equals(firstBookingCal)){
				if (registeredLessDaysAgo(user.created, booking.checkinDate, 1)){
					this.bookingsUsersToday++;
				}
				else if (registeredLessDaysAgo(user.created, booking.checkinDate, 7)){
					this.bookingsUsersLastWeek++;
				}
				else if (registeredLessDaysAgo(user.created, booking.checkinDate, 30)){
					this.bookingsUsersLastMonth++;
				}
				else if (registeredLessDaysAgo(user.created, booking.checkinDate, 60)){
					this.bookingsUsersTwoMonthsAgo++;
				}
				else if (registeredLessDaysAgo(user.created, booking.checkinDate, 90)){
					this.bookingsUsersThreeMonthsAgo++;
				}
				else if (registeredLessDaysAgo(user.created, booking.checkinDate, 180)){
					this.bookingsUsersSixMonthsAgo++;
				}
				else if (registeredLessDaysAgo(user.created, booking.checkinDate, 270)){
					this.bookingsUsersNineMonthsAgo++;
				}
				else if (registeredLessDaysAgo(user.created, booking.checkinDate, 365)){
					this.bookingsUsersOneYearAgo++;
				}
			}
		}
	}

	private void resetSecondsToCalendar(Calendar cal) {
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
	}

	private Boolean registeredLessDaysAgo(Date registered, Date booking, int days){
		Calendar cal = Calendar.getInstance();
		cal.setTime(booking);
		cal.add(Calendar.DAY_OF_YEAR, -days);
		if (registered == null){
			return true;
		}
		Calendar calRegistered = Calendar.getInstance();
		calRegistered.setTime(registered);
		return calRegistered.after(cal);
	}

	private void countBookingsWithCredits(Booking booking) {
		if (booking.credits != null && booking.credits > 0){
			this.bookingsWithCredits++;
		}
		else{
			this.bookingsWithoutCredits++;
		}		
	}

	private void countBookingOrigin(Booking booking){
		if (booking.fromWeb){
			this.bookingsWeb++;
		}
		else{
			this.bookingsApps++;
		}
	}
	
	private void addDistinctUser(List<User> users, User user) {
		for (User u: users){
			if(u.id.equals(user.id)){
				this.countUserBookings(user);
				return;
			}
		}
		users.add(user);
	}

	private void countUserBookings(User user) {
		for (User u: usersSeveralBookings){
			if(u.id.equals(user.id)){
				return;
			}
		}
		this.usersSeveralBookings.add(user);
		int bookings = Booking.countBookingsByUser(user, this.startDate, this.endDate);		
		if (bookings == 2){
			this.twoBookings++;
		}
		else if (bookings == 3){
			this.threeBookings++;
		}
		else if (bookings > 3 && bookings <= 5){
			this.threeFiveBookings++;
		} 
		else if (bookings > 5 && bookings <= 10){
			this.sixTenBookings++;
		}
		else if (bookings > 10){
			this.moreTenBookings++;
		}
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