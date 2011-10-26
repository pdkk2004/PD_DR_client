package com.pd.odls.scale;

import com.pd.odls.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class FallsEvaluationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.falls_evaluation);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.confidence_scale, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fillSpinner(adapter);
	}
	
	private void fillSpinner(ArrayAdapter<CharSequence> adapter) {
		Spinner spinner = (Spinner)findViewById(R.id.falls_1);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.falls_2);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.falls_3);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.falls_4);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.falls_5);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.falls_6);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.falls_7);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.falls_8);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.falls_9);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.falls_10);
		spinner.setAdapter(adapter);
	}

}
