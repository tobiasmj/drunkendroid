package itu.dd.server;
import org.restlet.Component;
import org.restlet.data.Protocol;
public class Main {
	public static void main(String[] args) {  
	    try {  
	        // Create a new Component.  
	        Component component = new Component();  
	  
	        // Add a new HTTP server listening on port 8182.  
	        component.getServers().add(Protocol.HTTP, 8182);  
	  
	        // Arrach the drunkendroid application
	        component.getDefaultHost().attach(new DrunkDroidServerApplication());
	  
	        // Start the component.  
	        component.start();  
	    } catch (Exception e) {  
	        // Something is wrong.  
	        e.printStackTrace();  
	    }  
	}  
}
