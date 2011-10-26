package com.pd.odls.test;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public abstract class BaseTestPanel extends SurfaceView 
			implements SurfaceHolder.Callback {
		
	protected Canvas canvas;
	
	public BaseTestPanel(Context context) {
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
	
	public abstract void update(int x, int y);
	
	public abstract void update();
	
	public abstract void resetPanel();
	
	public abstract void render(Canvas canvas);
}
