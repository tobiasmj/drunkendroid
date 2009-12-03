package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.IRESTCache;
import itu.malta.drunkendroid.tech.LocalDataFacadeForSQLite;
import itu.malta.drunkendroid.tech.RESTCache;
import itu.malta.drunkendroid.tech.exception.RESTFacadeException;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;

public class DataFacade implements IDataFacade {
	private static final String _LOGTAG = "Drunkendroid DataFacade";
	private IRESTCache _remote;
	private ILocalDataFacade _local;
	
	//Don't call the default constructor!
	@SuppressWarnings("unused")
	private DataFacade(){};
	
	public DataFacade(Context context){
		_remote = new RESTCache(context);
		//_remote = new DummyRESTserver();
		_local = new LocalDataFacadeForSQLite(context);
	}
	
	/**
	 * Add an event to the local and foreign database.
	 */
	public void addEvent(Trip t, Event e) {
		//The event is persisted.
		_local.addEvent(t, e);
		
		//The server is being updated
		if(e.latitude != null && e.longitude != null){
			//also upload to the server, if the location is known.
			if(t.remoteId == null){
				_remote.uploadTrip(t);
			}
			else{
				ArrayList<Event> eventList = new ArrayList<Event>();
				eventList.add(e);
				_remote.updateTrip(t, eventList);
			}	
		}	
	}
	
	/**
	 * Begin a new empty trip.
	 * @return a Trip with no events, but a unique localId.
	 */
	public Trip startTrip(){
		return _local.startTrip();
	}
	
	/**
	 * Close the trip in the database.
	 */
	public void closeTrip(Trip t) {
		_local.closeTrip(t);
		//We could also consider closing all active trips.
	}

	/**
	 * @return All trips belonging to the user.
	 */
	public List<Trip> getAllTrips() {
		return _local.getAllTrips();
	}

	/**
	 * Get aggregated mood data
	 * @param startTime the beginning of the aggregation
	 * @param endTime the ending of the aggregation
	 * @param latiude latitude of the center of the area of interest.
	 * @param longitude longitude of the center of the area of interest.
	 * @throws RESTFacadeException with a message for the user.
	 */
	public List<ReadingEvent> getReadingEvents(Long startTime, Long endTime, Double ulLatitude,
			Double ulLongitude, Double lrLatitude, Double lrLongitude) throws RESTFacadeException {
		return _remote.getReadingEvents(startTime, endTime, ulLatitude, ulLongitude, lrLatitude, lrLongitude);
	}


	public int getEventCount(Trip t){
		return _local.getEventCount(t);
	}
	/**
	 * @return The trip belonging to the user, which started on the unique moment.
	 * @param startTime the unique start.
	 */
	public Trip getTrip(Long startTime) {
		return _local.getTrip(startTime);
	}

	/**
	 * This method handles events which have not yet been equipped with a location.
	 * This might be due to a failure or delay from the GPS module
	 */
	public void updateEventsWithoutLocation(Trip t, Double latitude,
			Double longitude) {

		Log.i(_LOGTAG, "Trying to update events witouth location in the trip with id " + t.localId);
		List<Event> updatedEvents = _local.updateEventsWithoutLocation(t, latitude, longitude);
		ArrayList<Event> eventList = new ArrayList<Event>();
		//now update the trip on the server.
		for(Event e : updatedEvents){
			//TODO This ought to be done in a different thread.
			eventList.add(e);
		}
		_remote.updateTrip(t, eventList);
	}

	/**
	 * Close down this facade and all the connections initiated by the facade.
	 */
	public void closeFacade() {
		_local.closeFacade();
		_remote.closeCache();
		_local = null;
		_remote = null;
	}

	/**
	 * If a trip has not been ended correctly, it is still open,
	 * and can be retrieved by calling this method.
	 * @return will return null if no active trips are available.
	 */
	public Trip getActiveTrip() {
		List<Trip> trips = _local.getActiveTrips();
		/**
		 * More than one Trip could in theory be active, if a Trip has not been ended correctly.
		 * Sometimes the UI layer will expect a single trip to be active, for instance when
		 * the UI is shutdown due to memory deallocation or another application comming in to focus,
		 * the active trip should still be in persistence for later use.
		 */
		
		if(trips.size() < 1){
			return null;
		}
		else{
			//Maybe we should implement a more intelligent way of choosing the trip
			//But the error with multiple active trips should be solved elsewhere.
			return trips.get(0);
		}
	}
	
	public void deleteTrip(Long startTime) {
		_local.deleteTrip(startTime);
	}
	
	public void uploadTrip(Trip t) {
		
		try {
			_remote.updateTrip(t, t.events);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
