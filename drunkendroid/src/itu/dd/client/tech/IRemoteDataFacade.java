package itu.dd.client.tech;

import java.util.ArrayList;

import itu.dd.client.domain.*;
import itu.dd.client.tech.exception.CommunicationException;

public interface IRemoteDataFacade {
	/**
	 * Query the server for events.
	 * @param starTime
	 * @param endTime
	 * @param latitude
	 * @param longitude
	 * @param distance
	 * @return A synthesized Trip which contains events returned by the query.
	 * @throws CommunicationException which needs to be shown to the user.
	 */
	public ArrayList<MoodEvent> getReadingEvents(Long starTime, Long endTime, Double ulLatitude, Double ulLongitude, 
			Double lrLatitude, Double lrLongitude) throws CommunicationException;
	
	/**
	 * Upload a trip to the server
	 * @param t
	 * @return the remoteID of the newly uploaded trip.
	 * 
	 * If an error occurs the trip is not updated
	 */
	public void uploadTrip(Trip t) throws CommunicationException;
	public void updateTrip(Trip t, ArrayList<Event> eventList) throws CommunicationException;
}
