package itu.dd.client.control;

import itu.dd.client.domain.Event;
import itu.dd.client.domain.Trip;

import java.util.ArrayList;

public interface ILocalDataFacade {
	public void addEvent(Trip t, Event e);
	public ArrayList<Event> updateEventsWithoutLocation(Trip t, Double latitude, Double longitude);

	/**
	 * 
	 * @param t
	 * @return The number of Events registered for t
	 */
	public int getEventCount(Trip t);
	public ArrayList<Trip> getAllTrips();
	/**
	 * 
	 * @param startTime
	 * @return a Trip full of associated Event
	 */
	public Trip getTrip(Long startTime);
	public ArrayList<Trip> getActiveTrips();
	public Trip startTrip(String name);
	public void deleteTrip(Long startTime);
	public void addRemoteIdToTrip(Trip t);
	public void closeTrip(Trip t);
	public void closeFacade();
}
