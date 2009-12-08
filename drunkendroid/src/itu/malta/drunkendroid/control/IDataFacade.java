package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.*;
import itu.malta.drunkendroid.tech.exception.RESTFacadeException;

import java.util.ArrayList;

public interface IDataFacade {
	public void addEvent(Trip t, Event e);
	public int getEventCount(Trip t);
	public void updateEventsWithoutLocation(Trip t, Double latitude, Double longitude);
	public ArrayList<Trip> getAllTrips();
	public Trip getTrip(Long startTime);
	public Trip startTrip(String name);
	public void deleteTrip(Long startTime);
	public Trip getActiveTrip();
	public void closeTrip(Trip t);
	public void updateFilteredTrip(Trip t) throws RESTFacadeException;
	
	public ArrayList<MoodEvent> getReadingEvents(Long starTime, Long endTime, Double ulLatitude, Double ulLongitude, 
			Double lrLatitude, Double lrLongitude) throws RESTFacadeException;
	
	public void closeFacade();
}
