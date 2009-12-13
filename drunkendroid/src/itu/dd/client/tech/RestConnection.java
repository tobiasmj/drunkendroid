package itu.dd.client.tech;

import itu.dd.client.tech.exception.CommunicationException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class RestConnection implements IWebserviceConnection{
	private static final String BASE_URI = "http://192.168.0.12:8182/drunkdroid/";
	private static final String targetDomain = "192.168.0.12";
	private static final String LOGTAG = "WebServiceConnectionREST";
	private DefaultHttpClient httpClient = new DefaultHttpClient();
	
	public RestConnection(){
		//Inspired by: http://groups.google.com/group/android-developers/browse_thread/thread/bf9474271ab80c64/9a200fd39171b5d2?lnk=raot&fwc=1
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000); //5 Seconds connection timeout
		HttpConnectionParams.setSoTimeout(params, 5000); //5 Seconds retrieve data timeout.
	}
	
	public HttpResponse post(String uri, String xmlContent) throws CommunicationException{
		HttpHost targetHost = new HttpHost(targetDomain, 8182, "http");
        // Using POST here
        HttpPost httpPost = new HttpPost(BASE_URI+uri);
        // Make sure the server knows what kind of a response we will accept
        httpPost.addHeader("Accept", "text/xml");
        // Also be sure to tell the server what kind of content we are sending
        httpPost.addHeader("Content-Type", "text/xml");
                
        try 
        {
            StringEntity entity = new StringEntity(xmlContent, "UTF-8");
            entity.setContentType("text/xml");
            httpPost.setEntity(entity);
            
            return httpClient.execute(targetHost, httpPost);
         }
        catch (Exception ex)
        {
        	Log.e(LOGTAG, "Caught an unknown exception");
            throw new CommunicationException(LOGTAG, "Caught an unknown Exception while connecting");
        }
	}

	public HttpResponse get(String uri) throws CommunicationException {
		HttpHost targetHost = new HttpHost(targetDomain, 8182, "http");
        // Using GET here
        HttpGet httpGet = new HttpGet(BASE_URI+uri);
        // Make sure the server knows what kind of a response we will accept
        httpGet.addHeader("Accept", "text/xml");
        // Also be sure to tell the server what kind of content we are sending
        httpGet.addHeader("Content-Type", "text/xml");
                
        try 
        {
            return httpClient.execute(targetHost, httpGet);
        }
        catch (Exception ex)
        {
        	Log.e(LOGTAG, "Caught an unknown exception, while doing a get call");
        	throw new CommunicationException(LOGTAG, "Caught an unknown Exception while doing a get call");
        }
	}
	
	public HttpResponse put(String uri, String xmlContent) throws CommunicationException{
		HttpHost targetHost = new HttpHost(targetDomain, 8182, "http");
        // Using PUT here
        HttpPut httpPut = new HttpPut(BASE_URI+uri);
        // Make sure the server knows what kind of a response we will accept
        httpPut.addHeader("Accept", "text/xml");
        // Also be sure to tell the server what kind of content we are sending
        httpPut.addHeader("Content-Type", "text/xml");
                
        try 
        {
            StringEntity entity = new StringEntity(xmlContent, "UTF-8");
            entity.setContentType("text/xml");
            httpPut.setEntity(entity);
            
            return httpClient.execute(targetHost, httpPut);
         }
        catch (Exception ex)
        {
        	Log.e(LOGTAG, "Caught an unknown exception, doing a put call");
            throw new CommunicationException(LOGTAG, "Caught an unknown Exception while doing a put call");
        }
	}

}