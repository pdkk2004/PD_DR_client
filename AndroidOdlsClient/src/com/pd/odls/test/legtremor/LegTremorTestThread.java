package com.pd.odls.test.legtremor;

import java.io.DataOutputStream;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.pd.odls.test.BaseTestPanel;
import com.pd.odls.test.MotionSensingThread;

public class LegTremorTestThread extends MotionSensingThread {

	public LegTremorTestThread(BaseTestPanel testPanel,
			SurfaceHolder surfaceHolder, DataOutputStream dout,
			Context context, Handler handler) {
		
		super(testPanel, surfaceHolder, dout, context, handler);
	}
	
}	
