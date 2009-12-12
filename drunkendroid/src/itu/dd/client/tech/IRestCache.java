package itu.dd.client.tech;

import java.util.ArrayList;

import itu.dd.client.domain.Event;
import itu.dd.client.domain.MoodEvent;
import itu.dd.client.domain.Trip;
import itu.dd.client.tech.exception.CommunicationException;

public interface IRestCache {
	
	public ArrayList<MoodEvent> getReadingEvents(Long starTime, Long endTime, Double ulLatitude, Double ulLongitude, 
			Double lrLatitude, Double lrLongitude) throws CommunicationException;
	
	public void uploadTrip();
	public void updateTrip();
	public void updateFilteredTrip(Trip t, ArrayList<Event> eventList) throws CommunicationException;

	public void closeCache();
}
