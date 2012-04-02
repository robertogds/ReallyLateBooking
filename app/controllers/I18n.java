package controllers;

import play.i18n.Lang;
import play.mvc.Before;
import play.mvc.Controller;

public class I18n extends Controller{

	@Before
    static void checkLang() throws Throwable {
        // Authent
        if(params._contains("lang")) {
        	String lang = params.get("lang");
        	Lang.change(lang);
        }
	}
	
	public static void changeLang(String lang, String redirectUrl){
		Lang.change(lang);
		redirect(redirectUrl);
	}
}
