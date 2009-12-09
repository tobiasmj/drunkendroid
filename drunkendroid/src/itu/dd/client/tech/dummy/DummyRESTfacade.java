package itu.dd.client.tech.dummy;

import java.util.ArrayList;
import java.util.List;

import itu.dd.client.domain.Event;
import itu.dd.client.domain.MoodEvent;
import itu.dd.client.domain.Trip;
import itu.dd.client.tech.IRESTCache;
import itu.dd.client.tech.exception.RESTFacadeException;

/**
 * 
 * @author ExxKA
 * This class is for doing manual tests!
 */
public class DummyRESTfacade implements IRESTCache {

	public ArrayList<MoodEvent> getReadingEvents(Long starTime, Long endTime,
			Double ulLatitude, Double ulLongitude, Double lrLatitude,
			Double lrLongitude) throws RESTFacadeException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateTrip(Trip t, List<Event> eventList) {
		// TODO Auto-generated method stub
		
	}

	public void closeCache() {
		// TODO Auto-generated method stub
		
	}

	public void updateFilteredTrip(Trip t, ArrayList<Event> eventList)
			throws RESTFacadeException {
		// TODO Auto-generated method stub
		
	}

	public void updateTrip(Trip t, ArrayList<Event> eventList) {
		// TODO Auto-generated method stub
		
	}

	public void uploadTrip(Trip t) {
		// TODO Auto-generated method stub
		
	}


}
