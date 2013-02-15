package models;

import helper.CreditCardHelper;
import helper.DateHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
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
	
	public static final double TAX = 0.21;
	public static final double HOTEL_TAX = 0.08;
    
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	@Required
    public String code;
    @Index("user_invoice_index")
    public User user;
    public String userName;
	public String userNif;
	public String userAddress;
    @Index("company_invoice_index")
    public Company company;
    @DateTime
    public Date created;
    public Integer total;
	public Float fee;
	public Float hotelTotal;
	public Float feeTax;
	public Float hotelTax;

    @MaxSize(10000)
	public String comment;
	public String bookingCode;
	public Date checkinDate;
	public String hotelName;
	public Integer nights;
	public Float subTotal;


    public Invoice(Company company) {
        this.company = company;
        this.created = Calendar.getInstance().getTime();
    }
    
    public Invoice(Booking booking, User user) {
    	Company company = Company.findById(booking.company.id);
        this.company = company;
        this.user = user;
        this.userName = StringUtils.isBlank(user.company)? user.firstName + " " + user.lastName: user.company;
        this.userNif = user.nif;
        this.userAddress = user.address;
        this.created = Calendar.getInstance().getTime();
    }
    
    private static Invoice createUserInvoice(Booking booking, User user){
    	Invoice invoice = new Invoice(booking, user);
    	invoice.insert();
    	invoice.assignBooking(booking);
    	invoice.update();
    	return invoice;
    }
    
	public static Invoice findOrCreateUserInvoice(Booking booking, User user) {
		Invoice invoice = Invoice.findByBooking(booking);
		if (invoice != null) {
			return invoice;
		}
		return createUserInvoice(booking, user);
	}
    

	private void assignBooking(Booking booking){
		this.code =  DateHelper.getYear() + "" + Invoice.all().count();
		this.checkinDate = booking.checkinDate;
		this.hotelName = booking.hotelName;
		this.nights = booking.nights;
		this.bookingCode = booking.code;
		this.total = booking.finalPrice;
		this.fee = booking.fee;
		this.feeTax = this.round(this.calculateTax(this.fee));
		this.feeTax = this.feeTax < 0 ? 0 : this.feeTax;
		this.hotelTotal = booking.netTotalSalePrice;
		this.hotelTax = this.round(this.calculateHotelTax(this.hotelTotal));
		this.subTotal = this.round(this.total - this.feeTax);
		booking.invoice = this;
		booking.invoiced = Boolean.TRUE;
		booking.update();
    }
    
    private Float round(Float number){
    	BigDecimal big = new BigDecimal(number);
   	 	big = big.setScale(2, RoundingMode.HALF_UP);
   	 	return big.floatValue();
    }
    
    private Float calculateTax(Float cost) {
		return new Float(TAX) * cost;
	}
    
    private Float calculateHotelTax(Float cost) {
		return new Float(HOTEL_TAX) * cost;
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
	
	private static Invoice findByBooking(Booking booking) {
		return Invoice.all().filter("bookingCode", booking.code).get();
	}

	@Override
	 public String toString() {
	        return id.toString();
	    }

}
