package itu.dd.client.domain;

import android.location.Location;

public class CallEvent extends Event {
	private String _phoneNumber;
	
	public CallEvent(Location location, String phoneNumber) {
		super(location);
		_phoneNumber = phoneNumber;
	}
	
	public CallEvent(Long dateTime, Double latitude, Double longitude, String phoneNumber) {
		super(dateTime, latitude, longitude);
		_phoneNumber = phoneNumber;
	}
	
	public String getPhonenumber() {
		return _phoneNumber;
	}
	
	public void setPhonenumber(String phoneNumber) {
		_phoneNumber = phoneNumber;
	}
}
