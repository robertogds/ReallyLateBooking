package controllers.admin;

import java.util.Calendar;
import java.util.Collection;

import models.Booking;
import models.Company;
import models.Invoice;
import play.Logger;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(Company.class)
public class Companies extends controllers.CRUD {
	
	public static void showCompanyBookings(Long companyId) {
		Company company = Company.findById(companyId);
        Collection<Booking> bookings = Booking.findByCompany(company);
        render(company, bookings);
	}
	
	public static void createInvoice(Long companyId) {
		Company company = Company.findById(companyId);
        Collection<Booking> bookings = Booking.findUninvoicedByCompany(company);
        Logger.debug("Creating invoice for " + companyId + " name:" + company.name + " Total Bookings: " + bookings.size());
        Invoice invoice = new Invoice(company);
        invoice.insert();
        invoice.assignBookings(bookings);
        render(company, bookings, invoice);
	}
	
	public static void updateBooking(Long bookingId, Long companyId, Boolean canceled, Boolean invoiced) {
		Booking booking = Booking.findById(bookingId);
		booking.canceled = canceled != null ? canceled : Boolean.FALSE;
		booking.invoiced = invoiced != null ? invoiced : Boolean.FALSE;
		booking.update();
		Company company = Company.findById(companyId);
		company.updated = Calendar.getInstance().getTime();
		company.update();
		
		showCompanyBookings(companyId);
	}
	
	
	
}

