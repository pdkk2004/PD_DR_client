package com.pd.odls.assessment.handtremor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import com.pd.odls.assessment.BaseAssessmentPanel;
import com.pd.odls.assessment.MovingObject;

public class HandTremorAssessmentPanel extends BaseAssessmentPanel {

	private static final String TAG = HandTremorAssessmentPanel.class.getSimpleName();
	
	public HandTremorAssessmentPanel(Context context, MovingObject o) {
		super(context);
		this.target = o;
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
		p.set(getWidth()/2, getHeight()/2);
		setTargetPosition(p);
	}
	
	@Override
	public void render(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		target.draw(canvas);
	}

	@Override
	public Point update(int x, int y, int z) {
		return target.changePosition(x, y, getWidth()/2, (getHeight() - 50) * 6/7/2);
	}
	
	@Override
	public void update() {
		target.changePosition(2, 2, getWidth()/2, (getHeight() - 50) * 6/7/2);
	}	
}
