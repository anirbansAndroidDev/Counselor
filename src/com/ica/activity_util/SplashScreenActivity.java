package com.ica.activity_util;

import com.ica.icacounselor.Login_activity;
import com.ica.icacounselor.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class SplashScreenActivity extends Activity {
	ImageView logo;

	// used to know if the back button was pressed in the splash screen activity
	// and avoid opening the next activity  
	private boolean mIsBackButtonPressed;
	private static final int SPLASH_DURATION = 2000; // 2 seconds

	String remember_user = null;
	String remember_password = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		logo= (ImageView) findViewById(R.id.imageView1);
		
		Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotator);
		logo.startAnimation(anim);
		
		Handler handler = new Handler();

		// run a thread after 2 seconds to start the home screen
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				
				

				// make sure we close the splash screen so the user won't come
				// back when it presses back key

				finish();

				if (!mIsBackButtonPressed) {

					Intent intent = new Intent(SplashScreenActivity.this,
							Login_activity.class);
					SplashScreenActivity.this.startActivity(intent);

				}

			}

		}, SPLASH_DURATION); // time in milliseconds (1 second = 1000
								// milliseconds) until the run() method will be
								// called

	}

	@Override
	public void onBackPressed() {

		// set the flag to true so the next activity won't start up
		mIsBackButtonPressed = true;
		super.onBackPressed();

	}

	public void makeAToast(String str) {
		// yet to implement
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}


}
