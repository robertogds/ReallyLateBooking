package controllers.api;


import java.text.SimpleDateFormat;
import java.util.*;

import controllers.ThreeScale;

import models.*;
import models.dto.ApiResponse;
import models.dto.CountryDTO;
import models.dto.DealDTO;
import play.*;
import play.i18n.Lang;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;
import net.threescale.api.ApiFactory;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.ApiTransaction;
import net.threescale.api.v2.ApiTransactionForAppId;
import net.threescale.api.v2.ApiTransactionForUserKey;
import net.threescale.api.v2.AuthorizeResponse;
import notifiers.Mails;

@With(ThreeScale.class)
public class Countries  extends Controller {

	public static void list() {
        Collection<Country> countries = Country.findActiveCountries();
        
        Collection<CountryDTO> countriesDto = new ArrayList<CountryDTO>();
		for (Country country: countries){
			countriesDto.add(new CountryDTO(country));
		}
		
		ApiResponse response = new ApiResponse();
		response.setStatus(Http.StatusCode.OK);
		response.setTotal(String.valueOf(countries.size()));
		response.put("countries", countriesDto);
        renderJSON(response.json);
	}
}

