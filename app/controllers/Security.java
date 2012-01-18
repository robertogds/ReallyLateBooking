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
        if(!session.contains("user")){
            Application.index();
        }else{
        	User user = new User();
        	user.id = Long.valueOf(session.get("userId"));
        	user.email = session.get("user");
        	user.firstName = session.get("firstName");
        	user.lastName = session.get("lastName");
        	user.uuid = Long.valueOf(session.get("uuid"));
            renderArgs.put("user", user);
        }
    }
}
