package drunkendroidserver;

import org.restlet.Application;  
import org.restlet.Restlet;  
import org.restlet.routing.Router;  
  
public class DrunkendroidserverApplication extends Application {
    /** 
     * Creates a root Restlet that will receive all incoming calls. 
     */  
    @Override  
    public synchronized Restlet createInboundRoot() {  
        // Create a router Restlet that routes each call to a  
        // new instance of drunkendroid
        Router router = new Router(getContext());  
  
        // Defines only one route  
        //router.attach("/drunkendroid", DrunkendroidResource.class);
        router.attach("/drunkendroid/trip/{IMEI}", TripResource.class);
        return router;  
    }  
  
}  