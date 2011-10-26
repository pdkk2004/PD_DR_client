package com.pd.odls.test.gait;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GaitTestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView view = new TextView(this);
		view.setText("This is for gait difficulty test");
		setContentView(view);
	}

}
