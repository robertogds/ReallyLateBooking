package controllers.admin;

import java.util.Collection;
import java.util.List;

import models.Booking;
import models.Company;
import models.InfoText;
import models.Invoice;
import models.User;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.CRUD.ObjectType;
import controllers.Security;

@Check(Security.ADMIN_ROLE)
@With(Secure.class)
@CRUD.For(Invoice.class)
public class Invoices  extends controllers.CRUD {
	
	public static void showInvoice(Long invoiceId) {
		Invoice invoice = Invoice.findById(invoiceId);
		User user= invoice.user;
		user.get();
        renderTemplate("Users/showBookingInvoice.html", user, invoice);
	}
	
	public static void exportAll() {
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<Invoice> objects = Invoice.all().fetch();
        render("admin/export.csv", objects, type);
    }
}

