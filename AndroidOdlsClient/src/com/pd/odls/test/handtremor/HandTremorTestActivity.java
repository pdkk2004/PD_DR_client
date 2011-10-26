package com.pd.odls.test.handtremor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dp.odls.model.Test;
import com.dp.odls.model.User;
import com.dp.odls.sqlite.OdlsDbAdapter;
import com.pd.odls.R;
import com.pd.odls.test.BaseTestActivity;

import java.io.*;
import java.lang.Thread.State;
import java.util.Timer;
import java.util.TimerTask;

public class HandTremorTestActivity extends BaseTestActivity {
	
	public static final int MSG_BUFFER_FULL = 25;
	private static final int DLG_BUFFER_FULL = 20;
	private static final int DLG_DATABASE_ERROR = 27;
	private static final int DLG_TEST_DONE = 30;
	
	private Button controlBtn;
	private TextView elapsedTimeView;
	private int elapsedTime;
	private Timer timer;
	private ByteArrayOutputStream buffer = new ByteArrayOutputStream(3 * 4 * 5 * 20);  //create the buffer to store 20 s test data at 5Hz
	private OdlsDbAdapter databaseManager;

	//Create the timer task to count test elapsed time
	private TimerTask timerTask;

	
	//Create handler to deal with UI change
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MSG_TEST_TIME_CHANGE:
				elapsedTimeView.setText(String.valueOf(elapsedTime));
				break;
			case MSG_BUFFER_FULL:
				stopTest();
				timer.cancel();
				controlBtn.setText("Start");
				showDialog(HandTremorTestActivity.DLG_BUFFER_FULL);
				
				DataInputStream din = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
				try {
					while(true) {
						System.out.print(din.readFloat() + " ");
					}
				}
				catch(EOFException eof) {
					Log.i(this.toString(), "Reach end of buffer");
				}
				catch(IOException e) {
					Log.e(HandTremorTestActivity.class.getCanonicalName(), e.getMessage());
				}
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(DLG_INSTRUCTION);
		
		//create the moving objects for test panel
		MovingObject mo = new MovingObject(BitmapFactory.decodeResource(
				getResources(), R.drawable.red_point));
		
		//create the test panel 
		testPanel = new HandTremorTestPanel(this, mo);
		testPanel.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					pauseOrResume();
					return true;
				}
				return false;
			}		
		});
		
		//build the test view at run time
		LayoutInflater inflater = (LayoutInflater)this.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		View controlPanel = inflater.inflate(R.layout.hand_tremor_test_control, layout, false);

		controlPanel.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 6));
		testPanel.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
		layout.addView(testPanel);
		layout.addView(controlPanel);
		setContentView(layout);
		
		controlBtn = (Button)findViewById(R.id.btn_control);
		
		controlBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				if(isRunning == false) {
					initializeTest();
					beginTest();
					controlBtn.setText("Stop");		
				}
				else {
					stopTest();
					timer.cancel();
					controlBtn.setText("Start");
					showDialog(DLG_TEST_DONE);
				}
			}		
		});	
		
		elapsedTimeView = (TextView)findViewById(R.id.textView_elapsedTime);
		
		//prepare database manager to save test data
		databaseManager = new OdlsDbAdapter(this);
	}
	
	@Override
	protected void pauseOrResume() {
		if(isRunning == false) {
			pause = false;
			return;
		}
		else {
			synchronized (testThread) {
				testThread.changePauseStatus();
				pause = testThread.getPauseStatus();				
				if(pause == false) {
					testThread.notify();
				}
			}
			synchronized (timerTask) {
				if(pause == false) {
					timerTask.notify();
				}
			}
		}
	}
	
	@Override
	protected void initializeTest() {
		//initialize test thread
		if(testThread == null || testThread.getState() == State.TERMINATED) {
			testThread = new HandTremorTestThread(testPanel, 
					testPanel.getHolder(), 
					buffer,
					this, 
					handler);	
		}
		
		//initialize test drawing panel to reset drawing
		testPanel.initialize();
		
		//initialize the timer to count test elapsed time
		timerTask = new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(MSG_TEST_TIME_CHANGE);
				elapsedTime += 1;
				synchronized(this) {
					if(pause) {
						try {
							wait();
						}
						catch (InterruptedException e) {
							Log.e(this.getClass().getName(), e.getMessage());
						}
					}					
				}
			}			
		};
		timer = new Timer();
		elapsedTime = 0;	
		timer.schedule(timerTask, 0, 1000);
	}	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch (id) {
		case DLG_INSTRUCTION:
			builder.setMessage("Please bind device on your wrist, " +
					"then press start button when you feel ready" +
					" to begin test.")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener()  {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog = builder.create();
			return dialog;
		case DLG_BUFFER_FULL:
			builder.setMessage("This test has already achieved the time limit.\nDo you want to save or discard this test result?")
					.setCancelable(false)
					.setPositiveButton("Save", new DialogInterface.OnClickListener()  {
						public void onClick(DialogInterface dialog, int which) {
							storeToDatabase();
							dialog.dismiss();
						}
					})
					.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();							
						}
					});
			dialog = builder.create();
			return dialog;
		case DLG_DATABASE_ERROR:
			builder.setTitle("Error:")
					.setMessage("Fail to open database on device.\n You cannot save test result on device!")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener()  {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog = builder.create();
			return dialog;
		case DLG_TEST_DONE:
			builder.setMessage("This test has already achieved the time limit.\nDo you want to save or discard this test result?")
					.setCancelable(false)
					.setPositiveButton("Save", new DialogInterface.OnClickListener()  {
						public void onClick(DialogInterface dialog, int which) {
							storeToDatabase();
							dialog.dismiss();
						}
					})
					.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();							
						}
					});
			dialog = builder.create();
			return dialog;	
		}
		return super.onCreateDialog(id);
	}
	
	public boolean storeToDatabase() {
		if(!databaseManager.isOnOpen()) {
			try {
				databaseManager.open();
			}
			catch (SQLException e) {
				Log.e(databaseManager.getClass().getName(), e.getMessage());
				showDialog(DLG_DATABASE_ERROR);
				return false;
			}
		}
		
		databaseManager.createTest(PreferenceManager.getDefaultSharedPreferences(this).getString(User.USER_NAME, "n/a"),
				10, Test.TEST_HAND_TREMOR_LEFT,
				(long)100000,
				(long)101000,
				(long)102000,
				1000,
				"Test record",
				5,
				2,
				buffer.toByteArray());
		return true;
		//Test database insert string
//		String sqlString = "INSERT INTO " + OdlsDbAdapter.DATABASE_TABLE + " VALUES " +
//		"('pdkk2004'," //FIELD_TESTER_ID
//		+ " 10, "	//FIELD_TEST_ID
//		+ Test.TEST_HAND_TREMOR_LEFT 	//FIELD_TYPE
//		+ ", "	//FIELD_DATE
//		+ 100000 + ", " 	//FIELD_BEGIN_TIME
//		+ 101000 + ", "		//FIELD_END_TIME
//		+ 1000 + ", "		//FIELD_DURATION
//		+ "'Test record'" + ", "	//FIELD_EXPLANATION
//		+ 5 + ", "		//FIELD_SAMPLE_RATE
//		+ "2" + ", "
//		+ ");";	//FIELD_SCALE

	
		

	}
}
