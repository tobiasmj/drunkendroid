package itu.malta.drunkendroid.domain;

import android.location.Location;

public class OutgoingCallEvent extends CallEvent {
	
	public OutgoingCallEvent(Location location, String phonebookName, String phonenumber, Long duration) {
		super(location,phonebookName,phonenumber,duration);
	}
}
