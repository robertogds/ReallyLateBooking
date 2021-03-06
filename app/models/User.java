package models;

import helper.UtilsHelper;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import models.dto.StatusMessage;
import models.dto.UserStatusMessage;
import notifiers.Mails;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.h2.util.MathUtils;

import play.Logger;
import play.Play;
import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Lang;
import play.i18n.Messages;
import play.modules.crudsiena.CrudUnique;
import play.mvc.Http;
import play.mvc.Scope.Session;
import play.templates.JavaExtensions;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.Table;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import controllers.Security;


@Table("users")
public class User extends Model{
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	@CrudUnique
	@Required
    @Email
    public String email;
	@Required
    @Password
    public String password;
	public String contactEmail;
	public String phone;
    public String firstName;
    public String lastName;
    public String nif;
    public String address;
	public String company;
    public Boolean isAdmin;
    public Boolean isOwner;
    public Boolean isFacebook;
	public boolean isInvestor;
	public boolean isEditor;
	public boolean isPartner;
	@Index("user_partner_index")
	public Partner partner;
    public String referer;
	public String refererId;
	public boolean vip;
	public boolean fromWeb;
	public String validationCode;
	public boolean validated;
	//Facebook id
	public String fbid;
	public String resetCode;
	@DateTime
    public Date created;
	@DateTime
    public Date lastAppLogin;
	@DateTime
    public Date lastWebLogin;
    public String token;
    public String secret;  
    public String fbLink;
    public String fbUsername;
    public String gender;
    public int timezone;
    public String locale;
    public String fbAccessToken;
    public String fbExpires;
	public boolean pendingSurvey;
	@DateTime
	public Date firstBookingDate;
	public Boolean fromWhiteLabel;
	public boolean isNew;
	
    
    public User() {
    	super();
    }
    
    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password =  password != null ? DigestUtils.md5Hex(password) : null;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public User(String email, String password, String firstName, String lastName, boolean isAdmin, boolean validated) {
        this.email = email;
        this.password =  password != null ? DigestUtils.md5Hex(password) : null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
        this.isOwner = isAdmin;
        this.validated = validated;
    }
    
    public User(Long id) {
    	this.id = id;
    }

	public User(String email, String firstName,
			String lastName, String phone, Boolean fromWhiteLabel) {
		this(email, "nopassrlb", firstName, lastName);	
		this.phone = phone;
		this.fromWhiteLabel = fromWhiteLabel;
	}

	@Override
	public void insert() {
    	this.validated =  true;
        this.validationCode = RandomStringUtils.randomAlphanumeric(12);
        this.token = RandomStringUtils.randomAlphanumeric(12);
        this.secret = RandomStringUtils.randomAlphanumeric(24);
        this.email = this.email !=null ? this.email.toLowerCase() : null;
        this.created = Calendar.getInstance().getTime();
        this.isAdmin = this.isAdmin!=null ? this.isAdmin : Boolean.FALSE;
		this.isOwner = this.isOwner!=null ? this.isOwner : Boolean.FALSE;
		this.isFacebook = this.isFacebook!=null ? this.isFacebook : Boolean.FALSE;
		this.locale = Lang.get();
		this.isNew = true;
		this.createUniqueRefererId();
    	super.insert();
    	this.createUserCoupons();
	}
    
	public void recreateRefererCoupon(){
		this.createRefererId();
		this.update();
		this.createUserCoupons();
	}
	
	private void createUniqueRefererId(){
		if (this.refererId == null){
			createRefererId();
		}
		else{
			this.refererId += RandomUtils.nextInt(9);
		}
		
		if (User.findByRefererId(this.refererId) != null){
			createUniqueRefererId();
		}
	}
	
	private void createRefererId() {
		String lastName = StringUtils.isBlank(this.lastName) ? "" : this.lastName.trim();
		lastName = JavaExtensions.slugify(lastName);
		lastName = UtilsHelper.truncate(lastName, 5);
		if (lastName.length() < 5){
			lastName += RandomStringUtils.randomAlphabetic(5 - lastName.length());
		}
		this.refererId = (lastName + RandomUtils.nextInt(99)).toUpperCase();
	}

	private void createUserCoupons(){
		String key = this.refererId;
		//Create his coupon for the first booking
		//MyCoupon myCoupon = new MyCoupon(this, key, Coupon.CREDITS_FIRST_DEFAULT);
		//myCoupon.insert();
		//Create the coupon so his friends can find it
		Coupon coupon =  new Coupon(key,new Integer(Play.configuration.getProperty("coupons.referal.credits")), Coupon.CREDITS_REFERER_TYPE);
		coupon.title = Messages.get("coupon.title");
		coupon.duration = new Integer(Play.configuration.getProperty("coupons.referal.duration"));
		coupon.insert();
	}
	
	public static void facebookOAuthCallback(JsonObject data){
		Logger.debug("User Json from FB: " + data);
		registerOrLoginFromFaceBook(data);
    }
	
	/**
	 * Login the user if already exists or register a new user
	 * if can't found user by email.
	 * @param data
	 */
    private static void registerOrLoginFromFaceBook(JsonObject data) {
    	Logger.debug("Login/Registering user from facebook: %s ", data.toString());
    	String email = data.get("email") != null ? data.get("email").getAsString() : null;
    	User user = email!= null? User.findByEmail(email) : null;
    	if (user == null){
    		user = registerFromFacebook(data);
    		user.get();
    	}
    	else{
    		user.getUserDataFromFB(data);
    	}
    	user.createUserSession();
	}
    
    private static User registerFromFacebook(JsonObject data) {
		Logger.debug("User new from Facebook" );
		User user = new User();
		user.fromWeb = true;
		user.getUserDataFromFB(data);
		user.insert();
		Mails.welcome(user);
		return user;
	}


	private void getUserDataFromFB(JsonObject data) {
		try {
			this.firstName = data.get("first_name") != null ?  data.get("first_name").getAsString() : "no_name";
			this.lastName = data.get("last_name") != null ?  data.get("last_name").getAsString() : "no_name";
			this.email = data.get("email") != null ? data.get("email").getAsString() : "no_email";
			this.isFacebook = Boolean.TRUE;
			this.fbid = data.get("id") != null ? data.get("id").getAsString() : "no_id";
			this.fbLink = data.get("link") != null ? data.get("link").getAsString() : "no_fb_link";
			this.fbUsername = data.get("username") != null ? data.get("username").getAsString() : null;
			this.gender = data.get("gender") != null ? data.get("gender").getAsString() : null;
			this.timezone = data.get("timezone") != null ? data.get("timezone").getAsInt() : null;
			this.locale = data.get("locale") != null ? data.get("locale").getAsString() : null;
			this.fbAccessToken = data.get("accessToken") != null ? data.get("accessToken").getAsString() : null;
			this.fbExpires = data.get("expires") != null ? data.get("expires").getAsString() : null;
		} catch (Exception e) {
			Logger.error("Error extracting data from fb user", e);
		}
	}
	
	public static User loginJson(String json) { 
		User user = new Gson().fromJson(json, User.class);
		return login(user);
	 }	
	
	public static User login(User user) {
		if (user != null){
			Boolean isFacebook = user.isFacebook != null && user.isFacebook;
			if (connect(user.email, user.password) != null || isFacebook){
				user = User.findByEmail(user.email);
				user.lastAppLogin = Calendar.getInstance().getTime();
		    	user.update();
			}
			else{
				user = null;
			}
		}
		return user;
	}
	
	public void createUserSession(){
    	Session.current().put("username", this.email);
    	Session.current().put("firstName", this.firstName);
    	Session.current().put("lastName", this.lastName);
    	Session.current().put("userId", this.id);
    	Session.current().put("uuid", this.fbid);
    	this.lastWebLogin = Calendar.getInstance().getTime();
    	this.update();
    }
	
	public static Collection<User> getAllOwners(){
    	return User.all().filter("isOwner", true).order("firstName").fetch();
    }
	
	public static User findByEmail(String email){
    	return email != null ? User.all().filter("email", email.trim().toLowerCase()).get(): null;
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
    	return email != null ? User.all().filter("email", email.trim().toLowerCase()).filter("password", password.trim()).get() : null;
    }
 
    public String toString() {
        return email;
    }

	public void updateDetails(User user) {
		if (this.email != null && user.email != null && !this.email.equalsIgnoreCase(user.email.trim())){
			this.email = user.email.trim().isEmpty() ? this.email : user.email.trim().toLowerCase();
			validateEmail();
		}
		this.password = user.password.trim().isEmpty() ? this.password : user.password.trim();
		this.firstName = user.firstName.trim().isEmpty() ? this.firstName : user.firstName.trim();
		this.lastName = user.lastName.trim().isEmpty() ? this.lastName : user.lastName.trim();
		this.phone = StringUtils.isBlank(user.phone) ? this.phone : user.phone.trim();
		this.address = StringUtils.isBlank(user.address) ? this.address : user.address.trim();
		this.nif = StringUtils.isBlank(user.nif) ? this.nif : user.nif.trim();
		this.company = StringUtils.isBlank(user.company) ? this.company : user.company.trim();
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
    
	public static List<User> findSurveyPending() {
		return User.all().filter("pendingSurvey", true).fetch();
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
	
	public static int countNewWebUsersByDate(Calendar calStart, Calendar calEnd) {
		int users  = User.all().filter("created>", calStart.getTime()).filter("fromWeb", Boolean.TRUE).
							filter("created<", calEnd.getTime()).count();
		return users;
	}

	public int calculateTotalCreditsFromMyCoupons() {
		List<MyCoupon> coupons = MyCoupon.findActiveByUser(this);
		Integer credits = 0;
		for (MyCoupon coupon : coupons){
			if (!coupon.expiredOrUsed()){
				credits += coupon.credits;
			}
		}
		return credits;
	}

	public void markMyCouponsAsUsed(Integer creditsUsed, Integer finalPrice) {
		List<MyCoupon> coupons = MyCoupon.findActiveByUserOrderByCredits(this);
		Integer credits = 0;
		for (MyCoupon coupon : coupons){
			if ( credits >= creditsUsed){
				break;
			}
			else if (!coupon.expiredOrUsed()){
				credits +=  coupon.useValidUnused(finalPrice);
			}
		}
	}

	public static int countAllFirstBookingByDate(Date start, Date end) {
		return allFirstBookingByDate(start, end).count();
		
	}
	
	public static Query<User> allFirstBookingByDate(Date start, Date end) {
		return User.all().filter("firstBookingDate>", start).filter("firstBookingDate<", end);
	}

	public static void updateJustBooked(Long id, Date checkinDate) {
		//set user first booking if needed
		User user = User.findById(id);
		user.firstBookingDate = user.firstBookingDate == null ? checkinDate : user.firstBookingDate;
		user.pendingSurvey = true;
		user.update();
	}

	public static User findOrCreateUserForWhiteLabel(String email, String firstName, String lastName, String phone) {
		User user = User.findByEmail(email);
		if (user == null){
			Boolean fromWhiteLabel = Boolean.TRUE;
			user = new User(email, firstName, lastName, phone, fromWhiteLabel);
			user.insert();
			
		}
		else{
			user.firstName = firstName;
			user.lastName = lastName;
			user.phone = phone;
			user.update();
		}
		user.get();
		Logger.debug("findOrCreateUserForWhiteLabel: %s ", user);
		return user;
	}

	public static List<User> findLastRegistered(int max) {
		return all().order("-created").fetch(max);
	}
	
	public static List<User> findLastAppLogin(int max) {
		return all().order("-lastAppLogin").fetch(max);
	}

	public static List<User> findByFirstName(String firstName) {
		return all().filter("firstName", firstName).fetch();
	}
	
	public static List<User> findByLastName(String lastName) {
		return all().filter("firstName", lastName).fetch();
	}


	/*
	public void updateUserCredits(Integer credits) {
		this.credits = this.credits - credits;
		this.update();
	}
	*/
}