package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.*;
import java.util.List;
import android.content.Context;

public class TripRepository{
	private Trip activeTrip = null;
	private IDataFacade data;
	
	public TripRepository(Context context){
		data = new DataFacade(context);
		
		activeTrip = data.getActiveTrip();
		if(activeTrip == null){
			activeTrip = data.startTrip();
		}
	}

	public void addEvent(Event e) {
		data.addEvent(activeTrip, e);
	}

	public void closeRepository() {
		data.closeFacade();
	}

	public void endTrip() {
		data.closeTrip(activeTrip);
		activeTrip = null;
	}

	public List<Trip> getAllTrips() {
		return data.getAllTrips();
	}

	public Trip getEvents(Long starTime, Long endTime, Long latitude,
			Long longitude, Long distance) {
		return data.getEvents(starTime, endTime, latitude, longitude, distance);
	}

	public Trip getTrip(Long startTime) {
		return data.getTrip(startTime);
	}

	public void updateEventsWithoutLocation(Long latitude,
			Long Longitude) {
		data.updateEventsWithoutLocation(activeTrip, latitude, Longitude);
	}
}
