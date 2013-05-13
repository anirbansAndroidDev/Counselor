package com.ica.allenquiry;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ica.commons.DatabaseHandler;
import com.ica.icacounselor.Follow_ups_list_first_level;
import com.ica.icacounselor.Follow_ups_list_fourth_level;
import com.ica.icacounselor.Follow_ups_list_third_level;
import com.ica.icacounselor.R;
import com.ica.icacounselor.Remarks_pop_up;

public class AllEnquiryDetailsActivity extends Activity {

	TextView name;
	TextView mobile;
	TextView source;

	String mobileStr;
	String sourceStr;

	Button remarks;
	Button call;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_enquiry_details);

		// assigning objects to layout
		name = (TextView) findViewById(R.id.name_textView);
		mobile = (TextView) findViewById(R.id.mobile_no_textView);
		source = (TextView) findViewById(R.id.source_textView);
		remarks= (Button)findViewById(R.id.remarks_button);
		call= (Button)findViewById(R.id.call_button);
		// showing student full name
		name.setText(getFromPreference("allEnqueryName"));

		mobile.setText(getMobile());
		getSource();

		saveInPreference("Mobile",getMobile());
		saveInPreference("Enquiry_id",getEnquiryId());

		//remarks button onclick
		remarks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(AllEnquiryDetailsActivity.this, Remarks_pop_up.class);
				intent.putExtra("from", "enquery");
				AllEnquiryDetailsActivity.this.startActivity(intent);

			}
		});

		//call button onclick
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				call();

			}
		});
	}
	@Override
	public void onBackPressed() {
		
		try 
		{
			Intent intent = new Intent(AllEnquiryDetailsActivity.this, AllEnquiryTableActivity.class);
			AllEnquiryDetailsActivity.this.startActivity(intent);
		} 
		catch (Throwable e) 
		{
			Log.d("Error", e+"");
		}
		super.onBackPressed();
	}

	//method to get mobile number from database
	String getMobile()
	{
		// getting status from database
		DatabaseHandler db = new DatabaseHandler(
				getApplicationContext());

		mobileStr= db.selectAllEnqueryMobile(getFromPreference("allEnqueryId"));
		return mobileStr;
	}

	//method to get Enquire Id from database
	String getEnquiryId()
	{
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());

		String EnquiryId = db.selectAllEnquiryEnquiryId(getFromPreference("allEnqueryId"));
		return EnquiryId;
	}

	//method to get source from database
	void getSource()
	{
		// getting status from database
		DatabaseHandler db = new DatabaseHandler(
				getApplicationContext());

		sourceStr= db.selectAllEnquerySource(getFromPreference("allEnqueryId"));
		source.setText(sourceStr);

	}

	private void call() {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"+mobileStr));
			startActivity(callIntent);
		} catch (ActivityNotFoundException e) {
			Log.e("helloandroid dialing example", "Call failed", e);
		}
	}

	// method to show toast message
	public void makeAToast(String str) {
		// yet to implement
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}


	//===================================================================================================================================
	//Preference variable
	//===================================================================================================================================

	//--------------------------------------------
	// method to save variable in preference
	//--------------------------------------------
	public void saveInPreference(String name, String content) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(name, content);
		editor.commit();
	}

	//--------------------------------------------
	// getting content from preferences
	//--------------------------------------------
	public String getFromPreference(String variable_name) {
		String preference_return;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		preference_return = preferences.getString(variable_name, "");

		return preference_return;
	}


	//===================================================================================================================================
	//Preference variable
	//===================================================================================================================================


}
