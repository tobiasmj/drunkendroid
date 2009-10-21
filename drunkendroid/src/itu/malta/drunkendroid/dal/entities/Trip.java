package itu.malta.drunkendroid.dal.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Trip {
	private ArrayList<Reading> readings = new ArrayList<Reading>();
	private Date startDate = null;
	
	private void setStartDate(Date d){
		if(this.startDate == null)
			startDate = d;
	}
	private void AddReading(Reading r){
		setStartDate(r.date);
		readings.add(r);
	}
	
	protected Iterator<Reading> getReadingIterator(){
		return readings.iterator();
	}
	
	public final class Reading {
		private Date date;
		private Double latitude;
		private Double longitude;
		private Double altitude;
		private Byte mood;
		
		public Reading(){
			Trip.this.AddReading(this);
		}
	}
}
