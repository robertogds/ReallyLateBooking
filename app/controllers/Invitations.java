package controllers;

import models.Invitation;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.data.validation.Email;
import play.data.validation.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Controller;

public class Invitations extends Controller {
	
	 public static void getInvite(@Required @Email String email, String returnUrl) throws Throwable {
        if(validation.hasErrors()) {
            flash.error(Messages.get("web.invite.email.invalid"));
            params.flash();
        }
        else{
        	if (User.findByEmail(email) != null){
        		 flash.error(Messages.get("web.invite.email.exists"));
                 params.flash();
        	}
        	else{
        		//Create the invitation instance.
            	String lang = Lang.get();
            	Invitation invite = new Invitation();
            	invite.email = email;
            	invite.locale = lang;
            	invite.insert();
            	flash.success(Messages.get("web.invite.success"));
        	}
        }
        if (StringUtils.isBlank(returnUrl)){
        	Application.index();
        }
        redirect(returnUrl);
    }
}
