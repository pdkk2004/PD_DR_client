package com.pd.odls.test.handtremor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import com.pd.odls.test.BaseTestPanel;

public class HandTremorTestPanel extends BaseTestPanel {

	private static final String TAG = HandTremorTestPanel.class.getSimpleName();

	private MovingObject target;
	
	public HandTremorTestPanel(Context context, MovingObject o) {
		super(context);
		this.target = o;
	}
	
	@Override
	public void initialize() {
		Point position = new Point(canvas.getWidth()/2, (canvas.getHeight() - 50) * 6/7/2 );
		target.setPosition(position);
	}

	public MovingObject getTarget() {
		return target;
	}

	public void setTarget(MovingObject target) {
		this.target = target;
	}
	
	private void setTargetPosition(Point p) {
		if(target != null) {
			target.setPosition(p);
		}
		else {
			Log.e(TAG, "No moving object target");
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
//		if(target != null ) 
//			target.draw(canvas);
	}	
	
	
	public void resetPanel() {
		Point p = new Point();
		p.set(canvas.getWidth()/2, canvas.getHeight()/2);
		setTargetPosition(p);
	}
	
	@Override
	public void render(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		target.draw(canvas);
	}

	@Override
	public void update(int x, int y) {
		target.changePosition(x, y);
	}
	
	@Override
	public void update() {
		target.changePosition(2, 2);
	}

	
}
