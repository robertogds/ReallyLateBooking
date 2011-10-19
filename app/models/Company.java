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
import play.modules.crudsiena.CrudUnique;
import siena.Column;
import siena.DateTime;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;
import siena.Unique;

/*
 * Represents the legal entity representing the hotels
 * Used for invoicing purposes
 * */
@Table("companies")
public class Company extends Model{
 
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
   
	@CrudUnique
	@Required
    public String name;
	public String nif;
	public String phone;
	public String address;
	@DateTime
    public Date updated;
	public Integer fee;

    public Company(String name, String nif, String phone, String lastName, Integer fee) {
        this.name = name;
        this.nif =  nif;
        this.phone = phone;
        this.fee = fee;
        this.updated = Calendar.getInstance().getTime();
    }
    
    public static Company findByName(String name){
    	return Company.all().filter("name", name.trim().toLowerCase()).get();
    }
    
    public static Query<Company> all() {
    	return Model.all(Company.class);
    }
    
    public static Company findById(Long id) {
        return all().filter("id", id).get();
    }
 
    public String toString() {
        return name;
    }
}