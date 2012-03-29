package com.pd.odls.assessment.fingertouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;

import com.pd.odls.assessment.BaseAssessmentPanel;
import com.pd.odls.assessment.MovingObject;

public class FingerTouchingAssessmentPanel extends BaseAssessmentPanel {

	private static final String TAG = FingerTouchingAssessmentPanel.class.getSimpleName();

	private MovingObject target;
	private Region touchTargetRegion;

	
	public FingerTouchingAssessmentPanel(Context context, MovingObject o) {
		super(context);
		this.target = o;
	}
	
	@Override
	public void initialize() {
		Point position = new Point(canvas.getWidth()/2, (canvas.getHeight() - 50) * 6/7/2 );
		target.setPosition(position);
		this.touchTargetRegion = new Region(new Rect(position.x - target.getWidth() / 2,
				position.y - target.getHeight() / 2,
				position.x + target.getWidth() / 2,
				position.y + target.getHeight() / 2
				));
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
	public Point update(int x, int y, int z) {
		return target.changePosition(x, y, canvas.getWidth()/2, (canvas.getHeight() - 50) * 6/7/2 );
	}
	
	@Override
	public void update() {
		target.changePosition(2, 2, canvas.getWidth()/2, (canvas.getHeight() - 50) * 6/7/2 );
	}
	
	public boolean hitTouchTarget(float x, float y) {
		return this.touchTargetRegion.contains((int)x, (int)y);
	}
	
	public void setTouchRegion(Region r) {
		this.touchTargetRegion = r;
	}
}
