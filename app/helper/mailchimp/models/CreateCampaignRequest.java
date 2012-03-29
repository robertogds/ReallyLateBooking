package helper.mailchimp.models;

import helper.MailChimpHelper;

import java.util.HashMap;
import java.util.Map;

import models.City;

public class CreateCampaignRequest {
	public static final String	CAMPAIGN_TYPE_REGULAR				= "regular";
	public static final String	CAMPAIGN_TYPE_PLAINTEXT				= "plaintext";
	public static final String	CAMPAIGN_TYPE_ABSPLIT				= "absplit";
	public static final String	CAMPAIGN_TYPE_RSS					= "rss";
	public static final String	CAMPAIGN_TYPE_TRANS					= "trans";

	public static final String	CAMPAIGN_OPTION_LIST_ID				= "list_id";
	public static final String	CAMPAIGN_OPTION_SUBJECT				= "subject";
	public static final String	CAMPAIGN_OPTION_FROM_EMAIL			= "from_email";
	public static final String	CAMPAIGN_OPTION_FROM_NAME			= "from_name";
	public static final String	CAMPAIGN_OPTION_TO_NAME				= "to_name";
	public static final String	CAMPAIGN_OPTION_TEMPLATE_ID			= "template_id";
	public static final String	CAMPAIGN_OPTION_FOLDER_ID			= "folder_id";
	public static final String	CAMPAIGN_OPTION_TRACKING			= "tracking";
	public static final String	CAMPAIGN_OPTION_TITLE				= "title";
	public static final String	CAMPAIGN_OPTION_AUTHENTICATE		= "authenticate";
	public static final String	CAMPAIGN_OPTION_ANALYTICS			= "analytics";
	public static final String	CAMPAIGN_OPTION_ANALYTICS_GOOGLE	= "google";
	public static final String	CAMPAIGN_OPTION_INLINE_CSS			= "inline_css";
	public static final String	CAMPAIGN_OPTION_GENERATE_TEXT		= "generate_text";
	public static final String	CAMPAIGN_OPTION_AUTO_TWEET			= "auto_tweet";
	public static final String	CAMPAIGN_OPTION_AUTO_FB			    = "auto_fb_post";

	public static final String	CAMPAIGN_CONTENT_HTML				= "html";
	public static final String	CAMPAIGN_CONTENT_TEXT				= "text";
	public static final String	CAMPAIGN_CONTENT_URL				= "url";
	public static final String	CAMPAIGN_CONTENT_HTML_HEADER		= "html_HEADER";
	public static final String	CAMPAIGN_CONTENT_HTML_MAIN			= "html_MAIN";
	public static final String	CAMPAIGN_CONTENT_HTML_HOTEL1		= "html_HOTEL1";
	public static final String	CAMPAIGN_CONTENT_HTML_HOTEL2		= "html_HOTEL2";
	public static final String	CAMPAIGN_CONTENT_HTML_HOTEL3		= "html_HOTEL3";
	public static final String	CAMPAIGN_CONTENT_HTML_IMAGE1		= "html_IMAGE1";
	public static final String	CAMPAIGN_CONTENT_HTML_IMAGE2		= "html_IMAGE2";
	public static final String	CAMPAIGN_CONTENT_HTML_IMAGE3		= "html_IMAGE3";
	public static final String	CAMPAIGN_CONTENT_HTML_SIDECOLUMN	= "html_SIDECOLUMN";
	public static final String	CAMPAIGN_CONTENT_HTML_FOOTER		= "html_FOOTER";
	public static final String	CAMPAIGN_CONTENT_HTML_CITY			= "html_CITY";
	public static final String	CAMPAIGN_CONTENT_HTML_PRICE1		= "html_content_price_1";
	public static final String	CAMPAIGN_CONTENT_HTML_PRICE2		= "html_content_price_2";
	public static final String	CAMPAIGN_CONTENT_HTML_PRICE3		= "html_content_price_3";
	public static final String	CAMPAIGN_CONTENT_HTML_BUTTON1		= "html_BUTTON_CONTENT01";
	public static final String	CAMPAIGN_CONTENT_HTML_BUTTON2		= "html_BUTTON_CONTENT02";
	public static final String	CAMPAIGN_CONTENT_HTML_BUTTON3		= "html_BUTTON_CONTENT03";
	public static final String	CAMPAIGN_CONTENT_HTML_SEEMORE		= "html_SEEMORELINK";
	
	
	public String apikey;
	public String type;
	public Map<String, String> options;
	public Map<String, String> content;


	public CreateCampaignRequest(String listId, String subject, String fromEmail, String fromName,String templateId) {
		super();
        this.apikey = MailChimpHelper.APIKEY;
        this.type = CAMPAIGN_TYPE_REGULAR;
        this.options = new HashMap<String, String>();
        this.options.put(CAMPAIGN_OPTION_LIST_ID, listId);
        this.options.put(CAMPAIGN_OPTION_SUBJECT, subject);
        this.options.put(CAMPAIGN_OPTION_FROM_EMAIL, fromEmail);
        this.options.put(CAMPAIGN_OPTION_FROM_NAME, fromName);
        this.options.put(CAMPAIGN_OPTION_TO_NAME, "*|FNAME|*");
        this.options.put(CAMPAIGN_OPTION_TEMPLATE_ID, templateId);
        this.content = new HashMap<String, String>();
	}
	
	public void setContentText(String contentText){
        this.content.put(CAMPAIGN_CONTENT_TEXT, contentText);
	}
	
	public void setHeader(String header){
		this.content.put(CAMPAIGN_CONTENT_HTML_HEADER, header);
	}
	
	public void setMain(String main){
        this.content.put(CAMPAIGN_CONTENT_HTML_MAIN, main);
	}
	public void setCity(String city){
        this.content.put(CAMPAIGN_CONTENT_HTML_CITY, city);
	}
	public void setSideColumn(String sideColumn){
        this.content.put(CAMPAIGN_CONTENT_HTML_SIDECOLUMN, sideColumn);
	}
	
	public void setFooter( String footer){
        this.content.put(CAMPAIGN_CONTENT_HTML_FOOTER, footer);
	}
	public void setSeeMoreLink( String link){
        this.content.put(CAMPAIGN_CONTENT_HTML_SEEMORE, getSeeMoreLinkHtml(link));
	}
	
	public void setHtmlContent( String htmlContent){
		this.content.put(CAMPAIGN_CONTENT_HTML, htmlContent);	
	}
	
	public void setHotel1( String hotel){
		this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL1, getHotelHtml(hotel));	
	}
	
	public void setImage1( String image){
		this.content.put(CAMPAIGN_CONTENT_HTML_IMAGE1, getImageHtml(image));	
	}
	
	public void setPrice1( String price){
		this.content.put(CAMPAIGN_CONTENT_HTML_PRICE1, price);	
	}
	
	public void setHotel2( String hotel){
		this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL2, getHotelHtml(hotel));	
	}
	
	public void setImage2( String image){
		this.content.put(CAMPAIGN_CONTENT_HTML_IMAGE2, getImageHtml(image));	
	}
	
	public void setPrice2( String price){
		this.content.put(CAMPAIGN_CONTENT_HTML_PRICE2, price);	
	}
	
	public void setHotel3( String hotel){
		this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL3, getHotelHtml(hotel));	
	}
	
	public void setImage3( String image){
		this.content.put(CAMPAIGN_CONTENT_HTML_IMAGE3, getImageHtml(image));	
	}
	
	public void setPrice3( String price){
		this.content.put(CAMPAIGN_CONTENT_HTML_PRICE3, price);	
	}

	public void addHotel(String hotelName) {
		if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_HOTEL1)){
			this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL1, getHotelHtml(hotelName));	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_HOTEL2)){
			this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL2, getHotelHtml(hotelName));	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_HOTEL3)){
			this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL3, getHotelHtml(hotelName));	
		}
	}
	
	public void addImage(String image) {
		if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_IMAGE1)){
			this.content.put(CAMPAIGN_CONTENT_HTML_IMAGE1, getImageHtml(image));	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_IMAGE2)){
			this.content.put(CAMPAIGN_CONTENT_HTML_IMAGE2, getImageHtml(image));	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_IMAGE3)){
			this.content.put(CAMPAIGN_CONTENT_HTML_IMAGE3, getImageHtml(image));	
		}
	}
	
	public void addButton(String link) {
		if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_BUTTON1)){
			this.content.put(CAMPAIGN_CONTENT_HTML_BUTTON1, getButtonHtml(link));	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_BUTTON2)){
			this.content.put(CAMPAIGN_CONTENT_HTML_BUTTON2, getButtonHtml(link));	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_BUTTON3)){
			this.content.put(CAMPAIGN_CONTENT_HTML_BUTTON3, getButtonHtml(link));	
		}
	}
	
	public void addPrice(String price) {
		if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_PRICE1)){
			this.content.put(CAMPAIGN_CONTENT_HTML_PRICE1, price);	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_PRICE2)){
			this.content.put(CAMPAIGN_CONTENT_HTML_PRICE2, price);	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_PRICE3)){
			this.content.put(CAMPAIGN_CONTENT_HTML_PRICE3, price);	
		}
	}
	
	
	private String getImageHtml(String hotelName){
		return "<img src='"+ hotelName +"'  style='height:auto; max-width:240px;' class='productImage'/>";
	}
	private String getHotelHtml(String hotelName){
		return "<h2>"+ hotelName +"</h2>";
	}
	private String getButtonHtml(String link){
		return "<a href='"+link+"?email=edreams' target='_blank'><em>Ver Oferta</em></a>";
	}
	private String getSeeMoreLinkHtml(String link){
		return "<a style='color:#EB4102;' href='"+link+"?email=edreams' alt='ver más'>ver más ofertas</a>";
	}
	
	
}
