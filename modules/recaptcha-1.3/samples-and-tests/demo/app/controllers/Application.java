package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import ugot.recaptcha.Recaptcha;

import models.*;


/**
 * 
 * @author Olivier Refalo
 */
public class Application extends Controller {

    public static void index() {
        render();
    }

   public static void good() {
        render();
    }
    public static void save(@Recaptcha String recaptcha) {

     if(validation.hasErrors()) {
        validation.keep();
 		index();
      }
      else {
	    // validation is fine, let the use know
        good();
      }
     
   }
}
