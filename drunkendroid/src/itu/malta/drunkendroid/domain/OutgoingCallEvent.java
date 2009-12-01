package itu.malta.drunkendroid.domain;

import android.location.Location;

public class OutgoingCallEvent extends CallEvent {
	
	public OutgoingCallEvent(Location location, String phonenumber) {
		super(location, phonenumber);
	}
	
	public OutgoingCallEvent(Long dateTime, Double latitude, Double longitude, String phonenumber) {
		super(dateTime, latitude, longitude, phonenumber);
	}
}
