package itu.malta.drunkendroid.control;

import java.util.List;

import itu.malta.drunkendroid.domain.*;

public interface IRemoteDataFacade {
	/**
	 * Query the server for events.
	 * @param starTime
	 * @param endTime
	 * @param latitude
	 * @param longitude
	 * @param distance
	 * @return A synthesized Trip which contains events returned by the query.
	 */
	public List<ReadingEvent> getReadingEvents(Long starTime, Long endTime, Double ulLatitude, Double ulLongitude, 
			Double lrLatitude, Double lrLongitude);
	
	/**
	 * Upload a trip to the server
	 * @param t
	 * @return the remoteID of the newly uploaded trip.
	 */
	public void uploadTrip(Trip t);
	public void updateTrip(Trip t, Event e);
}
