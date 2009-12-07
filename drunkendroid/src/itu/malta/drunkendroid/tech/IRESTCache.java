package itu.malta.drunkendroid.tech;

import java.util.List;

import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.MoodEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.exception.RESTFacadeException;

public interface IRESTCache {
	
	public List<MoodEvent> getReadingEvents(Long starTime, Long endTime, Double ulLatitude, Double ulLongitude, 
			Double lrLatitude, Double lrLongitude) throws RESTFacadeException;
	
	public void uploadTrip(Trip t);
	public void updateTrip(Trip t, List<Event> eventList);
	public void updateFilteredTrip(Trip t, List<Event> eventList) throws RESTFacadeException;

	public void closeCache();
}
