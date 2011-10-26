package com.dp.odls.model;

public class Speed {
	
	public static final int DIRECTION_RIGHT = 1;
	public static final int DIRECTION_LEFT = -1;
	public static final int DIRECTION_DOWN = 1;
	public static final int DIRECTION_UP = -1;

	private float xv = 1;
	private float yv = 1;
	
	private int xRightDirection = DIRECTION_RIGHT;
	private int yDownDirection = DIRECTION_DOWN;
	private int xLeftDirection = DIRECTION_LEFT;
	private int yUpDirection = DIRECTION_UP;
	
	public Speed() {
		this.xv = 1;
		this.xv = 1;
	}
	
	public Speed(float xv, float yv) {
		this.xv = xv;
		this.yv = yv;
	}

	public float getXv() {
		return xv;
	}

	public void setXv(float xv) {
		this.xv = xv;
	}

	public float getYv() {
		return yv;
	}

	public void setYv(float yv) {
		this.yv = yv;
	}

	public int getxRightDirection() {
		return xRightDirection;
	}

	public void setxRightDirection(int xRightDirection) {
		this.xRightDirection = xRightDirection;
	}

	public int getyDownDirection() {
		return yDownDirection;
	}

	public void setyDownDirection(int yDownDirection) {
		this.yDownDirection = yDownDirection;
	}

	public int getxLeftDirection() {
		return xLeftDirection;
	}

	public void setxLeftDirection(int xLeftDirection) {
		this.xLeftDirection = xLeftDirection;
	}

	public int getyUpDirection() {
		return yUpDirection;
	}

	public void setyUpDirection(int yUpDirection) {
		this.yUpDirection = yUpDirection;
	}

	
}
