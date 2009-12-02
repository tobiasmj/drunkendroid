package itu.malta.drunkendroid.tech;

import java.util.ArrayList;
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
import android.util.AndroidRuntimeException;
import android.util.Log;
import itu.malta.drunkendroid.control.IRemoteDataFacade;
import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.LocationEvent;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.tech.exception.RESTFacadeException;

public class RESTCache implements IRemoteDataFacade {
	private final int UPDATECALL = 1;
	private final int UPLOADCALL = 2;
	private Set<Class<?>> uploadTripFilter = null;
	
	DBHelper _dbHelper;
	LocalDataFacadeForSQLite _localSqlFacade;
	RESTServerFacade _server;
	Context _context;
	QueueLooper _queueLooper;
	
	public RESTCache(Context context){
		this._context = context;
		IWebserviceConnection conn = new WebserviceConnectionREST();
		_dbHelper = DBHelper.getInstance(this._context);
		_localSqlFacade = new LocalDataFacadeForSQLite(context);
		_server = new RESTServerFacade(this._context, conn);
		_queueLooper = new QueueLooper();
		_queueLooper.start();
		//Build the uploadTripFilter
		uploadTripFilter = new TreeSet<Class<?>>();
		uploadTripFilter.add(ReadingEvent.class);
		uploadTripFilter.add(LocationEvent.class);
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
	
	/**
	 * The parameters are already persisted in the db.
	 * So we will just find the ones which need to be uploaded
	 */
	public void uploadTrip(Trip t) {
		Message m = new Message();
		m.what = UPLOADCALL;
		_queueLooper.mHandler.sendMessage(m);
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
						List<Trip> uploadTrips = getUpLoadCandidates();
						try{
							for(Trip t : uploadTrips){
								_server.uploadTrip(t);
								//Check whether a remoteId has been obtained
								//Persist changes
								if(t.remoteId != null){
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
						}
						catch (RESTFacadeException e) {
							// Don't handle
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
		private void setEventProcessed(Event e){
			SQLiteDatabase db = _dbHelper.getDBInstance();
			ContentValues values = new ContentValues(1);
			final String whereClause = "id = ?";
			final String[] whereArgs = {String.valueOf(e.id)};
			
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
		
		private void setTripProcessedAndUpdateForeignId(Trip t){
			SQLiteDatabase db = _dbHelper.getDBInstance();
			ContentValues values = new ContentValues(1);
			final String whereClause = "id = ?";
			final String[] whereArgs = {String.valueOf(t.localId)};
			
			values.put("foreignId", String.valueOf(t.localId));
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
		private List<Trip> getUpdateCandidates() {
			//The trip must have been uploaded (set online)
			//and the trip must have events which have not been set online.
			SQLiteDatabase db = _dbHelper.getDBInstance();
			final String tripQuery = "SELECT t.id, t.startDateTime FROM Trip t, Event e WHERE " +
				"e.online != 1 AND t.online = 1 AND e.trip = t.id " +
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
			//Just get the trip as usual, then filter it later on.
			//This is to fight redundant SQL code.
			for(Trip t : candidateTrips){
				t = _localSqlFacade.getTrip(t.startDate);
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
			
			return candidateTrips;
		}
		
		/**
		 * If a trip has not been uploaded before,
		 * none of it's events have been uploaded either.
		 * So gather everything together.
		 * @return
		 */
		private List<Trip> getUpLoadCandidates() {
			SQLiteDatabase dbInstance = _dbHelper.getDBInstance();
			final String[] columns = {"startDateTime"};
			final String selection = " online != 1 AND foreignId IS NULL";;
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
			
			return trips;
		}
	}
}
