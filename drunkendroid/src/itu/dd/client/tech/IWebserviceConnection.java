package itu.dd.client.tech;

import itu.dd.client.tech.exception.RESTFacadeException;

import org.apache.http.HttpResponse;

public interface IWebserviceConnection {
	public HttpResponse post(String uri, String xmlContent) throws RESTFacadeException;
	public HttpResponse get(String string) throws RESTFacadeException;
	public HttpResponse put(String uri, String xmlContent) throws RESTFacadeException;
}
