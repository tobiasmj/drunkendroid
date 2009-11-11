package itu.malta.drunkendroid.domain;

public class ReadingEvent extends Event {
	private int mood;
	
	public ReadingEvent(Long datetime, Double latitude, Double longitude, int mood) {
		super(datetime,latitude,longitude);
		setMood(mood);
	}
	
	public int getMood() {
		return mood;
	}

	public void setMood(int mood) {
		this.mood = mood;
	}
}
