package itu.malta.drunkendroid.domain;

import android.location.Location;

public class IncomingCallEvent extends CallEvent {
	
	public IncomingCallEvent(Location location, String phonenumber) {
		super(location, phonenumber);
	}
	
	public IncomingCallEvent(Long dateTime, Double latitude, Double longitude, String phonenumber) {
		super(dateTime, latitude, longitude, phonenumber);
	}
}
