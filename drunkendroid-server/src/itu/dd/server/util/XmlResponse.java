package itu.dd.server.util;

import java.io.IOException;

import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlResponse {

	/** 
	 * Generate an XML representation of an error response. 
	 *  
	 * @param errorMessage 
	 *            the error message. 
	 * @param errorCode 
	 *            the error code. 
	 */  
	public static DomRepresentation generateErrorRepresentation(String errorMessage,String errorCode) {  
		DomRepresentation result = null;  
		// This is an error  
		// Generate the output representation  
		try {
			result = new DomRepresentation(MediaType.TEXT_XML);  
			Document d = result.getDocument();  

			Element eltError = d.createElement("error");  
			d.appendChild(eltError);

			Element eltCode = d.createElement("code");  
			eltCode.appendChild(d.createTextNode(errorCode));  
			eltError.appendChild(eltCode);  

			Element eltMessage = d.createElement("message");  
			eltMessage.appendChild(d.createTextNode(errorMessage));  
			eltError.appendChild(eltMessage);  
		}catch (IOException ioe) {
			// ignore and a empty envelope will be returned.
		}
		return result;  
	} 
}
