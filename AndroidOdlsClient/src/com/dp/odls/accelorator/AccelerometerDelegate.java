package com.dp.odls.accelorator;

/**
 * Accelerometer delegate to process accelerometer data. Aimed to be implemented.
 * @author Pan
 *
 */
public interface  AccelerometerDelegate {
	
	public void onAccelerationChanged(float x, float y, float z);
	 
	public void onShake(float force);

}
