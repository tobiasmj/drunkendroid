package itu.malta.drunkendroid.tech;

import org.apache.http.HttpResponse;

public interface IWebserviceConnection {
	public HttpResponse post(String uri, String xmlContent);
	public HttpResponse get(String string);
	public HttpResponse put(String uri, String xmlContent);
}
