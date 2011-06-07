package models;

import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;

@Table("cities")
public class City extends Model {

	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	public String name;
	
	public City(String name){
		this.name = name;
	}
	
	public static Query<City> all() {
    	return Model.all(City.class);
    }
	
    public static City findByName(String name){
    	return City.all().filter("name", name).get();
    }
    
    public static City findById(Long id) {
        return all().filter("id", id).get();
    }
	
	public String toString() {
		return name;
	}
}
