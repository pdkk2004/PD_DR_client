package com.dp.odls.accelorator;

import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

public class SimulatedAccelerometer {
	
    private Context CONTEXT;
    private AccelerometerDelegate accelerometerDelegate;
    private SensorManagerSimulator sensorManager;
    private float threshold = 0.2f;      //default value of threshold
    private int interval = 1000;         //default value of interval
    private int rate = SensorManager.SENSOR_DELAY_GAME;   
    private Handler handler;
    
	private Sensor sensor;
    private boolean running;
    private String elapsedTime;
    
    private SensorEventListener sensorListener = new SensorEventListener() { 
        private float x = 0;
        private float y = 0;
        private float z = 0;
 
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
 
        public void onSensorChanged(SensorEvent event) {
        	// use the event timestamp as reference
        	// so the manager precision won't depends 
        	// on the AccelerometerListener implementation
        	// processing time

        	x = event.values[0];
        	y = event.values[1];
        	z = event.values[2];
        	
        	elapsedTime = event.time;
        	Log.d(this.getClass().getName(), elapsedTime);

        	// if not interesting in shake events
        	// just remove the whole if then else bloc
//        	if (lastUpdate == 0) {
//        		lastUpdate = now;
//        		lastShake = now;
//        		lastX = x;
//        		lastY = y;
//        		lastZ = z;
//        	} else {
//        		timeDiff = now - lastUpdate;
//        		if (timeDiff > 0) {
//        			force = Math.abs(x + y + z - lastX - lastY - lastZ) 
//        			/ timeDiff;
//        			if (force > threshold) {
//        				if (now - lastShake >= interval) {
//        					// trigger shake event
//        					accelerometerDelegate.onShake(force);
//        				}
//        				lastShake = now;
//        			}
//        			lastX = x;
//        			lastY = y;
//        			lastZ = z;
//        			lastUpdate = now;
//        		}
//        	}
        	if(accelerometerDelegate != null)
        		accelerometerDelegate.onAccelerationChanged(x, y, z);
        }
    };
         
    public SimulatedAccelerometer(Context context) {
		super();
		CONTEXT = context;
		sensor = getSensor(context);
		accelerometerDelegate = null;
		handler = null;
	}
    
    private Sensor getSensor(Context context) {
    	Sensor s = null;
    	
    	sensorManager = SensorManagerSimulator.getSystemService(context, Context.SENSOR_SERVICE);
    	sensorManager.connectSimulator();

    	s = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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

	public void setAccelerometerDelegate(AccelerometerDelegate delegate) {
		this.accelerometerDelegate = delegate;
	}
	
	public void removeRegistryAccelerometerDelegate() {
		accelerometerDelegate = null;
	}

	public boolean start() {
		if(accelerometerDelegate != null) {
			running = true;
			sensorManager.registerListener(sensorListener, sensor, rate);
		}
		else running = false;
		return running;

    }
 
    public void stop() {
    	if(running == true && accelerometerDelegate != null) {
    		running = false;
    		sensorManager.unregisterListener(sensorListener);
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
