package com.pd.odls.sensor;

import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;

public class SimulatedMotionSensor {
	
    private Context CONTEXT;
    private SensorDelegate orientationDelegate;
    private SensorDelegate accelerationDelegate;
    private SensorManagerSimulator sensorManager;
    private float threshold = 0.2f;      //default value of threshold
    private int interval = 1000;         //default value of interval
    private int rate = SensorManager.SENSOR_DELAY_FASTEST;   
    private Handler handler;
    
	private Sensor sensorAcc;
	private Sensor sensorMeg;
	
    private boolean running;
    private String elapsedTime;
    
    private SensorEventListener sensorListener = new SensorEventListener() { 
 
        private float[] mGravity;
        float[] mGeomagnetic;    
        
    	private float x = 0;
        private float y = 0;
        private float z = 0;
        
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
 
        public void onSensorChanged(SensorEvent event) {
        	System.out.println("Orientation sensor on");

        	if (event.type == Sensor.TYPE_ACCELEROMETER) {
        		mGravity = event.values;
        		if(accelerationDelegate != null) {
        			accelerationDelegate.onSensedValueChanged(mGravity[0],
        					mGravity[1],
        					mGravity[2]);
        		}
        	}
        	if (event.type == Sensor.TYPE_MAGNETIC_FIELD)
        		mGeomagnetic = event.values;
        	
        	if (mGravity != null && mGeomagnetic != null) {
        		float R[] = new float[9];
        		float I[] = new float[9];
        		boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
        		if (success) {
        			float orientation[] = new float[3]; 
        			SensorManager.getOrientation(R, orientation); 
        			x = orientation[0]; // orientation azimut
        			y = orientation[1]; // orientation pitch
        			z = orientation[2]; // orientation roll
                	if(orientationDelegate != null)
                		orientationDelegate.onSensedValueChanged(x, y, z);
        		}
        	}
        }
    };
         
    public SimulatedMotionSensor(Context context) {
		super();
		CONTEXT = context;
		sensorAcc = getSensor(context, Sensor.TYPE_ACCELEROMETER);
		sensorMeg = getSensor(context, Sensor.TYPE_MAGNETIC_FIELD);
		orientationDelegate = null;
		handler = null;
	}
    
    private Sensor getSensor(Context context, int type) {
    	Sensor s = null;
    	
    	sensorManager = SensorManagerSimulator.getSystemService(context, Context.SENSOR_SERVICE);
    	sensorManager.connectSimulator();

    	s = sensorManager.getDefaultSensor(type);
    	return s;
    }
    
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
	
    public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}
	
	public SensorManagerSimulator getSensorManager() {
		return sensorManager;
	}

	public void setSensorManager(SensorManagerSimulator sensorManager) {
		this.sensorManager = sensorManager;
	}

	public void setOriDelegate(SensorDelegate delegate) {
		this.orientationDelegate = delegate;
	}
	
	public void setAccDelegate(SensorDelegate delegate) {
		this.accelerationDelegate = delegate;
	}
	
	public void removeDelegate() {
		orientationDelegate = null;
	}

	public boolean start() {		
		if(orientationDelegate != null) {
			running = true;
			sensorManager.registerListener(sensorListener, sensorMeg, rate);			
		}
		if(accelerationDelegate != null) {
			running = true;
			sensorManager.registerListener(sensorListener, sensorAcc, rate);						
		}
		else running = false;
		return running;

    }
 
    public void stop() {
    	if(running == true) {
    		running = false;
    		if(orientationDelegate != null)
        		sensorManager.unregisterListener(sensorListener, sensorMeg);    		
    		if(accelerationDelegate != null)
    			sensorManager.unregisterListener(sensorListener, sensorAcc);
    	}
    }
    
    public void config(int threshold, int interval) {
    	this.threshold = threshold;
    	this.interval = interval;
    }
 
    public Context getContext() {
        return CONTEXT;
    }

	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
    
    
}
