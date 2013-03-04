package com.icfoss.events;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "icfossEventsManager";
	private static final String TABLE_NAME = "events";
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "topic";
	private static final String KEY_DATE = "date";
	
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_NAME+" TEXT,"+KEY_DATE+" TEXT"+")";
		db.execSQL(CREATE_TABLE);
		
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

		// Create tables again
		onCreate(db);
		
	}

	// Adding new event
		void addEvent(String event, String date) {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_NAME, event); //  event name
			values.put(KEY_DATE, date); // date

			// Inserting Row
			db.insert(TABLE_NAME, null, values);
			db.close(); // Closing database connection
		}
	
}