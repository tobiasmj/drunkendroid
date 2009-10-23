package itu.malta.drunkendroid.dal.entities;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class is for describing a Trip loaded from the local or serverside database.
 * @author ExxKA
 */
public class OldTrip extends Trip {
	
	@Override
	public void AddReading(Reading r) {
		readings.add(r);
	}

	@Override
	public ArrayList<Reading> getTripReadings() {
		return readings;
	}
	
	public OldReading newReading(){
		return new OldReading();
	}
	
	public class OldReading extends Trip.Reading{
		
	}

	public void setDateInMilliSec(long timeInMillis) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);
		setStartDate(c);
	}

}
