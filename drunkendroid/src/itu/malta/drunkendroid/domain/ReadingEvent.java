package itu.malta.drunkendroid.domain;

import android.location.Location;

public class ReadingEvent extends Event {
	private int mood;
	
	public ReadingEvent(Location location, int mood) {
		super(location);
		setMood(mood);
	}
	
	public int getMood() {
		return mood;
	}

	public void setMood(int mood) {
		this.mood = mood;
	}
}
