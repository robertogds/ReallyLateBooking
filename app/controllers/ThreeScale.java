package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import controllers.api.Deals;

import models.dto.ApiResponse;
import net.threescale.api.ApiFactory;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.ApiTransaction;
import net.threescale.api.v2.ApiTransactionForUserKey;
import net.threescale.api.v2.AuthorizeResponse;
import notifiers.Mails;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.Http;
import java.util.logging.Logger;


public class ThreeScale extends Controller {
	private static final Logger log = Logger.getLogger(ThreeScale.class.getName());

	// This is YOUR key from your api contract.
    private static String provider_private_key = "41a463d86935c0d40f201892f3e2d2d6";

	@Before
	public static void setCORS() { 
		Http.Header hd = new Http.Header(); 
		hd.name = "Access-Control-Allow-Origin"; 
		hd.values = new ArrayList<String>(); 
		hd.values.add("*"); 
		Http.Response.current().headers.put("Access-Control-Allow-Origin",hd); 
	}
	
	@Before
	public static void checkThreeScale() { 
		String user_key = request.params.get("user_key");
		try {
            Api2 server = ApiFactory.createV2Api(provider_private_key);
            AuthorizeResponse response = server.authorizeWithUserKey(user_key , null, null);
            if (response.getAuthorized()) {
                Map hits = new HashMap();
                hits.put("hits","1");
                String url = StringUtils.replace(request.action, ".", "/");
                hits.put(url,"1");
                ApiTransaction[] transactions = { new ApiTransactionForUserKey(user_key, hits) };
                server.report(transactions);
            } else {
            	log.severe("Failed 3Scale authorization:" + response.getReason());
            	renderApiError();
            }
        } catch (ApiException e) {
        	log.severe(e.getErrorMessage());
        	renderApiError();
        }
	}
	
	@Catch(Exception.class)
    public static void renderInternalError(Throwable throwable) {
        log.severe("Internal error at Edreams Controller… " + throwable.getStackTrace().toString());
        Mails.errorMail("##IMPORTANT: Error en White Label Api", throwable.getMessage());
        ApiResponse response = new ApiResponse(Http.StatusCode.INTERNAL_ERROR, "error", "Error interno. Contacte con atención al cliente, por favor. ");
		renderJSON(response.json);
    }
	
	private static void renderApiError(){
		Mails.errorMail("##WARNING: Client not autorized to use the API", request.toString());
		ApiResponse response = new ApiResponse(Http.StatusCode.BAD_REQUEST, "ERROR", "Invalid api key. Contact soporte@reallylatebooking.com");
		renderJSON(response.json);
	}

}
