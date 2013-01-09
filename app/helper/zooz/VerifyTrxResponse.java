package helper.zooz;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class VerifyTrxResponse {
	
	private int statusCode;
	private String errorMessage;

	public static VerifyTrxResponse create(String responseStr){
		
		VerifyTrxResponse verifyTrxResponse = new VerifyTrxResponse();
		
		if (responseStr != null) {
			String[] params = responseStr.split("&");
			for (String keyValue : params) {
				String[] keyValuePair = keyValue.split("=");
				if (keyValuePair[0].equals("statusCode")) {
					verifyTrxResponse.statusCode = Integer.parseInt(keyValuePair[1]);
				}
				else if (keyValuePair[0].equals("errorMessage")) {
					try {
						verifyTrxResponse.errorMessage = URLDecoder.decode(keyValuePair[1], "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// Do nothing
					}
				}
			}
		}
		
		return verifyTrxResponse;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
}
