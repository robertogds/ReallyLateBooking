package controllers.admin;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import models.Booking;
import models.Coupon;
import models.MyCoupon;
import models.User;
import models.exceptions.InvalidCouponException;

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
import controllers.CRUD.ObjectType;
import controllers.Security;

@Check(Security.EDITOR_ROLE)
@With(Secure.class)
@CRUD.For(User.class)
public class Users extends controllers.CRUD  {
	
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
	
	public static void exportClientsCSV(){
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
		List<User> objects = User.all().filter("isOwner", Boolean.FALSE).fetch();
		render("admin/export.csv", objects, type);
	}
	
	public static void exportAll() {
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<User> objects = User.all().fetch();
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
				render(user, coupons, bookings, friends, credits);
			}
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
	
	
	
}
