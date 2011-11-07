package com.pd.odls.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.openintents.sensorsimulator.hardware.SensorEvent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.pd.odls.sensor.Accelerometer;
import com.pd.odls.sensor.Orientation;
import com.pd.odls.sensor.SensorDelegate;

public class MotionSensingThread extends BaseTestThread {
	
	private static final String TAG = MotionSensingThread.class.getSimpleName();

	private Context context;
	private int offSetX;
	private int offSetY;
	private int offSetZ;
	private ArrayList<Point> tracePoints = new ArrayList<Point>();
	
	private DrawPattern drawMotionTrace;
	
	//TODO change SimulatedAccelerometer to Accelerometer in production
	private Accelerometer accelerometer;
	private Orientation orientation;
//	private SimulatedAccelerometer accelerometer;
//	private SimulatedOrientation orientation;
	
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
	
	@Override
	public void initializeAccelerometer() {
		if(context != null) {
			//TODO change SimulatedAccelerometer to Accelerometer in production			
			accelerometer = new Accelerometer(this.context);
			orientation = new Orientation(this.context);
			
//			accelerometer = new SimulatedAccelerometer(this.context);
//			orientation = new SimulatedOrientation(this.context);
			
			orientation.setDelegate(delegateOri);
			accelerometer.setDelegate(delegateAcc);
		}
	}
	
	/**
	 * Delegate operation to deal with sensed data from accelerometer sensor
	 */
	private SensorDelegate delegateAcc = new SensorDelegate() {

		public void onSensedValueChanged(SensorEvent event) {
			try {
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];
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

		public void onSensedValueChanged(SensorEvent event) {
			try {
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];
				offSetX = Math.round(x);
				offSetY = Math.round(y);
				offSetZ = Math.round(z);
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
		if(accelerometer == null)
			initializeAccelerometer();
		if(accelerometer != null) {
			if(isRunning) {
				accelerometer.start();
				orientation.start();
			}
			else {
				accelerometer.stop();
				orientation.stop();
			}
		}	
	}

	@Override
	public void changePauseStatus() {
		super.changePauseStatus();
		if(accelerometer != null) {
			if(!paused) {
				accelerometer.start();
				orientation.start();
			}
			else {
				accelerometer.stop();
				orientation.stop();
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
