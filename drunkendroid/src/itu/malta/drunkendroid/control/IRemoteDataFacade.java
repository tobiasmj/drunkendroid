package itu.malta.drunkendroid.control;

import java.util.List;

import itu.malta.drunkendroid.domain.*;
import itu.malta.drunkendroid.tech.exception.RESTFacadeException;

public interface IRemoteDataFacade {
	/**
	 * Query the server for events.
	 * @param starTime
	 * @param endTime
	 * @param latitude
	 * @param longitude
	 * @param distance
	 * @return A synthesized Trip which contains events returned by the query.
	 * @throws RESTFacadeException which needs to be shown to the user.
	 */
	public List<MoodEventEvent> getReadingEvents(Long starTime, Long endTime, Double ulLatitude, Double ulLongitude, 
			Double lrLatitude, Double lrLongitude) throws RESTFacadeException;
	
	/**
	 * Upload a trip to the server
	 * @param t
	 * @return the remoteID of the newly uploaded trip.
	 * 
	 * If an error occurs the trip is not updated
	 */
	public void uploadTrip(Trip t) throws RESTFacadeException;
	public void updateTrip(Trip t, List<Event> eventList) throws RESTFacadeException;
}
