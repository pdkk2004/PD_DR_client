package com.pd.odls.utils.sensor;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

public class MotionSensor {
	
    private Context CONTEXT;
    private SensorDelegate orientationDelegate;
    private SensorDelegate accelerationDelegate;
    private SensorManager sensorManager;
    private float threshold = 0.2f;      //default value of threshold
    private int interval = 1000;         //default value of interval
    private int rate = SensorManager.SENSOR_DELAY_FASTEST;   
    private Handler handler;
    
	private Sensor sensorAcc;
	private Sensor sensorMeg;
	
    private boolean running;
    
    //declare sensorListener to listen to motion sensor events
    private SensorEventListener sensorListener = new SensorEventListener() {
        private float[] mGravity;
        float[] mGeomagnetic;   
 
        private float x = 0;
        private float y = 0;
        private float z = 0;
        
		private float R[] = new float[9];
		private float I[] = new float[9];
 
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
 
        /**
         * onSensorChanged delegate method to collect linear acceleration and orientation
         */

        public void onSensorChanged(SensorEvent event) {
        	if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            	
        		//obtain gravity
        		mGravity = event.values;
        		
    			//deal with acceleration
        		if(accelerationDelegate != null) {
        			accelerationDelegate.onSensedValueChanged(mGravity[0],
        					mGravity[1],
        					mGravity[2]);
        		}
        		
        		//obtain orientation acceleration and orientation
            	if (mGravity != null && mGeomagnetic != null) {
            		SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);           			               		
            		//float trueacceleration[] = new float[3];
            		//float accelerometervalues[] = new float[3];
            		//float orientationvalues[] = new float[3];
            		//trueacceleration[0] =(float) (accelerometervalues[0]*(Math.cos(orientationvalues[2])*Math.cos(orientationvalues[0])+Math.sin(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.sin(orientationvalues[0])) + accelerometervalues[1]*(Math.cos(orientationvalues[1])*Math.sin(orientationvalues[0])) + accelerometervalues[2]*(-Math.sin(orientationvalues[2])*Math.cos(orientationvalues[0])+Math.cos(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.sin(orientationvalues[0])));
            		//trueacceleration[1] = (float) (accelerometervalues[0]*(-Math.cos(orientationvalues[2])*Math.sin(orientationvalues[0])+Math.sin(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.cos(orientationvalues[0])) + accelerometervalues[1]*(Math.cos(orientationvalues[1])*Math.cos(orientationvalues[0])) + accelerometervalues[2]*(Math.sin(orientationvalues[2])*Math.sin(orientationvalues[0])+ Math.cos(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.cos(orientationvalues[0])));
            		//trueacceleration[2] = (float) (accelerometervalues[0]*(Math.sin(orientationvalues[2])*Math.cos(orientationvalues[1])) + accelerometervalues[1]*(-Math.sin(orientationvalues[1])) + accelerometervalues[2]*(Math.cos(orientationvalues[2])*Math.cos(orientationvalues[1])));

        			float orientation[] = new float[3]; 
        			SensorManager.getOrientation(R, orientation); 
        			x = orientation[0]; // orientation azimut
        			y = orientation[1]; // orientation pitch
        			z = orientation[2]; // orientation roll
                	
        			//deal with orientation
        			if(orientationDelegate != null)
                		orientationDelegate.onSensedValueChanged(x, y, z);
            	}
        	}        	
        	else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
        		mGeomagnetic = event.values;
        	}
        	else {
        		
        	}
        }
        
        /**
         * onSensorChanged delegate method to collect linear acceleration in device coordinate system,
         * and linear acceleration in world coordinate system
         */
        /*
        public void onSensorChanged(SensorEvent event) {
        	if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            	
        		//obtain gravity
        		mGravity = event.values;
        		
    			//deal with acceleration
        		if(accelerationDelegate != null) {
        			accelerationDelegate.onSensedValueChanged(mGravity[0],
        					mGravity[1],
        					mGravity[2]);
        		}
        		
        		//update rotation matrix
            	if (mGravity != null && mGeomagnetic != null) {
            		float[] rotationMatrix = new float[9];
            		boolean success = SensorManager.getRotationMatrix(rotationMatrix, I, mGravity, mGeomagnetic);
            		if (success) {
            			System.arraycopy(rotationMatrix, 0, R, 0, rotationMatrix.length);
            		}
            	}
            	
        		//transfer gravity in device coordinate system to world coordinate system
            	float X = R[0] * mGravity[0] + R[1] * mGravity[1] + R[2] * mGravity[2];
            	float Y = R[3] * mGravity[0] + R[4] * mGravity[1] + R[5] * mGravity[2];
            	float Z = R[6] * mGravity[0] + R[7] * mGravity[1] + R[8] * mGravity[2];
            	
        		//deal with acceleration in world coordinate system
        		if(orientationDelegate != null)
        			orientationDelegate.onSensedValueChanged(X, Y, Z);   
        		
        	}        	
        	else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
        		mGeomagnetic = event.values;
        	}
        	else {
        		
        	}
        }   
         */     
    };

         
    public MotionSensor(Context context) {
		super();
		CONTEXT = context;
		sensorAcc = getSensor(context, Sensor.TYPE_ACCELEROMETER);
		sensorMeg = getSensor(context, Sensor.TYPE_MAGNETIC_FIELD);
		orientationDelegate = null;
		handler = null;
	}
    
    private Sensor getSensor(Context context, int type) {
    	Sensor s = null;
    	
    	sensorManager = (SensorManager)CONTEXT.
			getSystemService(Context.SENSOR_SERVICE);

    	List<Sensor> sensors = sensorManager.getSensorList(type);
    	if (sensors.size() > 0) {
    		s = sensors.get(0);
    	}
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
	
	public SensorManager getSensorManager() {
		return sensorManager;
	}

	public void setSensorManager(SensorManager sensorManager) {
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
		accelerationDelegate = null;
	}

	public boolean start() {
		if(orientationDelegate != null) {
			running = true;
			sensorManager.registerListener(sensorListener, sensorMeg, rate, handler);			
		}
		if(accelerationDelegate != null) {
			running = true;
			sensorManager.registerListener(sensorListener, sensorAcc, rate, handler);						
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
}
