package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.Trip;

import java.util.List;

public interface ILocalDataFacade {
	public void addEvent(Trip t, Event e);
	public List<Event> updateEventsWithoutLocation(Trip t, Double latitude, Double longitude);

	/**
	 * 
	 * @param t
	 * @return The number of Events registered for t
	 */
	public int getEventCount(Trip t);
	public List<Trip> getAllTrips();
	public Trip getTrip(Long startTime);
	public List<Trip> getActiveTrips();
	public Trip startTrip();
	public void addRemoteIdToTrip(Trip t);
	public void closeTrip(Trip t);
	public void closeFacade();
}
