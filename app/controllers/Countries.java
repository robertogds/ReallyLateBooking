package controllers;

import helper.dto.CountryDTO;
import helper.dto.DealDTO;

import java.util.*;

import models.*;
import play.*;
import play.mvc.Controller;


public class Countries  extends Controller {
	
	public static void countryList() {
        Collection<Country> countries = Country.findActiveCountries();
        
        Collection<CountryDTO> countriesDto = new ArrayList<CountryDTO>();
		for (Country country: countries){
			countriesDto.add(new CountryDTO(country));
		}
        renderJSON(countriesDto);
	}
}

