package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.IWebserviceConnection;
import itu.malta.drunkendroid.tech.LocalDataFacadeForSQLite;
import itu.malta.drunkendroid.tech.RESTServerFacade;
import itu.malta.drunkendroid.tech.WebserviceConnectionREST;
import itu.malta.drunkendroid.tech.dummy.DummyRESTserver;

import java.util.List;
import android.content.Context;
import android.util.Log;

public class DataFacade implements IDataFacade {
	private static final String LOGTAG = "Drunkendroid DataFacade";
	private IRemoteDataFacade remote;
	private ILocalDataFacade local;
	
	//Don't call the default constructor!
	@SuppressWarnings("unused")
	private DataFacade(){};
	
	public DataFacade(Context context){
		IWebserviceConnection connection = new WebserviceConnectionREST();
		//remote = new RESTServerFacade(context, connection);
		remote = new DummyRESTserver();
		local = new LocalDataFacadeForSQLite(context);
	}
	
	/**
	 * Add an event to the local and foreign database.
	 */
	public void addEvent(Trip t, Event e) {
		local.addEvent(t, e);

		if(e.latitude != null && e.longitude != null){
			//also upload to the server, if the location is known.
			if(t.getRemoteID() == null){
				remote.uploadTrip(t);
				//persist the change
				local.addRemoteIdToTrip(t);
			}
			else{
				remote.updateTrip(t, e);
			}	
		}	
	}
	
	/**
	 * Begin a new empty trip.
	 * @return a Trip with no events, but a unique localId.
	 */
	public Trip startTrip(){
		return local.startTrip();
	}
	
	/**
	 * Close the trip in the database.
	 */
	public void closeTrip(Trip t) {
		local.closeTrip(t);
		//We could also consider closing all active trips.
	}

	/**
	 * @return All trips belonging to the user.
	 */
	public List<Trip> getAllTrips() {
		return local.getAllTrips();
	}

	/**
	 * Get aggregated mood data
	 * @param startTime the beginning of the aggregation
	 * @param endTime the ending of the aggregation
	 * @param latiude latitude of the center of the area of interest.
	 * @param longitude longitude of the center of the area of interest.
	 */
	public List<ReadingEvent> getReadingEvents(Long startTime, Long endTime, Double latitude,
			Double longitude, Long distance) {
		return remote.getReadingEvents(startTime, endTime, latitude, longitude, distance);
	}


	public int getEventCount(Trip t){
		return local.getEventCount(t);
	}
	/**
	 * @return The trip belonging to the user, which started on the unique moment.
	 * @param startTime the unique start.
	 */
	public Trip getTrip(Long startTime) {
		return local.getTrip(startTime);
	}

	/**
	 * This method handles events which have not yet been equipped with a location.
	 * This might be due to a failure or delay from the GPS module
	 */
	public void updateEventsWithoutLocation(Trip t, Double latitude,
			Double longitude) {
		Log.i(LOGTAG, "Trying to update events witouth location in the trip with id " + t.getLocalID());
		List<Event> updatedEvents = local.updateEventsWithoutLocation(t, latitude, longitude);
		//now update the trip on the server.
		for(Event e : updatedEvents){
			//TODO This ought to be done in a different thread.
			remote.updateTrip(t, e);
		}
	}

	/**
	 * Close down this facade and all the connections initiated by the facade.
	 */
	public void closeFacade() {
		local.closeFacade();
		//remote doesn't need to get closed.
		local = null;
		remote = null;
	}

	/**
	 * If a trip has not been ended correctly, it is still open,
	 * and can be retrieved by calling this method.
	 * @return will return null if no active trips are available.
	 */
	public Trip getActiveTrip() {
		List<Trip> trips = local.getActiveTrips();
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
	
	/**
	 * @param t must have a localId
	 */
	public void deleteTrip(Long startTime) {
		local.deleteTrip(startTime);
	}
}
