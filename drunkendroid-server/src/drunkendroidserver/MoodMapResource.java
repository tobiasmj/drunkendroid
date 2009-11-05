package drunkendroidserver;


import java.io.IOException;
import java.sql.SQLException;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import drunkendroidserver.Util.*;

public class MoodMapResource extends ServerResource {
	long startTimeStamp, endTimeStamp;
	double latitude, longitude, ULlatitude, ULlongitude, LRlatitude, LRlongitude;
	int height, width;
		
	@Get  
    public Representation represent() {  
		
		//Consider not alowing moodmaps at sizes bigger then...
		
		Representation result = null;
		
		// get the values posted to the server
		startTimeStamp = Long.parseLong(getRequest().getAttributes().get("StartTimeStamp").toString());
		endTimeStamp = Long.parseLong(getRequest().getAttributes().get("EndTimeStamp").toString());
		
		latitude = Double.parseDouble(getRequest().getAttributes().get("Latitude").toString());
		longitude = Double.parseDouble(getRequest().getAttributes().get("Longitude").toString());
		
		height = Integer.parseInt(getRequest().getAttributes().get("Height").toString());
		width = Integer.parseInt(getRequest().getAttributes().get("Width").toString());
		
		//Calculate corners of the moodmap
		ULlatitude = latitude + HelperFunctions.change_in_latitude(-width/1000);
		ULlongitude = longitude + HelperFunctions.change_in_longitude(ULlatitude, -height/1000);
		
		LRlatitude = latitude + HelperFunctions.change_in_latitude(width/1000);
		LRlongitude = longitude + HelperFunctions.change_in_longitude(LRlatitude, height/1000);
		
		// create the dbCommand
		MoodMap moodMap = new MoodMap(startTimeStamp, endTimeStamp, ULlatitude, ULlongitude, LRlatitude, LRlongitude);
		try {
			moodMap.execute();
			result = moodMap.getMoodMap();
		} catch (SQLException se) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			result = XmlResponse.generateErrorRepresentation("Error getting data from database", "5");
		} catch (IOException ioe) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			result = XmlResponse.generateErrorRepresentation("Error creating XML response", "6");			
		}
		
		return result;
	}
}

