package itu.malta.drunkendroid.domain;


import android.location.Location;

public class Event {
	public Long dateTime;
	public Double latitude;
	public Double longitude;
	
	public Event(Location location) {
		this.dateTime = location.getTime();
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}
	
	public Event(Long datetime, Double latitude, Double longitude) {
		this.dateTime = datetime;
		this.latitude = latitude;
		this.longitude = longitude;
	}

}
