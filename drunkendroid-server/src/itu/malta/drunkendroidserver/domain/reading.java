package itu.malta.drunkendroidserver.domain;

public class reading extends event {
int mood;
	public reading(String type, double longitude, double latitude) {
		super(type, longitude, latitude);
	}
	public int getMood() {
		return mood;
	}
	public void setMood(int mood) {
		this.mood = mood;
	}
}
