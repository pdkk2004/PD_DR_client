package com.pd.odls.utils.sensor;



/**
 * Accelerometer delegate to process accelerometer data. Aimed to be implemented.
 * @author Pan
 *
 */
public interface SensorDelegate {
	
	public void onSensedValueChanged(float d, float e, float f);
	 
	public void onShake(float force);

}
