package drunkendroidserver.Util;

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
            // Generate a DOM document representing the list of  
            // items.  
            Document d = result.getDocument();  
  
            Element eltError = d.createElement("error");  
            d.appendChild(eltError);
            
            Element eltCode = d.createElement("code");  
            eltCode.appendChild(d.createTextNode(errorCode));  
            eltError.appendChild(eltCode);  
  
            Element eltMessage = d.createElement("message");  
            eltMessage.appendChild(d.createTextNode(errorMessage));  
            eltError.appendChild(eltMessage);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
        return result;  
    } 
    /** 
     * Generate an XML representation of an response. 
     *  
     * @param message 
     *            the message. 
	 */
    public static DomRepresentation generateSuccessRepresentation(String message) {  
        DomRepresentation result = null;  
        // This is an sucess
        // Generate the output representation  
        try {  
            result = new DomRepresentation(MediaType.TEXT_XML);  
  
            Document d = result.getDocument();  
  
            Element eltSuccess = d.createElement("Success");  
            d.appendChild(eltSuccess);
            
            Element eltMessage = d.createElement("message");  
            eltMessage.appendChild(d.createTextNode(message));  
            eltSuccess.appendChild(eltMessage);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
        return result;  
    }  
}
