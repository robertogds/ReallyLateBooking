package helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import models.Deal;

import play.Logger;

public final class DateHelper {

	public static Date getTodayDate(){
		Calendar today = Calendar.getInstance();
		Integer hour = getCurrentHour();
		//Between 0am and 6am we need to book for the past day
		if (hour >= 0 && hour < 6){
			Logger.info("Its between 0am and 6am ");
			today.add(Calendar.DAY_OF_MONTH, -1 );
		}
		
		return today.getTime();
		 
	}
	
	public static Integer getCurrentHour(){
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
