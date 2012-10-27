package ugot.recaptcha;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;
import play.mvc.Http.Request;
import play.mvc.Scope.Params;

/**
 * 
 * @author Olivier Refalo
 */
public class RecaptchaCheck extends AbstractAnnotationCheck<Recaptcha> {

    final static String mes = "validation.recaptcha";

    @Override
    public void configure(Recaptcha recaptcha)
    {
		setMessage(recaptcha.message());
    }

    @Override
    public boolean isSatisfied(Object validatedObject, Object value, OValContext context, Validator validator)
	    throws OValException
    {
		return RecaptchaValidator.checkAnswer(Request.current(), Params.current());
    }

}