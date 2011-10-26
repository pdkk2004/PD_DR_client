package com.pd.odls.scale;

import com.pd.odls.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class ABCEvaluationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.abc_evaluation);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.confidence_pct, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		fillSpinner(adapter);
	}

	private void fillSpinner(ArrayAdapter<CharSequence> adapter) {
		Spinner spinner;
		
		spinner = (Spinner)findViewById(R.id.abc_1);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_2);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_3);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_4);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_5);
		spinner.setAdapter(adapter);

		spinner = (Spinner)findViewById(R.id.abc_6);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_7);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_8);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_9);
		spinner.setAdapter(adapter);

		spinner = (Spinner)findViewById(R.id.abc_10);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_11);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_12);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_13);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_14);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_15);
		spinner.setAdapter(adapter);
		
		spinner = (Spinner)findViewById(R.id.abc_16);
		spinner.setAdapter(adapter);
	}

}
