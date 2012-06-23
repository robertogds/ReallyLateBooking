package helper;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import models.Deal;

import play.Logger;
import play.Play;

public final class DateHelper {

	public static final int CITY_CLOSED = 0;
	public static final int CITY_OPEN_DAY = 1;
	public static final int CITY_OPEN_NIGHT = 2;
	
	public static final int OPEN_HOUR = 12;
	public static final int CLOSE_HOUR = 6;
	public static final int DAY_END_HOUR = 24;
	public static final int DAY_START_HOUR = 0;
	public static final int CRON_START_HOUR = 9;
	public static final int CRON_STOP_HOUR = 3;

	
	public static String dateToString(Date date){
		Format formatter = new SimpleDateFormat(Play.configuration.getProperty("date.format"));
		return formatter.format(date);
	}
	
	/**
	 * Returns the current state of a city base on his UTC time
	 * @param utcOffset hours offset over the UTC time
	 * @return one of three options: CITY_OPEN_DAY, CITY_OPEN_NIGHT, CITY_CLOSED
	 */
	public static int getCurrentStateByCityHour(int utcOffset) {
		int hour = DateHelper.getCurrentHour(utcOffset);
		if (isActiveTime(hour)){
			if (isActiveBeforeMidnight(hour)){
				return CITY_OPEN_DAY;
			}
			else{
				return CITY_OPEN_NIGHT;
			}
		}
		else{
			return CITY_CLOSED;
		}
	}
	
	/**
	 * @param hour between 0 and 23
	 * @return Date object containing remaining time to open RLB
	 */
	public static Date getTimeToOpen(int hour){
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, hour);
		//Set opening time to 12:00:00
		Calendar opening =  Calendar.getInstance();
		opening.set(Calendar.HOUR_OF_DAY, OPEN_HOUR);
		opening.set(Calendar.MINUTE, 0);
		opening.set(Calendar.SECOND, 0);
		//Substract current time
		opening.add(Calendar.HOUR_OF_DAY, -now.get(Calendar.HOUR_OF_DAY));
		opening.add(Calendar.MINUTE, -now.get(Calendar.MINUTE));
		opening.add(Calendar.SECOND, -now.get(Calendar.SECOND));
		Date timeToOpen = opening.getTime();
		return timeToOpen;
	}
	
	//FIXME no debería tener en cuenta la hora de la ciudad?
	public static Date getTodayDate(){
		Calendar today = Calendar.getInstance();
		Integer hour = today.get(Calendar.HOUR_OF_DAY);
		//Between 0am and 6am we need to book for the past day
		if (hour >= DAY_START_HOUR && hour < CLOSE_HOUR){
			today.add(Calendar.DAY_OF_YEAR, -1 );
			Logger.info("Its between 0am and 6am, day of month: " + today.get(Calendar.DAY_OF_MONTH));
		}
		return today.getTime();
	}
	
	
	//FIXME no debería tener en cuenta la hora de la ciudad?
	/**
	 * @param date
	 * @return true if the given date is between the opening and the closing hour of RLB
	 */
	public static Boolean isTodayDate(Date date){
		Calendar cal = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
		                  cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR);
	}
	
	/**
	 * @param days
	 * @return adds the given days to the current date
	 */
	public static Date getFutureDay(Integer days){
		Calendar date = Calendar.getInstance(); 
		date.setTime(getTodayDate());
		date.add(Calendar.DAY_OF_YEAR, days ); 
		return date.getTime();
	}
	
	/**
	 * Time when we show the city deals
	 * @param hour city time
	 * @return
	 */
	public static Boolean isActiveBeforeMidnight(Integer hour){
		return (hour >= OPEN_HOUR && hour < DAY_END_HOUR);
	}
	
	/**
	 * Time when we show the city deals
	 * @param hour city time
	 * @return
	 */
	public static Boolean isActiveTime(Integer hour){
		return (hour >= OPEN_HOUR || hour < CLOSE_HOUR);
	}
	 
	/**
	 * Time when we want the cron to work
	 * Hotusa price for example
	 * @return
	 */
	public static Boolean isWorkingTime(int offset){
	    int hour = DateHelper.getCurrentHour(offset);
	    Logger.info("Its time: " + hour);
		return (hour >= CRON_START_HOUR || hour <= CRON_STOP_HOUR);
	}
	
	/**
	 * @param utcOffset city time hour over UTC hour
	 * @return the current hour for a city
	 */
	public static Integer getCurrentHour(Integer utcOffset){
		Calendar now = Calendar.getInstance();
		Integer hour = now.get(Calendar.HOUR_OF_DAY);
		//Logger.info("Server hour is: " + hour);
		hour = hour + utcOffset; //we are utc+1 and gae is UTC
		// 24 = 0
		hour = hour == 24 ? 0 : hour;
		// 25 = 1
		hour = hour == 25 ? 1 : hour;
		
		if (Play.mode.isDev()){
			hour = 13;
		}
		//Logger.info("City local hour is: " + hour);

		return hour;
		
	}

	/**
	 * Given a Calendar object, sets it to the first minute of the day 00:00:00
	 * @param calStart
	 */
	public static void setFirstDayHour(Calendar calStart) {
		calStart.add(Calendar.DAY_OF_YEAR, -1);
		calStart.set(Calendar.HOUR_OF_DAY, 24);
		calStart.set(Calendar.MINUTE, 0);
		calStart.set(Calendar.SECOND, 0);
	}
	
	/**
	 * Given a Calendar object, sets it to the last minute of the day 23:59:59
	 * @param calEnd
	 */
	public static void setLastDayHour(Calendar calEnd) {
		calEnd.set(Calendar.HOUR_OF_DAY, 23);
		calEnd.set(Calendar.MINUTE, 59);
		calEnd.set(Calendar.SECOND, 59);
	}

	public static String getTimeToOpenString(Integer hour) {
		Date countdown = DateHelper.getTimeToOpen(hour);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(countdown);
		long miliseconds =  (calendar.get(Calendar.HOUR_OF_DAY) * 3600000) + (calendar.get(Calendar.MINUTE) * 60000) + (calendar.get(Calendar.SECOND) * 1000);
		//return calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
		return ""+miliseconds;
	}

	public static int daysBetween(Date start, Date end) {
		Calendar startCalendar =  Calendar.getInstance();
		startCalendar.setTime(start);
		Calendar endCalendar =  Calendar.getInstance();
		endCalendar.setTime(end);
		return daysBetweenCalendars(startCalendar, endCalendar);
	}
	
	//assert: startDate must be before endDate  
	public static int daysBetweenCalendars(Calendar startDate, Calendar endDate) {  
		  Calendar date = (Calendar) startDate.clone();  
		  int daysBetween = 0;  
		  while (date.before(endDate)) {  
		    date.add(Calendar.DAY_OF_MONTH, 1);  
		    daysBetween++;  
		  }  
		  return daysBetween;  
	}  
	
}
