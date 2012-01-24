package com.pd.odls.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.pd.odls.sensor.MotionSensor;
import com.pd.odls.sensor.SensorDelegate;

public class MotionSensingThread extends BaseTestThread {
	
	private static final String TAG = MotionSensingThread.class.getSimpleName();

	private Context context;
	private int offSetX;
	private int offSetY;
	private int offSetZ;
	private ArrayList<Point> tracePoints = new ArrayList<Point>();
	
	private DrawPattern drawMotionTrace;
	
	//TODO delete following two lines for running on device
//	private Accelerometer accelerometer;     //do not need any more
//	private SimulatedAccelerometer accelerometer;   //do not need any more
	
	//TODO change SimulatedAccelerometer to Accelerometer in production
	private MotionSensor motionSensor;
//	private SimulatedMotionSensor motionSensor;
	
	private DataOutputStream doutAcc;
	private DataOutputStream doutOri;
	
	@SuppressWarnings("unused")
	private Handler handler;
	
	public MotionSensingThread(BaseTestPanel testPanel,
			SurfaceHolder surfaceHolder, DataOutputStream doutAcc, DataOutputStream doutOri,
			Context context, Handler handler) {
		super(testPanel, surfaceHolder);
		this.offSetX = 0;
		this.offSetY = 0;
		this.offSetZ = 0;
		this.context = context;
		this.doutAcc = doutAcc;
		this.doutOri = doutOri;
		this.handler = handler;
	}
	
	
	//TODO Do not need any more, can delete
//	@Override
//	public void initializeAccelerometer() {
//		if(context != null) {
//			//TODO change SimulatedAccelerometer to Accelerometer in production			
//			accelerometer = new Accelerometer(this.context);			
//			accelerometer = new SimulatedAccelerometer(this.context);
//			
//			accelerometer.setDelegate(delegateAcc);
//		}
//	}
	
	public void initializeSensor() {
		if(context != null) {
			//TODO change SimulatedOrientation to Orientation in production			
			motionSensor = new MotionSensor(this.context);	
//			motionSensor = new SimulatedMotionSensor(this.context);
		
			motionSensor.setAccDelegate(delegateAcc);
			motionSensor.setOriDelegate(delegateOri);
		}
	}
	
	/**
	 * Delegate operation to deal with sensed data from accelerometer sensor
	 */
	private SensorDelegate delegateAcc = new SensorDelegate() {

		public void onSensedValueChanged(float x, float y, float z) {
			try {
				offSetX = Math.round(x);
				offSetY = Math.round(y);
				offSetZ = Math.round(z);
				doutAcc.writeFloat(x);
				doutAcc.writeFloat(y);
				doutAcc.writeFloat(z - (float)9.98);
				doutAcc.flush();
				System.out.println("Acceleration:" + x + " " + y + " " + z);
			}
			catch(IOException e) {
				Log.e(this.getClass().getName(), e.getMessage());
			}		
		}

		public void onShake(float force) {
			
		}
		
	};
	
	/**
	 * Delegate operation to deal with sensed data from accelerometer sensor
	 */
	private SensorDelegate delegateOri = new SensorDelegate() {

		public void onSensedValueChanged(float x, float y, float z) {
			try {
				doutOri.writeFloat(x);
				doutOri.writeFloat(y);
				doutOri.writeFloat(z);
				doutOri.flush();
				System.out.println("Orientation:" + x + " " + y + " " + z);
			}
			catch(IOException e) {
				Log.e(this.getClass().getName(), e.getMessage());
			}		
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
					//add moving object position to motion trace, which is stored in ArrayList tracePoints
					tracePoints.add(this.testPanel.update(offSetX, offSetY, offSetZ));
					
					this.testPanel.render(canvas);
				}
			}

			finally {
				if(canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}
			
			//check whether it is paused
			synchronized (this) {
				if(paused) {
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
		Log.i(TAG, "Draw tremor trace");
		if(drawMotionTrace != null)
			drawMotionTrace.draw(canvas);
	}

	@Override
	public void setRunning(boolean running) {
		super.setRunning(running);
//		if(accelerometer == null)
//			initializeAccelerometer();
		if(motionSensor == null)
			initializeSensor();
		
//		if(accelerometer != null) {
//			if(isRunning) {
//				accelerometer.start();
//			}
//			else {
//				accelerometer.stop();
//			}
//		}
		
		if(motionSensor != null) {
			if(isRunning) {
				motionSensor.start();
			}
			else {
				motionSensor.stop();
			}
		}
		
	}

	@Override
	public void changePauseStatus() {
		super.changePauseStatus();
//		if(accelerometer != null) {
//			if(!paused) {
//				accelerometer.start();
//			}
//			else {
//				accelerometer.stop();
//			}
//		}
		
		if(motionSensor != null) {
			if(!paused) {
				motionSensor.start();
			}
			else {
				motionSensor.stop();
			}
		}
		
	}
	
	/**
	 * draw tracked motion trace at one time after after test finish
	 */
	public void drawMotionTrace(Canvas canvas) {
		try {
			canvas = this.surfaceHolder.lockCanvas();
			synchronized (this.surfaceHolder) {
				//add moving object position to motion trace, which is stored in ArrayList tracePoints
				canvas.drawColor(Color.WHITE);
				
				Paint paint = new Paint();
				paint.setColor(Color.BLUE);
				paint.setStrokeWidth(2);
				paint.setStyle(Paint.Style.STROKE);

				Point[] pts = tracePoints.toArray(new Point[tracePoints.size()]);
				Path path = new Path();
				path.moveTo(pts[0].x, pts[0].y);
				for (int i = 1; i < pts.length; i++){
					path.lineTo(pts[i].x, pts[i].y);
				}
				canvas.drawPath(path, paint);
			}
		}
		finally {
			if(canvas != null)
				surfaceHolder.unlockCanvasAndPost(canvas);
		}		
	}

	public DrawPattern getDrawMotionTrace() {
		return drawMotionTrace;
	}

	public void setDrawMotionTrace(DrawPattern drawMotionTrace) {
		this.drawMotionTrace = drawMotionTrace;
	}

	public ArrayList<Point> getTracePoints() {
		return tracePoints;
	}
	
	
}
