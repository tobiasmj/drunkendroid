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
import android.util.AndroidRuntimeException;
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
			if(ReadingEvent.class.isInstance(e)){
				ReadingEvent r = (ReadingEvent)e;
				readingValues.put("mood", r.mood);
			}
			
			long success = db.insertOrThrow(DBHelper.TABLE_READING, null, readingValues);
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
        values.put("startDateTime", Calendar.getInstance().getTimeInMillis());
        values.put("active", true);
        // Add a value for the active state to the real sql table.

        Long tripId = db.insertOrThrow(DBHelper.TABLE_TRIP, null, values);
	    if(tripId < 0){
	    	Log.e(LOGTAG, "A new trip was not started correctly. Something is really wrong with the database");
			throw new SQLException("The new trip didn't get inserted to the trip table");
	    }
	    Trip newTrip = new Trip();
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
			values.put(activeColumn, false);
			db.update(DBHelper.TABLE_TRIP, values, whereClause, whereArgs);
			db.setTransactionSuccessful();
		}
		finally{
			db.endTransaction();
		}
	}

	public List<Trip> getAllTrips() {
		ArrayList<Trip> trips = new ArrayList<Trip>();
		String[] selectedTripColumns = {"startDateTime"};
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
				Trip trip = new Trip();
				trip.setDateInMilliSec(startDateTime);
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
			final String[] whereArgs = {"true"};
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
		//Build the trip
		String[] selectedReadingColumns = {"dateTime", "longitude", "latitude", "mood"};
		String[] whereTripEQ = {String.valueOf(tripId)};
		Cursor selectionOfReadings = db.query(DBHelper.TABLE_READING, selectedReadingColumns, "trip = ?", whereTripEQ, null, null, null);
		
		while(selectionOfReadings.moveToNext()){
			ReadingEvent r = new ReadingEvent(selectionOfReadings.getLong(0), 
					selectionOfReadings.getDouble(1), 
					selectionOfReadings.getDouble(2), 
					selectionOfReadings.getInt(3)); //Add mood
			//Add to the trip
			loadedTrip.AddEvent(r);
		}
		return loadedTrip;
	}

	public void updateEventsWithoutLocation(Trip t, Long latitude, Long Longitude) {
		// TODO Auto-generated method stub
		throw new AndroidRuntimeException("Not yet implemented");
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
}
