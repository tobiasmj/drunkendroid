package itu.malta.drunkendroid.dal.entities;

import java.util.*;

public class Trip {
	private ArrayList<Reading> readings = new ArrayList<Reading>();
	private Calendar startDate = null;
	
	private void setStartDate(Calendar d){
		if(this.startDate == null)
			if(this.startDate.after(d)) //If the startDate is earlier than the suggested one.
				startDate = d;
	}
	private void AddReading(Reading r){
		setStartDate(r.getDate());
		readings.add(r);
	}
	
	protected Iterator<Reading> getReadingIterator(){
		return readings.iterator();
	}
	
	public final class Reading {
		private Calendar date;
		private Double latitude;
		private Double longitude;
		private Byte mood;
		
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

		public Byte getMood() {
			return mood;
		}

		public void setMood(Byte mood) {
			this.mood = mood;
		}

		public Reading(){
			this.date = Calendar.getInstance();
			Trip.this.AddReading(this);
		}
	}
}
