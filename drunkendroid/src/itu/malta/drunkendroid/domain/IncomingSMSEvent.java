package itu.malta.drunkendroid.domain;

import android.location.Location;

public class IncomingSMSEvent extends SMSEvent {
	
	public IncomingSMSEvent(Location location, String phonebookName, String phonenumber, String textMessage) {
		super(location,phonebookName,phonenumber,textMessage);
	}
}
