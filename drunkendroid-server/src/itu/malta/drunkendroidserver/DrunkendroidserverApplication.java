package itu.malta.drunkendroidserver;

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
  

        // upload a trip
        router.attach("/drunkendroid/trip/{IMEI}", TripResource.class);
        
        //get a trip, and update a trip
        router.attach("/drunkendroid/trip/{IMEI}/{TripId}", TripResource.class);
                
        // add a event to a trip
        router.attach("/drunkendroid/trip/event/{IMEI}/{TripId}", EventResource.class);
        
        // get moodmap from server
        router.attach("/drunkendroid/moodmapOld/{StartTimeStamp}/{EndTimeStamp}/{Latitude}/{Longitude}/{Height}/{Width}", MoodMapResourceOld.class);
        router.attach("/drunkendroid/moodmap/{StartTimeStamp}/{EndTimeStamp}/{ULLatitude}/{ULLongitude}/{LRLatitude}/{LRLongitude}", MoodMapResource.class);
        return router;  
    }  
  
}  