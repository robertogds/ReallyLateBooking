package controllers.admin;

import models.Booking;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
@With(Secure.class)
@CRUD.For(Booking.class)
public class Bookings extends controllers.CRUD{

}

