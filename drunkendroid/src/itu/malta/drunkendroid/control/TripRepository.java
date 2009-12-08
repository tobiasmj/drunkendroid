package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.*;
import itu.malta.drunkendroid.tech.exception.RESTFacadeException;
import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.location.Location;

public class TripRepository {
	private Trip _activeTrip = null;
	private IDataFacade _data;
	private String _name = "N/A";

	public TripRepository(Context context) {
		_data = new DataFacade(context);
		_activeTrip = _data.getActiveTrip();
	}
	
	public TripRepository(Context context, String tripName) {
		_data = new DataFacade(context);
		_activeTrip = _data.getActiveTrip();
		_name = tripName;		
	}

	/**
	 * Returns whether there is an active trip or not. Used to indicate user of
	 * existing active trip when launching new trip.
	 */
	public boolean hasActiveTrip() {
		if(_activeTrip == null)
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
		if(_activeTrip != null){
			if(_activeTrip.getRemoteId() == null){
				_activeTrip = _data.getActiveTrip();
			}
		}
		else{
			Trip potentialTrip = _data.getActiveTrip();
			if(potentialTrip == null){
				_activeTrip = _data.startTrip(_name);
			}
			else{
				_activeTrip = potentialTrip;
			}
		}
	}

	public void addEvent(Event e) {
		refreshActiveTrip();
		_data.addEvent(_activeTrip, e);
	}

	public int getEventCount(Trip t) {
		return _data.getEventCount(t);
	}

	public void closeRepository() {
		if (_data != null) {
			_data.closeFacade();
		}
	}

	public void endTrip() {
		if (_activeTrip != null) {
			_data.closeTrip(_activeTrip);
		}
		_activeTrip = null;
	}

	public ArrayList<Trip> getAllTrips() {
		return _data.getAllTrips();
	}

	public ArrayList<MoodEvent> getEvents(Long starTime, Long endTime, Double ulLatitude, Double ulLongitude,
			Double lrLatitude, Double lrLongitude) throws RESTFacadeException {
		return _data.getReadingEvents(starTime, endTime, ulLatitude, ulLongitude, lrLatitude, lrLongitude);
	}

	public Trip getTrip(Long startTime) {
		return _data.getTrip(startTime);
	}

	public void deleteTrip(Long startDate) {
		if(_activeTrip != null && startDate.equals(_activeTrip.getStartDate())){
			throw new IllegalArgumentException("You cannot delete an ongoing trip. Please stop it first.");
		}
		else{
			_data.deleteTrip(startDate);
		}
	}

	public void updateEventsWithoutLocation(Location location) {
		refreshActiveTrip();
		_data.updateEventsWithoutLocation(_activeTrip,
			location.getLatitude(), location.getLongitude());
	}

	public void uploadTrip(Long startTime, HashSet<String> uploadTypes) throws RESTFacadeException {
		Trip t = _data.getTrip(startTime);
		t.setEvents(Trip.filterEvents(t.getEvents(), uploadTypes));
		if(t.getEvents().size() > 0)
			_data.updateFilteredTrip(t);
	}
}
