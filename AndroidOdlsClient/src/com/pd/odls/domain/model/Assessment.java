package com.pd.odls.domain.model;

import java.io.StringWriter;
import java.sql.Date;
import java.sql.Time;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.util.Base64;
import android.util.Xml;

import com.pd.odls.R;
import com.pd.odls.util.SupportingUtils;
import com.pd.odls.utils.sqlite.OdlsDbAdapter;


/**
 * Test is for representing on PD motion test. 
 * @author Pan
 *
 */
public class Assessment {
	
	public static int TEST_HAND_TREMOR_LEFT = 1;
	public static int TEST_HAND_TREMOR_RIGHT = 2;
	public static int TEST_LEFT_TURN = 3;
	public static int TEST_RIGHT_TURN = 4;
	public static int TEST_FINGER_TAPPING_LEFT = 5;
	public static int TEST_FINGER_TAPPING_RIGHT = 6;
	public static int TEST_GAIT = 7;
	
	public static String TEST_TYPES[] = {"Unknown", "Left Hand Tremor", "Rignt Hand Tremor",
		"Left Turn", "Right Turn", "Left Finger Tap", "Right Finger Tap",
		"Gait"};
	
	public static int TEST_TYPE_MAP[] = {R.drawable.alert_dialog_icon, R.drawable.hand_tremor,
		R.drawable.hand_tremor, R.drawable.leg_tremor, R.drawable.leg_tremor, 
		R.drawable.finger_tapping, R.drawable.finger_tapping, R.drawable.gait_difficulty};
	
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
	private byte[] data1;
	private byte[] data2;
	
	
	public byte[] getData1() {
		return data1;
	}

	public void setData1(byte[] data1) {
		this.data1 = data1;
	}

	public byte[] getData2() {
		return data2;
	}

	public void setData2(byte[] data2) {
		this.data2 = data2;
	}

	public Assessment() {
		super();
	}
	
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
	
	/**
	 * Instantiate Test instance from directly given value
	 * @param testerID
	 * @param testID
	 * @param type
	 * @param date
	 * @param beginTime
	 * @param endTime
	 * @param duration
	 * @param explaination
	 * @param sampleRate
	 * @param scale
	 * @param data1
	 * @param data2
	 */
	public void instantiateTest(String testerID, Integer testID,
			Integer type, Long date, Long beginTime, Long endTime, Integer duration, String explaination,
			Integer sampleRate, Integer scale, byte[] data1, byte[] data2) {
		this.setTesterID(testerID);
		this.setTestID(testID);
		this.setType(type);
		
		if(date != null) {
			this.setDate(new Date(date));
		}
		else this.setDate(null);
		
		if(beginTime != null) {
			this.setBeginTime(new Time(beginTime));
		}
		else this.setBeginTime(null);
		
		if(endTime != null) {
			this.setEndTime(new Time(endTime));
		}
		else this.setEndTime(null);
		
		this.setDuration(duration);
		this.setExplaination(explaination);
		this.setSampleRate(sampleRate);
		this.setScale(scale);
		this.setData1(data1);
		this.setData2(data2);
		
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
	
	//transfer a test case to XML artifact
	public String toXML(Context context) throws Exception{
		if(this.testerID == null || this.testerID == null) return null;
		
		String namespace = "http://www.mapc.com/odls";
		String prefix = "odls";
		XmlSerializer xmlSerializer = Xml.newSerializer(); 
		StringWriter writer = new StringWriter();
		
	    xmlSerializer.setOutput(writer);
	    // start DOCUMENT
	    xmlSerializer.startDocument("UTF-8", true);
	    // open tag: <patient>
	    xmlSerializer.setPrefix(prefix, namespace);
	    xmlSerializer.startTag(namespace, "patient");
	    xmlSerializer.attribute(namespace, "id", this.testerID);

	    // open tag: <test>
	    xmlSerializer.startTag(namespace, "test");
	    xmlSerializer.attribute(namespace, "id", String.valueOf(this.testID));
	    xmlSerializer.attribute(namespace, "type", TEST_TYPES[type]);
	    xmlSerializer.attribute(namespace, "date", this.date == null ? namespace : SupportingUtils.dateFormatter.format(date));
	    xmlSerializer.attribute(namespace, "begin", this.beginTime == null ? namespace : SupportingUtils.timeFormatter.format(beginTime));
	    xmlSerializer.attribute(namespace, "end", this.endTime == null ? namespace : SupportingUtils.timeFormatter.format(this.endTime));
	    xmlSerializer.attribute(namespace, "last", this.duration == null ? namespace : String.valueOf(this.duration));
	    xmlSerializer.attribute(namespace, "sample rate", this.sampleRate == null ? namespace : String.valueOf(this.sampleRate));
	    xmlSerializer.attribute(namespace, "scale", this.scale == null ? namespace : String.valueOf(this.scale));

	
	    // open tag: <data>
	    xmlSerializer.startTag(namespace, "data");
	    xmlSerializer.attribute(namespace, "type", TEST_TYPES[type]);
	    xmlSerializer.attribute(namespace, "num", "1");
	    String sData = Base64.encodeToString(data1, Base64.DEFAULT);
	    xmlSerializer.text(sData);
	    // close tag: </data>
	    xmlSerializer.endTag(namespace, "data");	
	    
	    // open tag: <data>
	    xmlSerializer.startTag(namespace, "data");
	    xmlSerializer.attribute(namespace, "type", TEST_TYPES[type]);
	    xmlSerializer.attribute(namespace, "num", "2");
	    sData = Base64.encodeToString(data2, Base64.DEFAULT);
	    xmlSerializer.text(sData);
	    // close tag: </data>
	    xmlSerializer.endTag(namespace, "data");

	    // open tag: <explanation>
	    xmlSerializer.startTag(namespace, "explanation");
	    xmlSerializer.text(explaination == null ? "" : explaination);
	    // close tag: <explanation>
	    xmlSerializer.endTag(namespace, "explanation");

	    // close tag: </test>
	    xmlSerializer.endTag(namespace, "test");
	    // close tag: </patient>
	    xmlSerializer.endTag(namespace, "patient");

	    // end DOCUMENT
	    xmlSerializer.endDocument();
	    xmlSerializer.flush();

		return writer.toString();
	}
	
	public String[] getTestTypes() {
		return TEST_TYPES;
	}
	
	public int getTestIcon() {
		return TEST_TYPE_MAP[this.getType()];
	}
}
