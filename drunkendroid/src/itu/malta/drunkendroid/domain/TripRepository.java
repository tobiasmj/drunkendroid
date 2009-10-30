package itu.malta.drunkendroid.domain;

import itu.malta.drunkendroid.Constants;
import itu.malta.drunkendroid.domain.entities.NewTrip;
import itu.malta.drunkendroid.domain.entities.OldTrip;
import itu.malta.drunkendroid.domain.entities.Trip;
import itu.malta.drunkendroid.tech.DBHelper;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TripRepository {
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	
	public TripRepository(Context context){
		dbHelper = DBHelper.getInstance(context);
		db = dbHelper.getDBInstance();
	}
	
	/**
	 * Inserts a trip including all of the accompanied readings. <br>
	 * {@link Trip}
	 * @param t a trip including the Readings
	 * @return whether or not the insertion was a success.
	 * @exception IllegalArgumentException if there are no attached Readings
	 * @exception SQLException if something with the underlying database is wrong
	 */
	
	public void insert(NewTrip t) throws IllegalArgumentException, SQLException {
		
		if(t.getTripReadings().isEmpty())
			throw new IllegalArgumentException("There are no readings");
		try{
			ContentValues values = new ContentValues();
			values.put("startDateTime", t.getStartDate().getTimeInMillis());
			long tripId = db.insertOrThrow(DBHelper.TABLE_TRIP, null, values);
			if(tripId < 0)
				throw new SQLException("The new trip didn't get inserted to the trip table");
			try{
				db.beginTransaction();
				for(Trip.Reading r : t.getTripReadings()){
					//Prepare insert statements for all the readings, refering to the trip.
					ContentValues readingValues = new ContentValues();
					readingValues.put("trip", tripId);
					readingValues.put("dateTime", r.getDate().getTimeInMillis());
					readingValues.put("longitude", r.getLongitude());
					readingValues.put("latitude", r.getLatitude());
					readingValues.put("mood", r.getMood().intValue());
					
					long success = db.insertOrThrow(DBHelper.TABLE_READING, null, readingValues);
					if(success == -1)
						throw new SQLException("The readings where not inserted");
				}
				db.setTransactionSuccessful();
			}
			finally{
				db.endTransaction();
			}
		}
		catch(SQLException e){
			//TODO implement exception handling
			Log.e(Constants.LOGTAG, "Tried to insert a Trip", e);
			throw e;
		}
	}
	
	public OldTrip selectTripByStartDate(Calendar startDate){
		OldTrip loadedTrip = new OldTrip();
		//TODO: Complete
		String[] selectedColumns = {"id"};
		String[] whereDateTimeEQ = {String.valueOf(startDate.getTimeInMillis())};
		Cursor selectionCursor = db.query(DBHelper.TABLE_TRIP, selectedColumns, "startDateTime = ?", whereDateTimeEQ, null, null, null);
		//Find the TripId
		selectionCursor.moveToNext();
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
			OldTrip.OldReading r = loadedTrip.newReading();
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
	
	public ArrayList<Trip> selectAllTrips() {
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
				OldTrip trip = new OldTrip();
				trip.setDateInMilliSec(startDateTime);
				trips.add(trip);
			}
		}
		finally{
			returnedTrips.close();
		}
		
		return trips;
	}

	public void cleanup() {
		dbHelper.cleanup();
	}

	public void flushDatabase() {
		dbHelper.flushDatabase();
	}
}
