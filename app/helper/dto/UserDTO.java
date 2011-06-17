package helper.dto;

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
	
	 public UserDTO(User user) {
		 validateUser(user);
		 id = user.id;
		 email = user.email;
		 firstName = user.firstName;
		 lastName = user.lastName;
		 token = user.token;
		 secret = user.secret;
		 password = user.password;
	 }

	private void validateUser(User user) {
		if (user == null){
			throw new InvalidParameterException("Object User cannot be null");
		}
	}
	 
	 
	 
}
