package models;

import helper.DateHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import play.Logger;

import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;

@Table("statistics")
public class Statistic extends Model{
	
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	
	public String path;
	public int count;
	@DateTime
    public Date date;
	public String lang;
	
	public Statistic(String path, int count) {
		super();
		this.path = path;
		this.count = count;
		this.date = Calendar.getInstance().getTime();
	}
	
	public static Query<Statistic> all() {
    	return Model.all(Statistic.class);
    }
	
    public static Statistic findByPath(String path){
    	return Statistic.all().filter("path", path).order("-date").get();
    }
    
    public static Statistic findById(Long id) {
        return all().filter("id", id).get();
    }
	
	public String toString() {
		return path;
	}
	
	public static void saveVisit(String path){
		Statistic visit = Statistic.findByPath(path);
		//If its a new url or a new day, we create a new instance.
		if (visit == null){
			visit = new Statistic(path, 1);
			visit.insert();
		}
		else{
			visit.date = visit.date == null ? Calendar.getInstance().getTime() : visit.date;
			if  (!DateHelper.isTodayDate(visit.date)){
				visit = new Statistic(path, 1);
				visit.insert();
			}
			else{
				visit.count++;
				visit.update();
			}
		}
	}
	
	public static List<Statistic> findByDate(Date start, Date end) {
		return Statistic.all().filter("date>", start).filter("date<", end)
				.order("date").fetch();
	}

	public static int countVisitsByCity(City city, Date start, Date end) {
		String path = "/deals/" + city.url;
		Logger.debug("Path is: " + path);
		List<Statistic> statistics = Statistic.all().filter("path", path)
        	.filter("date>", start).filter("date<", end).order("-date").fetch();
		int total = 0;
		for (Statistic statistic : statistics){
			total =+ statistic.count;
		}
		return total;
	}
	
}
