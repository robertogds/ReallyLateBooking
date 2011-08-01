package models;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

import controllers.CRUD.Hidden;


import play.Logger;
import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Crypto;
import play.libs.OAuth.TokenPair;
import play.modules.crudsiena.CrudUnique;
import siena.Column;
import siena.DateTime;
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
	public String resetCode;
    
    public String firstName;
    public String lastName;
    public Boolean isAdmin;
    public Boolean isOwner;
    public Boolean isFacebook;
	@DateTime
    public Date created;
    public String token;
    public String secret;  
    public Integer credit;
    public String inviteByCode;
    public String inviteCode;
    
    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password =  DigestUtils.md5Hex(password);
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public User(String email, String password, String firstName, String lastName, boolean isAdmin, boolean validated) {
        this.email = email;
        this.password =  DigestUtils.md5Hex(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
        this.isOwner = isAdmin;
        this.validated = validated;
    }
       
    
    public User(Long id) {
    	this.id = id;
    }

	@Override
	public void insert() {
    	this.validated =  true;
        this.validationCode = RandomStringUtils.randomAlphanumeric(12);
        this.token = RandomStringUtils.randomAlphanumeric(12);
        this.secret = RandomStringUtils.randomAlphanumeric(24);
        this.email = this.email.toLowerCase();
        this.created = Calendar.getInstance().getTime();
        this.isAdmin = this.isAdmin!=null ? this.isAdmin : Boolean.FALSE;
		this.isOwner = this.isOwner!=null ? this.isOwner : Boolean.FALSE;
		this.isFacebook = this.isFacebook!=null ? this.isFacebook : Boolean.FALSE;
    	super.insert();
	}
    
    public static User findByEmail(String email){
    	return User.all().filter("email", email.trim().toLowerCase()).get();
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
    
    public static User findByEmailAndPassword(String email, String password){
    	return User.all().filter("email", email.trim().toLowerCase()).filter("validated", true).filter("password", password.trim()).get();
    }
 
    public String toString() {
        return email;
    }

	public void updateDetails(User user) {
		if (!this.email.equalsIgnoreCase(user.email.trim())){
			this.email = user.email.trim().isEmpty() ? this.email : user.email.trim().toLowerCase();
			this.validateEmail();
		}
		this.password = user.password.trim().isEmpty() ? this.password : user.password.trim();
		this.firstName = user.firstName.trim().isEmpty() ? this.firstName : user.firstName.trim();
		this.lastName = user.lastName.trim().isEmpty() ? this.lastName : user.lastName.trim();
	}


	public void validate() {
		validateEmail();
	}
	
	private void validateEmail(){
		if (Validation.valid("email", this).message(Messages.get("user.validation.email.invalid")).ok && User.findByEmail(this.email) != null){
			Validation.addError("email", Messages.get("user.validation.email.unavailable"));
		}
	}
	
	public void setPasswordResetCode() {
		this.resetCode = RandomStringUtils.randomAlphanumeric(10);
		this.update();
	}
	
    public static User findByResetCode(String resertCode){
    	return User.all().filter("resetCode", resertCode.trim()).get();
    }
    
    public static User findByToken(String token){
    	return User.all().filter("token", token.trim()).get();
    }

	public Boolean checkFacebookUserExists() {
		if (this.isFacebook != null && this.isFacebook && User.findByEmail(this.email) != null){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
    
}