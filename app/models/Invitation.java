package models;

import play.Logger;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.i18n.Lang;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;

public class Invitation extends Model {
    
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	
	@Required
	@Email
	public String email;
	@Required
	public String locale;
	
	public boolean sent;
	
	public static Query<Invitation> all() {
    	return Model.all(Invitation.class);
    }
	
	public static Invitation findByEmail(String email){
    	return Invitation.all().filter("email", email).get();

	}
	
    public static Invitation findById(Long id) {
        return all().filter("id", id).get();
    }

	@Override
	public String toString() {
		return email + " " + locale ;
	}
    
}
