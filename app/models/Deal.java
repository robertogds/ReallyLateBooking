package models;

import helper.ImageHelper;

import java.util.List;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.Table;

@Table("deals")
public class Deal extends Model {
	
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	@Required
	public String hotelName;
	public boolean active;
	@Index("city_index")
    public City city;
	@Required
	public Integer salePriceCents;
	public Integer priceCents;
	public Integer quantity;
    @Required
    @MaxSize(10000)
	public String description;
	@Required
    @MaxSize(500)
	public String shortDescription;
	public String roomType;
	public Integer hotelCategory;
	public String address;
	public String latitude;
	public String longitude;
	@Required
    public String mainImageBig;
	@Required
    public String mainImageSmall;
	public String image1;
	public String image2;
	public String image3;
	public String image4;
	public String image5;	
	
	
	public Deal(String hotelName, City city) {
		this.hotelName = hotelName;
		this.city = city;
	}
	
	public Deal(String hotelName, City city, Boolean active) {
		this.hotelName = hotelName;
		this.city = city;
		this.active = active;
	}
	
	public static List<Deal> findActiveDealsByCity(City city){
		return all().filter("city", city).filter("active", Boolean.TRUE).order("-priceCents").fetch(3);
	}
	
	public static List<Deal> findByCity(City city) {
        return all().filter("city", city).order("-priceCents").fetch();
    }
	
	public static Query<Deal> all() {
    	return Deal.all(Deal.class);
    }
	
	public String toString() {
		return hotelName;
	}
	
	public void fecthCity(){
		this.city = City.findById(this.city.id);
	}
	
	public void prepareImages(){
		ImageHelper.prepareImagesRoutes(this);
	}
}


