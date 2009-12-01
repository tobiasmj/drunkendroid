package itu.malta.drunkendroid.domain;

import android.location.Location;

public class OutgoingSMSEvent extends SMSEvent {
	
	public OutgoingSMSEvent(Location location, String phonenumber, String textMessage) {
		super(location, phonenumber, textMessage);
	}
	
	public OutgoingSMSEvent(Long dateTime, Double latitude, Double longitude, String phonenumber, String textMessage) {
		super(dateTime, latitude, longitude, phonenumber, textMessage);
	}
}
