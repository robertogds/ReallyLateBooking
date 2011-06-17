package controllers;

import play.Logger;
import models.*;

public class Security extends Secure.Security {
	
	static boolean authenticate(String username, String password) {
	    return User.connect(username, password) != null;
	}
    
	static boolean check(String profile) {
	    if("admin".equals(profile)) {
	    	User user = User.findByEmail(connected());
	        return user != null && user.isAdmin;
	    }
	    return false;
	}
	
}
