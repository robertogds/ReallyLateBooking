package controllers.api;


import java.util.*;

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

public class Countries  extends Controller {
	
	public static void list() {
        Collection<Country> countries = Country.findActiveCountries();
        
        Collection<CountryDTO> countriesDto = new ArrayList<CountryDTO>();
		for (Country country: countries){
			countriesDto.add(new CountryDTO(country));
		}
		
		ApiResponse response = new ApiResponse();
		response.setStatus(ApiResponse.OK);
		response.setTotal(String.valueOf(countries.size()));
		response.put("countries", countriesDto);
        renderJSON(response.json);
	}
}

