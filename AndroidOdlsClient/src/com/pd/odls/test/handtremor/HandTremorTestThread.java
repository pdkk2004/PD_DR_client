package com.pd.odls.test.handtremor;

import java.io.DataOutputStream;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.pd.odls.test.BaseTestPanel;
import com.pd.odls.test.MotionSensingThread;

public class HandTremorTestThread extends MotionSensingThread {

	public HandTremorTestThread(BaseTestPanel testPanel,
			SurfaceHolder surfaceHolder, DataOutputStream doutAcc,
			DataOutputStream doutOri,
			Context context, Handler handler) {
		super(testPanel, surfaceHolder, doutAcc, doutOri, context, handler);
		// TODO Auto-generated constructor stub
	}
	
}
