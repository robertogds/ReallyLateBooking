package controllers.admin;

import helper.hotusa.HotUsaApiHelper;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import models.Booking;
import models.City;
import models.Coupon;
import models.Deal;
import models.MyCoupon;
import models.User;
import models.exceptions.InvalidBookingCodeException;
import models.exceptions.InvalidCouponException;
import notifiers.Mails;

import org.apache.commons.codec.digest.DigestUtils;

import play.Logger;
import play.data.binding.Binder;
import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.modules.siena.SienaModelUtils;
import play.modules.siena.SienaPlugin;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;
import controllers.CRUD.ObjectType;

@Check(Security.EDITOR_ROLE)
@With(Secure.class)
@CRUD.For(User.class)
public class Users extends controllers.CRUD  {
	
	/*
	 * Override list method from CRUD 
	 * 
	 * */
	public static void list(int page, String search, String searchFields, String orderBy, String order, String[] fields) {
		searchFields = null;
		search = null;
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        
        List<Object> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = new Long(User.all().count());
        Long totalCount = count;
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }
	/*
	 * Override save method from CRUD in order to MD5 the pass
	 * 
	 * */
	 public static void save(String id) throws Exception {
	    	ObjectType type = ObjectType.get(getControllerClass());
	        notFoundIfNull(type);
	        User object = (User)SienaModelUtils.findById(type.entityClass, id);
	        notFoundIfNull(object);
	        String oldPass = object.password;
	        Binder.bind(object, "object", params.all());
	        Logger.debug("Password param %s", oldPass);
	        Logger.debug("Password bd %s", object.password);
	        //Override the password with the MD5 value
	        if(!oldPass.equalsIgnoreCase(object.password)){
	        	Logger.debug("No son iguales");
	        	object.password = DigestUtils.md5Hex(object.password);
	        }
	        
	        validation.valid(object);
	        if (Validation.hasErrors()) {
	            renderArgs.put("error", Messages.get("crud.hasErrors"));
	            try {
	                render(request.controller.replace(".", "/") + "/show.html", type, object);
	            } catch (TemplateNotFoundException e) {
	                render("CRUD/show.html", type, object);
	            }
	        }
	        SienaPlugin.pm().save(object);
	
	        flash.success(Messages.get("crud.saved", type.modelName));
	        if (params.get("_save") != null) {
	            redirect(request.controller + ".list");
	        }
	        redirect(request.controller + ".show", SienaModelUtils.keyValue(object));
	 }

	/*
	 * Override create method from CRUD in order to MD5 the pass
	 * 
	 * */
	public static void create() throws Exception {
	        ObjectType type = ObjectType.get(getControllerClass());
	        notFoundIfNull(type);
	        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
	        constructor.setAccessible(true);
	        User object = (User)constructor.newInstance();
	        
	        Binder.bind(object, "object", params.all());
	        Logger.debug("Pass:" + object.password);
	        //Override the password with the MD5 value
	        object.password = DigestUtils.md5Hex(object.password);
	        Logger.debug("Pass:" + object.password);
	        
	        validation.valid(object);
	        if (Validation.hasErrors()) {
	            renderArgs.put("error", Messages.get("crud.hasErrors"));
	            try {
	                render(request.controller.replace(".", "/") + "/blank.html", type);
	            } catch (TemplateNotFoundException e) {
	                render("CRUD/blank.html", type);
	            }
	        }
	        SienaPlugin.pm().save(object);
	        flash.success(Messages.get("crud.created", type.modelName));
	        if (params.get("_save") != null) {
	            redirect(request.controller + ".list");
	        }
	        if (params.get("_saveAndAddAnother") != null) {
	            redirect(request.controller + ".blank");
	        }
	        
	        
	        redirect(request.controller + ".show", SienaModelUtils.keyValue(object));
	}
	
	public static void exportClientsCSV(int page){
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        int limit = 10000;
        int offset = limit * page;
		List<User> objects = User.all().filter("isOwner", Boolean.FALSE).offset(offset).fetch(limit);
		render("admin/export.csv", objects, type);
	}
	
	public static void exportAll(int page) {
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        int limit = 10000;
        int offset = limit * page;
        List<User> objects = User.all().offset(offset).fetch(limit);
        render("admin/export.csv", objects, type);
    }
	
	public static void showUserActionsByEmail(@Required @Email String email){
		if (!validation.hasErrors()){
			User user = User.findByEmail(email);
			if (user != null){
				List<MyCoupon> coupons = MyCoupon.findByUser(user);
				List<Booking> bookings = Booking.findAllByUser(user);
				List<User> friends = new ArrayList<User>();
				List<MyCoupon> friendsCoupons = MyCoupon.findByKey(user.refererId);
				Logger.debug("Cupones encontrados: %s", friendsCoupons.size());
				for (MyCoupon myCoupon: friendsCoupons){
					friends.add(User.findById(myCoupon.user.id));
				}
				Logger.debug("Amigos encontrados: %s", friends.size());
				int credits = user.calculateTotalCreditsFromMyCoupons();
				List<City> cities = City.findActiveCities();
				render(user, coupons, bookings, friends, credits, cities);
			}
		}
		redirect("/admin/users");
	}
	
	public static void showUserActionsById(Long id){
		User user = User.findById(id);
		if (user != null){
			showUserActionsByEmail(user.email);
		}
		redirect("/admin/users");
	}
	
	public static void addCouponToUser(String key, User user){
		user = User.findById(user.id);
		try {
			Coupon.validateAndSave(user.id, key);
		} catch (InvalidCouponException e) {
			validation.addError("key", e.getMessage());
		}
		showUserActionsByEmail(user.email);
	}
	
	public static void createBooking(@Required Long dealId, @Required Long cityId, User user, Integer nights){
		user = User.findById(user.id);
		Deal deal = Deal.findById(dealId);
		Booking booking = new Booking(deal, user);
		booking.rooms = 1; //we dont allow more rooms by now
		booking.nights = nights;
		booking.validateNoCreditCart(); //Custom validation
		
		if (!validation.hasErrors()){ 
			booking.payed = true;
	        booking.canceled = false;
	        booking.pending = false;
			booking.insert();
			try {
				doCompleteReservation(booking);
			} catch (InvalidBookingCodeException e) {
		        flash.error(Messages.get("booking.validation.problem"));
		        showUserActionsByEmail(user.email);
			}
			updateAndNotifyUserBooking(booking);
			
			flash.success(Messages.get("web.bookingForm.success"));
		}
		else{
			params.flash(); // add http parameters to the flash scope
	        validation.keep(); // keep the errors for the next request
	        Logger.debug("Errors " + validation.errorsMap().toString());
		}
		showUserActionsByEmail(user.email);
	}
	
	
	
	//DUPLICATE FROM BOOKINGS CONTROLLER
	private static void doCompleteReservation(Booking booking) throws InvalidBookingCodeException{
		if ((booking.deal.isHotUsa != null && booking.deal.isHotUsa) && (booking.deal.isFake == null||!booking.deal.isFake )){ 
			//If we are booking for more than one nights we need to refresh de lin codes 
			if (booking.nights > 1){
				HotUsaApiHelper.refreshAvailability(booking);
			}
			String localizador = HotUsaApiHelper.reservation(booking);
			if (localizador != null){
				saveUnconfirmedBooking(booking, localizador);
			}
			else{
				validation.addError("rooms", Messages.get("booking.validation.problem"));
				booking.pending = Boolean.TRUE;
				booking.update();
				throw new InvalidBookingCodeException("Localizador from hotusa is null");
			}
		}
		else{
			updateDealRooms(booking.deal.id, booking.rooms, booking.nights);
		}
	}
	
	private static void updateAndNotifyUserBooking(Booking booking) {
		//We mark all the coupons needed as used
		booking.user =  User.findById(booking.user.id);
		booking.user.markMyCouponsAsUsed(booking.credits, booking.finalPrice);
		//inform user by mail 
		booking.code = booking.isHotusa ? Booking.RESTEL + "-"+booking.code : booking.code;
		//Send email to user and hotel
		Mails.hotelBookingConfirmation(booking);
		Mails.userBookingConfirmation(booking);
	}
	
	private static void saveUnconfirmedBooking(Booking booking, String localizador){
		Logger.debug("Correct booking: " + localizador);
		booking.code = localizador;
		booking.needConfirmation = Boolean.TRUE;
		booking.update();
	}
	
	private static void updateDealRooms(Long dealId, Integer rooms, int nights){
		Logger.debug("Deal id: %s ## Rooms: %s ## Nights: %s", dealId, rooms, nights);
		Deal deal = Deal.findById(dealId);
		deal.quantity = deal.quantity - rooms;
		if (deal.quantity == 0){
			deal.active = Boolean.FALSE;
		}
		if (nights > 1){
			deal.quantityDay2 = deal.quantityDay2 == null ? deal.quantity :deal.quantityDay2 - rooms;
			if (nights > 2){
				deal.quantityDay3 =  deal.quantityDay3 == null ? deal.quantity : deal.quantityDay3 - rooms;
				if (nights > 3){
					deal.quantityDay4 =  deal.quantityDay4 == null ? deal.quantity : deal.quantityDay4 - rooms;
					if (nights > 4){
						deal.quantityDay5 =  deal.quantityDay5 == null ? deal.quantity : deal.quantityDay5 - rooms;
					}
				}
			}
		}
		deal.update();
	}
}
