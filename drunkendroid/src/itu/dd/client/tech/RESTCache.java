package itu.dd.client.tech;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import itu.dd.client.domain.Event;
import itu.dd.client.domain.LocationEvent;
import itu.dd.client.domain.MoodEvent;
import itu.dd.client.domain.Trip;
import itu.dd.client.tech.exception.RESTFacadeException;

public class RESTCache implements IRESTCache {
	private final int UPDATECALL = 1;
	private final int UPLOADCALL = 2;
	private final String LOGTAG = "RESTCache";
	private HashSet<String> uploadTripFilter = null;
	private DBHelper _dbHelper;
	private LocalDataFacadeForSQLite _localSqlFacade;
	RESTServerFacade _server;
	private Context _context;
	static QueueLooper _queueLooper = null;
	
	public RESTCache(Context context, IWebserviceConnection conn){
		this._context = context;
		_dbHelper = DBHelper.getInstance(this._context);
		_localSqlFacade = new LocalDataFacadeForSQLite(context);
		_server = new RESTServerFacade(this._context, conn);
		//The Looper
		//Build the uploadTripFilter
		uploadTripFilter = new HashSet<String>();	
		uploadTripFilter.add(MoodEvent.class.getName());
		uploadTripFilter.add(LocationEvent.class.getName());
		//A little bit of thread safety.
		if(_queueLooper == null){
			_queueLooper = new QueueLooper();
			_queueLooper.isDaemon();
			_queueLooper.setName("QueueLooper");
			_queueLooper.start();
		}
	}

	/**
	 * Blocking call
	 * @throws RESTFacadeException which needs to be shown to the user
	 */
	public ArrayList<MoodEvent> getReadingEvents(Long starTime, Long endTime,
			Double ulLatitude, Double ulLongitude, Double lrLatitude,
			Double lrLongitude) throws RESTFacadeException {
		return _server.getReadingEvents(starTime, endTime, ulLatitude, ulLongitude, lrLatitude, lrLongitude);
	}
	
	/**
	 * The parameters are already persisted in the db.
	 * So we will discover ourselves what needs to be updated.
	 */
	public void updateTrip(Trip t, ArrayList<Event> eventList) {
		Message m = new Message();
		m.what = UPDATECALL;
		_queueLooper.mHandler.sendMessage(m);
	}
	
	public void updateFilteredTrip(Trip t, ArrayList<Event> eventList) throws RESTFacadeException {
		try {
			if(t.getRemoteId() == null) {
				uploadTrip(t);
				throw new RESTFacadeException(LOGTAG,"Tried to upload a trip without Remote ID");
			}
			_server.updateTrip(t, eventList);
			int length = eventList.size();
			for(int i = 0; i < length; i++)
				setEventProcessed(eventList.get(i));
		} catch (RESTFacadeException e) {
			throw e;
		}
	}
	
	/**
	 * The parameters are already persisted in the db.
	 * So we will just find the ones which need to be uploaded
	 */
	public void uploadTrip(Trip t) {
		Message m = new Message();
		m.what = UPLOADCALL;
		_queueLooper.mHandler.sendMessage(m);
	}
	
	synchronized void setEventProcessed(Event e){
		SQLiteDatabase db = _dbHelper.getDBInstance();
		ContentValues values = new ContentValues(1);
		final String whereClause = "id = ?";
		final String[] whereArgs = {String.valueOf(e.getId())};
		
		values.put("online", String.valueOf(1)); //set to online
		try{
			db.beginTransaction();
			db.update(DBHelper.TABLE_EVENT, values, whereClause, whereArgs);
			db.setTransactionSuccessful();
		}
		finally{
			db.endTransaction();
		}
	}
	
	synchronized void setTripProcessedAndUpdateForeignId(Trip t){
		SQLiteDatabase db = _dbHelper.getDBInstance();
		ContentValues values = new ContentValues(1);
		final String whereClause = "id = ?";
		final String[] whereArgs = {String.valueOf(t.getLocalId())};
		
		values.put("foreignId", String.valueOf(t.getRemoteId()));
		values.put("online", String.valueOf(1)); //set to online
		try{
			db.beginTransaction();
			db.update(DBHelper.TABLE_TRIP, values, whereClause, whereArgs);
			db.setTransactionSuccessful();
		}
		finally{
			db.endTransaction();
		}
	}
	
	/**
	 * Find the trip and events belonging to the trip
	 * which should be updated. More than one trip might be found
	 * if several trips have not been updated, even though they have
	 * been added to the cache. This might be because there has been no
	 * connectivity so the request could not be handled be the original
	 * command (message sent to handler), but an earlier command has
	 * picked up the changes in the cache before hand.
	 * @return Trips filled with events belonging to them, which have not
	 * 		   been processed.
	 */
	synchronized private ArrayList<Trip> getUpdateCandidates() {
		//The trip must have been uploaded (set online)
		//and the trip must have events which have not been set online.
		SQLiteDatabase db = _dbHelper.getDBInstance();
		final String tripQuery = "SELECT t.id, t.startDateTime, t.name FROM Trip t, Event e WHERE " +
			"e.online IS NULL AND t.online = 1 AND e.trip = t.id " +
			"GROUP BY t.id";
		
		//Find Trips with !online events
		ArrayList<Trip> candidateTrips = new ArrayList<Trip>();
		db.beginTransaction();
		Cursor cursor = db.rawQuery(tripQuery, null);
		try{
			while(cursor.moveToNext()){
				Trip t = new Trip();
				t.setLocalId(cursor.getLong(0));
				t.setStartDate(cursor.getLong(1));
				t.setName(cursor.getString(2));
				candidateTrips.add(t);
			}
			db.setTransactionSuccessful();
		}
		finally{
			cursor.close();
			db.endTransaction();
		}
		
		//Fill !online events into each trip.
		//Just get the trip as usual, remove processed events
		//then filter it later on.
		//This is to fight redundant SQL code.
		for(int i=0; i < candidateTrips.size(); i++)
		{
			Trip t = candidateTrips.get(i);
			t = _localSqlFacade.getTrip(t.getStartDate());
			//TODO We might want to check whether a trip was found.
			t = removeProcessedEvents(t);
			//Filter it.
			//The discarded events should also be set online, to show they have been processed
			HashSet<Event> filteredOutEvents = new HashSet<Event>();
			filteredOutEvents.addAll(t.getEvents());
			ArrayList<Event> filtered = Trip.filterEvents(t.getEvents(), uploadTripFilter);
			t.setEvents(filtered);
			//Now remove the ones which will be processed.
			filteredOutEvents.removeAll(t.getEvents());
			//Mark the rest as processed.
			for(Event e : filteredOutEvents){
				setEventProcessed(e);
			}
			//Needed to replace the object in the array.
			candidateTrips.set(i, t);
		}
		
		return candidateTrips;
	}

	synchronized private Trip removeProcessedEvents(Trip t){
		Trip resultTrip = new Trip();
		//Clone
		resultTrip.getEvents().addAll(t.getEvents());
		resultTrip.setStartDate(t.getStartDate());
		resultTrip.setLocalId(t.getLocalId());
		resultTrip.setRemoteId(t.getRemoteId());
		resultTrip.setName(t.getName());
		//Collect id's of processed events for the trip
		Set<Integer> processedEvents = getProcessedEventIDs(t);
		
		//If the event has an id in the list of processed events
		//remove the event from the result
		for(Event e : t.getEvents()){
			for(Integer i : processedEvents){
				if(i.intValue() == e.getId()){
					resultTrip.getEvents().remove(e);
				}
			}
		}
		
		return resultTrip;
	}
	
	private Set<Integer> getProcessedEventIDs(Trip t){
		SQLiteDatabase dbInstance = _dbHelper.getDBInstance();
		final String[] columns = {"id"};
		final String whereClause = "online = 1 AND trip = ?";
		final String[] whereArgs = {String.valueOf(t.getLocalId())};
		HashSet<Integer> result = new HashSet<Integer>();
		
		dbInstance.beginTransaction();
		Cursor cursor = dbInstance.query(DBHelper.TABLE_EVENT, columns, whereClause, whereArgs, null, null, null);
		try{
			while(cursor.moveToNext()){
				result.add(cursor.getInt(0));
			}
		}
		finally{
			dbInstance.endTransaction();
			cursor.close();
		}
		return result;
	}
	
	/**
	 * If a trip has not been uploaded before,
	 * none of it's events have been uploaded either.
	 * So gather everything together. And filter it before returning.
	 * @return
	 */
	synchronized ArrayList<Trip> getUploadCandidates() {
		SQLiteDatabase dbInstance = _dbHelper.getDBInstance();
		final String[] columns = {"startDateTime"};
		final String selection = " online IS NULL AND foreignId IS NULL";;
		ArrayList<Trip> trips = new ArrayList<Trip>();
		
		dbInstance.beginTransaction();
		Cursor cursor = dbInstance.query(DBHelper.TABLE_TRIP, columns, selection, null, null, null, null);
		try{
			while(cursor.moveToNext()){
				long startDateTime;
				startDateTime = cursor.getLong(0);
				//Fill events into it
				Trip t = _localSqlFacade.getTrip(startDateTime);
				trips.add(t);
			}
			dbInstance.setTransactionSuccessful();
		}
		finally{
			dbInstance.endTransaction();
			cursor.close();
		}
		
		//Filter the trips
		int length = trips.size();
		Trip t;
		for(int i = 0; i < length; i++) {
			t = trips.get(i);
			//Filter it.
			//The discarded events should also be set online, to show they have been processed
			TreeSet<Event> filteredOutEvents = new TreeSet<Event>();
			filteredOutEvents.addAll(t.getEvents());
			
			t.setEvents(Trip.filterEvents(t.getEvents(), uploadTripFilter));
			//Now remove the ones which will be processed.
			filteredOutEvents.removeAll(t.getEvents());
			//Mark the rest as processed.
			for(Event e : filteredOutEvents){
				setEventProcessed(e);
			}
		}
		
		return trips;
	}

	/**
	 * Inspired by http://developer.android.com/reference/android/os/Looper.html
	 * @author ExxKA
	 *
	 */
	private class QueueLooper extends Thread{
		private final String LOGTAG ="RestRequestLooper";
		public Handler mHandler;
		
		public void run() {
			Looper.prepare();
			mHandler = new Handler(){
				public void handleMessage(Message msg) {
					waitForConnectivity();
					super.handleMessage(msg);
					//Process
					switch (msg.what) {
					case UPDATECALL:
						Log.i(LOGTAG, "Handling an update call");
						ArrayList<Trip> updateTrips = getUpdateCandidates();
						//Try to update each Trip
						int length = updateTrips.size();
						Trip trip;
						for(int i = 0; i < length; i++) {
							trip = updateTrips.get(i);
							//The server should throw exceptions all the way here.
							try{
								_server.updateTrip(updateTrips.get(i), trip.getEvents());
								int len = trip.getEvents().size();
								for(int j = 0; j < len; j++) {
									//The trip is already set to online.
									setEventProcessed(trip.getEvents().get(j));
								}
							} catch (RESTFacadeException e) {
								//don't care it will be picked up later.
							}
						}
						
						break;
					case UPLOADCALL:
						Log.i(LOGTAG, "Handling an upload call");
						ArrayList<Trip> uploadTrips = getUploadCandidates();
						try{
							int len = uploadTrips.size();
							Trip t;
							for(int i = 0; i < len; i++) {
								t = uploadTrips.get(i);
								_server.uploadTrip(t);
								//Persist changes
								//a remoteId has been obtained.
								//and the trip is now online.
								setTripProcessedAndUpdateForeignId(t);
								//set the events online
								int l = t.getEvents().size();
								for(int j = 0; i < l; j++) {
									/*
									 * All events will be set online.
									 * also events which have been filtered by 
									 * the uploadTrip function on the 
									 * IRemoteDataFace implementation.
									 * 
									 * Filtered: Personal event which should not
									 * be uploaded unless the user has consented.
									 */
									setEventProcessed(t.getEvents().get(j));
									}
								}
							}
						catch (RESTFacadeException e) {
							// Just log it.
							Log.e(LOGTAG, "Tried to execute an uploadcall: " + e.getMessage());
						}
						break;
					default:
						Log.e(LOGTAG, "Cannot understand the message");
						break;
					}			
				}
			};
			//Start the looper, and start handling messages for this thread
			Looper.loop();
		}
		
		/**
		 * Blocking the thread until connectivity is obtained.
		 */
		private void waitForConnectivity(){
			long sleepTime = 60000; //a minute
			ConnectivityManager connMgr = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			while(connMgr.getActiveNetworkInfo() != null && !connMgr.getActiveNetworkInfo().isConnected()){
				try {
					Log.i(LOGTAG, "Going to sleep, waiting for network connectivity");
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					Log.i(LOGTAG, "My sleep was interrupted");
				}
			}
		}
	}

	public void closeCache() {
		/*//Remember to close the QueueLooper Thread.
		if(_queueLooper != null)
			_queueLooper.mHandler.getLooper().quit();
		_queueLooper = null;*/
	}
}
