package com.pd.odls.assessment.finger;

import java.io.DataOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.pd.odls.assessment.BaseAssessmentPanel;
import com.pd.odls.assessment.BaseAssessmentThread;
import com.pd.odls.assessment.DrawPattern;

public class FingerTappingAssessmentThread extends BaseAssessmentThread {
	
	private static final String TAG = FingerTappingAssessmentThread.class.getSimpleName();

	@SuppressWarnings("unused")
	private Context context;
	
	private DrawPattern drawTappingTag;
	
	private DataOutputStream doutTime;
	
	@SuppressWarnings("unused")
	private Handler handler;
		

	public FingerTappingAssessmentThread(BaseAssessmentPanel testPanel,
			SurfaceHolder holder, DataOutputStream doutTime,
			Context context, Handler handler) {
		super(testPanel, holder);
		this.context = context;
		this.doutTime = doutTime;
		this.handler = handler;		
	}


	public void setDrawTappingTag(DrawPattern pattern) {
		this.drawTappingTag = pattern;

	}
	
	
	public DrawPattern getDrawTappingTag() {
		return drawTappingTag;
	}

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
					this.testPanel.update();
					this.testPanel.render(canvas);
				}
				Log.i(TAG, "Target appears at:" + System.currentTimeMillis());
				doutTime.writeLong(0);
				doutTime.writeLong(System.currentTimeMillis());
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			finally {
				if(canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}
			
			synchronized (this) {
				try {
					wait();
					Log.i(TAG, "Hit target at:" + System.currentTimeMillis());
					doutTime.writeLong(1);
					doutTime.writeLong(System.currentTimeMillis());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (InterruptedException e) {
					Log.w(TAG, e.getMessage());
				}
			}
		}
		Log.i(TAG, "Stop test");
	}



	@Override
	public boolean initializeSensor() {
		// TODO Leave blank do not need Accelerometer
		return false;
	}
	
}
