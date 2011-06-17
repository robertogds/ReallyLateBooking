package controllers;

import models.InfoText;
import play.Logger;
import play.i18n.Lang;
import play.mvc.Controller;

public class InfoTexts extends Controller {

	public static void show(String key){
		InfoText info = InfoText.findByKey(key);
		Logger.debug("InfoTexts: looking fo text for key " + key + " with locale " + Lang.get());
		//if theres no infotext with current locale return default locale: English
		if (info == null){
			info = InfoText.findByKeyAndLocale(key, Lang.getLocale().ENGLISH.getLanguage());
			Logger.debug("InfoTexts: looking for text for key " + key + " with default locale " + Lang.getLocale().ENGLISH.getLanguage());
		}
		renderJSON(info);
	}
}
