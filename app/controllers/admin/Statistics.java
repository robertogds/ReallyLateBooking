package controllers.admin;

import helper.DateHelper;

import java.util.Calendar;
import java.util.Collection;

import models.City;
import models.Statistic;
import models.User;
import models.dto.StatisticDTO;
import models.dto.StatsGraphDTO;
import play.mvc.Controller;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
public class Statistics extends Controller {
	
	public static void index() {
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		DateHelper.setFirstDayHour(calStart);
		Calendar calStartB = Calendar.getInstance();
		DateHelper.setFirstDayHour(calStartB);
		calStartB.add(Calendar.DAY_OF_YEAR, -7);
		Calendar calEndB = Calendar.getInstance();
		calEndB.add(Calendar.DAY_OF_YEAR, -7);
		StatisticDTO data = prepareStatistic(calStart, calEnd,calStartB,calEndB);
        render(data);
	}
	
	
	public static void updateStatistics(int dayStart, int monthStart, int yearStart,
			int dayEnd, int monthEnd, int yearEnd, int dayStartB, int monthStartB, 
			int yearStartB, int dayEndB, int monthEndB, int yearEndB){
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		Calendar calStartB = Calendar.getInstance();
		Calendar calEndB = Calendar.getInstance();
		calStart.set(yearStart, monthStart-1, dayStart);
		DateHelper.setFirstDayHour(calStart);
		calEnd.set(yearEnd, monthEnd-1, dayEnd);
		DateHelper.setLastDayHour(calEnd);
		calStartB.set(yearStartB, monthStartB-1, dayStartB);
		DateHelper.setFirstDayHour(calStartB);
		calEndB.set(yearEndB, monthEndB-1, dayEndB);
		DateHelper.setLastDayHour(calEndB);
		StatisticDTO data = prepareStatistic(calStart, calEnd, calStartB, calEndB);
		
        renderTemplate("admin/Statistics/index.html", data);
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


	private static StatisticDTO prepareStatistic(Calendar start, Calendar end, Calendar startB, Calendar endB) {
		Collection<City> cities = City.all().fetch();
		StatisticDTO dto = new StatisticDTO(cities, start, end, startB, endB);
		return dto;
	}
	
}

