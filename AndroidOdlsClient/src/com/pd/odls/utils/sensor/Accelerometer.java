package com.pd.odls.utils.sensor;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

/**
 * Obsoleted. Used MotionSensor instead, which provides the acceleration and orientation
 * monitoring. 
 * @author Pan
 *
 */
public class Accelerometer {
	
    private Context CONTEXT;
    private SensorDelegate accelerometerDelegate;
    private SensorManager sensorManager;
    private float threshold = 0.2f;      //default value of threshold
    private int interval = 1000;         //default value of interval
    private int rate = SensorManager.SENSOR_DELAY_FASTEST;   
    private Handler handler;
    
	private Sensor sensor;
    private boolean running;
    
    private SensorEventListener sensorListener = new SensorEventListener() { 
        private float x = 0;
        private float y = 0;
        private float z = 0;
 
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
 
        public void onSensorChanged(SensorEvent event) {
        	x = event.values[0];
        	y = event.values[1];
        	z = event.values[2];
        	
        	if(accelerometerDelegate != null)
        		accelerometerDelegate.onSensedValueChanged(x, y, z);
        }
    };
         
    public Accelerometer(Context context) {
		super();
		CONTEXT = context;
		sensor = getSensor(context);
		accelerometerDelegate = null;
		handler = null;
	}
    
    private Sensor getSensor(Context context) {
    	Sensor s = null;
    	
    	sensorManager = (SensorManager)CONTEXT.
			getSystemService(Context.SENSOR_SERVICE);

    	List<Sensor> sensors = sensorManager.getSensorList(
    			Sensor.TYPE_ACCELEROMETER);
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

	public void setDelegate(SensorDelegate delegate) {
		this.accelerometerDelegate = delegate;
	}
	
	public void removeRegistryAccelerometerDelegate() {
		accelerometerDelegate = null;
	}

	public boolean start() {
		if(accelerometerDelegate != null) {
			running = true;
			sensorManager.registerListener(sensorListener, sensor, rate, handler);			
		}
		else running = false;
		return running;

    }
 
    public void stop() {
    	if(running == true && accelerometerDelegate != null) {
    		running = false;
    		sensorManager.unregisterListener(sensorListener, sensor);
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
