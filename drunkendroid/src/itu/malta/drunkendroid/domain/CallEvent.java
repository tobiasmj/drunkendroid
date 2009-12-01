package itu.malta.drunkendroid.domain;

import android.location.Location;

public class CallEvent extends Event {
	private String phonenumber;
	
	public CallEvent(Location location, String phonenumber) {
		super(location);
	}
	
	public CallEvent(Long dateTime, Double latitude, Double longitude, String phonenumber) {
		super(dateTime, latitude, longitude);
	}
	
	public String getPhonenumber() {
		return phonenumber;
	}
	
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
}
