package itu.malta.drunkendroid.tech;

import itu.malta.drunkendroid.control.ILocalDataFacade;
import itu.malta.drunkendroid.domain.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocalDataFacadeForSQLite implements ILocalDataFacade {
	private static final String LOGTAG = "LocalDB DrunkenDroid";
	DBHelper dbHelper;
	
	//don't use the default constructor
	@SuppressWarnings("unused")
	private LocalDataFacadeForSQLite(){};
	public LocalDataFacadeForSQLite(Context context){
		dbHelper = DBHelper.getInstance(context);
	}
	
	public void addEvent(Trip t, Event e) {
		SQLiteDatabase db = dbHelper.getDBInstance();
		try{
			db.beginTransaction();
			ContentValues readingValues = new ContentValues();
			readingValues.put("trip", t.getLocalID());
			readingValues.put("dateTime", e.dateTime);
			readingValues.put("longitude", e.longitude);
			readingValues.put("latitude", e.latitude);
			
			//Handle other type of Events here
			if(ReadingEvent.class.isInstance(e))
				readingValues.put("mood", ((ReadingEvent)e).mood);
			else if(IncomingCallEvent.class.isInstance(e))
				readingValues.put("sender", ((CallEvent)e).getPhonenumber());
			else if(OutgoingCallEvent.class.isInstance(e))
				readingValues.put("receiver", ((CallEvent)e).getPhonenumber());
			else if(IncomingSMSEvent.class.isInstance(e)) {
				readingValues.put("sender", ((SMSEvent)e).getPhonenumber());
				readingValues.put("message", ((SMSEvent)e).getTextMessage());
			} 
			else if(OutgoingSMSEvent.class.isInstance(e)) {
				readingValues.put("receiver", ((SMSEvent)e).getPhonenumber());
				readingValues.put("message", ((SMSEvent)e).getTextMessage());
			}
			
			long success = db.insertOrThrow(DBHelper.TABLE_EVENT, null, readingValues);
			if(success == -1)
				throw new SQLException("The reading was not inserted");
			db.setTransactionSuccessful();
		}
		finally{
			db.endTransaction();
		}
	}
	public Trip startTrip(){
		SQLiteDatabase db = dbHelper.getDBInstance();

        ContentValues values = new ContentValues();
        Long currentTime = Calendar.getInstance().getTimeInMillis();
        values.put("startDateTime", currentTime);
        values.put("active", 1); //1 == true in sqlite3
        // Add a value for the active state to the real sql table.

        Long tripId = db.insertOrThrow(DBHelper.TABLE_TRIP, null, values);
	    if(tripId < 0){
	    	Log.e(LOGTAG, "A new trip was not started correctly. Something is really wrong with the database");
			throw new SQLException("The new trip didn't get inserted to the trip table");
	    }
	    Trip newTrip = new Trip();
	    newTrip.setDateInMilliSec(currentTime);
	    newTrip.setLocalID(tripId);
	    return newTrip;
	}
	
	public void closeTrip(Trip t) {
		final String activeColumn = "active";
		final String idColumn = "id";
		final String whereClause = idColumn + "= ?";
		final String[] whereArgs = {String.valueOf(t.getLocalID())};
		SQLiteDatabase db = dbHelper.getDBInstance();
		
		//End the ongoing trip, by setting the trip in the db as stored.
		db.beginTransaction();
		try{
			
			ContentValues values = new ContentValues();
			values.put(activeColumn, 0);// 0 == false in sqlite3
			db.update(DBHelper.TABLE_TRIP, values, whereClause, whereArgs);
			db.setTransactionSuccessful();
		}
		finally{
			db.endTransaction();
		}
	}

	public List<Trip> getAllTrips() {
		ArrayList<Trip> trips = new ArrayList<Trip>();
		String[] selectedTripColumns = {"startDateTime, id, foreignId"};
		Cursor returnedTrips = null;
		SQLiteDatabase db = dbHelper.getDBInstance();
		
		db.beginTransaction();
		try{
			returnedTrips = db.query(DBHelper.TABLE_TRIP, selectedTripColumns, null, null, null, null, null);
			db.setTransactionSuccessful();	
		}
		finally{
			db.endTransaction();
		}
		
		//Add trips to the list
		try{
			while(returnedTrips.moveToNext()){
				long startDateTime = returnedTrips.getLong(0);
				long localId = returnedTrips.getLong(1);
				long remoteId = returnedTrips.getLong(2);
				Trip trip = new Trip();
				trip.setDateInMilliSec(startDateTime);
				trip.setLocalID(localId);
				trip.setRemoteID(remoteId);
				trips.add(trip);
			}
		}
		finally{
			returnedTrips.close();
		}
		
		return trips;

	}

	public List<Trip> getActiveTrips() {
		SQLiteDatabase db = dbHelper.getDBInstance();
		List<Trip> resultList = new ArrayList<Trip>();
		
		try{
			final String[] returnColumns = {"id", "foreignId", "startDateTime"};
			final String whereClause = "active = ?";
			final String[] whereArgs = {"1"}; //1 == true in SQLite3
			db.beginTransaction();
			Cursor result = db.query(DBHelper.TABLE_TRIP, returnColumns, whereClause, whereArgs, null, null, null);
			db.setTransactionSuccessful();
			
			while(result.moveToNext()){
				Trip trip = new Trip();
				
				long tripId = result.getLong(0);
				long foreignId = result.getLong(1);
				long startTime = result.getLong(2);
				
				trip.setLocalID(tripId);
				trip.setRemoteID(foreignId);
				trip.setDateInMilliSec(startTime);
				
				resultList.add(trip);
			}
			//Remember to close the Cursor.
			result.close();
			
		}
		catch(SQLException e){
			//Log the error
			Log.e(LOGTAG, e.getMessage());
			return resultList;	
		}
		finally{
			db.endTransaction();
		}
		
		return resultList;
	}

	public Trip getTrip(Long startTime) {
		Trip loadedTrip = new Trip();
		SQLiteDatabase db = dbHelper.getDBInstance();
		
		String[] selectedColumns = {"id", "foreignId"};
		String[] whereDateTimeEQ = {String.valueOf(startTime)};
		Cursor selectionCursor = db.query(DBHelper.TABLE_TRIP, selectedColumns, "startDateTime = ?", whereDateTimeEQ, null, null, null);
		//Find the TripId
		if(!selectionCursor.moveToFirst())
			throw new IllegalArgumentException("The trip could not be located");
		//The trip was located, fill it with info
		loadedTrip.setDateInMilliSec(startTime);
		Long tripId = selectionCursor.getLong(0);
		loadedTrip.setLocalID(tripId);
		loadedTrip.setRemoteID(selectionCursor.getLong(1));
		selectionCursor.close();
		
		//Build the trip, by building each event
		String[] selectedReadingColumns = {"dateTime", "longitude", "latitude", "mood", "sender", "receiver", "message", "id"};
		String[] whereTripEQ = {String.valueOf(tripId)};
		Cursor selectionOfReadings = db.query(DBHelper.TABLE_EVENT, selectedReadingColumns, "trip = ?", whereTripEQ, null, null, null);
		
		while(selectionOfReadings.moveToNext()){
			Event e = null;
			Long date = selectionOfReadings.getLong(0);
			Double longitude = selectionOfReadings.getDouble(1);
			Double latitude = selectionOfReadings.getDouble(2);
			
			if(selectionOfReadings.isNull(3)){
				if(selectionOfReadings.isNull(6)) {
					//This is either a call or a LocationEvent since there is no mood or message
					if(selectionOfReadings.isNull(5)) {
						if(selectionOfReadings.isNull(4)) {
							//This is just a Location event, since there is no sender, receiver or message.
							e = (new LocationEvent(date, latitude, longitude));
						} else {
						//This is an incoming call, since there is a sender
						e = new IncomingCallEvent(date, latitude, longitude, selectionOfReadings.getString(4));
						}
					} 
					else {
						//This is and outgoing call, since there is a receiver
						e = new OutgoingCallEvent(date, latitude, longitude, selectionOfReadings.getString(5));
					}
				}
				else {
					//This is an SMS, since there is a message
					if(selectionOfReadings.isNull(5)) {
						//This is an incoming sms, since there is no receiver
						e = new IncomingSMSEvent(date, latitude, longitude, selectionOfReadings.getString(4), selectionOfReadings.getString(6));
					} else {
						//This is and outgoing sms, since there is a receiver
						e = new OutgoingSMSEvent(date, latitude, longitude, selectionOfReadings.getString(5), selectionOfReadings.getString(6));
					}
				}
			}
			else {
				//This is a reading event, since there is a mood
				e = new ReadingEvent(date, 
						latitude, 
						longitude, 
						selectionOfReadings.getInt(3)); //Add mood
			}
			//Add an id to the event
			e.id = selectionOfReadings.getInt(7);
			//Add the event to the trip
			loadedTrip.AddEvent(e);
		}
		selectionOfReadings.close();
		
		return loadedTrip;
	}

	public List<Event> updateEventsWithoutLocation(Trip t, Double latitude, Double longitude) {
		SQLiteDatabase db = dbHelper.getDBInstance();
		
		//Find the events which will be updated.
		final String[] columns = {"dateTime", "mood", "sender", "receiver", "message", "id"}; //the location is not known silly.
		final String selection = " longitude IS NULL AND latitude IS NULL";
		List<Event> events = new ArrayList<Event>();
		
		db.beginTransaction();
		Cursor cursor = db.query(DBHelper.TABLE_EVENT, columns, selection, null, null, null, null);
		try{
			if(cursor.moveToFirst()){
				do {
					Event e = null;
					if(cursor.isNull(2) && cursor.isNull(3) && cursor.isNull(4)) {
						//This is either a location- or readingEvent.
						Long date = cursor.getLong(0);
						//does the event have a mood?
						if(cursor.isNull(1)){
							//No
							//This is a locationEvent
							e = new LocationEvent(date, latitude, longitude);
						}
						else {
							//Yes
							//This is a ReadingEvent
							int mood = cursor.getInt(1);
							e = new ReadingEvent(date, latitude, longitude, mood);
						}
					}
					if(e != null){
						e.id = cursor.getInt(5);
					}
					
				} while (cursor.moveToNext());
			}	
			db.setTransactionSuccessful();
		}
		finally{
			db.endTransaction();
			cursor.close();
		}
		
		//Now do the update
		ContentValues values = new ContentValues(2);
		final String whereClause = " longitude IS NULL AND latitude IS NULL AND trip = ?";
		final String[] whereArgs = {String.valueOf(t.getLocalID())};
		
		values.put("longitude", String.valueOf(longitude));
		values.put("latitude",  String.valueOf(latitude));
		
		try{
			db.beginTransaction();
			db.update(DBHelper.TABLE_EVENT, values, whereClause, whereArgs);
			db.setTransactionSuccessful();
		}
		finally{
			db.endTransaction();
		}
		
		//return the event which where updated.
		return events;
	}
	
	/**
	 * Close the facade and all instanciated sub-components.
	 * Don't close active trips. Since they should also be persisted.
	 */
	public void closeFacade() {
		//Cleanup the helper nicely.
		dbHelper.cleanup();
	}

	public void flushDatabase() {
		dbHelper.flushDatabase();
	}
	/**
	 * Set the remoteId of a Trip already contained in the database.
	 * @param t The trip which contains a remoteId, but needs to get it set in the database.
	 */
	public void addRemoteIdToTrip(Trip t) {
		SQLiteDatabase db = dbHelper.getDBInstance();
		ContentValues values = new ContentValues(1);
		final String whereClause = "id = ?";
		final String[] whereArgs = {String.valueOf(t.getLocalID())};
		
		values.put("foreignId", String.valueOf(t.getRemoteID()));
		try{
			db.beginTransaction();
			db.update(DBHelper.TABLE_TRIP, values, whereClause, whereArgs);
			db.setTransactionSuccessful();
		}
		finally{
			db.endTransaction();
		}
		
	}
	public int getEventCount(Trip t) {
		Long localTripId = t.getLocalID();
		SQLiteDatabase db = dbHelper.getDBInstance();
		final String[] columns = {"count(*)"};
		final String whereClause = " trip = ?";
		final String[] whereArgs = {String.valueOf(localTripId)};
		int result = 0;
		db.beginTransaction();
		Cursor cursor = db.query(DBHelper.TABLE_EVENT, columns, whereClause, whereArgs, null, null, null);
		
		try{
			if(cursor.moveToFirst()){
				//Get the count
				result = cursor.getInt(0);
				db.setTransactionSuccessful();
			}
		}
		finally{
			db.endTransaction();
			cursor.close();
		}
		
		return result;
	}
	public void deleteTrip(Long startTime) {
		SQLiteDatabase db = dbHelper.getDBInstance();
		
		try{
			Trip t = this.getTrip(startTime);
			//Delete the trip
			final String whereClauseTRIP = "id = ?";
			final String[] whereArgsTRIP = {String.valueOf(t.getLocalID())};
			db.beginTransaction();
			db.delete(DBHelper.TABLE_TRIP, whereClauseTRIP, whereArgsTRIP);
			
			//Delete the events
			final String whereClauseEVENT = "trip = ?";
			final String[] whereArgsEVENT = {String.valueOf(t.getLocalID())};
			db.delete(DBHelper.TABLE_EVENT, whereClauseEVENT, whereArgsEVENT);
			db.setTransactionSuccessful();
		}
		finally{
			db.endTransaction();
		}
	}
}
