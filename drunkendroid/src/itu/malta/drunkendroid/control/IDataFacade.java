package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.*;

import java.util.List;

public interface IDataFacade {
	public void addEvent(Trip t, Event e);
	public void updateEventsWithoutLocation(Trip t, Long latitude, Long Longitude);
	public List<Trip> getAllTrips();
	public Trip getTrip(Long startTime);
	public Trip startTrip();
	public void closeTrip(Trip t);
	
	public Trip getEvents(Long starTime, Long endTime, Long latitude, Long longitude, Long distance);
	
	public void closeFacade();
}
