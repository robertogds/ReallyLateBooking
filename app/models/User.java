package models;

import java.util.*;
import models.crudsiena.SienaSupport;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;
 
@Table("users")
public class User extends SienaSupport {
 
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
    public String email;
    public String password;
    public String firstName;
    public String lastName;
    public boolean isAdmin;
    public Date created;
    
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
 
    public String toString() {
        return email;
    }
 
}