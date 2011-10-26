package com.dp.odls.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class OdlsDbAdapter {
	// Database fields
	public static final String FIELD_TESTER_ID = "tester_id";
	public static final String FIELD_TEST_ID = "test_id";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_DATE = "date";
	public static final String FIELD_BEGIN_TIME = "begin_time";
	public static final String FIELD_END_TIME = "end_time";
	public static final String FIELD_DURATION = "duration";
	public static final String FIELD_EXPLANATION = "explanation";
	public static final String FIELD_SAMPLE_RATE = "sample_rate";
	public static final String FIELD_SCALE = "scale";
	public static final String FIELD_DATA = "data";

	public static final String DATABASE_TABLE = "tests";
	
	//Database fields array except FIELD_DATA
	private static final String[] FIELDS = {FIELD_TESTER_ID, FIELD_TEST_ID, 
			FIELD_TYPE, FIELD_DATE, FIELD_BEGIN_TIME, FIELD_END_TIME,
			FIELD_DURATION, FIELD_EXPLANATION, FIELD_SAMPLE_RATE, FIELD_SCALE, FIELD_DATA};
	
	private Context context;
	private SQLiteDatabase database;
	private OdlsOpenDbHelper dbHelper;

	public OdlsDbAdapter(Context context) {
		this.context = context;
	}

	public void open() throws SQLException {
		try {
			dbHelper = new OdlsOpenDbHelper(context);
			database = dbHelper.getWritableDatabase();
		}
		catch(SQLException e) {
			throw e;
		}
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new test If the test is successfully created return the new
	 * test_id for that note, otherwise return a -1 to indicate failure.
	 */
	public long createTest(String testerID, Integer testID,
			Integer type, Long date, Long beginTime, Long endTime, Integer duration, String explaination,
			Integer sampleRate, Integer scale, byte[] data) 
	{
		ContentValues initialValues = createContentValues(testerID, testID,
				type, date, beginTime, endTime, duration, explaination, sampleRate, scale,
				data);

		return database.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Update the test
	 */
	public boolean updateTest(String testerID, Integer testID,
			Integer type, Long date, Long beginTime, Long endTime, Integer duration, String explaination,
			Integer sampleRate, Integer scale, byte[] data)
	{
		ContentValues updateValues = createContentValues(testerID, testID,
				type, date, beginTime, endTime, duration, explaination, sampleRate, scale,
				data);

		return database.update(DATABASE_TABLE, updateValues, FIELD_TEST_ID + "="
				+ testID, null) > 0;
	}
	
	/**
	 * Deletes test
	 */
	public boolean deleteTest(int testId) {
		return database.delete(DATABASE_TABLE, FIELD_TEST_ID + "=" + testId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all tests in the database
	 * 
	 * @return Cursor over all tests
	 */
	public Cursor fetchAllTest() throws SQLException {
		return database.query(DATABASE_TABLE, FIELDS, null, null, null,
				null, null);
	}

	/**
	 * Return a Cursor for selected first test
	 */
	public Cursor fetchTest(int testID) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE, FIELDS,
				FIELD_TEST_ID + "=" + testID, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 * Function used to perform INSERT, DELETE, or UPDATE operation on database
	 * @param sql
	 * @return
	 */
	public boolean insertOrDeleteOrUpdateTest(String sql) {
		try {
			database.execSQL(sql);
			return true;
		}
		catch(SQLException e) {
			Log.w(OdlsDbAdapter.class.getName(), e.getMessage(), e);
			return false;
		}
	}
	
	public static String[] getFields() {
		return FIELDS;
	}

	/**
	 * Query test database using SQL query string.
	 * @param sql SQL query string
	 * @return
	 */
	public Cursor fetchTest(String sql) {
		return database.rawQuery(sql, null);
	}

	private ContentValues createContentValues(String testerID, Integer testID,
			Integer type, Long date, Long beginTime, Long endTime, Integer duration, String explaination,
			Integer sampleRate, Integer scale, byte[] data)
	{
		ContentValues values = new ContentValues();
		if(testerID == null) {
			values.putNull(FIELD_TESTER_ID);
		}
		else {
			values.put(FIELD_TESTER_ID, testerID);
		}
		
		if(testerID == null) {
			values.putNull(FIELD_TEST_ID);
		}
		else {
			values.put(FIELD_TEST_ID, testID);
		}
		
		if(testerID == null) {
			values.putNull(FIELD_TYPE);
		}
		else {
			values.put(FIELD_TYPE, type);
		}
		
		if(date == null) {
			values.putNull(FIELD_DATE);
		}
		else {
			values.put(FIELD_DATE, date);
		}
		
		if(testerID == null) {
			values.putNull(FIELD_BEGIN_TIME);
		}
		else {
			values.put(FIELD_BEGIN_TIME, beginTime);
		}
		
		if(testerID == null) {
			values.putNull(FIELD_END_TIME);
		}
		else {
			values.put(FIELD_END_TIME, endTime);
		}
		
		if(testerID == null) {
			values.putNull(FIELD_DURATION);
		}
		else {
			values.put(FIELD_DURATION , duration);
		}
		
		if(testerID == null) {
			values.putNull(FIELD_EXPLANATION);
		}
		else {
			values.put(FIELD_EXPLANATION, explaination);
		}
		
		if(testerID == null) {
			values.putNull(FIELD_SAMPLE_RATE);
		}
		else {
			values.put(FIELD_SAMPLE_RATE, sampleRate);
		}
		
		if(testerID == null) {
			values.putNull(FIELD_SCALE);
		}
		else {
			values.put(FIELD_SCALE, scale);
		}
		
		if(testerID == null) {
			values.putNull(FIELD_DATA);
		}
		else {
			values.put(FIELD_DATA, data);
		}
			
		return values;
	}

	/**
	 * Return whether the odls database is currently opened
	 * @param database
	 * @return
	 */
	public boolean isOnOpen() {
		if ( database != null) 
			return database.isOpen();
		return false;
	}
}
