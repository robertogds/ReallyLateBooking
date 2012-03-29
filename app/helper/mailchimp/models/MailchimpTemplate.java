package helper.mailchimp.models;

public class MailchimpTemplate {

	public String id;
	public String name;
	public String layout; //Layout of the template - "basic", "left_column", "right_column", or "postcard"
	public String preview_image; //If we've generated it, the url of the preview image for the template. We do out best to keep these up to date, but Preview image urls are not guaranteed to be available
	public String date_created; //The date/time the template was created
	public boolean edit_source; //Whether or not you are able to edit the source of a template.
	
	public MailchimpTemplate() {
		super();
	}
}
