package itu.malta.drunkendroid.domain.entities;

import java.util.ArrayList;

public class NewTrip extends Trip {

	@Override
	public void AddReading(Reading r) {
		setStartDate(r.getDate());
		readings.add(r);
	}

	@Override
	public ArrayList<Reading> getTripReadings() {
		return readings;
	}

	@Override
	public NewReading newReading() {
		return new NewReading();
	}
	
	public class NewReading extends Reading{
	};

}
