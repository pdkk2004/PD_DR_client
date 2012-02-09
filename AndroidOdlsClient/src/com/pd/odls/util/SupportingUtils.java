package com.pd.odls.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;

public class SupportingUtils {
	
	public static int BYTES_PER_SAMPLING = Float.SIZE / Byte.SIZE;
	public static float GRAVITY = (float)9.98;
	public static SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
	public static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
	//Check whether the network is available
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null, otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	public static void vibrate(Context context, long[] pattern) {
		Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(pattern, -1);
	}
	
	/**
	 * Re-sample sensed data to get a sparser data sample based on given interval.
	 * Each sensed data is composed by x, y, z value;
	 * @param input
	 * @param interval
	 * @return
	 */
	public static Float[] resample(Float[] input, int interval) {
		assert input != null;
		int originalLength = input.length;
		int newLength = originalLength / interval;
		Float[] output = new Float[newLength];
		for(int i = 0, k = 0; i < originalLength && k < newLength; i = i + interval * 3, k++) {
			output[k] = input[i];
			output[k + 1] = input[i + 1];
			output[k + 2] = input[i + 2];
		}
		return output;
	}
	
	/**
	 * Convert byte array to Float array
	 * @param source
	 * @return
	 */
	public static Float[] byteToFloat(byte[] source) {
		assert source != null : "Input source is null";
		boolean EOF = false;
		ArrayList<Float> temp = new ArrayList<Float>();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(source);
		DataInputStream dis = new DataInputStream(bis);
		while(!EOF) {
			try {
				float f = dis.readFloat();
				temp.add(f);
			}
			catch(EOFException e) {
				EOF = true;
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Float[] output = new Float[temp.size()];
		temp.toArray(output);
		
		if(dis != null) {
			try {
				dis.close();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		return output;
	}
}
