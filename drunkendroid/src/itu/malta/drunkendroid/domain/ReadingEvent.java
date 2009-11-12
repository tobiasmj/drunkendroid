package itu.malta.drunkendroid.domain;

import android.location.Location;

public class ReadingEvent extends Event {
	public int mood;
	
	public ReadingEvent(Long datetime, Double latitude, Double longitude, int mood) {
		super(datetime, latitude, longitude);
		this.mood = mood;
	}
	
	public ReadingEvent(Location location, int mood) {
		super(location);
		this.mood = mood;
	}
}
