package ugot.recaptcha;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import play.Play;
import play.mvc.Http.Request;
import play.mvc.Scope.Params;

/**
 * 
 * @author Olivier Refalo
 */
public class RecaptchaValidator {

	public static final String YOUR_RECAPTCHA_PRIVATE_KEY = "YOUR_RECAPTCHA_PRIVATE_KEY";

	private RecaptchaValidator() {
	}

	public static boolean checkAnswer(Request request, Params params) {

		String remoteAddr = request.remoteAddress;

		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		String privatekey = Play.configuration.getProperty("ugot.recaptcha.privateKey", YOUR_RECAPTCHA_PRIVATE_KEY);
		if (privatekey == null || privatekey.trim().length() == 0 || YOUR_RECAPTCHA_PRIVATE_KEY.equals(privatekey))
			return false;

		reCaptcha.setPrivateKey(privatekey);
		String challenge = params.get("recaptcha_challenge_field");
		String uresponse = params.get("recaptcha_response_field");

		ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);
		return reCaptchaResponse.isValid();
	}
}
