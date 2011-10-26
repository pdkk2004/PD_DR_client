package com.pd.odls.registry;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pd.odls.R;
import com.pd.odls.http.OdlsHttpClientHelper;

public class RegistryActivity extends Activity implements Runnable {
	
	//Constant value
	private static final int DLG_FAIL = 10;
	private static final int DLG_SUCCESS = 11;
	private static final int DLG_ERROR = 15;
	
	//Widget components
	private EditText editCode;
	private EditText editPw;
	private EditText editPw2;
	private Button btnCancel;
	private ProgressDialog progDlg;
	
	//User registration information
	private String code;
	private String pw;
	
	private int result; //registration result. 0: fail 1:success
	
	//Handler to deal account registration
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progDlg.dismiss();
			if(msg.what == 1) {
				showDialog(DLG_SUCCESS);
				RegistryActivity.this.cancel(btnCancel);
			}
			else {
				showDialog(DLG_FAIL);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.fast_reg);
		
		//Associate widgets
		editCode = (EditText)findViewById(R.id.editCode);
		editPw = (EditText)findViewById(R.id.editPw);
		editPw2 = (EditText)findViewById(R.id.editPw2);
		btnCancel = (Button)findViewById(R.id.btnCancel);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		Builder builder = null;
		switch (id) {
		case DLG_FAIL:
			// Create AlterDialog
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Warnning");
			builder.setMessage("Fail to regist your account, please verify your input registration code");
			
			builder.setCancelable(true);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//TODO: OK button trigered event
				}
			});
			dialog = builder.create();
			return dialog;
		case DLG_SUCCESS:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Congratulations!");
			builder.setMessage("Your account has been successfully created!");
			
			builder.setCancelable(true);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//TODO: OK button trigered event
				}
			});
			dialog = builder.create();
			return dialog;
		case DLG_ERROR:
			// Create AlterDialog
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Error");
			builder.setMessage("System error, please try later");
			
			builder.setCancelable(true);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//TODO: OK button trigered event
				}
			});
			dialog = builder.create();
			return dialog;
		}
		return dialog;
	}
	
	public void submit(View view) {
		code = editCode.getText().toString();
		pw = editPw.getText().toString();
		String pw2 = editPw2.getText().toString();
		
		if(code.equals("") || pw.equals("") || pw2.equals("")) {
			Toast.makeText(this.getApplicationContext(),
					"Please fill all required fields to registry", 
					Toast.LENGTH_LONG).show();
			return;
		}
		if(!pw.equals(pw2)) {
			Toast.makeText(getApplicationContext(),
					"Unmatched 2 passwords input", 
					Toast.LENGTH_LONG).show();
			return;
		}
		
		progDlg = ProgressDialog.show(getApplicationContext(),
				"Working", "Submitting registration...");
		Thread thread = new Thread(this);
		thread.start();		
	}
	
	public int registAccount() {
		int result = 0;
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("Code", code));
		postParameters.add(new BasicNameValuePair("Password", pw));

		String response = null;
		try {
			response = OdlsHttpClientHelper.executeHttpPost("http://10.0.2.2:8080/Odls/registry", postParameters);
			String res=response.toString();
			res = res.replaceAll("\\s+", "");
			if(res.equals("1"))
				result = 1;
			else result = 0;
		} catch (Exception e) {
			System.out.println(response);
		}
		return result;
	}
	
	public void cancel(View view) {
		finish();
	}
	
	public void run( ) {
		result = registAccount();
		handler.sendEmptyMessage(result);
	}

	@Override
	public void finish() {
		setResult(result, null);
		super.finish();
	}
	
	
	
}
