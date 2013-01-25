package controllers.admin;

import helper.DateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import models.Booking;
import models.City;
import models.Statistic;
import models.User;
import models.dto.CityData;
import models.dto.StatisticDTO;
import models.dto.StatsGraphDTO;
import play.mvc.Controller;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;
import controllers.CRUD.ObjectType;

@Check(Security.INVESTOR_ROLE)
@With(Secure.class)
public class Statistics extends Controller {
	
	public static void index(boolean notIncludePending) {
		//A data to today
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		DateHelper.setFirstDayHour(calStart);
		StatisticDTO data = prepareStatistic(calStart, calEnd, !notIncludePending);

		//B data to the whole last year
		//Calendar calStartB = Calendar.getInstance();
		//DateHelper.setFirstDayHour(calStartB);
		//calStartB.add(Calendar.DAY_OF_YEAR, -365);
		//Calendar calEndB = Calendar.getInstance();
		//StatisticDTO dataB = prepareStatistic(calStartB, calEndB, includePending);
		
        render(data, notIncludePending);
	}
	
	
	public static void updateStatistics(int dayStart, int monthStart, int yearStart,
			int dayEnd, int monthEnd, int yearEnd, int dayStartB, int monthStartB, 
			int yearStartB, int dayEndB, int monthEndB, int yearEndB, boolean notIncludePending){
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		calStart.set(yearStart, monthStart-1, dayStart);
		DateHelper.setFirstDayHour(calStart);
		calEnd.set(yearEnd, monthEnd-1, dayEnd);
		DateHelper.setLastDayHour(calEnd);
		StatisticDTO data = prepareStatistic(calStart, calEnd, !notIncludePending);
		
		//Calendar calStartB = Calendar.getInstance();
		//Calendar calEndB = Calendar.getInstance();
		//calStartB.set(yearStartB, monthStartB-1, dayStartB);
		//DateHelper.setFirstDayHour(calStartB);
		//calEndB.set(yearEndB, monthEndB-1, dayEndB);
		//DateHelper.setLastDayHour(calEndB);
		//StatisticDTO dataB = prepareStatistic(calStartB, calEndB);
		
        renderTemplate("admin/Statistics/index.html", data, notIncludePending);
	}
	
	public static void exportStatistics(int dayStart, int monthStart, int yearStart,
			int dayEnd, int monthEnd, int yearEnd, boolean notIncludePending){
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		calStart.set(yearStart, monthStart-1, dayStart);
		DateHelper.setFirstDayHour(calStart);
		calEnd.set(yearEnd, monthEnd-1, dayEnd);
		DateHelper.setLastDayHour(calEnd);
		
		StatisticDTO data = prepareStatistic(calStart, calEnd, !notIncludePending);
	    
		render("admin/Statistics/exportByCity.csv", data);
	}
	
	public static void exportStatisticsByDays(int dayStart, int monthStart, int yearStart,
			int dayEnd, int monthEnd, int yearEnd){
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		calStart.set(yearStart, monthStart-1, dayStart);
		DateHelper.setFirstDayHour(calStart);
		calEnd.set(yearEnd, monthEnd-1, dayEnd);
		DateHelper.setLastDayHour(calEnd);
		
		Calendar now = calStart;
		List<CityData> dataList = new ArrayList<CityData>();
		while(true){
			Calendar tonight = Calendar.getInstance();
			tonight.setTime(now.getTime());
			DateHelper.setLastDayHour(tonight);
			
			Collection<Booking> bookingsToday = Booking.findAllBookingsByDate(now.getTime(), tonight.getTime());
		    CityData dataToday =  new CityData(bookingsToday, now.getTime(), tonight.getTime());
		    dataToday.startDate = now.getTime();
		    dataList.add(dataToday);
		    
			now.add(Calendar.DAY_OF_YEAR, 1);
			if (now.after(calEnd)){
				break;
			}
		}
		render("admin/Statistics/export.csv", dataList);
	}
	
	public static void stats(){
		Calendar calStart = Calendar.getInstance();
		calStart.add(Calendar.MONTH, -6);
		Calendar calEnd = Calendar.getInstance();
		StatsGraphDTO data = prepareStatsGraph(calStart, calEnd);
		render(data);
	}
	
	private static StatsGraphDTO prepareStatsGraph(Calendar calStart, Calendar calEnd) {
		Collection<City> cities = City.all().fetch();
		StatsGraphDTO dto = new StatsGraphDTO(cities, calStart, calEnd);
		return dto;
	}


	private static StatisticDTO prepareStatistic(Calendar start, Calendar end, boolean includePending) {
		Collection<City> rootCities = new ArrayList<City>();
		Collection<City> cities = City.findAllCities();
		for (City city: cities){
			if (city.isRootCity()){
				rootCities.add(city);
			}
		}
		StatisticDTO dto = new StatisticDTO(rootCities, start, end, includePending);
		return dto;
	}
	
}

