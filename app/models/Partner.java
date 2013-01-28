package models;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import controllers.admin.Bookings;

import play.data.validation.Email;
import play.data.validation.Required;
import play.modules.crudsiena.CrudUnique;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;


@Table("partners")
public class Partner extends Model{
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
 
	public String name;
	@CrudUnique
	@Required
    public String partnerId;
	public Boolean active;
	public String logo;
    
	@DateTime
    public Date created;

    public Partner() {
    	super();
    }
    
    public Partner(String partnerId, String name, Boolean active) {
        this.partnerId = partnerId;
        this.name =  name;
        this.active = active;
    }

	@Override
	public void insert() {
        this.created = Calendar.getInstance().getTime();
    	super.insert();
	}
	
	public static Collection<Partner> getAllActive(){
    	return Partner.all().filter("active", true).order("name").fetch();
    }
	
	public static Partner findByPartnerId(String partnerId){
    	return Partner.all().filter("partnerId", partnerId.trim()).get();
    }
    
    public static Query<Partner> all() {
    	return Model.all(Partner.class);
    }
    
    public static Partner findById(Long id) {
        return all().filter("id", id).get();
    }
    
    public Collection<Booking> findAllBookings(){
    	return Booking.findAllBookingsByPartner(this);
    }

    public String toString() {
        return name;
    }
}