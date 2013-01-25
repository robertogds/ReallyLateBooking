package controllers;

import java.util.logging.Logger;

import notifiers.Mails;
import models.Statistic;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.With;

public class LogExceptions extends Controller{
	
	private static final Logger log = Logger.getLogger(LogExceptions.class.getName());
	
	@Catch(Exception.class)
    public static void logIllegalState(Throwable throwable) {
        log.severe("Internal error …" + throwable);
        throwable.printStackTrace();
        Mails.errorMail("#WARNING# Internal Server Error", throwable.toString());
    }
	
}
