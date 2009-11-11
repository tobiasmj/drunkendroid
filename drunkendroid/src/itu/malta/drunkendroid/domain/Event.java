package itu.malta.drunkendroid.domain;

import java.util.Calendar;

import android.location.Location;

public class Event {
	private Location location;

	public Event(Location location) {
		setLocation(location);
		setDate(Calendar.getInstance().getTimeInMillis());
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	private void setDate(Long datetime) {
		this.location.setTime(datetime);
	}
}
