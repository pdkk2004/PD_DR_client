package com.pd.odls.test.legtremor;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class LegTremorTestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView view = new TextView(this);
		view.setText("This is for leg tremor test");
		setContentView(view);
	}

}
