package models.dto;

import helper.DateHelper;
import helper.ImageHelper;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import controllers.InfoTexts;

import models.City;
import models.Deal;
import models.InfoText;
import models.User;
import play.Logger;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import siena.Generator;
import siena.Id;
import siena.Index;

public class DealDTOV3 extends AbstractDealDTO{
    
	public DealDTOV3(Deal deal) {
		super(deal);
		this.textToHtml();
		ImageHelper.imagesToList(this);
	}

	
}
