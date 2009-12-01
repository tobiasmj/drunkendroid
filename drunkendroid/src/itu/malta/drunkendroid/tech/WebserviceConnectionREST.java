package itu.malta.drunkendroid.tech;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.AndroidRuntimeException;

public class WebserviceConnectionREST implements IWebserviceConnection{
	private static final String BASE_URI = "http://192.168.0.12:8182/drunkendroid/";
	private static final String targetDomain = "192.168.0.12";
	
	public HttpResponse post(String uri, String xmlContent){
		DefaultHttpClient httpClient = new DefaultHttpClient();
        
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
                ex.printStackTrace();
                return null;
        }
	}

	public HttpResponse get(String uri) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
        
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
                ex.printStackTrace();
                return null;
        }
	}
	
	public HttpResponse put(String uri, String xmlContent){
		DefaultHttpClient httpClient = new DefaultHttpClient();
        
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
                ex.printStackTrace();
                return null;
        }
	}

}