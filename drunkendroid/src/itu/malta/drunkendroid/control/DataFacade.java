package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.IWebserviceConnection;
import itu.malta.drunkendroid.tech.LocalDataFacadeForSQLite;
import itu.malta.drunkendroid.tech.RESTServerFacade;
import itu.malta.drunkendroid.tech.WebserviceConnectionREST;
import java.util.List;
import android.content.Context;
import android.util.AndroidRuntimeException;

public class DataFacade implements IDataFacade {
	private IRemoteDataFacade remote;
	private ILocalDataFacade local;
	
	//Don't call the default constructor!
	@SuppressWarnings("unused")
	private DataFacade(){};
	
	public DataFacade(Context context){
		IWebserviceConnection connection = new WebserviceConnectionREST();
		remote = new RESTServerFacade(context, connection);
		local = new LocalDataFacadeForSQLite(context);
	}
	
	/**
	 * Add an event to the local and foreign database.
	 */
	public void addEvent(Trip t, Event e) {
		local.addEvent(t, e);
		//also upload to the server!
		remote.updateTrip(t, e);
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
	public Trip getEvents(Long startTime, Long endTime, Long latitude,
			Long longitude, Long distance) {
		return remote.getEvents(startTime, endTime, latitude, longitude, distance);
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
	public void updateEventsWithoutLocation(Trip t, Long latitude,
			Long Longitude) {
		throw new AndroidRuntimeException("Not Implemented");
		// TODO Auto-generated method stub

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
}
