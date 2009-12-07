package itu.malta.drunkendroid.domain;

import android.location.Location;

public class MoodEvent extends Event {
	public int mood;
	
	public MoodEvent(Long datetime, Double latitude, Double longitude, int mood) {
		super(datetime, latitude, longitude);
		this.mood = mood;
	}
	
	public MoodEvent(Location location, int mood) {
		super(location);
		this.mood = mood;
	}
}
