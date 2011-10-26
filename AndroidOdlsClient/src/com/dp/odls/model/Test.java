package com.dp.odls.model;

import java.sql.Date;
import java.sql.Time;

import android.database.Cursor;

import com.dp.odls.sqlite.OdlsDbAdapter;


/**
 * Test is for representing on PD motion test. 
 * @author Pan
 *
 */
public class Test {
	
	public static int TEST_HAND_TREMOR_LEFT = 1;
	public static int TEST_HAND_TREMOR_RIGHT = 2;
	public static int TEST_LEG_TREMOR_LEFT = 3;
	public static int TEST_LEG_TREMOR_RIGHT = 4;
	public static int TEST_FINGER_TAPPING_LEFT = 5;
	public static int TEST_FINGER_TAPPING_RIGHT = 6;
	public static int TEST_GAIT = 7;
	
	public static String TEST_TYPES[] = {"Unknown", "L Hand Tremor", "R Hand Tremor",
		"L Leg Tremor", "R Leg Tremor", "L Finger Tapping", "R Finger Tapping",
		"Gait"};
	
	//Test information
	private String testerID;
	private Integer testID;
	private Integer type;
	private Date date;
	private Time beginTime;
	private Time endTime;
	private Integer duration;
	private String explaination;
	private Integer sampleRate;
	private Integer scale;

	public String getTesterID() {
		return testerID;
	}



	public Date getDate() {
		return date;
	}



	public void setDate(Date date) {
		this.date = date;
	}



	public void setBeginTime(Time beginTime) {
		this.beginTime = beginTime;
	}



	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}



	public void setTesterID(String testerID) {
		this.testerID = testerID;
	}



	public Time getBeginTime() {
		return beginTime;
	}



	public Time getEndTime() {
		return endTime;
	}



	public Integer getTestID() {
		return testID;
	}



	public void setTestID(Integer testID) {
		this.testID = testID;
	}



	public Integer getType() {
		return type;
	}



	public void setType(Integer type) {
		this.type = type;
	}


	public Integer getDuration() {
		return duration;
	}



	public void setDuration(Integer duration) {
		this.duration = duration;
	}



	public String getExplaination() {
		return explaination;
	}



	public void setExplaination(String explaination) {
		this.explaination = explaination;
	}



	public Integer getSampleRate() {
		return sampleRate;
	}



	public void setSampleRate(Integer sampleRate) {
		this.sampleRate = sampleRate;
	}



	public Integer getScale() {
		return scale;
	}



	public void setScale(Integer scale) {
		this.scale = scale;
	}

	
	public void instantiateTest(Cursor c) {
		int columnIndex = -1;
		
		//fill testerID 
		columnIndex = c.getColumnIndex(OdlsDbAdapter.FIELD_TESTER_ID);
		if(!c.isNull(columnIndex)) 
			setTesterID(c.getString(columnIndex));
		else 
			setTesterID(null);
		
		//fill testID 
		columnIndex = c.getColumnIndex(OdlsDbAdapter.FIELD_TEST_ID);
		if(!c.isNull(columnIndex)) 
			setTestID(c.getInt(columnIndex));
		else 
			setTestID(null);
		
		//fill type
		columnIndex = c.getColumnIndex(OdlsDbAdapter.FIELD_TYPE);
		if(!c.isNull(columnIndex)) 
			setType(c.getInt(columnIndex));
		else 
			setType(null);
		
		//fill test date
		columnIndex = c.getColumnIndex(OdlsDbAdapter.FIELD_DATE);
		if(!c.isNull(columnIndex)) {
			Date d = new Date(c.getLong(columnIndex));
			setDate(d);
		}
		else
			setDate(null);
		
		//fill test beginTime
		columnIndex = c.getColumnIndex(OdlsDbAdapter.FIELD_BEGIN_TIME);
		if(!c.isNull(columnIndex)) {
			Time t = new Time(c.getLong(columnIndex));
			setBeginTime(t);
		}
		else 
			setBeginTime(null);
		
		//fill test endTime
		columnIndex = c.getColumnIndex(OdlsDbAdapter.FIELD_END_TIME);
		if(!c.isNull(columnIndex)) {
			Time t = new Time(c.getLong(columnIndex));
			setEndTime(t);
		}
		else 
			setEndTime(null);
		
		//fill duration
		columnIndex = c.getColumnIndex(OdlsDbAdapter.FIELD_DURATION);
		if(!c.isNull(columnIndex)) 
			setDuration(c.getInt(columnIndex));
		else 
			setDuration(null);
		
		//fill explanation
		columnIndex = c.getColumnIndex(OdlsDbAdapter.FIELD_EXPLANATION);
		if(!c.isNull(columnIndex)) 
			setExplaination(c.getString(columnIndex));
		else 
			setExplaination(null);
		
		//fill sampleRate
		columnIndex = c.getColumnIndex(OdlsDbAdapter.FIELD_SAMPLE_RATE);
		if(!c.isNull(columnIndex)) 
			setSampleRate(c.getInt(columnIndex));
		else 
			setSampleRate(null);
		
		
		//fill scale
		columnIndex = c.getColumnIndex(OdlsDbAdapter.FIELD_SCALE);
		if(!c.isNull(columnIndex)) 
			setScale(c.getInt(columnIndex));
		else 
			setScale(null);
	}
	
	public String[] getTestTypes() {
		return TEST_TYPES;
	}
}
