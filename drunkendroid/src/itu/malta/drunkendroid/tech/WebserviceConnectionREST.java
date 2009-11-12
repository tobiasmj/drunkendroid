package itu.malta.drunkendroid.tech;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class WebserviceConnectionREST implements IWebserviceConnection{
	private static final String BASE_URI = "http://192.168.0.13:8182/drunkendroid/";
	private static final String targetDomain = "192.168.0.13";
	
	public HttpResponse postTrip(String URI, String xmlContent){
		DefaultHttpClient httpClient = new DefaultHttpClient();
        
        HttpHost targetHost = new HttpHost(targetDomain, 8182, "http");
        // Using POST here
        HttpPost httpPost = new HttpPost(BASE_URI+URI);
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

}