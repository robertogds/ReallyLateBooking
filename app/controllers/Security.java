package controllers;

import org.apache.commons.codec.digest.DigestUtils;

import play.Logger;
import models.*;

public class Security extends Secure.Security {
	
	static boolean authenticate(String username, String password) {
	    return User.connect(username, DigestUtils.md5Hex(password)) != null;
	}
	
	//when coming from json password is already a md5 hash
	static boolean authenticateJson(String username, String password) {
	    return User.connect(username, password) != null;
	}
    
	static boolean check(String profile) {
	    if("admin".equals(profile)) {
	    	User user = User.findByEmail(connected());
	        return user != null && user.isAdmin;
	    
	    }
	    else if("owner".equals(profile)) {
	    	User user = User.findByEmail(connected());
		    return user != null && user.isOwner;
	    }
	    
	    return false;
	}
	
}
