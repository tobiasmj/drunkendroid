package itu.malta.drunkendroid.tech;

import java.util.ArrayList;
import java.util.Calendar;

import itu.malta.drunkendroid.Constants;
import itu.malta.drunkendroid.domain.entities.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

/**
 * This class is heavily inspired by the helper class DBHelper described in 
 * "Unlocking Android 2009", by W. Frank Ableson, Charlie Collins, Robi Sen.
 * <br>
 * The class takes care of the technical communication with the sqlite3 database
 * @author ExxKA
 */
public class DBHelper {
	public static final String DB_NAME = "drunkendroid";
	public static final Integer DB_VERSION = 4;
	public static final String TABLE_TRIP = "Trip";
	public static final String TABLE_READING = "Reading";
	public static final String CLASSNAME = "DBHelper";
	
	private SQLiteDatabase db;
	private final DBOpenHelper dbOpenHelper;
	
	public DBHelper(final Context context){
		this.dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
		this.establishDb();
	}
	
	private void establishDb(){
		if( this.db == null)
			this.db = this.dbOpenHelper.getWritableDatabase();
	}
	
	public void cleanup(){
		if(this.db != null){
			this.db.close();
			this.db = null;
		}		
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
			long tripId = db.insertOrThrow(TABLE_TRIP, null, values);
			if(tripId < -0)
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
					
					long success = db.insertOrThrow(TABLE_READING, null, readingValues);
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
		Cursor selectionCursor = db.query(TABLE_TRIP, selectedColumns, "startDateTime = ?", whereDateTimeEQ, null, null, null);
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
		Cursor selectionOfReadings = db.query(TABLE_READING, selectedReadingColumns, "trip = ?", whereTripEQ, null, null, null);
		
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
	
	/**
	 * This will delete all content in the database.
	 */
	public void flushDatabase(){
		final String deleteAllContentIN = "DELETE FROM ";
		try{
			db.beginTransaction();
			db.execSQL(deleteAllContentIN + TABLE_TRIP );
			db.execSQL(deleteAllContentIN + TABLE_READING);
			db.setTransactionSuccessful();
		}
		catch(SQLException e){
			Log.e(Constants.LOGTAG, "Tried to flush the database");
		}
		finally{
			db.endTransaction();
		}
		
	}
	
	public ArrayList<Trip> selectAllTrips() {
		ArrayList<Trip> trips = new ArrayList<Trip>();
		String[] selectedTripColumns = {"startDateTime"};
		Cursor returnedTrips = null;
		
		db.beginTransaction();
		try{
			returnedTrips = db.query(TABLE_TRIP, selectedTripColumns, null, null, null, null, null);
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
	
	private static class DBOpenHelper extends SQLiteOpenHelper{
		
		public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			// Just call the super method.
			super(context, DB_NAME, factory, DB_VERSION);
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {
			try{
				db.beginTransaction();
				db.execSQL("CREATE TABLE " + DBHelper.TABLE_TRIP + " (id INTEGER PRIMARY KEY AUTOINCREMENT, startDateTime DATETIME NOT NULL); ");
				db.execSQL("CREATE TABLE " + DBHelper.TABLE_READING + " (id INTEGER PRIMARY KEY AUTOINCREMENT, trip INTEGER NOT NULL, dateTime DATETIME NOT NULL, longitude LONG NOT NULL, latitude LONG NOT NULL, altitude LONG, mood SMALLINT);");
				db.execSQL("CREATE INDEX TripReading on " + DBHelper.TABLE_READING + " (trip DESC, id ASC);");
				db.setTransactionSuccessful();
			}
			catch (SQLException e) {
				Log.e(Constants.LOGTAG , DBHelper.CLASSNAME, e);
				//We should also notify the user that the program cannot save data.
			}
			finally{
				db.endTransaction();
			}
			
		}
		
		@Override
		public void onOpen(final SQLiteDatabase db){
			super.onOpen(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			//Start by tearing down the tables
			try{
			db.beginTransaction();
			db.execSQL("DROP TABLE IF EXISTS " + DBHelper.TABLE_TRIP + "; ");
			db.execSQL("DROP TABLE IF EXISTS " + DBHelper.TABLE_READING + "; ");
			db.setTransactionSuccessful();
			}
			finally{
				db.endTransaction();
			}
			this.onCreate(db);			
		}
		
	}

}
