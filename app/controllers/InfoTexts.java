package controllers;

import models.InfoText;
import play.Logger;
import play.i18n.Lang;
import play.mvc.Before;
import play.mvc.Controller;

public class InfoTexts extends Controller {
	
	@Before
	public static void checkLanguage(){
		Logger.debug("### Accept-language: " + request.acceptLanguage().toString());
	}
	
	public static void show(String key){
		InfoText info = InfoText.findByKey(key);
		//if theres no infotext with current locale return default locale: English
		if (info == null){
			info = InfoText.findByKeyAndLocale(key, Lang.getLocale().ENGLISH.getLanguage());
		}
		renderJSON(info);
	}
}
