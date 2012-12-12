package helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import play.i18n.Lang;

public final class UtilsHelper {
	
	 /**
	  Truncate a String to the given length with no warnings
	  or error raised if it is bigger.

	  @param  value String to be truncated
	  @param  length  Maximum length of string

	  @return Returns value if value is null or value.length() is less or equal to than length, otherwise a String representing
	    value truncated to length.
	*/
	public static String truncate(String value, int length)
	{
	  if (value != null && value.length() > length)
	    value = value.substring(0, length);
	  return value;
	}
	
	public  static Integer roundPrice(Float price){
		BigDecimal priceRounded = new BigDecimal(price);
		priceRounded = priceRounded.setScale(0, RoundingMode.DOWN);
		return price.intValue();
	}
	
	public static Boolean langIsFrench(String lang){
		if (lang != null && (lang.equalsIgnoreCase("fr")||lang.equals(Lang.getLocale().FRENCH.getLanguage()))){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public static Boolean langIsSpanish(String lang){
		if (lang != null && (lang.equalsIgnoreCase("es") || lang.equalsIgnoreCase("es_ES"))){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
