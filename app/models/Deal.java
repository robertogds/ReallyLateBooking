package models;

import java.util.*;

import play.*;
import models.crudsiena.SienaSupport;
import siena.*;

@Table("deals")
public class Deal extends SienaSupport {
	
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	public String hotelName;
	public boolean active;
	public String city;
	public Integer salePriceCents;
	public Integer priceCents;
	public Integer quantity;
	public String description;
	public String roomType;
	public Integer hotelCategory;
	public String address;
    public String image1;
	
	public Deal(String hotelName) {
		this.hotelName = hotelName;
	}
	
	public static Query<Deal> all() {
    	return Model.all(Deal.class);
    }
	
	public String toString() {
		return hotelName;
	}
}


