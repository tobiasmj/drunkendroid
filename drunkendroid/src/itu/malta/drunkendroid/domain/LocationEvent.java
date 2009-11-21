package itu.malta.drunkendroid.domain;

import android.location.Location;

public class LocationEvent extends Event {
	
	public LocationEvent(Location location) {
		super(location);
	}
	
	public LocationEvent(Long datetime, Double latitude, Double longitude){
		super(datetime, latitude, longitude);
	}
}
