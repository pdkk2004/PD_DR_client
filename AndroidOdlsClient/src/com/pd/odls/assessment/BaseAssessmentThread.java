package com.pd.odls.assessment;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


public abstract class BaseAssessmentThread extends Thread {
	
	private static final String TAG = BaseAssessmentThread.class.getSimpleName();
	
	//flags to indicate test state
	protected boolean isRunning = false;
	protected boolean paused = false;
	
	protected BaseAssessmentPanel testPanel;
	protected SurfaceHolder surfaceHolder;
	
	public BaseAssessmentThread(BaseAssessmentPanel testPanel, SurfaceHolder surfaceHolder) {
		super();
		this.testPanel = testPanel;
		this.surfaceHolder = surfaceHolder;
	}
	
	public BaseAssessmentThread(BaseAssessmentPanel testPanel, 
			SurfaceHolder surfaceHolder,
			Runnable runnable) {
		super(runnable);
		this.testPanel = testPanel;
		this.surfaceHolder = surfaceHolder;
	}
	
	
	public void setRunning(boolean running) {
		this.isRunning = running;
	}
	
	public void setPause(boolean pause) {
		this.paused = pause;
	}
	
	public boolean getPauseStatus() {
		return paused;
	}
	
	public void changePauseStatus() {
		this.paused = !this.paused;
	}

	@Override
	public void run() {
		Canvas canvas = null; 
		Log.d(TAG, "Starting test");
		while(isRunning) {
			if(!this.surfaceHolder.getSurface().isValid())
				continue;
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (this.surfaceHolder) {
					this.testPanel.update();
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
	}
	
	/**
	 * Set up Accelerometer associated with this thread
	 */
	public abstract boolean initializeSensor();
}
