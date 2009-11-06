package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.domain.entities.*;
import itu.malta.drunkendroid.tech.DBHelper;
import java.util.ArrayList;
import java.util.Calendar;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

public class TripRepository {
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private Long activeTripId = null;
	
	public TripRepository(Context context){
		dbHelper = DBHelper.getInstance(context);
		db = dbHelper.getDBInstance();
	}
	
	public void insert(Location location)
	{
		Reading reading = new Reading();
		reading.setLatitude(location.getLatitude());
		reading.setLongitude(location.getLongitude());
		reading.setMood((short)0);
		
		insert(reading);
	}	
	
	public void insert(Reading r){
		if(activeTripId == null){
			startNewTrip(r.getDate());
		}
		
		try{
			db.beginTransaction();
			ContentValues readingValues = new ContentValues();
			readingValues.put("trip", activeTripId);
			readingValues.put("dateTime", r.getDate().getTimeInMillis());
			readingValues.put("longitude", r.getLongitude());
			readingValues.put("latitude", r.getLatitude());
			readingValues.put("mood", r.getMood().intValue());
			
			long success = db.insertOrThrow(DBHelper.TABLE_READING, null, readingValues);
			if(success == -1)
				throw new SQLException("The reading where not inserted");
			db.setTransactionSuccessful();
		}
		finally{
			db.endTransaction();
		}	
	}
	
	private void startNewTrip(Calendar calendar) {
		//Check to search for active trips in the db
		Long tmpActiveTripId = getActiveTripId();
		if(tmpActiveTripId == null){
			ContentValues values = new ContentValues();
			values.put("startDateTime", calendar.getTimeInMillis());
			values.put("active", true);
			// Add a value for the active state to the real sql table.
			Long tripId = db.insertOrThrow(DBHelper.TABLE_TRIP, null, values);
			if(tripId < 0)
				throw new SQLException("The new trip didn't get inserted to the trip table");
			
			activeTripId = tripId;	
		}
		else{
			activeTripId = tmpActiveTripId;
		}
	}

	/**
	 * 
	 * @return the Id of the active Trip in the database
	 */
	private Long getActiveTripId(){
		try{
			final String[] returnColumns = {"tripId"};
			final String whereClause = "active = ?";
			final String[] whereArgs = {"true"};
			db.beginTransaction();
			Cursor result = db.query(DBHelper.TABLE_TRIP, returnColumns, whereClause, whereArgs, null, null, null);
			db.setTransactionSuccessful();
			
			switch (result.getCount()) {
			case 0:
				return null;
			case 1:
				result.moveToFirst();
				return result.getLong(0);
			default:
				throw new Exception("The database contains more than 1 active Trip");
			}	
		}
		catch(SQLException e){
			return null;	
		}
		catch(Exception e){
			//This is not good.
			//There is more than one active Trip in the database. how do we handle this in a clever way?
			//We close all trips recursively until there is only one active trip.
			Log.e(Resources.getSystem().getString(R.string.log_tag), "There is more than one active trip in the db, trying to solve the problem");
			
			final String[] returnColumns = {"tripId"};
			final String whereClause = "active = ?";
			final String[] whereArgs = {"true"};
			
			db.beginTransaction();
			Cursor result = db.query(DBHelper.TABLE_TRIP, returnColumns, whereClause, whereArgs, null, null, null);
			db.setTransactionSuccessful();
			
			result.moveToFirst();
			//Begin closing trips.
			this.activeTripId = result.getLong(0);
			this.endTrip();
			//Call this method recursively, we now hope there is only one active trip left in the db.
			return this.getActiveTripId();
		}
		finally{
			db.endTransaction();
		}
	}


	public Trip getTripByStartTime(Calendar startDate){
		Trip loadedTrip = new Trip();
		//TODO: Complete
		String[] selectedColumns = {"id"};
		String[] whereDateTimeEQ = {String.valueOf(startDate.getTimeInMillis())};
		Cursor selectionCursor = db.query(DBHelper.TABLE_TRIP, selectedColumns, "startDateTime = ?", whereDateTimeEQ, null, null, null);
		//Find the TripId
		selectionCursor.moveToFirst();
		long tripId = selectionCursor.getLong(0);
		if(tripId < 0)
			throw new IllegalArgumentException("The trip could not be located");
		//The trip was located, which means we now know the startdate
		loadedTrip.setDateInMilliSec(startDate.getTimeInMillis());
		selectionCursor.close();
		//Build the trip
		String[] selectedReadingColumns = {"dateTime", "longitude", "latitude", "mood"};
		String[] whereTripEQ = {String.valueOf(tripId)};
		Cursor selectionOfReadings = db.query(DBHelper.TABLE_READING, selectedReadingColumns, "trip = ?", whereTripEQ, null, null, null);
		
		while(selectionOfReadings.moveToNext()){
			Reading r = new Reading();
			//Set dateTime
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(selectionOfReadings.getLong(0));
			r.setDate(c);
			//Set Longitude
			r.setLongitude(selectionOfReadings.getDouble(1));
			//Set Latitude
			r.setLatitude(selectionOfReadings.getDouble(2));
			//Set Mood
			r.setMood(selectionOfReadings.getShort(3));
			
			//Add to the trip
			loadedTrip.AddReading(r);
		}
		return loadedTrip;
	}
	
	public ArrayList<Trip> getAllTrips() {
		ArrayList<Trip> trips = new ArrayList<Trip>();
		String[] selectedTripColumns = {"startDateTime"};
		Cursor returnedTrips = null;
		
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

	public void endTrip(){
		final String activeColumn = "active";
		final String whereClause = activeColumn + "= ?";
		final String[] whereArgs = {String.valueOf(activeTripId)};
			
		//End the ongoing trip, by setting the trip in the db as stored.
		// and set the activeTripId to null.
		db.beginTransaction();
		try{
			
			ContentValues values = new ContentValues();
			values.put(activeColumn, false);
			db.update(DBHelper.TABLE_TRIP, values, whereClause, whereArgs);
			db.setTransactionSuccessful();
			this.activeTripId = null;
		}
		finally{
			db.endTransaction();
		}
	}
	public void close() {
		dbHelper.cleanup();
	}

	public void flushDatabase() {
		dbHelper.flushDatabase();
	}
}
