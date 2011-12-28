package models;

import java.util.Collection;
import java.util.Date;

import play.data.validation.Required;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.Table;

@Table("countries")
public class Country extends Model {

	
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
	public String latitude;
	public String longitude;
	
	@DateTime
	public Date updated;
	public String hotusaCode;
	
	public Country(String name, String url){
		this.name = name;
		this.url = url;
	}
	
	public static Query<Country> all() {
    	return Model.all(Country.class);
    }
	
    public static Country findByName(String name){
    	return Country.all().filter("url", name).get();
    }
    
    public static Country findById(Long id) {
        return all().filter("id", id).get();
    }
	
	public String toString() {
		return name;
	}

	public static Collection<Country> findActiveCountries() {
		Collection<Country> cities = all().filter("active", true).order("name").fetch();
		return cities;
	}
	
	
}
