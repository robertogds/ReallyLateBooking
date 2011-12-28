package helper.dto;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.City;
import models.Statistic;
import models.User;
import play.Logger;

public class StatsGraphDTO {
	
	//FIXME Statistic must inherit from AbstractStats
    public List<Statistic> visits;
    public List<AbstractStats> registered;
    public List<AbstractStats> bookings;
    
    public int dayStart;
    public int monthStart;
    public int yearStart;
    public int dayEnd;
    public int monthEnd;
    public int yearEnd;
    

	public StatsGraphDTO(Collection<City> cities, Calendar calStart, Calendar calEnd) {
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
		
		this.visits = Statistic.findByDate(calStart.getTime(), calEnd.getTime());
	    
	}
}
