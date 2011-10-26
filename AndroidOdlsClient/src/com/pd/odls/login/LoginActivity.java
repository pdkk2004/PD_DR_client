package com.pd.odls.login;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dp.odls.model.User;
import com.pd.odls.R;
import com.pd.odls.http.OdlsHttpClientHelper;
import com.pd.odls.main.MainControlActivity;
import com.pd.odls.registry.RegistryActivity;


public class LoginActivity extends Activity implements Runnable {
    /** Called when the activity is first created. */
	
	private static final String TAG = LoginActivity.class.getName();
	
	//Request code for start activities
	private static final int REQUEST_REG_ACTIVITY = 100;
	
	@SuppressWarnings("unused")
	private static final int REQUEST_MAINFORM_ACTIVITY = 101;
	
	//Constant value
	private static final int DLG_FAIL = 10;
	private static final int DLG_PROGRESS = 17;
	private static final int DLG_SUCCESS = 11;
	private static final int DLG_ERROR = 15;
	
	//fields
	private String usrName;
	private String usrPw;
	private Button btnSubmit;
	private Button btnCancel;
	private EditText editUser;
	private EditText editPw;
	private TextView tvRegistry;
	private Boolean access;
	private SharedPreferences preferences;
	private ProgressDialog progDialog;
	
	//Handler to deal account verification result
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progDialog.dismiss();
			switch(msg.what) {
			case 1:
				Editor edit = preferences.edit();
				edit.putString(User.USER_NAME, usrName);
				edit.commit();
				Intent i = new Intent(LoginActivity.this, 
						MainControlActivity.class);
				startActivity(i);
				break;
			case 0:showDialog(DLG_FAIL);
				break;
			case -1:showDialog(DLG_ERROR);
				break;
			default:
				return;
			}
		}		
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        //bind view widgets
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        editUser = (EditText)findViewById(R.id.usr_name);
        editPw = (EditText)findViewById(R.id.pass_word);
        tvRegistry = (TextView)findViewById(R.id.tvRegistry);
        
        btnSubmit.setOnClickListener(new btnConfirmClickListener());
        btnCancel.setOnClickListener(new btnCancelClickListener());
        tvRegistry.setOnClickListener(new lblRegistryClickListener());
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        editUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
					showSoftKeyboard(); 
				showSoftKeyboard();
			}
		});

        
        editPw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
					showSoftKeyboard();
			}
		});
        
        //load App preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == 1 && requestCode == REQUEST_REG_ACTIVITY) {
    		
    	}
    }
    
    private class btnConfirmClickListener implements OnClickListener {
    	public void onClick(View v) {
    		usrName = editUser.getText().toString();
    		usrPw = editPw.getText().toString();
    		if(usrName == null || usrPw == null) {
    			showDialog(DLG_FAIL);
    			return;
    		}
    		
    		progDialog = ProgressDialog.show(LoginActivity.this, 
    				"Processing...",
    				"Verifying account information", true, false);
    		Thread thread = new Thread(LoginActivity.this);
    		thread.start();
    	}
    }
    
	private int verifyAccount() {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(User.USER_NAME, usrName));
		postParameters.add(new BasicNameValuePair(User.USER_PW, usrPw));

		String response = null;
		try {
			response = OdlsHttpClientHelper.executeHttpPost("http://10.0.2.2:8080/Odls/login", postParameters);
			String res=response.toString();
			res = res.replaceAll("\\s+", "");
			if(res.equals("1")) return 1;
			else return 0;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return -1;
	}  	
    
    @SuppressWarnings("unused")
	private class showUserInfoListener implements OnClickListener {
		public void onClick(View v) {
			String userName = preferences.getString("username", "n/a");
			String password = preferences.getString("password", "n/a");
			Toast.makeText(LoginActivity.this,
					"You entered user: " + userName +
							"and password: " + password, 
							Toast.LENGTH_SHORT).show();
		}
    	
    }
    
    private class btnCancelClickListener implements OnClickListener {
    	public void onClick(View v) {
    		editUser.setText("");
    		editPw.setText("");
    	}
    }
    
    private class lblRegistryClickListener implements OnClickListener {
    	public void onClick(View v) {
    		Intent intent = new Intent();
    		intent.setClass(LoginActivity.this, RegistryActivity.class);
    		startActivityForResult(intent, REQUEST_REG_ACTIVITY);
    	}
    }
    
    //build invalid user information warning dialog
    public Dialog buildWarnningDialog(Context context, String warnning) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setIcon(R.drawable.alert_dialog_icon);
    	builder.setTitle("Warnning");
    	builder.setMessage(warnning);
    	builder.setNeutralButton("OK", 
    			new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int which) {
    					
    				}
    		});
    	return builder.create();    	
    }
    
    /**
     * 
     */
    @SuppressWarnings("unused")
	private void saveLoginInfo() {
    	if(access) {
    		SharedPreferences userinfo = this.getSharedPreferences(User.USER_INFO, 0);
    		userinfo.edit().putString(User.USER_NAME, this.usrName);
    		userinfo.edit().putString(User.USER_PW, this.usrPw);
    	}
    	else {
    		return;
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Toast.makeText(this, "Just a test", Toast.LENGTH_SHORT).show();
		return true;
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
			builder.setMessage("Unmatched user name and pass word!");
			
			builder.setCancelable(true);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//TODO: OK button trigered event
				}
			});
			dialog = builder.create();
			return dialog;
		case DLG_PROGRESS:
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			return progressDialog;
		case DLG_SUCCESS:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("");
			builder.setMessage("Account Verified!");
			
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
			builder.setMessage("Application error!");
			
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
	
	/**
	 * Hide soft keyboard
	 */
	@SuppressWarnings("unused")
	private void hideSoftKeyboard(){ 
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		View currentFocus = getWindow().getCurrentFocus();
		if(currentFocus != null)
			imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 
					0);
	}
	
	/**
	 * Show soft keyboard
	 */
	private void showSoftKeyboard(){ 
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		View currentFocus = getWindow().getCurrentFocus();
		if(currentFocus != null)
			imm.showSoftInputFromInputMethod(currentFocus.getWindowToken(), 
				InputMethodManager.SHOW_FORCED); 
	}

	public void run() {
		handler.sendEmptyMessage(verifyAccount());
	}
	
	
	
	
}