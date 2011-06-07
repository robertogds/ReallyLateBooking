package models;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang.RandomStringUtils;


import play.Logger;
import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.libs.OAuth.TokenPair;
import play.modules.crudsiena.CrudUnique;
import siena.Column;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;
import siena.Unique;


@Table("users")
public class User extends Model{
 
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
   
	@CrudUnique
	@Required
    @Email
    public String email;
	public String validationCode;
	public boolean validated;
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
    
    public User(String email, String password, String firstName, String lastName, boolean isAdmin, boolean validated) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
        this.validated = validated;
    }
       
    
    @Override
	public void insert() {
    	this.validated =  true;
        this.validationCode = RandomStringUtils.randomAlphanumeric(12);
        this.token = RandomStringUtils.randomAlphanumeric(12);
        this.secret = RandomStringUtils.randomAlphanumeric(24);
        this.email = this.email.toLowerCase();
    	super.insert();
	}
    
    public static User findByEmail(String email){
    	return User.all().filter("email", email.toLowerCase()).get();
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
    	return User.all().filter("email", email.toLowerCase()).filter("validated", true).filter("password", password).get();
    }
 
    public String toString() {
        return email;
    }

	public void updateDetails(User user) {
		this.email = user.email.trim().isEmpty() ? this.email : user.email.toLowerCase();
		this.password = user.password.trim().isEmpty() ? this.password : user.password;
		this.firstName = user.firstName.trim().isEmpty() ? this.firstName : user.firstName;
		this.lastName = user.lastName.trim().isEmpty() ? this.lastName : user.lastName;
	}

	public Boolean emailValid(){
		return Validation.valid("email", this).ok;
	}
}