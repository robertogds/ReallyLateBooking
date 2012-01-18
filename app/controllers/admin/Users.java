package controllers.admin;

import java.lang.reflect.Constructor;
import java.util.List;

import models.User;

import org.apache.commons.codec.digest.DigestUtils;

import play.Logger;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.modules.siena.SienaModelUtils;
import play.modules.siena.SienaPlugin;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;

@Check("admin")
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
	        Binder.bind(object, "object", params.all());
	        //Override the password with the MD5 value
	        object.password = DigestUtils.md5Hex(object.password);
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
		List<User> users = User.all().filter("isOwner", Boolean.FALSE).fetch();
		renderTemplate("admin/Users/users.csv",users);
	}
	
	public static void exportAllCSV(){
		List<User> users = User.all().fetch();
		renderTemplate("admin/Users/users.csv",users);
	}
	
	
}
