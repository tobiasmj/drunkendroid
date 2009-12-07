package itu.malta.drunkendroid.domain;

import android.location.Location;

public class MoodEvent extends Event {
	private int _mood;
	
	public int getMood() {
		return _mood;
	}

	public void setMood(int mood) {
		_mood = mood;
	}

	public MoodEvent(Long datetime, Double latitude, Double longitude, int mood) {
		super(datetime, latitude, longitude);
		this._mood = mood;
	}
	
	public MoodEvent(Location location, int mood) {
		super(location);
		this._mood = mood;
	}
}
