package itu.malta.drunkendroid.dal.entities;

import java.util.*;


public abstract class Trip {
	protected ArrayList<Reading> readings = new ArrayList<Reading>();
	private Calendar startDate = null;
	
	protected void setStartDate(Calendar d){
		if(this.startDate != null){
			if(this.startDate.after(d))
				startDate = d;
		}//If the startDate is earlier than the suggested one.
		else{
			startDate = d;
		}
	}
	
	public Calendar getStartDate(){
		return this.startDate;
	}
	
	public abstract void AddReading(Reading r);
	
	public abstract ArrayList<Reading> getTripReadings();
	
	public abstract Reading newReading();
	
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
}
