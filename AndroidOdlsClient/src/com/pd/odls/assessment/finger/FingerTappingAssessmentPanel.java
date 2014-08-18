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

	private Point topLeft;
	private Point bottomRight;
	private int flag;
	private Region topLeftRegion;
//	private Region topRightRegion;
//	private Region bottomLeftRegion;	
//	private Point topRight;
//	private Point bottomLeft;	
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
//		this.topRight = new Point(0 + target.getWidth()/2, canvas.getHeight() - target.getHeight() / 2 - 50);
		this.bottomRight = new Point(getWidth() - target.getWidth()/2, getHeight() - target.getHeight() / 2 - 50);
//		this.bottomLeft = new Point(canvas.getWidth() - target.getWidth()/2, 0 + target.getHeight()/2);
		
		this.topLeftRegion = new Region(new Rect(0, 0, target.getWidth(), target.getHeight()));
//		this.topRightRegion = new Region(new Rect(0, canvas.getHeight() - 50 - target.getHeight(), target.getWidth(), canvas.getHeight() - 50));
		this.bottomRightRegion = new Region(new Rect(getWidth() - target.getWidth(),
				getHeight() - target.getHeight() - 50,
				getWidth(),
				getHeight() - 50
				));
//		this.bottomLeftRegion = new Region(new Rect(canvas.getWidth() - target.getWidth(),
//				0, canvas.getWidth(), target.getHeight()));

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
		return target.changePosition(x, y, getWidth()/2, getHeight() * 6/7/2);
	}
	
	/**
	 * Alternatively change tapping marker between top-left corner and bottom-right corner
	 * @param flag
	 */
	@Override
	public void update() {
		//Set target alternative appearing on topLeft and bottomRight region
		flag *= -1;
		if(flag == 1) {
			target.setPosition(topLeft);
			this.setTouchRegion(topLeftRegion);
		}
		else {
			target.setPosition(bottomRight);
			this.setTouchRegion(bottomRightRegion);
		}
		
		
		// Set target to appear randomly on the touch screen
		/*
		int newFlag = (int)Math.floor(Math.random() * 4);
		while(flag == newFlag) {
			newFlag = (int)Math.floor(Math.random() * 4);
		}
		
		flag = newFlag;
		switch(newFlag) {
		case 0:
			target.setPosition(topLeft);
			this.setTouchRegion(topLeftRegion);
			break;
		case 1:
			target.setPosition(topRight);
			this.setTouchRegion(topRightRegion);
			break;
		case 2:
			target.setPosition(bottomLeft);
			this.setTouchRegion(bottomLeftRegion);
			break;
		case 3:
			target.setPosition(bottomRight);
			this.setTouchRegion(bottomRightRegion);
			break;
		default:
		}
		*/
	}
	
	public boolean hitTouchTarget(float x, float y) {
		return this.touchTarget.contains((int)x, (int)y);
	}
}
