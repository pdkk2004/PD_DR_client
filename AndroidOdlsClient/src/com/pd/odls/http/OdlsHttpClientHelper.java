package com.pd.odls.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class OdlsHttpClientHelper{
	/** The time it takes for our client to timeout */
	public static final int HTTP_TIMEOUT = 10 * 1000; // milliseconds

	/** Single instance of our HttpClient */
	private static HttpClient httpClient;
	
	private static String TAG = OdlsHttpClientHelper.class.getName();
	
	/**
	 * Get singleton of HttpClient object.
	 *
	 * @return an HttpClient object with connection parameters set
	 */
	private static HttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
			final HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
			ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
		}
		return httpClient;
	}

	/**
	 * Performs an HTTP Post request to the specified url with the
	 * specified parameters, and return the string from the HttpResponse.
	 *
	 * @param url The web address to post the request to
	 * @param postParameters The parameters to send via the request
	 * @return The result string of the request
	 * @throws Exception
	 */
	public static String executeHttpPost(String url, ArrayList<NameValuePair> postParameters) throws Exception {
		BufferedReader in = null;
		try {
			HttpClient client = getHttpClient();
			HttpPost request = new HttpPost(url);
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
			request.setEntity(formEntity);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();

			String result = sb.toString();
			return result;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Performs an HTTP Post request to the specified url with the
	 * specified parameters, and return the HttpResponse directly. 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse getPostHttpResponse(String url, ArrayList<NameValuePair> params) throws Exception {
		HttpResponse response = null;
		
		try {
			HttpClient client = getHttpClient();
			HttpPost request = new HttpPost(url);
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params);
			request.setEntity(formEntity);
			response = client.execute(request);
		}
		catch(Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
		return response;
	}

	/**
	 * Performs an HTTP GET request to the specified url, and return the String
	 * read from HttpResponse.
	 * @param url The web address to post the request to
	 * @return The result of the request
	 * @throws Exception
	 */
	public static String executeHttpGet(String url) throws Exception {
		BufferedReader in = null;
		try {
			HttpClient client = getHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();

			String result = sb.toString();
			return result;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Performs an HTTP GET request to the specified url, and return HttpResponse directly.
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse getGetHttpResponse(String url) throws Exception{
		HttpResponse response = null;
		try {
			HttpClient httpClient = getHttpClient();
			HttpGet request = new HttpGet( );
			request.setURI(new URI(url));
			response = httpClient.execute(request);
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
		return response;
	}
}
