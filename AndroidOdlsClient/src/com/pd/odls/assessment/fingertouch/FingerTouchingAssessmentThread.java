package com.pd.odls.assessment.fingertouch;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.pd.odls.assessment.BaseAssessmentPanel;
import com.pd.odls.assessment.BaseAssessmentThread;
import com.pd.odls.assessment.DrawPattern;

public class FingerTouchingAssessmentThread extends BaseAssessmentThread {
	
	private static final String TAG = FingerTouchingAssessmentThread.class.getSimpleName();
	
	private boolean shownTarget;
	
	@SuppressWarnings("unused")
	private Context context;
	
	private DrawPattern drawTappingTag;
		
	@SuppressWarnings("unused")
	private Handler handler;
		

	public FingerTouchingAssessmentThread(BaseAssessmentPanel testPanel,
			SurfaceHolder holder, Context context, Handler handler) {
		super(testPanel, holder);
		this.context = context;
		this.handler = handler;	
		this.shownTarget = false;
	}


	public boolean isShownTarget() {
		return shownTarget;
	}


	public void setShownTarget(boolean shownTarget) {
		this.shownTarget = shownTarget;
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
		
		while(isRunning && !shownTarget) {
			if(!this.surfaceHolder.getSurface().isValid())
				continue;
			canvas = this.surfaceHolder.lockCanvas();
			synchronized (this.surfaceHolder) {
				this.testPanel.render(canvas);
				this.setShownTarget(true);
			}
			if(canvas != null)
				surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}



	@Override
	public void initializeSensor() {
		// TODO Leave blank do not need Accelerometer		
	}
	
}
