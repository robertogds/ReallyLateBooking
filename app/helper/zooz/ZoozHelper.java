/*
 * The information in this document is proprietary
 * to TactusMobile and the TactusMobile Product Development.
 * It may not be used, reproduced or disclosed without
 * the written approval of the General Manager of
 * TactusMobile Product Development.
 * 
 * PRIVILEGED AND CONFIDENTIAL
 * TACTUS MOBILE PROPRIETARY INFORMATION
 * REGISTRY SENSITIVE INFORMATION
 *
 * Copyright (c) 2010 TactusMobile, Inc.  All rights reserved.
 */

package helper.zooz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import models.User;

import play.Logger;


/**
 * Sample servlet for mobile web checkout.
 * This class demonstrates the integration with the ZooZ server.
 * 
 */
public final class ZoozHelper {

	private static final long serialVersionUID = 3570259871387669173L;
	
	//Unique ID as registered on the ZooZ developer portal
	private static final String bundleId = "es.edreams.m.hotels";
		
	//TODO move to config in backoffice
	//The app key for this registered app
	private static final String appKey = "f52a398c-9bb7-4e6b-91ac-5cae047ec976";
	
	private static final String zoozServer = "https://sandbox.zooz.co";


/*		if (isSandbox)
			zoozServer = "https://sandbox.zooz.co";
		else
			zoozServer = "https://app.zooz.com";
*/

	public static String openTransaction(double amount, User user) throws IOException {

			// parameters from the client
			String currencyCode = "EUR";

			// prepare transaction data
			StringBuilder trxData = new StringBuilder();

			// mandatory parameters
			trxData.append("&cmd=openTrx");
			trxData.append("&amount=").append(amount);
			trxData.append("&currencyCode=").append(currencyCode);
			
			// additional parameters
			trxData.append("&user.firstName=" + user.firstName);
			trxData.append("&user.lastName="+ user.lastName);
			trxData.append("&user.phone.phoneNumber="+ user.phone);
			trxData.append("&user.email="+user.email);
			
			String responseStr = postToServer(trxData.toString());
			OpenTrxResponse openTrxResponse = OpenTrxResponse.create(responseStr);

			if (openTrxResponse.getStatusCode() != 0 && openTrxResponse.getErrorMessage() != null){
				Logger.error("Error at Zooz open Transaction %s", openTrxResponse.getErrorMessage());
				return null;
			}			
			else{
				String sessionToken = openTrxResponse.getSessionToken();
				return(sessionToken);
			}
			
	}
	
	public static void verifyTransaction(String transactionID, String transactionDisplayID, String sessionToken){
		try {
			// Verify transaction with the payment ID specified
			if (transactionID != null){
				String requestStr = "&cmd=verifyTrx&trxId=" + transactionID;
				String responseStr = postToServer(requestStr);
				VerifyTrxResponse verifyTrxResponse = VerifyTrxResponse.create(responseStr);
				if (verifyTrxResponse.getStatusCode() == 0){
					Logger.debug("Payment " + transactionID + " was confirmed. Display ID: " + transactionDisplayID);
					return;
				}
				else {
					Logger.debug("Error to confirm payment. " + verifyTrxResponse.getErrorMessage());
				}
			}
		} catch (Exception e) {
			Logger.debug("General error when processing request.", e);
		}
	}

	/**
	 * Post the request to the ZooZ server.
	 * @param data input data for the request.
	 * @return response from server.
	 * @throws IOException if failed to send request or read the response.
	 */
	private static String postToServer(String data) throws IOException {

		Writer writer = null;
		BufferedReader reader = null;
		StringBuilder resultSB = new StringBuilder();
		HttpURLConnection conn;
		try {	
			conn = (HttpURLConnection) new URL(zoozServer + "/mobile/SecuredWebServlet").openConnection();
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");

			// set bundleId and appKey for authentication to ZooZ secured server
			conn.setRequestProperty("ZooZUniqueID", bundleId);
			conn.setRequestProperty("ZooZAppKey", appKey);
			conn.setRequestProperty("ZooZResponseType", "NVP");

			// Send data
			writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(data);
			writer.flush();
			
			/*
			 *	Curl -H "ZooZUniqueID: com.zooz.mobileweb.sample" -H "ZooZAppKey: my-app-key" \
			 *  -H "ZooZResponseType: NVP" -d cmd=openTrx -d amount=0.99 \
			 *  -d currencyCode=USD https://sandbox.zooz.co/mobile/SecuredWebServlet
			 *  
			 *  &cmd=openTrx&amount=145.0&currencyCode=EUR&invoice.number=12358FF&invoice.items(0).name=Mushroom&invoice.items(0).price=0.35
			 *  &invoice.items(0).quantity=2&invoice.items(0).id=asd-34s2&invoice.additionalDetails=Hello world&user.firstName=John&user.lastName=Doe
			 *  &user.phone.countryCode=1&user.phone.phoneNumber=7188785775&user.email=test@zooz.com
			 * */
			Logger.debug("Connection OutputStream: %s", conn.getOutputStream().toString());
			
			// Get the response
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				resultSB.append(line);
			}

		} catch (IOException ex) {
			Logger.debug("Error to read/write from zooz server."+ ex.getMessage(), ex);
			throw ex;
		} finally {
			try {
				if (writer != null)
					writer.close();
				if (reader != null)
					reader.close();
			} catch (IOException ex) {
				Logger.debug("Error to close reader/writer.", ex);
			}
		}

		return resultSB.toString();
	}
}
