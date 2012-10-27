package ugot.recaptcha;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import net.tanesha.recaptcha.ReCaptchaFactory;
import play.Play;
import play.exceptions.TagInternalException;
import play.exceptions.TemplateExecutionException;
import play.i18n.Lang;
import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;

/**
 * How to use: #{ugot.recaptcha /}
 * 
 * @author Olivier Refalo
 */
@FastTags.Namespace("ugot")
public class RecaptchaTag extends FastTags {

	private static final String ERROR_MSG = "Please define valid ugot.recaptcha.publicKey and ugot.recaptcha.privateKey in application.conf";
	private static final String[] SUPPORTED_LANG = { "en", "nl", "fr", "de", "pt", "ru", "es", "tr" };

	public static void _recaptcha(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {

		String publickey = Play.configuration.getProperty("ugot.recaptcha.publicKey", "YOUR_RECAPTCHA_PUBLIC_KEY");
		String privatekey = Play.configuration.getProperty("ugot.recaptcha.privateKey",
				RecaptchaValidator.YOUR_RECAPTCHA_PRIVATE_KEY);

		if (publickey == null || privatekey == null || publickey.trim().length() == 0 || privatekey.trim().length() == 0
				|| "YOUR_RECAPTCHA_PUBLIC_KEY".equals(publickey)
				|| RecaptchaValidator.YOUR_RECAPTCHA_PRIVATE_KEY.equals(privatekey)) {

			throw new TemplateExecutionException(template.template, fromLine, ERROR_MSG, new TagInternalException(ERROR_MSG));
		} else {

			Properties props = new Properties();

			Object o = args.get("tabindex");
			if (o != null) {
				String tabindex = o.toString();
				if (tabindex != null)
					props.put("tabindex", tabindex);
			}

			String theme = (String) args.get("theme");
			if (theme != null)
				props.put("theme", theme);

			String lang = (String) args.get("lang");
			if (lang == null) {
				// figure what language the application use and see if recaptcha
				// supports it, defaults to en
				lang = Lang.get();
				if (lang == null || lang.trim().length() == 0 || !isLangSupported(lang))
					lang = "en";
			}
			props.put("lang", lang);
			
			//add support for captcha over https:
			Boolean https = (Boolean) args.get("https");
			
			if (https == null) {
				https = false;
			}
			
			String captcha;
			if (https) {
				captcha = ReCaptchaFactory.newSecureReCaptcha(publickey, privatekey, false).createRecaptchaHtml(null, props);
				
			} else {
				captcha = ReCaptchaFactory.newReCaptcha(publickey, privatekey, false).createRecaptchaHtml(null, props);
			}

			out.print(captcha);
		}

	}

	private static boolean isLangSupported(String lang) {
		for (int i = SUPPORTED_LANG.length; --i >= 0;)
			if (SUPPORTED_LANG[i].equals(lang))
				return true;
		return false;
	}

}