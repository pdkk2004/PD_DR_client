package com.pd.odls.test.finger;

import java.io.DataOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.pd.odls.test.BaseTestPanel;
import com.pd.odls.test.BaseTestThread;
import com.pd.odls.test.DrawPattern;

public class FingerTappingTestThread extends BaseTestThread {
	
	private static final String TAG = FingerTappingTestThread.class.getSimpleName();

	@SuppressWarnings("unused")
	private Context context;
	
	private DrawPattern drawTappingTag;
	
	private DataOutputStream dout;
	
	@SuppressWarnings("unused")
	private Handler handler;
		

	public FingerTappingTestThread(BaseTestPanel testPanel,
			SurfaceHolder holder, DataOutputStream dout,
			Context context,
			Handler handler) {
		super(testPanel, holder);
		this.context = context;
		this.dout = dout;
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
			}
			
			finally {
				if(canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}
			
			synchronized (this) {
				try {
					wait();
					dout.writeLong(System.currentTimeMillis());
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
	public void initializeAccelerometer() {
		// TODO Leave blank do not need Accelerometer		
	}
	
}
