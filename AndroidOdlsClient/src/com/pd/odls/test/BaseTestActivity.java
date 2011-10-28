package com.pd.odls.test;

import android.app.Activity;


public abstract class BaseTestActivity extends Activity {
	protected static final int DLG_INSTRUCTION = 19;
	protected static final int MSG_TEST_TIME_CHANGE = 16;

	protected BaseTestPanel testPanel;
	protected boolean isRunning;
	protected boolean pause;
	protected BaseTestThread testThread;
	protected long beginTime = 0;
	protected long endTime = 0;

	
	protected void beginTest() {
		isRunning = true;
		synchronized (testThread) {
			testThread.setRunning(this.isRunning);
			testThread.start();
		}
		beginTime = System.currentTimeMillis();
	}
	
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
		}
	}
	
	protected void stopTest() {
		isRunning = false;
		synchronized (testThread) {
			if(testThread != null) {
					testThread.setRunning(isRunning);
			}
		}
		endTime = System.currentTimeMillis();
	}
	
	/**
	 * To set test thread in this method. Aimed to be implemented
	 */
	abstract protected void initializeTest();
}
