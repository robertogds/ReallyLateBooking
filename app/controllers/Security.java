package controllers;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.mvc.Scope.Session;
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
		Logger.debug("User connected: " + connected());
		User user = User.findByEmail(connected());
	    if("admin".equals(profile)) {
	        return user != null && user.isAdmin;
	    
	    }
	    else if("owner".equals(profile)) {
		    return user != null && user.isOwner;
	    }
	    
	    return user != null;
	}
	
    static void checkConnected() {
        if(!session.contains("username") || !connectedUserExists()){
            Application.index();
        }else{
        	User user = new User();
        	user.id = Long.valueOf(session.get("userId"));
        	user.email = session.get("username");
        	user.firstName = session.get("firstName");
        	user.lastName = session.get("lastName");
        	user.fbid  = session.get("uuid");
            renderArgs.put("user", user);
        }
    }
    
    static boolean connectedUserExists(){
		try{
			User user = User.findById(Long.valueOf(session.get("userId")));
			return user != null;
		}
		catch(NumberFormatException e){
			return false;
		}
	}
}
