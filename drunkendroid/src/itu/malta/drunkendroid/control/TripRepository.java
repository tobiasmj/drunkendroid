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
	
	/*
	 * This is to solve an issue where a trip is updated with a remoteId in
	 * the cache, but the this repo' actie trip will not get updated, since
	 * the reference cannot be passed on, serialized into a message and 
	 * deserialized again.
	 */
	private void refreshActiveTrip(){
		if(activeTrip != null){
			if(activeTrip.remoteId == null){
				activeTrip = data.getActiveTrip();
			}
		}
		else{
			Trip potentialTrip = data.getActiveTrip();
			if(potentialTrip == null){
				activeTrip = data.startTrip();
			}
			else{
				activeTrip = potentialTrip;
			}
		}
	}

	public void addEvent(Event e) {
		refreshActiveTrip();
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

	public List<MoodEvent> getEvents(Long starTime, Long endTime, Double ulLatitude, Double ulLongitude,
			Double lrLatitude, Double lrLongitude) throws RESTFacadeException {
		return data.getReadingEvents(starTime, endTime, ulLatitude, ulLongitude, lrLatitude, lrLongitude);
	}

	public Trip getTrip(Long startTime) {
		return data.getTrip(startTime);
	}

	public void deleteTrip(Long startDate) {
		if(activeTrip != null && startDate.equals(activeTrip.startDate)){
			throw new IllegalArgumentException("You cannot delete an ongoing trip. Please stop it first.");
		}
		else{
			data.deleteTrip(startDate);
		}
	}

	public void updateEventsWithoutLocation(Location location) {
		refreshActiveTrip();
		data.updateEventsWithoutLocation(activeTrip,
			location.getLatitude(), location.getLongitude());
	}

	public void uploadTrip(Long startTime, Set<String> uploadTypes) throws RESTFacadeException {
		Trip t = data.getTrip(startTime);
		t.events = Trip.filterEvents(t.events, uploadTypes);
		if(t.events.size() > 0)
			data.updateFilteredTrip(t);
	}
}
