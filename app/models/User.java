package models;

import java.util.Calendar;
import java.util.Date;

import notifiers.Mails;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.h2.util.MathUtils;

import play.Logger;
import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.modules.crudsiena.CrudUnique;
import play.mvc.Scope.Session;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;

import com.google.gson.JsonObject;


@Table("users")
public class User extends Model{
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	
	//Facebook id
	public String fbid;
   
	@CrudUnique
	@Required
    @Email
    public String email;
	public String validationCode;
	public boolean validated;
	public boolean vip;
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
    public String fbLink;
    public String fbUsername;
    public String gender;
    public int timezone;
    public String locale;
    public String fbAccessToken;
    public String fbExpires;
    public String referer;
    public int credits;
	public String refererId;
    
    public User() {
    	super();
    }
    
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
		this.refererId = this.createRefererId();
    	super.insert();
    	this.createUserCoupons();
	}
    
	private String createRefererId() {
		return (this.firstName.trim() + this.lastName.trim() + RandomUtils.nextInt(100)).toUpperCase();
	}

	private void createUserCoupons(){
		String key = this.refererId;
		//Create his coupon for the first booking
		MyCoupon myCoupon = new MyCoupon(this, key, Coupon.CREDITS_FIRST_DEFAULT);
		myCoupon.insert();
		//Create the coupon so his friends can find it
		Coupon coupon =  new Coupon(key,Coupon.CREDITS_REFERER_DEFAULT, Coupon.CREDITS_REFERER_TYPE);
		coupon.insert();
	}
	
	public static void facebookOAuthCallback(JsonObject data){
		Logger.debug("User Json from FB: " + data);
		registerOrLogin(data);
    }
	
	/**
	 * Login the user if already exists or register a new user
	 * if can't found user by email.
	 * @param data
	 */
    private static void registerOrLogin(JsonObject data) {
    	String email = data.get("email").getAsString();
    	User user = User.findByEmail(email);
    	if (user != null){
    		Logger.debug("User exists: " + user.email);
    		user.getUserDataFromFB(data);
    		user.update();
    		user.createUserSession();
    	}
    	else{
    		Logger.debug("User new: " + email);
    		user = new User();
    		user.getUserDataFromFB(data);
    		user.insert();
    		Mails.welcome(user);
    		user.createUserSession();
    	}
	}
    
    public void createUserSession(){
    	Logger.debug("User session: " + Session.current().toString());
    	Session.current().put("username", this.email);
    	Session.current().put("firstName", this.firstName);
    	Session.current().put("lastName", this.lastName);
    	Session.current().put("userId", this.id);
    	Session.current().put("uuid", this.fbid);
    }

	private void getUserDataFromFB(JsonObject data) {
		this.firstName = data.get("first_name").getAsString();
		this.lastName = data.get("last_name").getAsString();
		this.email = data.get("email").getAsString();
		this.isFacebook = Boolean.TRUE;
		this.fbid = data.get("id").getAsString();
		this.fbLink = data.get("link").getAsString();
		this.fbUsername = data.get("username") != null ? data.get("username").getAsString() : null;
		this.gender = data.get("gender").getAsString();
		this.timezone = data.get("timezone").getAsInt();
		this.locale = data.get("locale").getAsString();
		this.fbAccessToken = data.get("accessToken").getAsString();
		this.fbExpires = data.get("expires").getAsString();
	}
	

	public static User findByEmail(String email){
    	return User.all().filter("email", email.trim().toLowerCase()).get();
    }
	
	public static User findByRefererId(String refererId){
    	return User.all().filter("refererId", refererId.trim().toUpperCase()).get();
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
    	return User.all().filter("email", email.trim().toLowerCase()).filter("password", password.trim()).get();
    }
 
    public String toString() {
        return email;
    }

	public void updateDetails(User user) {
		if (!this.email.equalsIgnoreCase(user.email.trim())){
			this.email = user.email.trim().isEmpty() ? this.email : user.email.trim().toLowerCase();
			validateEmail();
		}
		this.password = user.password.trim().isEmpty() ? this.password : user.password.trim();
		this.firstName = user.firstName.trim().isEmpty() ? this.firstName : user.firstName.trim();
		this.lastName = user.lastName.trim().isEmpty() ? this.lastName : user.lastName.trim();
	}


	public void validate() {
		validateEmail();
	}
	
	private void validateEmail(){
		if (User.findByEmail(this.email) != null){
			Validation.addError("user.email", Messages.get("user.validation.email.unavailable"));
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
		if (this.isFacebook != null && this.isFacebook && this.email!= null && User.findByEmail(this.email) != null){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public static int countNewUsersByDate(Calendar calStart, Calendar calEnd) {
		int users  = User.all().filter("created>", calStart.getTime()).
							filter("created<", calEnd.getTime()).count();
		return users;
	}

}