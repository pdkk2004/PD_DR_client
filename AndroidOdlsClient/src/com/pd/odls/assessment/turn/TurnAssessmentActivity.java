package com.pd.odls.assessment.turn;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pd.odls.R;
import com.pd.odls.assessment.BaseAssessmentActivity;
import com.pd.odls.assessment.DrawPattern;
import com.pd.odls.assessment.MotionSensingThread;
import com.pd.odls.assessment.MovingObject;
import com.pd.odls.domain.model.Assessment;
import com.pd.odls.domain.model.User;
import com.pd.odls.util.SupportingUtils;
import com.pd.odls.utils.sqlite.OdlsDbAdapter;

public class TurnAssessmentActivity extends BaseAssessmentActivity {
	
	public static final int MSG_TIME_END = 25;
	public static final int MSG_COUNT_DOWN = 24;
	private static final int DLG_BUFFER_FULL = 20;
	private static final int DLG_DATABASE_ERROR = 27;
	private static final int DLG_TEST_DONE = 30;
	
	//UI components
	private Button controlBtn;
	private TextView elapsedTimeView;
	protected ProgressDialog countDownDlg;


	//Create the timer task to count test elapsed time
	private TimerTask timerTask;	
	private Timer timer;
	private int elapsedTime;
	
	//Create the timer task to count down time before test begin
	private CountDownTimerTask countDownTask;
	
	private ByteArrayOutputStream bufferAcc = new ByteArrayOutputStream();  //create the buffer to store acceleration data
	private ByteArrayOutputStream bufferOri = new ByteArrayOutputStream();  //create the buffer to store orientation data

	private DataOutputStream doutAcc;
	private DataOutputStream doutOri;
	
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
				elapsedTimeView.setText(String.valueOf(elapsedTime));
				break;
			case MSG_TIME_END:
				stopTest();
				timer.cancel();
				controlBtn.setText("Start");
				showDialog(TurnAssessmentActivity.DLG_TEST_DONE);
				break;
			case MSG_COUNT_DOWN:
				if(msg.arg1 > 0) {
					TurnAssessmentActivity.this.countDownDlg.setMessage(
							Html.fromHtml("<big><font color='red'>" + String.valueOf(msg.arg1) + "s" + "</font></big>"));
				}
				else {
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
				getResources(), R.drawable.red_point));
		
		//create the test panel 
		testPanel = new TurnAssessmentPanel(this, mo);
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
					Timer t = new Timer();
					countDownDlg = ProgressDialog.show(TurnAssessmentActivity.this, "Ready... Test will begin in", 
							Html.fromHtml("<big><font color='red'>" + String.valueOf(countDownTask.getDuration()) + "s" + "</font></big>"));
					t.schedule(countDownTask, 1000, 1000);
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
		
		//initialize DataOutputStream to store sensed data
		this.doutAcc = new DataOutputStream(bufferAcc);
		this.doutOri = new DataOutputStream(bufferOri);
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
		bufferAcc.reset();
		bufferOri.reset();
		
		//Reset isRunning to false
		isRunning = false;
		pause = false;
		
		//initialize test thread
		if(testThread == null || testThread.getState() == State.TERMINATED) {
			testThread = new TurnAssessmentThread(testPanel, 
					testPanel.getHolder(), 
					doutAcc,
					doutOri,
					this, 
					handler);	
		}
		
		//set DrawMotionTrace interface for test thread, which decouple the DrawMotionTrace with testThread.
		((MotionSensingThread)testThread).setDrawMotionTrace(new DrawPattern() {
			
			public void draw(Canvas canvas) {
				SurfaceHolder surfaceHolder = testPanel.getHolder();
				try {
					canvas = surfaceHolder.lockCanvas();
					synchronized (surfaceHolder) {
						//add moving object position to motion trace, which is stored in ArrayList tracePoints
						canvas.drawColor(Color.WHITE);
						
						Paint paint = new Paint();
						paint.setColor(Color.BLUE);
						paint.setStrokeWidth(2);
						paint.setStyle(Paint.Style.STROKE);

						Point[] pts = ((MotionSensingThread)testThread).getTracePoints().
								toArray(new Point[((MotionSensingThread)testThread).getTracePoints().size()]);
						Path path = new Path();
						path.moveTo(pts[0].x, pts[0].y);
						for (int i = 1; i < pts.length; i++){
							path.lineTo(pts[i].x, pts[i].y);
						}
						canvas.drawPath(path, paint);
					}
				}
				finally {
					if(canvas != null)
						surfaceHolder.unlockCanvasAndPost(canvas);
				}						
			}				
		});
		
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
				if(elapsedTime > 15) {
					handler.sendEmptyMessage(MSG_TIME_END);
				}
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
		long[] pattern = {0, 1000};
		SupportingUtils.vibrate(this, pattern);
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			timer.schedule(timerTask, 0, 1000);
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
			builder.setMessage("Please bind device on one of your ankle, " +
					"then press Next to proceed to next step, or press cancel to exit test")
					.setCancelable(false)
					.setPositiveButton("Next", new DialogInterface.OnClickListener()  {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							try {
								Thread.sleep(300);
							}
							catch(InterruptedException e) {
								
							}
							showDialog(DLG_INSTRUCTION_2);
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {						
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
						}
					});
			dialog = builder.create();
			return dialog;
		case DLG_INSTRUCTION_2:
			builder.setMessage("Please select which direction you want to turn, left or right? ")
					.setCancelable(false)
					.setPositiveButton("Left", new DialogInterface.OnClickListener()  {
						public void onClick(DialogInterface dialog, int which) {
							setTestType(Assessment.TEST_LEFT_TURN);
							dialog.dismiss();
							showDialog(DLG_INSTRUCTION_3);
						}
					})
					.setNegativeButton("Right", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							setTestType(Assessment.TEST_LEFT_TURN);
							dialog.dismiss();
							showDialog(DLG_INSTRUCTION_3);
						}
					});
			dialog = builder.create();
			return dialog;
		case DLG_INSTRUCTION_3:
			builder.setMessage(Html.fromHtml("Are you ready to begin the test? Please press Start button to begin, "
					+ "or exit test by pressing <font color = 'yellow'><b>Back</b></font> button on your cell phone\n<br />" + 
					"<font color = 'red'><b>You will feel a short vibration when test start.</b></font>"))
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
							leave();
						}
					})
					.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							leave();
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
					controlBtn.setText("Start");
					TurnAssessmentActivity.this.leave();
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
			doutAcc.close();
			doutOri.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] data1 = bufferAcc.toByteArray();
		byte[] data2 = bufferOri.toByteArray();
		
		int samplePoints = data1.length / SupportingUtils.BYTES_PER_SAMPLING / 3;
		int testDuration = (int)(endTime - beginTime);
		int sampleRate = (int)(1000 * samplePoints / testDuration);
		String userId = PreferenceManager.getDefaultSharedPreferences(this).getString(User.USER_NAME, "n/a");

		int testId = (int)databaseManager.createTest(
				userId,
				null,
				testType,
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
		bufferAcc.reset();
		bufferOri.reset();
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
	 * Force leave current test
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
		this.bufferAcc.reset();
		this.bufferOri.reset();
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
