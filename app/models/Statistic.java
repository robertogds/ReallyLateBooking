package models;

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
	
	public Statistic(String path, int count) {
		super();
		this.path = path;
		this.count = count;
	}
	
	public static Query<Statistic> all() {
    	return Model.all(Statistic.class);
    }
	
    public static Statistic findByPath(String path){
    	return Statistic.all().filter("path", path).get();
    }
    
    public static Statistic findById(Long id) {
        return all().filter("id", id).get();
    }
	
	public String toString() {
		return path;
	}
	
	public static void saveVisit(String path){
		Statistic visit = Statistic.findByPath(path);
		if (visit == null){
			visit = new Statistic(path, 1);
			visit.insert();
		}
		else{
			visit.count++;
			visit.update();
		}
	}
	
}
