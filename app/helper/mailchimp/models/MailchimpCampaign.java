package helper.mailchimp.models;

public class MailchimpCampaign {
	
	public String id;//Campaign Id (used for all other campaign functions)
	public int web_id; //The Campaign id used in our web app, allows you to create a link directly to it
	public String list_id ;//The List used for this campaign
	public int folder_id; //The Folder this campaign is in
	public int template_id;	//The Template this campaign uses
	public String content_type;	//How the campaign's content is put together - one of 'template', 'html', 'url'
	public String title;	//Title of the campaign
	public String type;	//The type of campaign this is (regular,plaintext,absplit,rss,inspection,auto)
	public String create_time;	//Creation time for the campaign
	public String send_time;	//Send time for the campaign - also the scheduled time for scheduled campaigns.
	public int	emails_sent;	//Number of emails email was sent to
	public String status;	//Status of the given campaign (save,paused,schedule,sending,sent)
	public String from_name; //	From name of the given campaign
	public String from_email; //	Reply-to email of the given campaign
	public String subject; //Subject of the given campaign
	public String to_name; //Custom "To:" email public String using merge variables
	public String archive_url; //Archive link for the given campaign
	public boolean inline_css; //Whether or not the campaign content's css was auto-inlined
	public String analytics; //Either "google" if enabled or "N" if disabled
	public String analytics_tag; //	The name/tag the campaign's links were tagged with if analytics were enabled.
	public boolean authenticate; //	Whether or not the campaign was authenticated
	public boolean ecomm360; //	Whether or not ecomm360 tracking was appended to links
	public boolean auto_tweet; //Whether or not the campaign was auto tweeted after sending
	public String auto_fb_post; //A comma delimited list of Facebook Profile/Page Ids the campaign was posted to after sending. If not used, blank.
	public boolean auto_footer; //Whether or not the auto_footer was manually turned on
	public boolean timewarp; //Whether or not the campaign used Timewarp
	public String timewarp_schedule; //	The time, in GMT, that the Timewarp campaign is being sent. For A/B Split campaigns, this is blank and is instead in their schedule_a and schedule_b in the type_opts array
	//public String[] tracking; //the various tracking options used
	public boolean html_clicks; //	whether or not tracking for html clicks was enabled.
	public boolean text_clicks; //	whether or not tracking for text clicks was enabled.
	public boolean opens; //whether or not opens tracking was enabled.
	public String segment_text; //	a public String marked-up with HTML explaining the segment used for the campaign in plain English
	//public String[]	segment_opts; //the segment used for the campaign - can be passed to campaignSegmentTest() or campaignCreate()
	//public String[]	type_opts; //the type-specific options for the campaign - can be passed to campaignCreate()
	
	public MailchimpCampaign() {
		super();
	}
}
