package com.pd.odls.assessment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public abstract class BaseAssessmentPanel extends SurfaceView 
			implements SurfaceHolder.Callback {
		
	protected Canvas canvas;
	
	public BaseAssessmentPanel(Context context) {
		super(context);
		getHolder().addCallback(this);
		canvas = getHolder().lockCanvas();
		setFocusable(true);
	}


	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		if(canvas == null) {
			canvas = arg0.lockCanvas();
		}
		this.onDraw(canvas);
		arg0.unlockCanvasAndPost(canvas);
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
	}
	
	public abstract void initialize();
	
	/**
	 * Update moving object on panel and return the new position of moving object.
	 */
	public abstract Point update(int x, int y, int z);
	
	public abstract void update();
	
	public abstract void resetPanel();
	
	public abstract void render(Canvas canvas);
}
