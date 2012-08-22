package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.Logger;
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
	public int position;
	
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
		List<City> cities = all().filter("active", Boolean.TRUE).order("url").offset(offset).fetch(limit);
		return cities;
	}
	
	public static List<City> findActiveCities() {
		List<City> cities = all().filter("active", Boolean.TRUE).order("url").fetch();
		return cities;
	}
	
	public static List<City> findAllCities() {
		List<City> cities = all().order("url").fetch();
		return cities;
	}


	public static List<City> findAllRootCities() {
		List<City> cities = all().filter("root", "").order("url").fetch();
		return cities;
	}
	
	public static List<City> findActiveCitiesByCountry(Country country){
		return all().filter("country", country).filter("active", Boolean.TRUE).order("url").fetch();
	}
	
	public static List<City> findActiveCitiesByRoot(String root){
		return all().filter("root", root).filter("active", Boolean.TRUE).order("position").fetch();
	}
	
	public static List<City> findCitiesByRoot(String root){
		return all().filter("root", root).order("position").fetch();
	}
	
	
	/**
	 * Cities, not zones
	 * @param order 
	 * @param orderBy 
	 * @return
	 */
	public static List<City> findAllMainCities(String orderBy, String order) {
		List<City> cities = new ArrayList<City>();
		if (StringUtils.isBlank(orderBy)){
			cities = findAllCities();
		}
		else{
			cities = all().order(getOrderString(orderBy, order)).fetch();
		}
		List<City> mainCities = new ArrayList<City>();
		for (City city: cities){
			if (city.isRootCity()){
				Logger.debug("City %s is main",city.name);
				mainCities.add(city);
			}
			else{
				Logger.debug("City %s is NOT main", city.name);
			}
		}
		return mainCities;
	}
	
	private static String getOrderString(String orderBy, String order){
		return order.equals("DESC") ? "-" + orderBy : orderBy;
	}
	
	public static void addTestCity(Collection<City> cities) {
		City city = findByUrl("test");
		if (city != null){
			cities.add(city);
		}
	}

	public boolean isRootCity() {
		return isRootCityWithZones() || isSimpleCity();
	}
	
	public boolean isRootCityWithZones() {
		return StringUtils.isBlank(this.root);
	}
	
	/**
	 * Check if city has no zones
	 * @return
	 */
	public boolean isSimpleCity(){
		return this.root != null && this.root.equalsIgnoreCase(this.url);
	}
	
}
