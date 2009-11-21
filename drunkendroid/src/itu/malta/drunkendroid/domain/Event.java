package itu.malta.drunkendroid.domain;

import java.util.Calendar;

import android.location.Location;

public class Event {
	public Long dateTime;
	public Double latitude;
	public Double longitude;
	
	public Event(Location location) {
		this.dateTime = Calendar.getInstance().getTimeInMillis();
		if(location.getExtras() != null && !location.getExtras().getBoolean("isOutdated",false))
		{
			this.latitude = location.getLatitude();
			this.longitude = location.getLongitude();
		}
	}
	
	public Event(Long datetime, Double latitude, Double longitude) {
		this.dateTime = datetime;
		this.latitude = latitude;
		this.longitude = longitude;
	}

}
