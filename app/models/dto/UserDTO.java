package models.dto;

import java.security.InvalidParameterException;

import siena.NotNull;
import models.User;

public class UserDTO {

	 public Long id;
	 public String email;
	 public String firstName;
	 public String lastName;
	 public String token;
	 public String secret;
	 public String password;
	 public String referer;
	 public String refererId;
	
	 public UserDTO(User user) {
		 validateUser(user);
		 id = user.id;
		 email = user.email;
		 firstName = user.firstName;
		 lastName = user.lastName;
		 token = user.token;
		 secret = user.secret;
		 password = user.password;
		 referer = user.referer;
		 refererId = user.refererId;
	 }

	private void validateUser(User user) {
		if (user == null){
			throw new InvalidParameterException("Object User cannot be null");
		}
	}
	 
}
