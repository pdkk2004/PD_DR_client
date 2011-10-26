package com.pd.odls.test.tapping;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TappingTestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView view = new TextView(this);
		view.setText("This is for finger tapping test");
		setContentView(view);
	}
	
}
