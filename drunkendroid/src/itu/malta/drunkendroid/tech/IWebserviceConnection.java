package itu.malta.drunkendroid.tech;

import org.apache.http.HttpResponse;

public interface IWebserviceConnection {
	public HttpResponse postTrip(String URI, String xmlContent);
}
