package itu.malta.drunkendroid.domain;

import android.location.Location;

public class OutgoingSMSEvent extends SMSEvent {
	
	public OutgoingSMSEvent(Location location, String phonebookName, String phonenumber, String textMessage) {
		super(location,phonebookName,phonenumber,textMessage);
	}
}
