package com.pd.odls.sensor;

import org.openintents.sensorsimulator.hardware.SensorEvent;

/**
 * Accelerometer delegate to process accelerometer data. Aimed to be implemented.
 * @author Pan
 *
 */
public interface SensorDelegate {
	
	public void onSensedValueChanged(SensorEvent event);
	 
	public void onShake(float force);

}
