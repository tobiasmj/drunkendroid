package itu.malta.drunkendroid.domain.entities;

import java.util.Calendar;

public class Reading {
	private Calendar date;
	private Double latitude;
	private Double longitude;
	private Short mood;
	
	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Short getMood() {
		return mood;
	}

	public void setMood(Short mood) {
		if(mood > 255)
			this.mood = 255;
		this.mood = mood;
	}

	public Reading(){
		this.date = Calendar.getInstance();
	}
}