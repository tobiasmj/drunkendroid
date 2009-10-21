package itu.malta.drunkendroid.tech;

import itu.malta.drunkendroid.Constants;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

/*
 *This class is heavily inspired by the helper class described in 
 * "Unlocking Android 2009", by W. Frank Ableson, Charlie Collins, Robi Sen
 */
public class DBHelper {
	public static final String DB_NAME = "drunkendroid";
	public static final Integer DB_VERSION = 3;
	public static final String TABLE_TRIP = "Trip";
	public static final String TABLE_READING = "Reading";
	public static final String CLASSNAME = "DBHelper";
	
	private SQLiteDatabase db;
	private final DBOpenHelper dbOpenHelpder;
	
	public DBHelper(final Context context){
		this.dbOpenHelpder = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
	}
	
	private void establishDb(){
		if( this.db == null)
			this.db = this.dbOpenHelpder.getWritableDatabase();
	}
	
	public void cleanup(){
		if(this.db != null){
			this.db.close();
			this.db = null;
		}		
	}
	
	
	
	private static class DBOpenHelper extends SQLiteOpenHelper{
		private static final String TBLS_CREATE = "BEGIN TRANSACTION; "
			+ "CREATE TABLE " + DBHelper.TABLE_TRIP + " (id INTEGER PRIMARY KEY AUTOINCREMENT, startDateTime DATETIME NOT NULL); "
			+ "CREATE TABLE " + DBHelper.TABLE_READING + " (id INTEGER PRIMARY KEY AUTOINCREMENT, trip INTEGER NOT NULL, dateTime DATETIME NOT NULL, longitude LONG NOT NULL, latitude LONG NOT NULL, altitude LONG, mood SMALLINT); "
			+ "CREATE INDEX TripReading on " + DBHelper.TABLE_READING + " (trip DESC, id ASC); "
			+ "COMMIT TRANSACTION;";
		private static final String TBLS_DROP = "BEGIN TRANSACTION;"
			+ "DROP TABLE IF EXISTS " + DBHelper.TABLE_TRIP + "; "
			+ "DROP TABLE IF EXISTS " + DBHelper.TABLE_READING + "; "
			+ "COMMIT TRANSACTION;";
			
		
		public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			// Just call the super method.
			super(context, DB_NAME, factory, DB_VERSION);
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {
			try{
				db.execSQL(DBOpenHelper.TBLS_CREATE);
			}
			catch (SQLException e) {
				Log.e(Constants.LOGTAG , DBHelper.CLASSNAME, e);
				//We should also notify the user that the program cannot save data.
			}
		}
		
		@Override
		public void onOpen(final SQLiteDatabase db){
			super.onOpen(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			db.execSQL(DBOpenHelper.TBLS_DROP);
			this.onCreate(db);			
		}
		
	}
}
