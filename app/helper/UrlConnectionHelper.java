package helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

import play.Logger;
import play.libs.XML;
import com.google.appengine.api.urlfetch.FetchOptions.Builder.*;
import com.google.appengine.repackaged.org.apache.http.HttpRequest;

public final class UrlConnectionHelper {
	private static final String ENCODING =  "ISO-8859-1";
	private static final int CONNECT_TIMEOUT = 15000;
	private static final int READ_TIMEOUT = 15000;
	public static final String CONTENT_JSON = "application/json";
	public static final String CONTENT_XML = "text/xml";
	
	public static Document prepareRequest(String url){
		try {
			URL parsedUrl = new URL(url);
			URLConnection urlConnection = parsedUrl.openConnection(); 
			urlConnection.setConnectTimeout(CONNECT_TIMEOUT); 
			urlConnection.setReadTimeout(READ_TIMEOUT); 
			
			String xml = convertStreamToString(urlConnection.getInputStream());
			xml = StringUtils.trim(xml);
			Logger.debug("response code: " + xml);
			
    		Document doc = XML.getDocument(xml);
    		return doc;

        } catch (Exception e) {
           Logger.error("Error receiving xml from hotusa: " + e);
        } 
		return null;
	}
	
	public static String prepareRequest(String url, String params, String contenType){
		 try {
			URL parsedUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) parsedUrl.openConnection();
		    connection.setDoOutput(true);
		    connection.setRequestMethod("POST");
		    connection.addRequestProperty("Content-Type", contenType);
		    connection.connect();  
		    
		    if (params != null){
		        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		        writer.write(params);
		        writer.flush();
		        writer.close();
		    }
		    
		    Logger.debug("Connection string: %s", connection.getOutputStream().toString());
	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            // Note: Should check the content-encoding.
	        	String response = UrlConnectionHelper.convertStreamToString(connection.getInputStream());
				response = StringUtils.trim(response);
				Logger.debug("response code: " + response);
	    		return response;
	        } else {
	            throw new Exception();
	        }
	    } catch (Exception e) {
	    	 Logger.error("Error receiving response: " + e);
	    }
		return null;
		
	}
	
	public static String doSecureGetIgnoreCertificate(String url){
		try {
			URL parsedUrl = new URL(url);
			
			FetchOptions options = FetchOptions.Builder.doNotValidateCertificate().setDeadline(60.0);
			HTTPRequest request =  new HTTPRequest(parsedUrl, HTTPMethod.GET, options);
			URLFetchService service = URLFetchServiceFactory.getURLFetchService();
			try {
				HTTPResponse response = service.fetch(request);
				if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
		        	String responseAsString =  new String(response.getContent());
		        	Logger.debug("responseAsString: " + responseAsString);
		    		return responseAsString;
		        } 
				
			} catch (IOException e) {
				Logger.error("IOException: " + e);
			}
			
		} catch (MalformedURLException e) {
			Logger.error("MalformedURLException: " + e);
		}
		
		return null;
	}
	
	public static String doGet(String url){
		 try {
			Logger.debug("Connection url GET: %s", url);
			URL parsedUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) parsedUrl.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
		    connection.setRequestMethod("GET");
		    connection.connect();  
		    
	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	        	String response = convertStreamToString(connection.getInputStream());
	        	Logger.debug("response: " + response);
	    		return response;
	        } 
	        else if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM) {
	        	String location = connection.getHeaderField("location");
	        	connection.disconnect();
	        	URL redirectUrl = new URL(location);
	        	connection = (HttpURLConnection) redirectUrl.openConnection();
	        	connection.connect();  
	        	String response = convertStreamToString(connection.getInputStream());
	        	Logger.debug(" redirect response code: %s", connection.getResponseCode());
	        	Logger.debug("response: " + response );
	    		return response;
	        }
	        else{
	        	Logger.error("Error receiving response, responseCode: %s", connection.getResponseCode());
	            throw new Exception();
	        }
	    } catch (Exception e) {
	    	 Logger.error("Error receiving response: " + e);
	    }
		return null;
		
	}
	
	public static String convertStreamToString(InputStream is)
	    throws IOException {
		/*
		 * To convert the InputStream to String we use the
		 * Reader.read(char[] buffer) method. We iterate until the
		 * Reader return -1 which means there's no more data to
		 * read. We use the StringWriter class to produce the string.
		 */
		if (is != null) {
		    Writer writer = new StringWriter();
		
		    char[] buffer = new char[1024];
		    try {
		        Reader reader = new BufferedReader(
		                new InputStreamReader(is, ENCODING));
		        int n;
		        while ((n = reader.read(buffer)) != -1) {
		            writer.write(buffer, 0, n);
		        }
		    } finally {
		        is.close();
		    }
		    return writer.toString();
		} else {        
		    return "";
		}
	}
}
