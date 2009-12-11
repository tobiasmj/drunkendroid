package itu.dd.client.domain;

import android.location.Location;

public class IncomingSmsEvent extends SmsEvent {
	
	public IncomingSmsEvent(Location location, String phonenumber, String textMessage) {
		super(location,phonenumber,textMessage);
	}
	
	public IncomingSmsEvent(Long dateTime, Double latitude, Double longitude, String phonenumber, String textMessage) {
		super(dateTime, latitude, longitude, phonenumber, textMessage);
	}
}
