package helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import play.Logger;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;


public final class HttpClientHelper {

	public static String fetchContent(String pageURL, String pageCharacterEncoding, String userAgent, Map<String, String> cookies) {
		try {
			StringBuilder output = new StringBuilder();
			URL url = new URL(pageURL);
		
            URLFetchService urlFetchService = (URLFetchService) URLFetchServiceFactory.getURLFetchService();
            HTTPRequest httpRequest = new HTTPRequest(url);
            
            
            if (userAgent != null) {
             httpRequest.addHeader(new HTTPHeader("User-Agent", userAgent));
             Logger.info("Set user agent to: " + userAgent);
            }
            
            if (cookies != null) {
             for (String cookieName : cookies.keySet()) {
             final String cookieLine = cookieName + "=" + cookies.get(cookieName);
                 httpRequest.addHeader(new HTTPHeader("Cookie", cookieLine));
             Logger.info("Added cookie header: " + cookieLine);
             }
            }
         
            httpRequest.addHeader(new HTTPHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7"));
            httpRequest.addHeader(new HTTPHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
            
            HTTPResponse result = urlFetchService.fetch(httpRequest);
            if (result.getResponseCode() == HttpURLConnection.HTTP_OK) {
             return readResponseBody(pageCharacterEncoding, output, result);

            } else {
             Logger.warn("Error response code was: " + result.getResponseCode());
             Logger.warn(readResponseBody(pageCharacterEncoding, output, result));
            }
		
		} catch (MalformedURLException e) {
			Logger.error("MalformedURLException: " + e);
		} catch (IOException e) {
			Logger.error("IOException " + e);
		}
		return null;
	}


	private static String readResponseBody(String pageCharacterEncoding,
		StringBuilder output, HTTPResponse result)
		throws UnsupportedEncodingException, IOException {
		String line;
		
		InputStream inputStream = new ByteArrayInputStream(result.getContent());
		InputStreamReader input = new InputStreamReader(inputStream, pageCharacterEncoding);
		BufferedReader reader = new BufferedReader(input);
		while ((line = reader.readLine()) != null) {
		output.append(line);
		}
		reader.close();
		return output.toString();
	}

}