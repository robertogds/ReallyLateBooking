package helper.mailchimp.models;

import helper.DateHelper;
import helper.MailChimpHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import play.Logger;
import play.i18n.Lang;
import play.i18n.Messages;

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
	public static final String	CAMPAIGN_OPTION_SEGMENT_LANG_ES		= "ES";
	public static final String	CAMPAIGN_OPTION_SEGMENT_LANG_EN		= "EN";
	public static final String	CAMPAIGN_OPTION_SEGMENT_LANG_FR		= "FR";

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
	public static final String	CAMPAIGN_CONTENT_SEE_CORRECTLY		= "html_SEE_CORRECTLY";
	public static final String	CAMPAIGN_CONTENT_SEE_BROWSER		= "html_SEE_BROWSER";
	public static final String	CAMPAIGN_CONTENT_H1_HEADER			= "html_H1_HEADER";
	public static final String	CAMPAIGN_CONTENT_NOW1				= "html_NOW1";
	public static final String	CAMPAIGN_CONTENT_NOW2				= "html_NOW2";
	public static final String	CAMPAIGN_CONTENT_NOW3				= "html_NOW3";
	public static final String	CAMPAIGN_CONTENT_TESTIMONIALS		= "html_TESTIMONIALS";
	public static final String	CAMPAIGN_CONTENT_TESTIMONIAL_TITLE_1		= "html_TESTIMONIAL_TITLE_1";
	public static final String	CAMPAIGN_CONTENT_TESTIMONIAL_TITLE_2		= "html_TESTIMONIAL_TITLE_2";
	public static final String	CAMPAIGN_CONTENT_TESTIMONIAL_TEXT_1		= "html_TESTIMONIAL_TEXT_1";
	public static final String	CAMPAIGN_CONTENT_TESTIMONIAL_TEXT_2		= "html_TESTIMONIAL_TEXT_2";
	public static final String	CAMPAIGN_CONTENT_TESTIMONIAL_NAME_1		= "html_TESTIMONIAL_NAME_1";
	public static final String	CAMPAIGN_CONTENT_TESTIMONIAL_NAME_2		= "html_TESTIMONIAL_NAME_2";
	public static final String	CAMPAIGN_CONTENT_DOWNLOAD				= "html_DOWNLOAD";
	public static final String	CAMPAIGN_CONTENT_SOCIAL_TWITTER			= "html_TWITTER";
	public static final String	CAMPAIGN_CONTENT_SOCIAL_FACEBOOK		= "html_FACEBOOK";
	public static final String	CAMPAIGN_CONTENT_SOCIAL_SEND_FRIEND		= "html_SEND_FRIEND";
	public static final String	CAMPAIGN_CONTENT_UNSUBSCRIBE			= "html_UNSUBSCRIBE";
	
	public String apikey;
	public String type;
	public Map<String, Object> options;
	public Map<String, Object> content;
	public SegmentOptions segment_opts;


	public CreateCampaignRequest(String listId, String subject, String fromEmail, String fromName,String templateId) throws UnsupportedEncodingException {
		super();
        this.apikey = MailChimpHelper.APIKEY;
        this.type = CAMPAIGN_TYPE_REGULAR;
        this.options = new HashMap<String, Object>();
        this.options.put(CAMPAIGN_OPTION_LIST_ID, listId);
        this.options.put(CAMPAIGN_OPTION_SUBJECT, URLEncoder.encode(subject, "UTF8"));
        this.options.put(CAMPAIGN_OPTION_FROM_EMAIL, fromEmail);
        this.options.put(CAMPAIGN_OPTION_FROM_NAME, fromName);
        this.options.put(CAMPAIGN_OPTION_TO_NAME, "*|FNAME|*");
        this.options.put(CAMPAIGN_OPTION_TEMPLATE_ID, templateId);
        this.options.put(CAMPAIGN_OPTION_GENERATE_TEXT, "true");
        
        this.content = new HashMap<String, Object>();
        this.content.put(CAMPAIGN_CONTENT_SEE_CORRECTLY, URLEncoder.encode(Messages.get("mailchimp.template.preheader.see_correctly"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_SEE_BROWSER, URLEncoder.encode(Messages.get("mailchimp.template.preheader.see_browser"), "UTF8") );
        this.content.put(CAMPAIGN_CONTENT_H1_HEADER,URLEncoder.encode(Messages.get("mailchimp.template.h1_header"), "UTF8") );
        this.content.put(CAMPAIGN_CONTENT_NOW1, URLEncoder.encode(Messages.get("mailchimp.template.now"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_NOW2, URLEncoder.encode(Messages.get("mailchimp.template.now"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_NOW3, URLEncoder.encode(Messages.get("mailchimp.template.now"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_TESTIMONIALS, URLEncoder.encode(Messages.get("mailchimp.template.testimonials"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_TESTIMONIAL_TITLE_1, URLEncoder.encode(Messages.get("mailchimp.template.testimonials.testimonial_title_1"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_TESTIMONIAL_TEXT_1, URLEncoder.encode(Messages.get("mailchimp.template.testimonials.testimonial_text_1"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_TESTIMONIAL_NAME_1, URLEncoder.encode(Messages.get("mailchimp.template.testimonials.testimonial_name_1"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_TESTIMONIAL_TITLE_2, URLEncoder.encode(Messages.get("mailchimp.template.testimonials.testimonial_title_2"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_TESTIMONIAL_TEXT_2, URLEncoder.encode(Messages.get("mailchimp.template.testimonials.testimonial_text_2"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_TESTIMONIAL_NAME_2, URLEncoder.encode(Messages.get("mailchimp.template.testimonials.testimonial_name_2"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_DOWNLOAD, URLEncoder.encode(Messages.get("mailchimp.template.download"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_SOCIAL_FACEBOOK, URLEncoder.encode(Messages.get("mailchimp.template.social.facebook"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_SOCIAL_TWITTER, URLEncoder.encode(Messages.get("mailchimp.template.social.twitter"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_SOCIAL_SEND_FRIEND, URLEncoder.encode(Messages.get("mailchimp.template.social.send_friend"), "UTF8"));
        this.content.put(CAMPAIGN_CONTENT_UNSUBSCRIBE, URLEncoder.encode(Messages.get("mailchimp.template.unsubscribe"), "UTF8"));
        this.segment_opts = new SegmentOptions();
        this.segment_opts.addDateCondition(SegmentOptions.FIELD_DEPT_DATE, SegmentOptions.OP_EQ, DateHelper.getTodayDate());
	}
	
	
	public void setCitySegment(String cityCode){
		this.segment_opts.addCondition(SegmentOptions.FIELD_CITY, SegmentOptions.OP_EQ, cityCode);
	}
    
	public void setLangSegment(String lang){
		this.segment_opts.addCondition(SegmentOptions.FIELD_WEBSITE, SegmentOptions.OP_EQ, lang);
	}
	
	public Integer testSegmentOptions(){
		String response = MailChimpHelper.sendSegmentTest((String)this.options.get(CAMPAIGN_OPTION_LIST_ID), this.segment_opts);
		Logger.debug("## Segment test respose: %s", response);
		Integer number = Integer.valueOf(response);
		return number;
	}
	
	public void setContentText(String contentText) throws UnsupportedEncodingException{
        this.content.put(CAMPAIGN_CONTENT_TEXT, URLEncoder.encode(contentText, "UTF8"));
	}
	
	public void setHeader(String header) throws UnsupportedEncodingException{
		this.content.put(CAMPAIGN_CONTENT_HTML_HEADER, URLEncoder.encode(Messages.get("mailchimp.template.header", header), "UTF8"));
	}
	
	public void setMain(String main) throws UnsupportedEncodingException{
        this.content.put(CAMPAIGN_CONTENT_HTML_MAIN, URLEncoder.encode(main, "UTF8"));
	}
	public void setCity(String city) throws UnsupportedEncodingException{
        this.content.put(CAMPAIGN_CONTENT_HTML_CITY, URLEncoder.encode(Messages.get("mailchimp.template.preheader.city", city), "UTF8"));
        this.setAnalyticsTracking(city);
	}
	public void setAnalyticsTracking(String city) {
		Map<String, Object> analytics = new HashMap<String, Object>();
		SimpleDateFormat sdf=new SimpleDateFormat("dd_MM_yyyy");
		String today = sdf.format(DateHelper.getTodayDate());
        analytics.put(CAMPAIGN_OPTION_ANALYTICS_GOOGLE, city + "_" + Lang.get() + "_" + today);
        this.options.put(CAMPAIGN_OPTION_ANALYTICS, analytics);
	}
	
	public void setSideColumn(String sideColumn) throws UnsupportedEncodingException{
        this.content.put(CAMPAIGN_CONTENT_HTML_SIDECOLUMN, URLEncoder.encode(sideColumn, "UTF8"));
	}
	
	public void setFooter( String footer) throws UnsupportedEncodingException{
        this.content.put(CAMPAIGN_CONTENT_HTML_FOOTER, URLEncoder.encode(footer, "UTF8"));
	}
	public void setSeeMoreLink( String link) throws UnsupportedEncodingException{
        this.content.put(CAMPAIGN_CONTENT_HTML_SEEMORE, URLEncoder.encode(getSeeMoreLinkHtml(link), "UTF8"));
	}
	
	public void setHtmlContent( String htmlContent) throws UnsupportedEncodingException{
		this.content.put(CAMPAIGN_CONTENT_HTML, URLEncoder.encode(htmlContent, "UTF8"));	
	}
	
	public void setHotel1( String hotel) throws UnsupportedEncodingException{
		this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL1, URLEncoder.encode(getHotelHtml(hotel), "UTF8"));	
	}
	
	public void setImage1( String image){
		this.content.put(CAMPAIGN_CONTENT_HTML_IMAGE1, getImageHtml(image));	
	}
	
	public void setPrice1( String price){
		this.content.put(CAMPAIGN_CONTENT_HTML_PRICE1, price);	
	}
	
	public void setHotel2( String hotel) throws UnsupportedEncodingException{
		this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL2, URLEncoder.encode(getHotelHtml(hotel), "UTF8"));	
	}
	
	public void setImage2( String image){
		this.content.put(CAMPAIGN_CONTENT_HTML_IMAGE2, getImageHtml(image));	
	}
	
	public void setPrice2( String price){
		this.content.put(CAMPAIGN_CONTENT_HTML_PRICE2, price);	
	}
	
	public void setHotel3( String hotel) throws UnsupportedEncodingException{
		this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL3, URLEncoder.encode(getHotelHtml(hotel), "UTF8"));	
	}
	
	public void setImage3( String image){
		this.content.put(CAMPAIGN_CONTENT_HTML_IMAGE3, getImageHtml(image));	
	}
	
	public void setPrice3( String price){
		this.content.put(CAMPAIGN_CONTENT_HTML_PRICE3, price);	
	}

	public void addHotel(String hotelName) throws UnsupportedEncodingException {
		if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_HOTEL1)){
			this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL1, URLEncoder.encode(getHotelHtml(hotelName), "UTF8"));	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_HOTEL2)){
			this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL2, URLEncoder.encode(getHotelHtml(hotelName), "UTF8"));	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_HOTEL3)){
			this.content.put(CAMPAIGN_CONTENT_HTML_HOTEL3, URLEncoder.encode(getHotelHtml(hotelName), "UTF8"));	
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
	
	public void addButton(String link) throws UnsupportedEncodingException {
		if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_BUTTON1)){
			this.content.put(CAMPAIGN_CONTENT_HTML_BUTTON1, URLEncoder.encode(getButtonHtml(link), "UTF8"));	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_BUTTON2)){
			this.content.put(CAMPAIGN_CONTENT_HTML_BUTTON2, URLEncoder.encode(getButtonHtml(link), "UTF8"));	
		}
		else if (!this.content.containsKey(CAMPAIGN_CONTENT_HTML_BUTTON3)){
			this.content.put(CAMPAIGN_CONTENT_HTML_BUTTON3, URLEncoder.encode(getButtonHtml(link), "UTF8"));	
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
		return "<a href='"+link+"?email=edreams' target='_blank'><em>"+ Messages.get("mails.seedeal.link") +"</em></a>";
	}
	private String getSeeMoreLinkHtml(String link){
		return "<a style='color:#EB4102;' href='"+link+"?email=edreams' alt='see more'>"+ Messages.get("mails.seemore.link")+"</a>";
	}
	
	
}
