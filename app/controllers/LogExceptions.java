package controllers;

import notifiers.Mails;
import models.Statistic;
import play.Logger;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.With;

public class LogExceptions extends Controller{
	
	@Catch(Exception.class)
    public static void logIllegalState(Throwable throwable) {
        Logger.error("Internal error %s…", throwable);
        throwable.printStackTrace();
        Mails.errorMail("#WARNING# Internal Server Error", throwable.toString());
    }
	
}
