package com.dp.odls.accelorator;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

public class Accelerometer {
	
    private Context CONTEXT;
    private AccelerometerDelegate accelerometerDelegate;
    private SensorManager sensorManager;
    private float threshold = 0.2f;      //default value of threshold
    private int interval = 1000;         //default value of interval
    private int rate = SensorManager.SENSOR_DELAY_GAME;   
    private Handler handler;
    
	private Sensor sensor;
    private boolean running;
    
    private SensorEventListener sensorListener = new SensorEventListener() {
        private long now = 0;
        private long timeDiff = 0;
        private long lastUpdate = 0;
        private long lastShake = 0;
 
        private float x = 0;
        private float y = 0;
        private float z = 0;
        private float lastX = 0;
        private float lastY = 0;
        private float lastZ = 0;
        private float force = 0;
 
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
 
        public void onSensorChanged(SensorEvent event) {
        	// use the event timestamp as reference
        	// so the manager precision won't depends 
        	// on the AccelerometerListener implementation
        	// processing time
        	now = event.timestamp;

        	x = event.values[0];
        	y = event.values[1];
        	z = event.values[2];

        	// if not interesting in shake events
        	// just remove the whole if then else bloc
        	if (lastUpdate == 0) {
        		lastUpdate = now;
        		lastShake = now;
        		lastX = x;
        		lastY = y;
        		lastZ = z;
        	} else {
        		timeDiff = now - lastUpdate;
        		if (timeDiff > 0) {
        			force = Math.abs(x + y + z - lastX - lastY - lastZ) 
        			/ timeDiff;
        			if (force > threshold) {
        				if (now - lastShake >= interval) {
        					// trigger shake event
        					accelerometerDelegate.onShake(force);
        				}
        				lastShake = now;
        			}
        			lastX = x;
        			lastY = y;
        			lastZ = z;
        			lastUpdate = now;
        		}
        	}
        	if(accelerometerDelegate != null)
        		accelerometerDelegate.onAccelerationChanged(x, y, z);
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

	public void setAccelerometerDelegate(AccelerometerDelegate delegate) {
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
