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
			Logger.info("Its between 0am and 6am, day of month: " + Calendar.DAY_OF_MONTH);
			today.add(Calendar.DAY_OF_MONTH, -1 );
		}
		
		return today.getTime();
		 
	}
	
	public static Date getFutureDay(Integer days){
		Calendar date = Calendar.getInstance(); 
		date.setTime(getTodayDate());
		date.add(Calendar.DAY_OF_MONTH, days ); 
		return date.getTime();
	}
	
	public static Boolean isActiveTime(){
	    int hour = DateHelper.getCurrentHour() ;
	    Logger.info("Its time: " + hour);
		if (hour > 12 || hour < 6){
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
			hour = hour + 2; //we are utc+2 and gae is UTC
			// 24 = 0
			hour = hour == 24 ? 0 : hour;
			// 25 = 1
			hour = hour == 25 ? 1 : hour;
			return hour;
		}
		
	}
	
}
