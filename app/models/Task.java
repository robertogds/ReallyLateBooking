package models;

import java.util.*;

import play.*;
import models.crudsiena.SienaSupport;
import siena.*;

@Table("tasks")
public class Task extends SienaSupport {
	
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
	
	public Task(String hotelName) {
		this.hotelName = hotelName;
	}
	
	public static Query<Task> all() {
    	return Model.all(Task.class);
    }
	
	public String toString() {
		return hotelName;
	}
}


