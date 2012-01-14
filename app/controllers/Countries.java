package controllers;


import java.util.*;

import models.*;
import models.dto.CountryDTO;
import models.dto.DealDTO;
import play.*;
import play.i18n.Lang;
import play.mvc.Before;
import play.mvc.Controller;


public class Countries  extends Controller {
	
	@Before
	static void analytics(){
		Statistic.saveVisit(request.path);
	}
	
	public static void countryList() {
        Collection<Country> countries = Country.findActiveCountries();
        
        Collection<CountryDTO> countriesDto = new ArrayList<CountryDTO>();
		for (Country country: countries){
			countriesDto.add(new CountryDTO(country));
		}
        renderJSON(countriesDto);
	}
}

