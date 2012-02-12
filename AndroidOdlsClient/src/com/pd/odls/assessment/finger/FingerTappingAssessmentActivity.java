package com.pd.odls.assessment.finger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.Timer;
import java.util.TimerTask;

import android.R.color;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.pd.odls.R;
import com.pd.odls.assessment.BaseAssessmentActivity;
import com.pd.odls.assessment.MovingObject;
import com.pd.odls.domain.model.Assessment;
import com.pd.odls.domain.model.User;
import com.pd.odls.util.SupportingUtils;
import com.pd.odls.utils.sqlite.OdlsDbAdapter;

public class FingerTappingAssessmentActivity extends BaseAssessmentActivity {
	private static final String TAG = FingerTappingAssessmentActivity.class.getSimpleName();

	public static final int MSG_BUFFER_FULL = 25;
	public static final int MSG_COUNT_DOWN = 24;
	private static final int DLG_DATABASE_ERROR = 27;
	private static final int DLG_TEST_DONE = 30;
	
	//UI components
	private ToggleButton controlBtn;
	private ProgressBar testProgressBar;
	protected ProgressDialog countDownDlg;

	//Create the timer task to count test elapsed time
	private TimerTask timerTask;	
	private Timer timer;
	private int elapsedTime;
	
	//Create the timer task to count down time before test begin
	private CountDownTimerTask countDownTask;
	
	private ByteArrayOutputStream bufferTime = new ByteArrayOutputStream();  //create the buffer to store 20 s test data at 5Hz
	private DataOutputStream doutTime;
	private ByteArrayOutputStream bufferCount = new ByteArrayOutputStream();
	private DataOutputStream doutCount;

	
	private OdlsDbAdapter databaseManager;
	
	private int testType;

	public int getTestType() {
		return testType;
	}

	public void setTestType(int testType) {
		this.testType = testType;
	}
	
	//Create handler to deal with UI change
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MSG_TEST_TIME_CHANGE:
				testProgressBar.setProgress((int)Math.round(elapsedTime / 30.0 * 100.0));
				break;
			case MSG_BUFFER_FULL:
				stopTest();
				timer.cancel();
//				controlBtn.setImageResource(R.drawable.start_80);
				showDialog(FingerTappingAssessmentActivity.DLG_BUFFER_FULL);
				
				DataInputStream din = new DataInputStream(new ByteArrayInputStream(bufferTime.toByteArray()));
				try {
					while(true) {
						System.out.print(din.readFloat() + " ");
					}
				}
				catch(EOFException eof) {
					Log.i(this.toString(), "Reach end of buffer");
				}
				catch(IOException e) {
					Log.e(FingerTappingAssessmentActivity.class.getCanonicalName(), e.getMessage());
				}
				break;
			case MSG_COUNT_DOWN:
				if(msg.arg1 > 0) {
					System.out.println("Count time -1");
					FingerTappingAssessmentActivity.this.countDownDlg.setMessage(
							Html.fromHtml("<big><font color='red'>" + String.valueOf(msg.arg1) + "s" + "</font></big>"));
				}
				else {
					System.out.println("Ready to begin");
					countDownDlg.dismiss();
					beginTest();
				}
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(DLG_INSTRUCTION_1);
		
		//create the moving objects for test panel
		MovingObject mo = new MovingObject(BitmapFactory.decodeResource(
				getResources(), R.drawable.target_80));
		
		//create the test panel
		testPanel = new FingerTappingAssessmentPanel(this, mo);
		
		testPanel.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					if(isRunning && ((FingerTappingAssessmentPanel)testPanel).
							hitTouchTarget((int)event.getX(), (int)event.getY())) {
						try {
							Log.i(TAG, "Hit target at:" + (int)event.getX() + " " + (int)event.getY());
							doutCount.writeBoolean(true);
						}
						catch(IOException e) {
							e.printStackTrace();
						}
						synchronized(testThread) {
							testThread.notify();
						}
					}
					else if(isRunning) {
						try {
							Log.i(TAG, "Miss target at:" + (int)event.getX() + " " + (int)event.getY());
							doutCount.writeBoolean(false);
						}
						catch(IOException e) {
							e.printStackTrace();
						}
					}
					else {
						//Do nothing. 
					}
					return true;
				}
				return false;
			}		
		});
		
		testPanel.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		//set the frame layout as the container to overlay test panel, control button, and test progress bar
		FrameLayout layout = new FrameLayout(this);
		layout.setBackgroundColor(color.white);
		
		layout.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		//set the control button to start/stop test
		controlBtn = new ToggleButton(this);
		controlBtn.setText(" ");
		controlBtn.setTextOn(" ");
		controlBtn.setTextOff(" ");
		controlBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_btn_selector));
		
		controlBtn.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL));
		
		controlBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				if(isRunning == false) {
					initializeTest();
					Timer t = new Timer();
					countDownDlg = ProgressDialog.show(FingerTappingAssessmentActivity.this, "Ready... Test will begin in", 
							Html.fromHtml("<big><font color='red'>" + String.valueOf(countDownTask.getDuration()) + "s" + "</font></big>"));
					t.schedule(countDownTask, 1000, 1000);
				}
				else {
					stopTest();
					timer.cancel();
					showDialog(DLG_TEST_DONE);
				}
			}		
		});	
		
		//set the test progress bar
		testProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal | android.R.attr.progressBarStyleLarge);
		testProgressBar.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL));
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(new MarginLayoutParams(0, 10));
		testProgressBar.setLayoutParams(params);
		testProgressBar.setProgress(0);
		
		//set up the view layout
		layout.addView(testPanel);
		layout.addView(testProgressBar);
		layout.addView(controlBtn);

		//set view content to activity
		setContentView(layout);
		
		//prepare database manager to save test data
		databaseManager = new OdlsDbAdapter(this);
		
		//initialize DataOutputStream to store sensed data
		this.doutTime = new DataOutputStream(bufferTime);
		this.doutCount = new DataOutputStream(bufferCount);
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
	
//	@Override
//	protected void beginTest() {
//		Timer t = new Timer();
//		countDownDlg = ProgressDialog.show(this, "Ready... Test will begin in", 
//				Html.fromHtml("<big><font color='red'>" + String.valueOf(countDownTask.getDuration()) + "s" + "</font></big>"));
//		t.schedule(countDownTask, 1000, 1000);
//	}
	
	@Override
	protected void initializeTest() {
		//Reset buffer to empty
		bufferTime.reset();
		
		//Reset isRunning to false
		isRunning = false;
		pause = false;
		
		//initialize test thread
		if(testThread == null || testThread.getState() == State.TERMINATED) {
			testThread = new FingerTappingAssessmentThread(testPanel, 
					testPanel.getHolder(), 
					doutTime,
					this, 
					handler);
		}
		
		//initialize count down timer task before task beginning
		this.setCountDownTask(new CountDownTimerTask(3, handler));
		
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
	}	
	
	
	
	@Override
	protected void beginTest() {
		//Vibrate for 500 milliseconds to indicate test begin
		long[] pattern = {0, 500};
		SupportingUtils.vibrate(this, pattern);
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			timer.schedule(timerTask, 0, 1000);
			testProgressBar.setVisibility(View.VISIBLE);
			super.beginTest();
		}
	}

	@Override
	protected void stopTest() {
		super.stopTest();
		timer.cancel();
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
		//Vibrate for 500 milliseconds stop 200 millisecond for 2 times to indicate test stop
			long[] pattern = {0, 500, 200, 500};
			SupportingUtils.vibrate(this, pattern);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch (id) {
		case DLG_INSTRUCTION_1:
			builder.setMessage(Html.fromHtml("Please hold your device in landscape orientation. \n<br />" +
					"<font color = 'yellow'><b>Then please select which hand are you going to test, Left or Right?</b></front>"))
					.setCancelable(false)
					.setPositiveButton("Left", new DialogInterface.OnClickListener()  {
						public void onClick(DialogInterface dialog, int which) {
							setTestType(Assessment.TEST_FINGER_TAPPING_LEFT);
							dialog.dismiss();
							showDialog(DLG_INSTRUCTION_2);
						}
					})
					.setNegativeButton("Right", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							setTestType(Assessment.TEST_FINGER_TAPPING_RIGHT);
							dialog.dismiss();
							showDialog(DLG_INSTRUCTION_2);
						}
					});
			dialog = builder.create();
			return dialog;
		case DLG_INSTRUCTION_2:
			builder.setMessage(Html.fromHtml("In this test, you need to use your index finger to touch the red target, alternative displaying on diagnal position on the screen?" +
					"The test time is 30 seconds.Please press <font color = 'red'><b>Start</b></font> button to begin, "
					+ "or exit test by pressing <font color = 'yellow'><b>Back</b></font> button on your cell phone\n<br />" + 
					"<font color = 'red'><b>You will have 3 seconds to get ready after you press Start.</b></font>"))
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
							storeTest();
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
					.setMessage("Fail to access database. The test result has not been saved.\n" +
							"Please try again or check your device database setting!")
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener()  {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog = builder.create();
			return dialog;
		case DLG_TEST_DONE:
			builder.setMessage("Test completed.\nDo you want to save or discard this test result?")
					.setCancelable(false)
					.setPositiveButton("Save", new DialogInterface.OnClickListener()  {
						public void onClick(DialogInterface dialog, int which) {
							storeTest();
							dialog.dismiss();
						}
					})
					.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							bufferTime.reset();
						}
					});
			dialog = builder.create();
			return dialog;
		case DLG_INTERRUPT_TEST:
			builder.setMessage("Are you sure you are going to leave current ruuning test? The test data will not be saved if you leave.\n" +
					"Press OK to leave current test.\n" +
					"Press Cancel to resume to test.")
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener()  {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
//					pauseOrResume();
					stopTest();
					timer.cancel();
					FingerTappingAssessmentActivity.this.leave();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					pauseOrResume();
				}
			});
	dialog = builder.create();
	return dialog;			
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public boolean storeTest() {
		//store to database
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
		
		try {
			doutTime.close();
			doutCount.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] data1 = bufferTime.toByteArray();
		byte[] data2 = bufferCount.toByteArray();
		
		int samplePoints = data1.length / SupportingUtils.BYTES_PER_SAMPLING / 3;
		int testDuration = (int)(endTime - beginTime);
		int sampleRate = (int)(1000 * samplePoints / testDuration);
		String userId = PreferenceManager.getDefaultSharedPreferences(this).getString(User.USER_NAME, "n/a");

		int testId = (int)databaseManager.createTest(
				userId,
				null,
				testType,
				beginTime,
				beginTime,
				endTime,
				testDuration,
				"Test record",
				sampleRate,
				null,
				data1,
				data2
		);
						
		//write to xml file
		Assessment toXML = new Assessment();
		toXML.instantiateTest(
				userId,
				testId,
				testType,
				beginTime,
				beginTime,
				endTime,
				testDuration,
				"Test record",
				sampleRate,
				null,
				data1,
				data2);

		try {
			String xml = toXML.toXML(this);
			String path = userId + "_" + testId + ".xml";
			SupportingUtils.saveFileToInternalStorage(this, xml, path);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		
		//clear buffer for next use
		bufferTime.reset();
		bufferCount.reset();
		return true;
	}

	public TimerTask getCountDownTask() {
		return countDownTask;
	}

	public void setCountDownTask(CountDownTimerTask countDownTask) {
		this.countDownTask = countDownTask;
	}
	
	/**
	 * TimerTask to perform the time count down before beginning test.
	 * @author Pan
	 *
	 */
	private class CountDownTimerTask extends TimerTask {
		
		private int duration;
		private Handler handler;
		
		public CountDownTimerTask(int duration, Handler h) {
			this.duration = duration;
			this.handler = h;
		}
		
		@Override
		public void run() {
			duration--;
			Message m = new Message();
			m.arg1 = duration;
			m.what = MSG_COUNT_DOWN;
			handler.sendMessage(m);
			if(duration <= 0) {
				this.cancel();
			}
		}

		public int getDuration() {
			return duration;
		}		
	}

	@Override
	public void finish() {
		if(isRunning) {
			pauseOrResume();
			this.showDialog(DLG_INTERRUPT_TEST);
		}
		else {
			super.finish();			
		}
	}
	
	/*
	 * Release resource, close database and then call finish() to go back to parent view.
	 */
	public void leave() {
		testThread = null;
		try {
			if(databaseManager.isOnOpen())
				databaseManager.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		super.finish();
	}
	
	@Override
	protected void onDestroy() {
		testThread = null;
		try {
			if(databaseManager.isOnOpen())
				databaseManager.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		databaseManager = null;
		super.onDestroy();
	}
	

}
