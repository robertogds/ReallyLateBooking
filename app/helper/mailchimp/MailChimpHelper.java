package helper.mailchimp;


import helper.UrlConnectionHelper;
import helper.hotusa.HotUsaApiHelper;
import helper.mailchimp.models.CreateCampaignRequest;
import helper.mailchimp.models.MailchimpCampaign;
import helper.mailchimp.models.MailchimpList;
import helper.mailchimp.models.MailchimpTemplate;
import helper.mailchimp.models.SegmentOptions;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Booking;
import models.Setting;

import org.apache.commons.lang.StringUtils;

import play.Logger;

import com.google.appengine.repackaged.org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class MailChimpHelper {
	public static final String APIKEY = "ccbb9b057ff56ed5b9a6722c5d0e6493-us2";
	private static final String BASE_URL = "http://us2.api.mailchimp.com/1.3/?method=";
	private static final String PING = "ping";
	private static final String LISTS = "lists";
	private static final String LISTMEMBERINFO = "listMemberInfo";
	private static final String TEMPLATES = "templates";
	private static final String CAMPAIGNS = "campaigns";
	private static final String DELETE_CAMPAIGN = "campaignDelete";
	private static final String CREATE_CAMPAIGN = "campaignCreate";
	private static final String CAMPAIGN_SEND_TEST = "campaignSendTest";
	private static final String CAMPAIGN_SEND = "campaignSendNow";
	private static final String CAMPAIGN_SEGMENT_TEST = "campaignSegmentTest";
	
	/**
	 * Checks service availability, returns {@code true} if successful.
	 * 
	 * @return Ping response string
	 */
	public static boolean ping(){
		return prepareRequest(PING, null) != null;
	}
	
	
	/**
	 * Returns the lists in the account. The result is a Collection of {@code
	 * MailchimpList}
	 * 
	 * @return {@code Collection<MailchimpList>}
	 */
	public static Collection<MailchimpList> lists() {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("apikey", APIKEY);
		String params = new Gson().toJson(paramsMap);
		String lists = prepareRequest(LISTS, params);
		if (lists != null){
			JsonObject listsJson = new JsonParser().parse(lists).getAsJsonObject();
			JsonArray arrayLists = listsJson.get("data").getAsJsonArray();
			Collection<MailchimpList> mailchimpLists = new ArrayList<MailchimpList>();
			for (JsonElement list: arrayLists){
				mailchimpLists.add(new Gson().fromJson(list, MailchimpList.class));
			}
			return mailchimpLists;
		}
		 return null;
	}

	
	/**
	 * Retrieve all templates defined for your user account
	 * 
	 * @return {@code Collection<MailchimpTemplate>}
	 */
	public static Collection<MailchimpTemplate> campaignTemplates() {
		Map<String, String> apiKey = new HashMap<String, String>();
		apiKey.put("apikey", APIKEY);
		String params = new Gson().toJson(apiKey);
		String templates = prepareRequest(TEMPLATES, params);
		if (templates != null){
			JsonObject listsJson = new JsonParser().parse(templates).getAsJsonObject();
			JsonArray arrayTemplates = listsJson.get("user").getAsJsonArray();
			Collection<MailchimpTemplate> mailchimpTemplates= new ArrayList<MailchimpTemplate>();
			for (JsonElement list: arrayTemplates){
				mailchimpTemplates.add(new Gson().fromJson(list, MailchimpTemplate.class));
			}
			return mailchimpTemplates;
		}
		return null;
	}
	
	public static Collection<MailchimpCampaign> campaigns() {
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("apikey", APIKEY);
		paramsMap.put("limit", "100");
		String params = new Gson().toJson(paramsMap);
		String campaigns = prepareRequest(CAMPAIGNS, params);
		if (campaigns != null){
			JsonObject listsJson = new JsonParser().parse(campaigns).getAsJsonObject();
			JsonArray arrayTemplates = listsJson.get("data").getAsJsonArray();
			Collection<MailchimpCampaign> mailchimpCampaigns= new ArrayList<MailchimpCampaign>();
			for (JsonElement list: arrayTemplates){
				mailchimpCampaigns.add(new Gson().fromJson(list, MailchimpCampaign.class));
			}
			return mailchimpCampaigns;
		}
		return null;
	}
	
	public static String campaignCreate(CreateCampaignRequest campaign) {
        String params = new Gson().toJson(campaign);
        String response = prepareRequest(CREATE_CAMPAIGN, params);
        String campaignId = response.trim().replaceAll("\"", "");
		return campaignId;
	}
	
	public static String sendCampaign(String campaignId){
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("apikey", APIKEY);
		paramsMap.put("cid", campaignId);
		String params = new Gson().toJson(paramsMap);
		return prepareRequest(CAMPAIGN_SEND, params) ;
	}
	
	
	public static String sendCampaignTest(String campaignId, Collection<String> emails){
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("apikey", APIKEY);
		paramsMap.put("cid", campaignId);
		paramsMap.put("test_emails", emails);
		String params = new Gson().toJson(paramsMap);
		return prepareRequest(CAMPAIGN_SEND_TEST, params) ;
	}
	
	public static String sendSegmentTest(String listId, SegmentOptions segments){
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("apikey", APIKEY);
		paramsMap.put("list_id", listId);
		paramsMap.put("options", segments);
		String params = new Gson().toJson(paramsMap);
		return prepareRequest(CAMPAIGN_SEGMENT_TEST, params) ;
	}
	
	
	
	public static List<String> findBookingsFromSuscribers(List<String> webBookingsEmail) {
		List<List<String>> emailLists = splitEmailList(webBookingsEmail);
		List<String> suscribers =  new ArrayList<String>();
		String listId = Setting.findByKey(Setting.MC_LIST_ID).value;
		for (List<String> emailList: emailLists){
			suscribers.addAll(findSuscriberEmails(emailList, listId));
		}
		
		return suscribers;
	}
	
	
	/**
	 * Returns the emails found on the given list. The result is a Collection of 
	 * Emails
	 * 
	 * @return {@code Collection<String>}
	 */
	public static Collection<String> findSuscriberEmails(List<String> webBookingsEmail, String listId) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("apikey", APIKEY);
		paramsMap.put("id", listId);
		paramsMap.put("email_address", webBookingsEmail);
		String params = new Gson().toJson(paramsMap);
		String lists = prepareRequest(LISTMEMBERINFO, params);
		List<String> suscribers =  new ArrayList<String>();
		if (lists != null){
			JsonObject listsJson = new JsonParser().parse(lists).getAsJsonObject();
			JsonArray arrayLists = listsJson.get("data").getAsJsonArray();
			for (JsonElement user: arrayLists){
				String email = user!= null && user.getAsJsonObject().get("email") != null ? 
						user.getAsJsonObject().get("email").getAsString() : null;
				if (email != null){
					suscribers.add(email);
				}
			}
			return suscribers;
		}
		return null;
	}

	
	
	private static List<List<String>> splitEmailList(
			List<String> webBookingsEmail) {
		List<List<String>> emailLists = new ArrayList<List<String>>();
		while(webBookingsEmail.size() >= 50){
			List<String> emails = webBookingsEmail.subList(0, 49);
			List<String> subList = new ArrayList<String>(emails);
			emails.clear();
			emailLists.add(subList);
		}
		if (webBookingsEmail.size() > 0){
			emailLists.add(webBookingsEmail);
		}
		return emailLists;
	}


	private static String prepareRequest(String method, String params) {
		return UrlConnectionHelper.prepareRequest(createUrl(method), params, UrlConnectionHelper.CONTENT_JSON);
	}

	private static String createUrl(String method){
		try {
			method = URLEncoder.encode(method, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Logger.error("Error encoding url: %s", e);
			return null;
		}
		return BASE_URL + method;
	}
	
}
