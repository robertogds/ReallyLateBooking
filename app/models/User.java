package models;

import java.util.*;


import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.libs.OAuth.TokenPair;
import models.crudsiena.SienaSupport;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;
import siena.Unique;


@Table("users")
public class User extends SienaSupport{
 
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
   
	@Unique("email")
	@Required
    @Email
    public String email;
	@Required
    @Password
    public String password;
    
    public String firstName;
    public String lastName;
    public boolean isAdmin;
    public Date created;
    
    public String token;
    public String secret;   
    
    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public User(String email, String password, String firstName, String lastName, Boolean isAdmin) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
    }
    
    public static User findByEmail(String email){
    	return User.all().filter("email", email).get();
    }
    
    public static Query<User> all() {
    	return Model.all(User.class);
    }
    
    public static User findById(Long id) {
        return all().filter("id", id).get();
    }
    
    public static User connect(String email, String password) {
        return findByEmailAndPassword(email, password);
    }
    
    private static User findByEmailAndPassword(String email, String password){
    	return User.all().filter("email", email).filter("password", password).get();
    }
 
    public String toString() {
        return email;
    }

	public void updateDetails(User user) {
		this.email = user.email.trim().isEmpty() ? this.email : user.email;
		this.password = user.password.trim().isEmpty() ? this.password : user.password;
		this.firstName = user.firstName.trim().isEmpty() ? this.firstName : user.firstName;
		this.lastName = user.lastName.trim().isEmpty() ? this.lastName : user.lastName;
	}
	
    public TokenPair getTokenPair() {
        return new TokenPair(token, secret);
    }

    public void setTokenPair(TokenPair tokens) {
        this.token = tokens.token;
        this.secret = tokens.secret;
        this.update();
    }

}