package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.*;
import java.util.List;
import android.content.Context;
import android.location.Location;

public class TripRepository{
	private Trip activeTrip = null;
	private IDataFacade data;
	
	public TripRepository(Context context){
		data = new DataFacade(context);
		
		activeTrip = data.getActiveTrip();
	}

	public void addEvent(Event e) {
		if(activeTrip == null){
			activeTrip = data.startTrip();
		}
		data.addEvent(activeTrip, e);
	}
	
	public int getEventCount(Trip t){
		return data.getEventCount(t);
	}

	public void closeRepository() {
		if(data != null){
			data.closeFacade();
		}
	}

	public void endTrip() {
		data.closeTrip(activeTrip);
		activeTrip = null;
	}

	public List<Trip> getAllTrips() {
		return data.getAllTrips();
	}

	public List<ReadingEvent> getEvents(Long starTime, Long endTime, Double latitude,
			Double longitude, Long distance) {
		return data.getReadingEvents(starTime, endTime, latitude, longitude, distance);
	}

	public Trip getTrip(Long startTime) {
		return data.getTrip(startTime);
	}

	public void updateEventsWithoutLocation(Location location) {
		data.updateEventsWithoutLocation(activeTrip, location.getLatitude(), location.getLongitude());
	}
}
