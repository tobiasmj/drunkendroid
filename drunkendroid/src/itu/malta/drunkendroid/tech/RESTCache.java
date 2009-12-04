package itu.malta.drunkendroid.tech;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.LocationEvent;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.exception.RESTFacadeException;

public class RESTCache implements IRESTCache {
	private final int UPDATECALL = 1;
	private final int UPLOADCALL = 2;
	private final String LOGTAG = "RESTCache";
	private Set<String> uploadTripFilter = null;
	
	DBHelper _dbHelper;
	LocalDataFacadeForSQLite _localSqlFacade;
	RESTServerFacade _server;
	Context _context;
	static QueueLooper _queueLooper = null;
	
	public RESTCache(Context context, IWebserviceConnection conn){
		this._context = context;
		_dbHelper = DBHelper.getInstance(this._context);
		_localSqlFacade = new LocalDataFacadeForSQLite(context);
		_server = new RESTServerFacade(this._context, conn);
		//The Looper
		//Build the uploadTripFilter
		uploadTripFilter = new TreeSet<String>();	
		uploadTripFilter.add(ReadingEvent.class.getName());
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
	public List<ReadingEvent> getReadingEvents(Long starTime, Long endTime,
			Double ulLatitude, Double ulLongitude, Double lrLatitude,
			Double lrLongitude) throws RESTFacadeException {
		return _server.getReadingEvents(starTime, endTime, ulLatitude, ulLongitude, lrLatitude, lrLongitude);
	}
	
	/**
	 * The parameters are already persisted in the db.
	 * So we will discover ourselves what needs to be updated.
	 */
	public void updateTrip(Trip t, List<Event> eventList) {
		Message m = new Message();
		m.what = UPDATECALL;
		_queueLooper.mHandler.sendMessage(m);
	}
	
	public void updateFilteredTrip(Trip t, List<Event> eventList) throws RESTFacadeException {
		try {
			if(t.remoteId == null) {
				uploadTrip(t);
				throw new RESTFacadeException(LOGTAG,"Tried to upload a trip without Remote ID");
			}
			_server.updateTrip(t, eventList);
			for(Event e : eventList) {
				setEventProcessed(e);
			}
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
	
	synchronized private void setEventProcessed(Event e){
		SQLiteDatabase db = _dbHelper.getDBInstance();
		ContentValues values = new ContentValues(1);
		final String whereClause = "id = ?";
		final String[] whereArgs = {String.valueOf(e.id)};
		
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
	
	synchronized private void setTripProcessedAndUpdateForeignId(Trip t){
		SQLiteDatabase db = _dbHelper.getDBInstance();
		ContentValues values = new ContentValues(1);
		final String whereClause = "id = ?";
		final String[] whereArgs = {String.valueOf(t.localId)};
		
		values.put("foreignId", String.valueOf(t.remoteId));
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
	synchronized private List<Trip> getUpdateCandidates() {
		//The trip must have been uploaded (set online)
		//and the trip must have events which have not been set online.
		SQLiteDatabase db = _dbHelper.getDBInstance();
		final String tripQuery = "SELECT t.id, t.startDateTime FROM Trip t, Event e WHERE " +
			"e.online IS NULL AND t.online = 1 AND e.trip = t.id " +
			"GROUP BY t.id";
		
		//Find Trips with !online events
		List<Trip> candidateTrips = new ArrayList<Trip>();
		db.beginTransaction();
		Cursor cursor = db.rawQuery(tripQuery, null);
		try{
			while(cursor.moveToNext()){
				Trip t = new Trip();
				t.localId = cursor.getLong(0);
				t.startDate = cursor.getLong(1);
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
			t = _localSqlFacade.getTrip(t.startDate);
			t = removeProcessedEvents(t);
			//Filter it.
			//The discarded events should also be set online, to show they have been processed
			TreeSet<Event> filteredOutEvents = new TreeSet<Event>();
			filteredOutEvents.addAll(t.events);
			List<Event> filtered = Trip.filterEvents(t.events, uploadTripFilter);
			t.events = filtered;
			//Now remove the ones which will be processed.
			filteredOutEvents.removeAll(t.events);
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
		resultTrip.events.addAll(t.events);
		resultTrip.startDate = t.startDate;
		resultTrip.localId = t.localId;
		resultTrip.remoteId = t.remoteId;
		//Collect id's of processed events for the trip
		Set<Integer> processedEvents = getProcessedEventIDs(t);
		
		//If the event has an id in the list of processed events
		//remove the event from the result
		for(Event e : t.events){
			for(Integer i : processedEvents){
				if(i.intValue() == e.id){
					resultTrip.events.remove(e);
				}
			}
		}
		
		return resultTrip;
	}
	
	private Set<Integer> getProcessedEventIDs(Trip t){
		SQLiteDatabase dbInstance = _dbHelper.getDBInstance();
		final String[] columns = {"id"};
		final String whereClause = "online = 1 AND trip = ?";
		final String[] whereArgs = {String.valueOf(t.localId)};
		Set<Integer> result = new HashSet<Integer>();
		
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
	synchronized private List<Trip> getUploadCandidates() {
		SQLiteDatabase dbInstance = _dbHelper.getDBInstance();
		final String[] columns = {"startDateTime"};
		final String selection = " online IS NULL AND foreignId IS NULL";;
		List<Trip> trips = new ArrayList<Trip>();
		
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
		for(Trip t : trips){
			//Filter it.
			//The discarded events should also be set online, to show they have been processed
			TreeSet<Event> filteredOutEvents = new TreeSet<Event>();
			filteredOutEvents.addAll(t.events);
			
			t.events = Trip.filterEvents(t.events, uploadTripFilter);
			//Now remove the ones which will be processed.
			filteredOutEvents.removeAll(t.events);
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
		private final String LOGTAG ="QueueHandler";
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
						//TODO: Implement
						List<Trip> updateTrips = getUpdateCandidates();
						//Try to update each Trip
						for(Trip t : updateTrips){
							//The server should throw exceptions all the way here.
							try{
								_server.updateTrip(t, t.events);
								for(Event e : t.events){
									//The trip is already set to online.
									setEventProcessed(e);
								}
							} catch (RESTFacadeException e) {
								//don't care it will be picked up later.
							}
						}
						
						break;
					case UPLOADCALL:
						Log.i(LOGTAG, "Handling an upload call");
						//TODO: Implement
						List<Trip> uploadTrips = getUploadCandidates();
						try{
							for(Trip t : uploadTrips){
								_server.uploadTrip(t);
								//Persist changes
								//a remoteId has been obtained.
								//and the trip is now online.
								setTripProcessedAndUpdateForeignId(t);
								//set the events online
								for(Event e : t.events){
									/*
									 * All events will be set online.
									 * also events which have been filtered by 
									 * the uploadTrip function on the 
									 * IRemoteDataFace implementation.
									 * 
									 * Filtered: Personal event which should not
									 * be uploaded unless the user has consented.
									 */
									setEventProcessed(e);
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
		 * Blocking the thread untill connectivity is obtained.
		 */
		private void waitForConnectivity(){
			long sleepTime = 60000; //a minute
			ConnectivityManager connMgr = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			while(!connMgr.getActiveNetworkInfo().isConnected()){
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
		//Remember to close the QueueLooper Thread.
		_queueLooper.mHandler.getLooper().quit();
	}
}
