package itu.dd.client.control;

import itu.dd.client.domain.Event;
import itu.dd.client.domain.MoodEvent;
import itu.dd.client.domain.Trip;
import itu.dd.client.tech.ILocalDataFacade;
import itu.dd.client.tech.IRestCache;
import itu.dd.client.tech.IWebserviceConnection;
import itu.dd.client.tech.SqliteAdapter;
import itu.dd.client.tech.RestCache;
import itu.dd.client.tech.RestConnection;
import itu.dd.client.tech.exception.CommunicationException;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;

public class DataFacade implements IDataFacade {
	private static final String _LOGTAG = "Drunkendroid DataFacade";
	private IRestCache _remote;
	private ILocalDataFacade _local;
	
	//Don't call the default constructor!
	@SuppressWarnings("unused")
	private DataFacade(){};
	
	public DataFacade(Context context){
		IWebserviceConnection conn = new RestConnection();
		_remote = new RestCache(context, conn);
		//_remote = new DummyRESTserver();
		_local = new SqliteAdapter(context);
	}
	
	/**
	 * Add an event to the local and foreign database.
	 */
	public void addEvent(Trip t, Event e) {
		//The event is persisted.
		_local.addEvent(t, e);
		
		//The server is being updated
		if(e.getLatitude() != null && e.getLongitude() != null){
			//also upload to the server, if the location is known.
			if(t.getRemoteId() == null){
				_remote.uploadTrip();
			}
			else{
				ArrayList<Event> eventList = new ArrayList<Event>();
				eventList.add(e);
				_remote.updateTrip();
			}	
		}	
	}
	
	/**
	 * Begin a new empty trip.
	 * @return a Trip with no events, but a unique localId.
	 */
	public Trip startTrip(String name){
		return _local.startTrip(name);
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
	public ArrayList<Trip> getAllTrips() {
		return _local.getAllTrips();
	}

	/**
	 * Get aggregated mood data
	 * @param startTime the beginning of the aggregation
	 * @param endTime the ending of the aggregation
	 * @param latiude latitude of the center of the area of interest.
	 * @param longitude longitude of the center of the area of interest.
	 * @throws CommunicationException with a message for the user.
	 */
	public ArrayList<MoodEvent> getReadingEvents(Long startTime, Long endTime, Double ulLatitude,
			Double ulLongitude, Double lrLatitude, Double lrLongitude) throws CommunicationException {
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

		Log.i(_LOGTAG, "Trying to update events witouth location in the trip with id " + t.getLocalId());
		List<Event> updatedEvents = _local.updateEventsWithoutLocation(t, latitude, longitude);
		ArrayList<Event> eventList = new ArrayList<Event>();
		//now update the trip on the server.
		int length = updatedEvents.size();
		for(int i = 0; i < length; i++)
			eventList.add(updatedEvents.get(i));
		
		//If trip has no RemoteId, upload trip and get it.
		if(t.getRemoteId() == null)
			_remote.uploadTrip();
		else
			_remote.updateTrip();
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
	
	public void updateFilteredTrip(Trip t) throws CommunicationException {
		_remote.updateFilteredTrip(t, t.getEvents());		
	}
}
