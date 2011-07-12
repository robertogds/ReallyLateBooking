package models;

import helper.ImageHelper;

import java.util.List;

import play.data.validation.Email;
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
	@Required
	@Index("city_index")
    public City city;
	@Required
	@Index("owner_index")
    public User owner;
	@Email
	public String contactEmail;
	@Required
	public Integer salePriceCents;
	public Integer priceCents;
	public Integer quantity;
	public Integer position;
    @MaxSize(10000)
	public String description;
    @MaxSize(500)
	public String shortDescription;
	@MaxSize(500)
	public String detailText;
	@MaxSize(500)
	public String hotelText;
	@MaxSize(500)
	public String roomText;
	@MaxSize(500)
	public String foodDrinkText;
	@MaxSize(500)
	public String aroundText;
    @MaxSize(10000)
	public String descriptionEN;
    @MaxSize(500)
	public String shortDescriptionEN;
	@MaxSize(500)
	public String detailTextEN;
	@MaxSize(500)
	public String hotelTextEN;
	@MaxSize(500)
	public String roomTextEN;
	@MaxSize(500)
	public String foodDrinkTextEN;
	@MaxSize(500)
	public String aroundTextEN;
    @MaxSize(10000)
	public String descriptionFR;
    @MaxSize(500)
	public String shortDescriptionFR;
	@MaxSize(500)
	public String detailTextFR;
	@MaxSize(500)
	public String hotelTextFR;
	@MaxSize(500)
	public String roomTextFR;
	@MaxSize(500)
	public String foodDrinkTextFR;
	@MaxSize(500)
	public String aroundTextFR;

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
	
	public Deal(Long id) {
		this.id = id;
	}

	public static List<Deal> findActiveDealsByOwner(User owner){
		return all().filter("owner", owner).order("position").fetch();
	}
	
	public static List<Deal> findActiveDealsByCity(City city){
		return all().filter("city", city).filter("active", Boolean.TRUE).order("position").order("-priceCents").fetch(3);
	}
	
	public static List<Deal> findByCity(City city) {
        return all().filter("city", city).order("position").order("-priceCents").fetch();
    }
	
	public static Query<Deal> all() {
    	return Deal.all(Deal.class);
    }
	
	public String toString() {
		return hotelName;
	}
	
	public void fecthCity(){
		if (this.city != null){
			this.city = City.findById(this.city.id);
		}	
	}
	
	public void prepareImages(){
		ImageHelper.prepareImagesRoutes(this);
	}
	

	public static Deal findById(Long id) {
		return all().filter("id", id).get();
	}
}


