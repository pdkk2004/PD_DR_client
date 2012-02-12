package com.pd.odls.assessment.handtremor;

import java.io.DataOutputStream;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.pd.odls.assessment.BaseAssessmentPanel;
import com.pd.odls.assessment.MotionSensingThread;

public class HandTremorAssessmentThread extends MotionSensingThread {

	public HandTremorAssessmentThread(BaseAssessmentPanel testPanel,
			SurfaceHolder surfaceHolder, DataOutputStream doutAcc,
			DataOutputStream doutOri,
			Context context, Handler handler) {
		super(testPanel, surfaceHolder, doutAcc, doutOri, context, handler);
		// TODO Auto-generated constructor stub
	}
	
}
