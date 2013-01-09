package helper.zooz;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class OpenTrxResponse {
	
	private int statusCode;
	private String errorMessage;
	private String sessionToken;

	private static String getValue(String[] keyValuePair) {
		String value = null;
		try {
			 value = URLDecoder.decode(keyValuePair[1], "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Do nothing
		}
		return value;
	}
	
	public static OpenTrxResponse create(String responseStr){
		
		OpenTrxResponse openTrxResponse = new OpenTrxResponse();
		
		if (responseStr != null) {
			String[] params = responseStr.split("&");
			for (String keyValue : params) {
				String[] keyValuePair = keyValue.split("=");
				if (keyValuePair[0].equals("statusCode")) {
					openTrxResponse.statusCode = Integer.parseInt(getValue(keyValuePair));
				}
				else if (keyValuePair[0].equals("errorMessage")) {
					openTrxResponse.errorMessage = getValue(keyValuePair);
				}
				else if (keyValuePair[0].equals("sessionToken")) {
					openTrxResponse.sessionToken = getValue(keyValuePair);
				}
			}
		}
		
		return openTrxResponse;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getSessionToken() {
		return sessionToken;
	}
}
