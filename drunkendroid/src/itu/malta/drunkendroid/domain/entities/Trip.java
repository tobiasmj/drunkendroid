package itu.malta.drunkendroid.domain.entities;

import java.util.*;


public class Trip {
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

	public void setDateInMilliSec(long timeInMillis) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);
		setStartDate(c);
	}
	
	public Calendar getStartDate(){
		return this.startDate;
	}
	
	public void AddReading(Reading r) {
		readings.add(r);
	}
	
	public ArrayList<Reading> getTripReadings() {
		return readings;
	}
}
