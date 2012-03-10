package helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import models.Deal;

import play.Logger;
import play.Play;

public final class DateHelper {

	public static Date getTodayDate(){
		Calendar today = Calendar.getInstance();
		Integer hour = today.get(Calendar.HOUR_OF_DAY);
		//Between 0am and 6am we need to book for the past day
		if (hour >= 0 && hour < 6){
			today.add(Calendar.DAY_OF_YEAR, -1 );
			Logger.info("Its between 0am and 6am, day of month: " + today.get(Calendar.DAY_OF_MONTH));
		}
		return today.getTime();
	}
	
	public static Boolean isTodayDate(Date date){
		Calendar cal = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
		                  cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR);
	}
	
	public static Date getFutureDay(Integer days){
		Calendar date = Calendar.getInstance(); 
		date.setTime(getTodayDate());
		date.add(Calendar.DAY_OF_YEAR, days ); 
		return date.getTime();
	}
	
	public static Boolean isActiveTime(){
	    int hour = DateHelper.getCurrentHour() ;
		if (hour > 12 || hour < 6){
			return Boolean.TRUE;
		}
		else{
			return Boolean.FALSE;
		}
	}
	 
	/**
	 * Time when we want the cron to work
	 * Hotusa price for example
	 * @return
	 */
	public static Boolean isWorkingTime(){
	    int hour = DateHelper.getCurrentHour() ;
	    Logger.info("Its time: " + hour);
		if (hour > 9 || hour < 3){
			return Boolean.TRUE;
		}
		else{
			return Boolean.FALSE;
		}
	}
	
	public static Integer getCurrentHour(){
		if (Play.mode.isDev()){
			Integer hour = 16;
			return hour;
		}
		else{
			Calendar now = Calendar.getInstance();
			Integer hour = now.get(Calendar.HOUR_OF_DAY);
			hour = hour + 1; //we are utc+1 and gae is UTC
			// 24 = 0
			hour = hour == 24 ? 0 : hour;
			// 25 = 1
			hour = hour == 25 ? 1 : hour;
			return hour;
		}
		
	}

	public static void setFirstDayHour(Calendar calStart) {
		calStart.add(Calendar.DAY_OF_YEAR, -1);
		calStart.set(Calendar.HOUR_OF_DAY, 24);
		calStart.set(Calendar.MINUTE, 0);
		calStart.set(Calendar.SECOND, 0);
	}

	public static void setLastDayHour(Calendar calEnd) {
		calEnd.set(Calendar.HOUR_OF_DAY, 23);
		calEnd.set(Calendar.MINUTE, 59);
		calEnd.set(Calendar.SECOND, 59);
	}
	
}
