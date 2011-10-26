package com.dp.odls.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class OdlsOpenDbHelper extends SQLiteOpenHelper  {


	private static final String DATABASE_NAME = "odls.db";
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_TEST_TABLE = "CREATE TABLE IF NOT EXISTS tests" +
			" (tester_id text not null, "
			+ "test_id integer not null,"
			+ "type integer,"
			+ "date integer,"
			+ "begin_time integer,"
			+ "end_time integer,"
			+ "duration integer,"
			+ "explanation text,"
			+ "sample_rate integer,"
			+ "scale integer,"
			+ "data blob,"
			+ "primary key(tester_id, test_id));";
	
	private static final String CREATE_PATIENT_TABLE = "CREATE TABLE IF NOT EXISTS patients" +
			"(tester_id text primary key not null, "
			+ "password text not null"
			+ ");";
		
	public OdlsOpenDbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public OdlsOpenDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		try {
			Log.i(this.getClass().getName(), "Creating table tests");
			database.execSQL(CREATE_TEST_TABLE);
//			database.execSQL(CREATE_PATIENT_TABLE);
		}
		catch(SQLException e) {
			Log.w(OdlsOpenDbHelper.class.getName(), e.getMessage(), e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(OdlsOpenDbHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("Drop table if exist tests");
		database.execSQL(CREATE_TEST_TABLE);
	}
	
}
