package itu.malta.drunkendroid.domain;

import android.location.Location;

public class SMSEvent extends Event {
	private String phonebookName;
	private String phonenumber;
	private String textMessage;
	
	public SMSEvent(Location location, String phonebookName, String phonenumber, String textMessage) {
		super(location);
	}
	
	public String getPhonebookName() {
		return phonebookName;
	}
	
	public void setPhonebookName(String phonebookName) {
		this.phonebookName = phonebookName;
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
