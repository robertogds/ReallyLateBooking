package models;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import play.data.validation.Required;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Index;
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
	public String nameEN;
	@Required
	public String nameFR;
	@Required
	public String url;
	public boolean active;
	@DateTime
	public Date updated;
	
	public String latitude;
	public String longitude;
	
	@Required
	@Index("country_index")
    public Country country;
	
	//url string of the City root
	public String root;
	public String hotusaProvCode;
	public String hotusaCode;
	
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

	public static List<City> findActiveCities() {
		List<City> cities = all().filter("active", Boolean.TRUE).order("name").fetch();
		return cities;
	}

	public static List<City> findActiveCitiesByCountry(Country country){
		return all().filter("country", country).filter("active", Boolean.TRUE).order("name").fetch();
	}
	
	public static List<City> findActiveCitiesByRoot(String root){
		return all().filter("root", root).filter("active", Boolean.TRUE).order("name").fetch();
	}
	
	public static void addTestCity(Collection<City> cities) {
		City city = findByName("test");
		if (city != null){
			cities.add(city);
		}
	}
	
	
}
