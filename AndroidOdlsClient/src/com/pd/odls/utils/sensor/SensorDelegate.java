package com.pd.odls.utils.sensor;



/**
 * Accelerometer delegate to process accelerometer data. Aimed to be implemented.
 * @author Pan
 *
 */
public interface SensorDelegate {
	
	public void onSensedValueChanged(float x, float y, float z);
	 
	public void onShake(float force);

}
