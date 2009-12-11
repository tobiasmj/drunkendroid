package itu.dd.client.domain;

import android.location.Location;

public class SmsEvent extends Event {
	private String _phoneNumber;
	private String _textMessage;
	
	public SmsEvent(Location location, String phoneNumber, String textMessage) {
		super(location);
		_phoneNumber = phoneNumber;
		_textMessage = textMessage;
		
	}
	
	public SmsEvent(Long dateTime, Double latitude, Double longitude, String phoneNumber, String textMessage) {
		super(dateTime, latitude, longitude);
		_phoneNumber = phoneNumber;
		_textMessage = textMessage;
	}
	
	public String getPhonenumber() {
		return _phoneNumber;
	}
	
	public void setPhonenumber(String phoneNumber) {
		_phoneNumber = phoneNumber;
	}
	
	public String getTextMessage() {
		return _textMessage;
	}
	
	public void setTextMessage(String textMessage) {
		_textMessage = textMessage;
	}
}
