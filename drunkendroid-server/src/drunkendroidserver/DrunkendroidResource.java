package drunkendroidserver;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class DrunkendroidResource extends ServerResource {
		  
	    @Get  
	    public String represent() {  
	        return "hello, world";  
	    }

}
