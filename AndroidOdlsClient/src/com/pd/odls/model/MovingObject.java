package com.pd.odls.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

public class MovingObject {
	
	private Bitmap bitmap;
	private Point position; 
	private int width;
	private int height;
		
	public MovingObject(Bitmap bitmap) {
		this.bitmap = bitmap;
		if(bitmap != null) {
			this.width = bitmap.getWidth();
			this.height = bitmap.getHeight();
		}
		else {
			width = 0;
			height = 0;
		}
	}

	public int getWidth() {
		return bitmap.getWidth();
	}

	public int getHeight() {
		return bitmap.getHeight();
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}
	
	public Point changePosition(int x, int y, int refX, int refY) {
		position.x = refX - x * 30;
		position.y = refY + y * 30;
		return new Point(position.x - width/2, position.y - height/2);
	}
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, position.x - width/2, position.y - height/2, null);
	}
	
}
