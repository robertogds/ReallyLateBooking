package controllers.admin;

import java.util.Collection;
import java.util.List;

import models.Booking;
import models.Company;
import models.InfoText;
import models.Invoice;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.CRUD.ObjectType;

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
	
	public static void exportAll() {
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<Invoice> objects = Invoice.all().fetch();
        render("admin/export.csv", objects, type);
    }
}

