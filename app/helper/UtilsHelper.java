package helper;

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
}
