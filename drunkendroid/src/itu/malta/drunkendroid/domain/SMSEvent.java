package itu.malta.drunkendroid.domain;

import android.location.Location;

public class SMSEvent extends Event {
	private String phonenumber;
	private String textMessage;
	
	public SMSEvent(Location location, String phonenumber, String textMessage) {
		super(location);
	}
	
	public SMSEvent(Long dateTime, Double latitude, Double longitude, String phonenumber, String textMessage) {
		super(dateTime, latitude, longitude);
	}
	
	public String getPhonenumber() {
		return phonenumber;
	}
	
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	
	public String getTextMessage() {
		return textMessage;
	}
	
	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}
}
