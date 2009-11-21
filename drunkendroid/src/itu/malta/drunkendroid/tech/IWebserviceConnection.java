package itu.malta.drunkendroid.tech;

import org.apache.http.HttpResponse;

public interface IWebserviceConnection {
	public HttpResponse post(String URI, String xmlContent);
	public HttpResponse get(String string);
}
