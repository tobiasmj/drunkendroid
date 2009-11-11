package itu.malta.drunkendroid.domain;

import android.location.Location;

public class IncomingCallEvent extends CallEvent {
	
	public IncomingCallEvent(Location location, String phonebookName, String phonenumber, Long duration) {
		super(location,phonebookName,phonenumber,duration);
	}
}
