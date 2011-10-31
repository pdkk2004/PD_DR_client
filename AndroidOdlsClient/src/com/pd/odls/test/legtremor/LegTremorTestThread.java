package com.pd.odls.test.legtremor;

import java.io.DataOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.pd.odls.accelorator.AccelerometerDelegate;
import com.pd.odls.accelorator.SimulatedAccelerometer;
import com.pd.odls.test.BaseTestPanel;
import com.pd.odls.test.BaseTestThread;

public class LegTremorTestThread extends BaseTestThread {
	
	private static final String TAG = LegTremorTestThread.class.getSimpleName();

	private Context context;
	private int offSetX;
	private int offSetY;
	
	//TODO change SimulatedAccelerometer to Accelerometer in production
//	private Accelerometer accelerometer;
	private SimulatedAccelerometer accelerometer;
	
	private DataOutputStream dout;
	private Handler handler;

	
	public LegTremorTestThread(BaseTestPanel testPanel,
			SurfaceHolder surfaceHolder, DataOutputStream dout, Context context, Handler handler) {
		super(testPanel, surfaceHolder);
		this.offSetX = 0;
		this.offSetY = 0;
		this.context = context;
		this.dout = dout;
		this.handler = handler;
	}
	
	public void initializeAccelerometer() {
		if(context != null) {
			//TODO change SimulatedAccelerometer to Accelerometer in production			
//			accelerometer = new Accelerometer(this.context);
			accelerometer = new SimulatedAccelerometer(this.context);
			
			accelerometer.setAccelerometerDelegate(delegate);
		}
	}
	
	/**
	 * Delegate operation to deal with sensed data from accelerometer sensor
	 */
	private AccelerometerDelegate delegate = new AccelerometerDelegate() {

		public void onAccelerationChanged(float x, float y, float z) {
			try {
				offSetX = Math.round(x);
				offSetY = Math.round(y);
				dout.writeFloat(x);
				dout.writeFloat(y);
				dout.writeFloat(z);
				dout.flush();
			}
			catch(IOException e) {
				Log.e(this.getClass().getName(), e.getMessage());
			}
			
			if(dout.size() >= 3 * 4 * 5 * 1000)
				handler.sendEmptyMessage(LegTremorTestActivity.MSG_BUFFER_FULL);				
		}

		public void onShake(float force) {
			
		}
		
	};
	
	@Override
	public void run() {
		Canvas canvas = null; 
		Log.i(TAG, "Starting test");
		
		while(isRunning) {
			if(!this.surfaceHolder.getSurface().isValid())
				continue;
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (this.surfaceHolder) {
					this.testPanel.update(offSetX, offSetY);
					this.testPanel.render(canvas);
				}
			}

			finally {
				if(canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}
			
			//check whether it is paused
			synchronized (this) {
				if(pause) {
					try {
						wait();
					}
					catch (InterruptedException e) {
						Log.w(TAG, e.getMessage());
					}
				}
			}
		}
		Log.i(TAG, "Stop test");
	}

	@Override
	public void setRunning(boolean running) {
		super.setRunning(running);
		if(accelerometer == null)
			initializeAccelerometer();
		if(accelerometer != null) {
			if(isRunning)
				accelerometer.start();
			else
				accelerometer.stop();
		}	
	}

	@Override
	public void changePauseStatus() {
		super.changePauseStatus();
		if(accelerometer != null) {
			if(!pause)
				accelerometer.start();
			else
				accelerometer.stop();
		}
	}
	
	
}
