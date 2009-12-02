package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.*;
import itu.malta.drunkendroid.tech.exception.RESTFacadeException;

import java.util.List;
import java.util.Set;

import android.content.Context;
import android.location.Location;

public class TripRepository {
	private Trip activeTrip = null;
	private IDataFacade data;

	public TripRepository(Context context) {
		data = new DataFacade(context);

		activeTrip = data.getActiveTrip();
	}

	/**
	 * Returns whether there is an active trip or not. Used to indicate user of
	 * existing active trip when launching new trip.
	 */
	public boolean hasActiveTrip() {
		if(activeTrip == null)
			return false;
		else 
			return true;
	}

	public void addEvent(Event e) {
		if (activeTrip == null) {
			activeTrip = data.startTrip();
		}
		data.addEvent(activeTrip, e);
	}

	public int getEventCount(Trip t) {
		return data.getEventCount(t);
	}

	public void closeRepository() {
		if (data != null) {
			data.closeFacade();
		}
	}

	public void endTrip() {
		if (activeTrip != null) {
			data.closeTrip(activeTrip);
		}
		activeTrip = null;
	}

	public List<Trip> getAllTrips() {
		return data.getAllTrips();
	}

	public List<ReadingEvent> getEvents(Long starTime, Long endTime, Double ulLatitude, Double ulLongitude,
			Double lrLatitude, Double lrLongitude) throws RESTFacadeException {
		return data.getReadingEvents(starTime, endTime, ulLatitude, ulLongitude, lrLatitude, lrLongitude);
	}

	public Trip getTrip(Long startTime) {
		return data.getTrip(startTime);
	}

	public void deleteTrip(Long startTime) {
		data.deleteTrip(startTime);
	}

	public void updateEventsWithoutLocation(Location location) {
		if (activeTrip == null) {
			data.getActiveTrip();
		}
		if (activeTrip != null) {
			data.updateEventsWithoutLocation(activeTrip,
					location.getLatitude(), location.getLongitude());
		}
	}

	public void uploadTrip(Long startTime, Set<Class<?>> uploadTypes) {
		Trip t = data.getTrip(startTime);
		t.events = Trip.filterEvents(t.events, uploadTypes);
		data.uploadTrip(t);
	}
}
