package com.pd.odls.assessment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public abstract class BaseAssessmentPanel extends SurfaceView 
			implements SurfaceHolder.Callback {
	
	protected MovingObject target;
	
	protected BaseAssessmentPanel(Context context) {
		super(context);
		getHolder().addCallback(this);
		setFocusable(true);
	}


	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		Canvas canvas = arg0.lockCanvas();
		onDraw(canvas);
		arg0.unlockCanvasAndPost(canvas);
	}
	
	public void surfaceDestroyed(SurfaceHolder arg0) {
		this.target = null;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
	}	
	
	public void initialize() {
		Point position = new Point(getWidth()/2, (getHeight() - 50) * 6/7/2 );
		target.setPosition(position);
	}	
	/**
	 * Update moving object on panel and return the new position of moving object.
	 */
	public abstract Point update(int x, int y, int z);
	
	public abstract void update();
	
	public abstract void resetPanel();
	
	public abstract void render(Canvas canvas);
	
}
