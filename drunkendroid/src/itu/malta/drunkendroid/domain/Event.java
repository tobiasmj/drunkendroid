package itu.malta.drunkendroid.domain;

import java.util.Calendar;

import android.location.Location;

public abstract class Event implements Comparable<Event> {
	public Long dateTime;
	public Double latitude;
	public Double longitude;
	public int id;
	
	public Event(Location location) {
		this.dateTime = Calendar.getInstance().getTimeInMillis();
		/* TODO: Needs to be handled correctly */
		if(location.getExtras() == null)
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
	
	public int compareTo(Event event) {
		if(this.dateTime < event.dateTime)
			return -1;
		else if(this.dateTime > event.dateTime)
			return 1;
		else
			return 0;
	}


}
