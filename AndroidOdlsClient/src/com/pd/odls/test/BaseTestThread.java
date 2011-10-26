package com.pd.odls.test;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


public class BaseTestThread extends Thread {
	
	private static final String TAG = BaseTestThread.class.getSimpleName();
	
	//flag to hold test state
	protected boolean isRunning = false;
	protected boolean pause = false;
	protected BaseTestPanel testPanel;
	protected SurfaceHolder surfaceHolder;
	
	public BaseTestThread(BaseTestPanel testPanel, SurfaceHolder surfaceHolder) {
		super();
		this.testPanel = testPanel;
		this.surfaceHolder = surfaceHolder;
	}
	
	public BaseTestThread(BaseTestPanel testPanel, 
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
		this.pause = pause;
	}
	
	public boolean getPauseStatus() {
		return pause;
	}
	
	public void changePauseStatus() {
		this.pause = !this.pause;
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
	}	
}
