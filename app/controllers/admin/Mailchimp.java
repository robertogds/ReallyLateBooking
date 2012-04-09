package controllers.admin;

import helper.MailChimpHelper;
import helper.mailchimp.models.CreateCampaignRequest;
import helper.mailchimp.models.MailchimpCampaign;
import helper.mailchimp.models.MailchimpList;
import helper.mailchimp.models.MailchimpTemplate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.City;
import models.Deal;
import models.InfoText;
import models.Setting;
import models.dto.DealDTO;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.validation.Email;
import play.data.validation.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.With;
import controllers.Check;
import controllers.Secure;


@Check("admin")
@With(Secure.class)
public class Mailchimp extends Controller{
	
	public static void index(){
		if (!MailChimpHelper.ping()){
			flash.error("Mailchimp no responde, no puedes crear una campaña ahora.");
		}
		List<City> cities = City.findActiveCities();
		Collection<MailchimpList> mailLists =  MailChimpHelper.lists();
		Collection<MailchimpCampaign> campaigns =  MailChimpHelper.campaigns();
		Collection<MailchimpTemplate> templates = MailChimpHelper.campaignTemplates();
		
		render(campaigns, mailLists, templates, cities);
	}
	
	public static void create( @Required Long deal1Id, @Required Long deal2Id, @Required Long deal3Id, @Required Long cityId) throws UnsupportedEncodingException{
		
		 if (!validation.hasErrors()){
			 City city = City.findById(cityId);
				if (city != null){
					String originalLang = Lang.get();
					Lang.change("es");
					createI18nCampaignByCity(city, deal1Id, deal2Id, deal3Id, CreateCampaignRequest.CAMPAIGN_OPTION_SEGMENT_LANG_ES);
					Lang.change("en");
					createI18nCampaignByCity(city, deal1Id, deal2Id, deal3Id, CreateCampaignRequest.CAMPAIGN_OPTION_SEGMENT_LANG_EN);
					Lang.change("fr");
					createI18nCampaignByCity(city, deal1Id, deal2Id, deal3Id, CreateCampaignRequest.CAMPAIGN_OPTION_SEGMENT_LANG_FR);
					Lang.change(originalLang);
				}
		    }
		    else{
		    	flash.error(Messages.get("web.users.updateaccount.error"));
		    	params.flash(); // add http parameters to the flash scope
		        validation.keep(); // keep the errors for the next request
		    }
		index();
	}
	
	private static void createI18nCampaignByCity(City city, Long deal1Id, Long deal2Id, Long deal3Id, String lang) throws UnsupportedEncodingException{
			Collection<Deal> deals = new ArrayList<Deal>();
			deals.add(Deal.findById(deal1Id));
			deals.add(Deal.findById(deal2Id));
			deals.add(Deal.findById(deal3Id));
	        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
			for (Deal deal: deals){
				dealsDtos.add(new DealDTO(deal));
			}
			
			String header = city.name; 
			String listId = Setting.findByKey(Setting.MC_LIST_ID).value;
			String fromEmail = Setting.findByKey(Setting.MC_FROM_EMAIL).value;
			String fromName = Setting.findByKey(Setting.MC_FROM_NAME).value;
			String templateId = Setting.findByKey(Setting.MC_TEMPLATE_ID).value;
			String subject =   InfoText.findByKey(controllers.InfoTexts.MAILCHIMP_SUBJECT).content;
			subject = StringUtils.replace(subject, "##", city.name);
			CreateCampaignRequest campaign = new CreateCampaignRequest(listId, subject, fromEmail, fromName, templateId);
			campaign.setHeader(header);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("cityUrl", city.url);
			campaign.setSeeMoreLink(Router.getFullUrl("Deals.list", params));
			campaign.setCitySegment(city.mailchimpCode);
			campaign.setLangSegment(lang);
			campaign.setCity(city.name);
			for (DealDTO deal: dealsDtos){
				campaign.addHotel(deal.hotelName);
				campaign.addImage(deal.mainImageBig);
				campaign.addPrice(deal.salePriceCents.toString());
				Map<String, Object> paramsDeal = new HashMap<String, Object>();
				paramsDeal.put("id", deal.id);
				paramsDeal.put("cityUrl", city.url);
				campaign.addButton(Router.getFullUrl("Deals.show", paramsDeal));
			}
			
			Integer emailsNumber = campaign.testSegmentOptions();
			if (emailsNumber > 0){
				String id = MailChimpHelper.campaignCreate(campaign);
				Logger.debug("Repuesta de Mailchimp, el id es: %s", id);
			}
			else{
				flash.error("No se ha creado la campaña porque o bien algún dato es erróneo o ningún email cumple las condiciones.");
				Logger.error("No se ha creado la campaña porque o bien algún dato es erróneo o ningún email cumple las condiciones.");
			}
	}

	public static void settings(@Required String listId, @Required @Email String fromEmail, @Required String fromName,  
			 @Required String templateId) throws UnsupportedEncodingException{
		if (!validation.hasErrors()){
			 Setting.updateMailchimpSettings(listId, fromEmail, fromName, templateId);
	    }
	    else{
	    	flash.error(Messages.get("web.users.updateaccount.error"));
	    	params.flash(); // add http parameters to the flash scope
	        validation.keep(); // keep the errors for the next request
	    }
		index();
	}
	public static void sendTest(@Required String campaignId){
		Collection<String> emails = new ArrayList<String>();
		emails.add("hola@reallylatebooking.com");
		MailChimpHelper.sendCampaignTest(campaignId, emails);
		index();
	}
	
	public static void sendCampaign(@Required String campaignId){
		MailChimpHelper.sendCampaign(campaignId);
		index();
	}
}
