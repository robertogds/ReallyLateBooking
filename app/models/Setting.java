package models;

import play.data.validation.Email;
import play.data.validation.Required;
import play.i18n.Lang;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;

public class Setting extends Model{
	
	public static final String MC_LIST_ID = "mc_list_id";
	public static final String MC_FROM_EMAIL = "mc_from_email";
	public static final String MC_TEMPLATE_ID = "mc_template_id";
	public static final String MC_FROM_NAME = "mc_from_name";
 
	@Id(Generator.AUTO_INCREMENT)
	public Long id;

	@Required
	public String key;

	@Required
	public String value;
	
	

	public Setting(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public static void updateMailchimpSettings(String listId, String fromEmail,
			String fromName, String templateId) {
		createOrUpdate(MC_LIST_ID, listId);
		createOrUpdate(MC_FROM_EMAIL, fromEmail);
		createOrUpdate(MC_TEMPLATE_ID, templateId);
		createOrUpdate(MC_FROM_NAME, fromName);
		
	}
	
	private static void createOrUpdate(String key, String value) {
		Setting setting = Setting.findByKey(key);
		if (setting == null){
			Setting newSetting = new Setting(key,value);
			newSetting.save();
		}
		else{
			setting.value = value;
			setting.update();
		}
	}

	public static Setting findByKey(String key){
    	return Setting.all().filter("key", key).get();
    }
    
    public static Setting findById(Long id) {
        return all().filter("id", id).get();
    }

	public static Query<Setting> all() {
    	return Model.all(Setting.class);
    }
}
