package itu.dd.client.tech;

import itu.dd.client.tech.exception.CommunicationException;

import org.apache.http.HttpResponse;

public interface IWebserviceConnection {
	public HttpResponse post(String uri, String xmlContent) throws CommunicationException;
	public HttpResponse get(String string) throws CommunicationException;
	public HttpResponse put(String uri, String xmlContent) throws CommunicationException;
}
