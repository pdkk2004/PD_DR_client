package com.pd.odls.assessment.finger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;

import com.pd.odls.assessment.BaseAssessmentPanel;
import com.pd.odls.assessment.MovingObject;

public class FingerTappingAssessmentPanel extends BaseAssessmentPanel {

	private static final String TAG = FingerTappingAssessmentPanel.class.getSimpleName();

	private MovingObject target;
	private Point topLeft;
	private Point bottomRight;
	private int flag;
	private Region topLeftRegion;
	private Region bottomRightRegion;
	private Region touchTarget;
	
	public FingerTappingAssessmentPanel(Context context, MovingObject o) {
		super(context);
		this.target = o;
	}
	
	@Override
	public void initialize() {
		this.flag = 1;
		this.topLeft = new Point(0 + target.getWidth()/2, target.getHeight()/2);
		this.bottomRight = new Point(canvas.getWidth() - target.getWidth()/2, canvas.getHeight() - target.getHeight() / 2 - 50);
		this.topLeftRegion = new Region(new Rect(0, 0, target.getWidth(), target.getHeight()));
		this.bottomRightRegion = new Region(new Rect(canvas.getWidth() - target.getWidth(),
				canvas.getHeight() - target.getHeight() - 50,
				canvas.getWidth(),
				canvas.getHeight() - 50
				));

		target.setPosition(topLeft);
		
	}
	
	public void setTouchRegion(Region r) {
		this.touchTarget = r;
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
		p.set(0, 50);
		setTargetPosition(p);
	}
	
	@Override
	public void render(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		target.draw(canvas);
	}

	@Override
	public Point update(int x, int y, int z) {
		return target.changePosition(x, y, canvas.getWidth()/2, canvas.getHeight() * 6/7/2);
	}
	
	/**
	 * Alternatively change tapping marker between top-left corner and bottom-right corner
	 * @param flag
	 */
	@Override
	public void update() {
		flag *= -1;
		if(flag == 1) {
			target.setPosition(topLeft);
			this.setTouchRegion(topLeftRegion);
		}
		else {
			target.setPosition(bottomRight);
			this.setTouchRegion(bottomRightRegion);
		}
	}
	
	public boolean hitTouchTarget(float x, float y) {
		return this.touchTarget.contains((int)x, (int)y);
	}
}
