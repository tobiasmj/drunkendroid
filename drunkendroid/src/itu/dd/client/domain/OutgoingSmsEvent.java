package itu.dd.client.domain;

import android.location.Location;

public class OutgoingSmsEvent extends SmsEvent {
	
	public OutgoingSmsEvent(Location location, String phonenumber, String textMessage) {
		super(location, phonenumber, textMessage);
	}
	
	public OutgoingSmsEvent(Long dateTime, Double latitude, Double longitude, String phonenumber, String textMessage) {
		super(dateTime, latitude, longitude, phonenumber, textMessage);
	}
}
