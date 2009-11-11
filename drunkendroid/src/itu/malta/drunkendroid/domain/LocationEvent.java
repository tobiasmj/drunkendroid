package itu.malta.drunkendroid.domain;

public class LocationEvent extends Event {
	
	public LocationEvent(Long datetime, Double latitude, Double longitude) {
		super(datetime,latitude,longitude);
	}
}
