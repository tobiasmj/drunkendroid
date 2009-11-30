package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.*;

import java.util.List;

public interface IDataFacade {
	public void addEvent(Trip t, Event e);
	public int getEventCount(Trip t);
	public void updateEventsWithoutLocation(Trip t, Double latitude, Double longitude);
	public List<Trip> getAllTrips();
	public Trip getTrip(Long startTime);
	public Trip startTrip();
	public void deleteTrip();
	public Trip getActiveTrip();
	public void closeTrip(Trip t);
	
	public List<ReadingEvent> getReadingEvents(Long starTime, Long endTime, Double latitude, Double longitude, Long distance);
	
	public void closeFacade();
}
