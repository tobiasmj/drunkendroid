package itu.dd.client.tech;

import android.content.Context;
import android.content.res.Resources;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import itu.dd.client.R;

/**
 * This class is inspired by the helper class DBHelper described in 
 * "Unlocking Android 2009", by W. Frank Ableson, Charlie Collins, Robi Sen.
 * <br>
 * The class takes care of the technical communication with the sqlite3 database
 */
public class DbHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "drunkendroid";
	public static final Integer DB_VERSION = 15;
	public static final String TABLE_TRIP = "Trip";
	public static final String TABLE_EVENT = "Event";
	public static final String CLASSNAME = "DBHelper";
	
	private static DbHelper helper;
	private SQLiteDatabase db;
	
	public static DbHelper getInstance(Context context){
		if(helper == null){
			helper = new DbHelper(context);
		}
		return helper;
	}
	
	public SQLiteDatabase getDBInstance(){
		establishDb();
		return db;
	}
	
	private DbHelper(Context context) {
		// Just call the super method.
		super(context, DB_NAME, null, DB_VERSION);
		this.establishDb();
	}

	
	private void establishDb(){
		if( this.db == null)
			this.db = this.getWritableDatabase();
	}
	
	public void cleanup(){
		if(this.db != null){
			this.db.close();
			this.db = null;
		}		
	}

	
	/**
	 * This will delete all content in the database.
	 */
	public void flushDatabase(){
		final String deleteAllContentIN = "DELETE FROM ";
		try{
			db.beginTransaction();
			db.execSQL(deleteAllContentIN + TABLE_TRIP );
			db.execSQL(deleteAllContentIN + TABLE_EVENT);
			db.setTransactionSuccessful();
		}
		catch(SQLException e){
			Log.e(Resources.getSystem().getString(R.string.log_tag), "Tried to flush the database");
		}
		finally{
			db.endTransaction();
		}
		
	}
	
	@Override
	public void onCreate(final SQLiteDatabase db) {
		try{
			db.beginTransaction();
			db.execSQL("CREATE TABLE " + DbHelper.TABLE_TRIP + " (id INTEGER PRIMARY KEY AUTOINCREMENT, startDateTime LONG NOT NULL, active BOOLEAN NOT NULL, foreignId LONG, online BOOLEAN, name VARCHAR);");
			db.execSQL("CREATE TABLE " + DbHelper.TABLE_EVENT + " (id INTEGER PRIMARY KEY AUTOINCREMENT, trip INTEGER NOT NULL, dateTime LONG NOT NULL, longitude DOUBLE, latitude DOUBLE, altitude LONG, mood SMALLINT, sender VARCHAR, receiver VARCHAR, message VARCHAR, online BOOLEAN);");
			db.execSQL("CREATE INDEX TripEvent on " + DbHelper.TABLE_EVENT + " (trip DESC, id ASC);");
			db.setTransactionSuccessful();
		}
		catch (SQLException e) {
			Log.e(Resources.getSystem().getString(R.string.log_tag), DbHelper.CLASSNAME, e);
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
		db.execSQL("DROP TABLE IF EXISTS " + "Reading" + "; "); //This should be called event by now
		db.execSQL("DROP TABLE IF EXISTS " + DbHelper.TABLE_TRIP + "; ");
		db.execSQL("DROP TABLE IF EXISTS " + DbHelper.TABLE_EVENT + "; ");
		db.setTransactionSuccessful();
		}
		finally{
			db.endTransaction();
		}
		this.onCreate(db);			
	}
}
