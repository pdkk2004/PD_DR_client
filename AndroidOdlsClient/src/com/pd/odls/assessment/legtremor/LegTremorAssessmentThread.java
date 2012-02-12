package com.pd.odls.assessment.legtremor;

import java.io.DataOutputStream;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.pd.odls.assessment.BaseAssessmentPanel;
import com.pd.odls.assessment.MotionSensingThread;

public class LegTremorAssessmentThread extends MotionSensingThread {

	public LegTremorAssessmentThread(BaseAssessmentPanel testPanel,
			SurfaceHolder surfaceHolder, DataOutputStream doutAcc,
			DataOutputStream doutOri,
			Context context, Handler handler) {
		super(testPanel, surfaceHolder, doutAcc, doutOri, context, handler);
	}
	
}	
