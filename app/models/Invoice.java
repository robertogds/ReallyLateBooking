package models;

import helper.CreditCardHelper;
import helper.DateHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.PostPersist;
import javax.persistence.Transient;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.h2.util.MathUtils;

import play.Logger;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.data.validation.Validation.Validator;
import play.i18n.Messages;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.core.lifecycle.PostInsert;
import siena.core.lifecycle.PostSave;

public class Invoice extends Model {
	
	public static final double TAX = 0.18;
    
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
    
    @Required
    @Index("company_index")
    public Company company;
    @DateTime
    public Date created;
    public Float subTotal;
    public Float totalAmount;
    public Boolean payed;
    public Float totalTax;
    

    public Invoice(Company company) {
        this.company = company;
        this.created = Calendar.getInstance().getTime();
        this.payed = Boolean.FALSE;
        this.totalAmount = new Float(0);
        this.totalTax = new Float(0);
    }
	
    public void assignBookings(Collection<Booking> bookings){
    	for(Booking booking: bookings){
			booking.fee = this.calculateCommission(booking.salePriceCents);
			this.totalAmount += booking.fee;
			this.totalTax += this.calculateTax(booking.fee);
			this.subTotal = this.totalAmount - this.totalTax;
			booking.invoice = this;
			booking.invoiced = Boolean.TRUE;
			booking.update();
		}
    	this.update();
    }
    
    private Float calculateTax(Float fee) {
		return new Float(TAX) * fee;
	}


	private Float calculateCommission(Integer price){
    	return price * this.company.fee * new Float(0.01);
    }
    
	public static Invoice findById(Long id) {
        return all().filter("id", id).get();
    }
    
    public static Query<Invoice> all() {
    	return Model.all(Invoice.class);
    }
    
    public static List<Invoice> findByDeal(Deal deal){
    	return Invoice.all().filter("deal", deal).fetch();
    }
    
	public static Collection<Invoice> findByCompany(Company company) {
		return Invoice.all().filter("company", company).fetch();
	}


	@Override
	public String toString() {
		return "Invoice [id=" + id + ", company=" + company + ", created="
				+ created + ", subTotal=" + subTotal + ", totalAmount="
				+ totalAmount + ", payed=" + payed + ", totalTax=" + totalTax
				+ "]";
	}
	
	
}
