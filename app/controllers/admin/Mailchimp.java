package controllers.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.tools.javac.code.Attribute.Array;

import models.City;
import models.Deal;
import models.dto.DealDTO;


import helper.MailChimpHelper;
import helper.mailchimp.models.CreateCampaignRequest;
import helper.mailchimp.models.MailchimpCampaign;
import helper.mailchimp.models.MailchimpList;
import helper.mailchimp.models.MailchimpTemplate;
import play.Logger;
import play.data.validation.Email;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.With;
import controllers.Check;
import controllers.Secure;


@Check("admin")
@With(Secure.class)
public class Mailchimp extends Controller{
	
	public static void index(){
		if (MailChimpHelper.ping()){
			flash.success("Se acaba de comprobar el estada de Mailchimp y esta activo.");
		}
		else{
			flash.success("Mailchimp no responde, no puedes crear una campaña ahora.");
		}
		
		List<City> cities = City.findActiveCities();
		Collection<MailchimpList> mailLists =  MailChimpHelper.lists();
		Collection<MailchimpCampaign> campaigns =  MailChimpHelper.campaigns();
		Collection<MailchimpTemplate> templates = MailChimpHelper.campaignTemplates();
		
		render(campaigns, mailLists, templates, cities);
	}
	
	public static void create(@Required String listId, @Required String subject, @Required @Email String fromEmail, @Required String fromName,  
			 @Required String templateId, @Required Long deal1Id, @Required Long deal2Id, @Required Long deal3Id, @Required Long cityId){
		City city = City.findById(cityId);
		if (city != null){
			Collection<Deal> deals = new ArrayList<Deal>();
			deals.add(Deal.findById(deal1Id));
			deals.add(Deal.findById(deal2Id));
			deals.add(Deal.findById(deal3Id));
	        Collection<DealDTO> dealsDtos = new ArrayList<DealDTO>();
			for (Deal deal: deals){
				Logger.debug("##hotel %s", deal.hotelName);
				dealsDtos.add(new DealDTO(deal));
			}
			
			//TODO ie18n
			String textContent = "Mandar todas las ofertas en texto en esta ciudad, " + city.name;
			String header =  city.name; 
			
			CreateCampaignRequest campaign = new CreateCampaignRequest(listId, subject, fromEmail, fromName, templateId);
			campaign.setContentText(textContent);
			campaign.setHeader(header);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("cityUrl", city.url);
			campaign.setSeeMoreLink(Router.getFullUrl("Deals.list", params));
			
			campaign.setCity(city.name);
			for (DealDTO deal: dealsDtos){
				Logger.debug("Metemos el hotel %s con la foto %s", deal.hotelName, deal. mainImageBig);
				campaign.addHotel(deal.hotelName);
				campaign.addImage(deal.mainImageBig);
				campaign.addPrice(deal.salePriceCents.toString());
				Map<String, Object> paramsDeal = new HashMap<String, Object>();
				paramsDeal.put("id", deal.id);
				paramsDeal.put("cityUrl", city.url);
				campaign.addButton(Router.getFullUrl("Deals.show", paramsDeal));
			}
			
			String id = MailChimpHelper.campaignCreate(campaign);
			flash.put("warning", "Repuesta de Mailchimp: "+ id);
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
