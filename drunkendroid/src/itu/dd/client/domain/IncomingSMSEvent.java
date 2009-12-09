package itu.dd.client.domain;

import android.location.Location;

public class IncomingSMSEvent extends SMSEvent {
	
	public IncomingSMSEvent(Location location, String phonenumber, String textMessage) {
		super(location,phonenumber,textMessage);
	}
	
	public IncomingSMSEvent(Long dateTime, Double latitude, Double longitude, String phonenumber, String textMessage) {
		super(dateTime, latitude, longitude, phonenumber, textMessage);
	}
}
