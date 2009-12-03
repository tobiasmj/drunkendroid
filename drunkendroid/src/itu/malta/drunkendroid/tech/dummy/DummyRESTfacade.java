package itu.malta.drunkendroid.tech.dummy;

import java.util.List;

import itu.malta.drunkendroid.control.IRemoteDataFacade;
import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.IRESTCache;
import itu.malta.drunkendroid.tech.exception.RESTFacadeException;

/**
 * 
 * @author ExxKA
 * This class is for doing manual tests!
 */
public class DummyRESTfacade implements IRESTCache {

	public List<ReadingEvent> getReadingEvents(Long starTime, Long endTime,
			Double ulLatitude, Double ulLongitude, Double lrLatitude,
			Double lrLongitude) throws RESTFacadeException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateTrip(Trip t, List<Event> eventList) {
		// TODO Auto-generated method stub
		
	}

	public void uploadTrip(Trip t) {
		// TODO Auto-generated method stub
		
	}

	public void closeCache() {
		// TODO Auto-generated method stub
		
	}

	public void updateFilteredTrip(Trip t, List<Event> eventList)
			throws RESTFacadeException {
		// TODO Auto-generated method stub
		
	}


}
