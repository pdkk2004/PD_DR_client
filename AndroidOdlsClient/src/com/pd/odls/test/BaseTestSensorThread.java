package com.pd.odls.test;

import android.os.Handler;

import com.dp.odls.accelorator.Accelerometer;

public class BaseTestSensorThread extends Thread {
	private Accelerometer testSensor;

	public BaseTestSensorThread(Accelerometer accelerometer) {
		this.testSensor = accelerometer;
	}
	
	@Override
	public void run() {
		Handler handler = new Handler() {
			
		};
		testSensor.setHandler(handler);
		testSensor.start();
	}
	
	
}
