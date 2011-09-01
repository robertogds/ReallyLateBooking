package models;

import java.util.Collection;

import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;

@Table("cities")
public class City extends Model {

	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	@Required
	public String name;
	@Required
	public String url;
	public boolean active;
	
	public City(String name, String url){
		this.name = name;
		this.url = url;
	}
	
	public static Query<City> all() {
    	return Model.all(City.class);
    }
	
    public static City findByName(String name){
    	return City.all().filter("url", name).get();
    }
    
    public static City findById(Long id) {
        return all().filter("id", id).get();
    }
	
	public String toString() {
		return name;
	}

	public static Collection<City> findActiveCities() {
		Collection<City> cities = all().filter("active", true).order("name").fetch();
		return cities;
	}

	public static void addTestCity(Collection<City> cities) {
		City city = findByName("test");
		if (city != null){
			cities.add(city);
		}
	}
	
	
}
