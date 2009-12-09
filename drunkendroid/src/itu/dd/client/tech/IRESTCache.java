package itu.dd.client.tech;

import java.util.ArrayList;

import itu.dd.client.domain.Event;
import itu.dd.client.domain.MoodEvent;
import itu.dd.client.domain.Trip;
import itu.dd.client.tech.exception.RESTFacadeException;

public interface IRESTCache {
	
	public ArrayList<MoodEvent> getReadingEvents(Long starTime, Long endTime, Double ulLatitude, Double ulLongitude, 
			Double lrLatitude, Double lrLongitude) throws RESTFacadeException;
	
	public void uploadTrip(Trip t);
	public void updateTrip(Trip t, ArrayList<Event> eventList);
	public void updateFilteredTrip(Trip t, ArrayList<Event> eventList) throws RESTFacadeException;

	public void closeCache();
}
