package itu.malta.drunkendroid.domain;

import android.location.Location;

public class MoodEventEvent extends Event {
	public int mood;
	
	public MoodEventEvent(Long datetime, Double latitude, Double longitude, int mood) {
		super(datetime, latitude, longitude);
		this.mood = mood;
	}
	
	public MoodEventEvent(Location location, int mood) {
		super(location);
		this.mood = mood;
	}
}
