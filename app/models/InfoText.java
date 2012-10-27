package models;

import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.i18n.Lang;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;

public class InfoText extends Model {
    
	@Id(Generator.AUTO_INCREMENT)
    public Long id;
	
	@Required
	public String key;
	
	@Required
    @MaxSize(100000)
	public String content;
	
	@Required
	public String locale;

	public static Query<InfoText> all() {
    	return Model.all(InfoText.class);
    }
	
	public static InfoText findByKey(String key){
		Logger.debug("InfoTexts: looking for text for key " + key + " with locale " + Lang.get());
    	return InfoText.all().filter("key", key).filter("locale", Lang.get()).get();
	}
	
	public static InfoText findByKeyAndLocale(String key, String locale){
		Logger.debug("InfoTexts: looking for text for key " + key + " with locale " + locale);
    	return InfoText.all().filter("key", key).filter("locale", locale).get();
    }
    
    public static InfoText findById(Long id) {
        return all().filter("id", id).get();
    }

	@Override
	public String toString() {
		return key + " " + locale ;
	}
    
    
}
