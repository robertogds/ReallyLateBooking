package models;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.data.validation.Required;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.Table;

/**
 * @author pablopr
 *
 */
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
	
	@Index("zone_index")
	public City mainZone;
	@Required
	public int utcOffset;
	public String mailchimpCode;
	
	public boolean showHint;
	public String hintES;
	public String hintEN;
	public String hintFR;	
	
	public City(String name, String url){
		this.name = name;
		this.url = url;
	}
	
	public static Query<City> all() {
    	return Model.all(City.class);
    }
	
    public static City findByUrl(String url){
    	return City.all().filter("url", url).get();
    }
    
    public static City findById(Long id) {
        return all().filter("id", id).get();
    }
	
	public String toString() {
		return name;
	}

	public static List<City> findActiveCities(int offset, int limit) {
		List<City> cities = all().filter("active", Boolean.TRUE).order("name").offset(offset).fetch(limit);
		return cities;
	}
	
	public static List<City> findActiveCities() {
		List<City> cities = all().filter("active", Boolean.TRUE).order("name").fetch();
		return cities;
	}
	
	public static List<City> findAllCities() {
		List<City> cities = all().order("name").fetch();
		return cities;
	}


	public static List<City> findAllRootCities() {
		List<City> cities = all().filter("root", "").order("name").fetch();
		return cities;
	}
	
	public static List<City> findActiveCitiesByCountry(Country country){
		return all().filter("country", country).filter("active", Boolean.TRUE).order("name").fetch();
	}
	
	public static List<City> findActiveCitiesByRoot(String root){
		return all().filter("root", root).filter("active", Boolean.TRUE).order("name").fetch();
	}
	
	
	public static void addTestCity(Collection<City> cities) {
		City city = findByUrl("test");
		if (city != null){
			cities.add(city);
		}
	}

	public boolean isRootCity() {
		return StringUtils.isBlank(this.root);
	}

	
	/**
	 * Check if city has no zones
	 * @return
	 */
	public boolean isSimpleCity(){
		return this.root != null && root.equalsIgnoreCase(this.url);
	}
	
	
}
