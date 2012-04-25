package helper;


import helper.mailchimp.models.CreateCampaignRequest;
import helper.mailchimp.models.MailchimpCampaign;
import helper.mailchimp.models.MailchimpList;
import helper.mailchimp.models.MailchimpTemplate;
import helper.mailchimp.models.SegmentOptions;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Logger.debug("############# ENVIO DE LA CAMPAÑA ##########");
		return prepareRequest(CAMPAIGN_SEND, params) ;
	}
	
	
	
	public static String sendCampaignTest(String campaignId, Collection<String> emails){
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("apikey", APIKEY);
		paramsMap.put("cid", campaignId);
		paramsMap.put("test_emails", emails);
		String params = new Gson().toJson(paramsMap);
		Logger.debug("############# ENVIO DEL TEST DE LA CAMPAÑA ##########");
		return prepareRequest(CAMPAIGN_SEND_TEST, params) ;
	}
	
	public static String sendSegmentTest(String listId, SegmentOptions segments){
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("apikey", APIKEY);
		paramsMap.put("list_id", listId);
		paramsMap.put("options", segments);
		String params = new Gson().toJson(paramsMap);
		Logger.debug("############# ENVIO DEL TEST DE LOS SEGMENTS ##########");
		return prepareRequest(CAMPAIGN_SEGMENT_TEST, params) ;
	}
	
	private static String prepareRequest(String method, String params){
		 try {
			method = URLEncoder.encode(method, "UTF-8");
			String url = BASE_URL + method;
			URL parsedUrl = new URL(url);
		    
			HttpURLConnection connection = (HttpURLConnection) parsedUrl.openConnection();
		    connection.setDoOutput(true);
		    connection.setRequestMethod("POST");
		    connection.addRequestProperty("Content-Type", "application/json");
			
		    if (params != null){
		        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		        writer.write(params);
		        writer.close();
		    }
		    
		    Logger.debug("Connection string: %s", connection.getOutputStream().toString());
	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            // Note: Should check the content-encoding.
	        	String response = HotUsaApiHelper.convertStreamToString(connection.getInputStream());
				response = StringUtils.trim(response);
				Logger.debug("response code: " + response);
	    		return response;
	        } else {
	            throw new Exception();
	        }
	    } catch (Exception e) {
	    	 Logger.error("Error receiving response from mailchimp: " + e);
	    }
		return null;
		
	}

	
}
