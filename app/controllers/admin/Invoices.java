package controllers.admin;

import java.util.Collection;

import models.Booking;
import models.Company;
import models.Invoice;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(Invoice.class)
public class Invoices  extends controllers.CRUD {
	
	public static void showInvoice(Long invoiceId) {
		Invoice invoice = Invoice.findById(invoiceId);
		Company company = Company.findById(invoice.company.id);
        Collection<Booking> bookings = Booking.findByInvoice(invoice);
        renderTemplate("admin/Companies/createInvoice.html",company, bookings, invoice);
	}
}

