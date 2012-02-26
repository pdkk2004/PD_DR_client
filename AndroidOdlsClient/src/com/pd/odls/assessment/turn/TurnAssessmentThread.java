package com.pd.odls.assessment.turn;

import java.io.DataOutputStream;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.pd.odls.assessment.BaseAssessmentPanel;
import com.pd.odls.assessment.MotionSensingThread;

public class TurnAssessmentThread extends MotionSensingThread {

	public TurnAssessmentThread(BaseAssessmentPanel testPanel,
			SurfaceHolder surfaceHolder, DataOutputStream doutAcc,
			DataOutputStream doutOri,
			Context context, Handler handler) {
		super(testPanel, surfaceHolder, doutAcc, doutOri, context, handler);
	}
	
}	
