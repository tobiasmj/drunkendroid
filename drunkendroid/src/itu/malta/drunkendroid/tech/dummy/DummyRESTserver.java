package itu.malta.drunkendroid.tech.dummy;

import java.util.List;

import itu.malta.drunkendroid.control.IRemoteDataFacade;
import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;

/**
 * 
 * @author ExxKA
 * This class is for doing manual tests!
 */
public class DummyRESTserver implements IRemoteDataFacade {

	public List<ReadingEvent> getReadingEvents(Long starTime, Long endTime,
			Double latitude, Double longitude, Long distance) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateTrip(Trip t, Event e) {
		// TODO Auto-generated method stub

	}

	public void uploadTrip(Trip t) {
		// TODO Auto-generated method stub

	}

}
