package com.ica.icacounselor;

import java.util.Iterator;
import java.util.List;

import com.ica.allenquiry.AllEnquiryTableActivity;
import com.ica.commons.DatabaseHandler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class Follow_ups_list_third_level extends Activity {

	TableLayout follow_up_table;
	TableRow followup_tr_data;

	List<String> time_name_list_time_asc;
	List<String> student_name_list_time_asc;

	List<String> time_name_list_time_dsc;
	List<String> student_name_list_time_dsc;

	List<String> time_name_list_studname_asc;
	List<String> student_name_studname_asc;

	List<String> time_name_list_studname_dsc;
	List<String> student_name_list_studname_dsc;
	
	String probFlag = null;
	String dayFlag = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follow_ups_list_third_level);

		follow_up_table=(TableLayout) findViewById(R.id.follow_up_table);

		//===========================================================================================================
		//Getting data from previous activity
		//===========================================================================================================
		Bundle extras = getIntent().getExtras();
		if (extras == null) 
		{
			return;
		}
		probFlag = extras.getString("probFlag");
		dayFlag = extras.getString("dayFlag");
		//===========================================================================================================
		//END Getting data from previous activity
		//===========================================================================================================

		populateTable("timeAsc");
	}
	
	@Override
	public void onBackPressed() {
		String comStr = getFromPreference("SendOnBackBtnFromThird");
		
		if(comStr.equalsIgnoreCase("Today"))
		{
			Log.d("Day", "Today");
			Intent showList = new Intent(Follow_ups_list_third_level.this, Follow_ups_list_second_level.class);
			showList.putExtra("DayFlag", "TODAY");
			startActivity(showList);
		}
		else if(comStr.equalsIgnoreCase("Tomorrow"))
		{
			Log.d("Day", "Tomorrow");
			Intent showList = new Intent(Follow_ups_list_third_level.this, Follow_ups_list_second_level.class);
			showList.putExtra("DayFlag", "TOMORROW");
			startActivity(showList);
		}
		else if(comStr.equalsIgnoreCase("Day after tomorrow"))
		{
			Log.d("Day", "Day after tomorrow");
			Intent showList = new Intent(Follow_ups_list_third_level.this, Follow_ups_list_second_level.class);
			showList.putExtra("DayFlag", "DAY AFTER TOMORROW");
			startActivity(showList);
		}
		else if(comStr.equalsIgnoreCase("Rest"))
		{
			Log.d("Day", "Rest");
			Intent showList = new Intent(Follow_ups_list_third_level.this, Follow_ups_list_second_level.class);
			showList.putExtra("DayFlag", "REST");
			startActivity(showList);
		}
		else if(comStr.equalsIgnoreCase("Enquery"))
		{
			Log.d("Day", "Enquery");
			Intent intent = new Intent(Follow_ups_list_third_level.this, AllEnquiryTableActivity.class);
        	Follow_ups_list_third_level.this.startActivity(intent);
		}
		super.onBackPressed();
	}

	//===================================================================================================================
	// Functions for Table View
	//===================================================================================================================

	//Time ascending method
	public void populateTable(final String state)
	{
		if(state.equalsIgnoreCase("timeAsc"))
		{
			getTimeNameTimeAsc();
			getStudentNameTimeAsc();
		}
		else if(state.equalsIgnoreCase("timeDsc"))
		{
			getTimeNameTimeDsc();
			getStudentNameTimeDsc();
		}
		else if(state.equalsIgnoreCase("nameAsc"))
		{
			getTimeNameSyudentAsc();
			getStudentNameStudentAsc();
		}
		else if(state.equalsIgnoreCase("nameDsc"))
		{
			getTimeNameStudentDsc();
			getStudentNameStudentDsc();
		}

		follow_up_table.removeAllViews();

		//--------------------------------------------------------------------------------------------------
		// Creating Table Header
		//--------------------------------------------------------------------------------------------------

		TableRow followup_tr_head = new TableRow(this);
		followup_tr_head.setId(10);
		followup_tr_head.setBackgroundResource(R.drawable.list_header);
		followup_tr_head.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));


		final TextView time_name_title = new TextView(this);
		time_name_title.setId(20);
		time_name_title.setText("Time");
		time_name_title.setTextColor(Color.WHITE);
		time_name_title.setPadding(5,5,5,5);
		followup_tr_head.addView(time_name_title);// add the column to the table row here
		time_name_title.setTextSize(12);    

		final TextView student_name_title = new TextView(this);
		student_name_title.setId(20);
		student_name_title.setText("Student Name");
		student_name_title.setTextColor(Color.WHITE);
		student_name_title.setPadding(5,5,5,5);
		followup_tr_head.addView(student_name_title);// add the column to the table row here
		student_name_title.setTextSize(12);    

		//----------------------On click time_name_title---------------------------------------
		time_name_title.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					time_name_title.setTextColor(Color.YELLOW);
					break;
				case MotionEvent.ACTION_UP:
					time_name_title.setTextColor(Color.YELLOW);
					break;
				}
				return false;
			}
		});

		time_name_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(state.equalsIgnoreCase("timeAsc"))
				{
					populateTable("timeDsc");
				}
				else if(state.equalsIgnoreCase("timeDsc"))
				{
					populateTable("timeAsc");
				}
				else
				{
					populateTable("timeAsc");
				}

			}
		});
		//----------------------On click time_name_title---------------------------------------



		//----------------------On click student_name_title---------------------------------------
		student_name_title.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					student_name_title.setTextColor(Color.YELLOW);
					break;
				case MotionEvent.ACTION_UP:
					student_name_title.setTextColor(Color.YELLOW);
					break;
				}
				return false;
			}
		});

		student_name_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(state.equalsIgnoreCase("nameAsc"))
				{
					populateTable("nameDsc");
				}
				else if(state.equalsIgnoreCase("nameDsc"))
				{
					populateTable("nameAsc");
				}
				else
				{
					populateTable("nameAsc");
				}


			}
		});
		//----------------------On click student_name_title---------------------------------------


		follow_up_table.addView(followup_tr_head, new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		//--------------------------------------------------------------------------------------------------
		// END Creating Table Header
		//--------------------------------------------------------------------------------------------------

		Iterator itr = null;
		Iterator itr2 = null;

		if(state.equalsIgnoreCase("timeAsc"))
		{
			itr = time_name_list_time_asc.iterator();
			itr2 = student_name_list_time_asc.iterator();
		}
		else if(state.equalsIgnoreCase("timeDsc"))
		{
			itr = time_name_list_time_dsc.iterator();
			itr2 = student_name_list_time_dsc.iterator();
		}
		else if(state.equalsIgnoreCase("nameAsc"))
		{
			itr = time_name_list_studname_asc.iterator();
			itr2 = student_name_studname_asc.iterator();
		}
		else if(state.equalsIgnoreCase("nameDsc"))
		{
			itr = time_name_list_studname_dsc.iterator();
			itr2 = student_name_list_studname_dsc.iterator();
		}


		while(itr.hasNext())
		{
			while(itr2.hasNext())
			{
				//--------------------------------------------------------------------------------------------------
				// Creating Table Body
				//--------------------------------------------------------------------------------------------------
				followup_tr_data = new TableRow(this);
				followup_tr_data.setId(10);
				followup_tr_data.setBackgroundResource(R.drawable.grey_list_bg);
				followup_tr_data.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));


				final TextView timeNameText = new TextView(this);
				timeNameText.setId(20);
				timeNameText.setText(Html.fromHtml(itr.next().toString()));
				timeNameText.setTextColor(Color.BLACK);
				timeNameText.setPadding(5,5,5,5);
				timeNameText.setTextSize(10);
				followup_tr_data.addView(timeNameText);// add the column to the table row here

				String[] tempArray = itr2.next().toString().split(",");

				final TextView StudentNameText = new TextView(this);
				StudentNameText.setId(20);
				StudentNameText.setText(Html.fromHtml(tempArray[0]));
				StudentNameText.setTextColor(Color.BLACK);
				StudentNameText.setPadding(5,5,5,5);
				StudentNameText.setTextSize(10);
				followup_tr_data.addView(StudentNameText);// add the column to the table row here
				
				final TextView IdText = new TextView(this);
				IdText.setId(20);
				IdText.setText(Html.fromHtml(tempArray[1]));
				IdText.setTextColor(Color.BLACK);
				IdText.setPadding(5,5,5,5);
				IdText.setTextSize(10);
				IdText.setVisibility(View.GONE);
				followup_tr_data.addView(IdText);// add the column to the table row here

				follow_up_table.addView(followup_tr_data, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));

				//--------------------------------------------------------------------------------------------------
				// END Creating Table Body
				//--------------------------------------------------------------------------------------------------

				//----------------------On click table row---------------------------------------

				followup_tr_data.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) 
					{
						saveInPreference("followUpId", IdText.getText().toString());
						saveInPreference("followUpName", StudentNameText.getText().toString());
						Intent showList = new Intent(Follow_ups_list_third_level.this, Follow_ups_list_fourth_level.class);
						startActivity(showList);
					}
				});
				//----------------------On click table row---------------------------------------

			}

		}
	}
	//===================================================================================================================
	// END Functions for Table View
	//===================================================================================================================




	//=====================================================================================================================	
	//Functions for DB
	//=====================================================================================================================


			//=================Time name ascending====================================
			public void getTimeNameTimeAsc()
			{
		
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
				time_name_list_time_asc = db.getTimeNameTimeAsc(probFlag,dayFlag);
				Iterator itr = time_name_list_time_asc.iterator();
				while(itr.hasNext())
				{
					System.out.println(itr.next());
				}
		
			}
		
			public void getStudentNameTimeAsc()
			{
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
				student_name_list_time_asc = db.getStudentNameTimeAsc(probFlag,dayFlag);
				Iterator itr = student_name_list_time_asc.iterator();
				while(itr.hasNext())
				{
					System.out.println(itr.next());
				}
			}
		
			//=================END Time name ascending=================================
		
		
		
			//=================Time name descending====================================
			public void getTimeNameTimeDsc()
			{
		
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
				time_name_list_time_dsc = db.getTimeNameTimeDsc(probFlag,dayFlag);
				Iterator itr = time_name_list_time_dsc.iterator();
				while(itr.hasNext())
				{
					System.out.println(itr.next());
				}
		
			}
		
			public void getStudentNameTimeDsc()
			{
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
				student_name_list_time_dsc = db.getStudentNameTimeDsc(probFlag,dayFlag);
				Iterator itr = student_name_list_time_dsc.iterator();
				while(itr.hasNext())
				{
					System.out.println(itr.next());
				}
			}
		
			//=================END Time name descending=================================
		
		
			
			//=================Student name ascending===================================
			public void getTimeNameSyudentAsc()
			{
		
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
				time_name_list_studname_asc = db.getTimeNameSyudentAsc(probFlag,dayFlag);
				Iterator itr = time_name_list_studname_asc.iterator();
				while(itr.hasNext())
				{
					System.out.println(itr.next());
				}
		
			}
		
			public void getStudentNameStudentAsc()
			{
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
				student_name_studname_asc = db.getStudentNameStudentAsc(probFlag,dayFlag);
				Iterator itr = student_name_studname_asc.iterator();
				while(itr.hasNext())
				{
					System.out.println(itr.next());
				}
			}
		
			//=================END Student name ascending=================================
		
		
		
			//=================Student name descending====================================
			public void getTimeNameStudentDsc()
			{
		
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
				time_name_list_studname_dsc = db.getTimeNameStudentDsc(probFlag,dayFlag);
				Iterator itr = time_name_list_studname_dsc.iterator();
				while(itr.hasNext())
				{
					Log.d("Error from", "getTimeNameStudentDsc");
					System.out.println(itr.next());
				}
		
			}
		
			public void getStudentNameStudentDsc()
			{
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
				student_name_list_studname_dsc = db.getStudentNameStudentDsc(probFlag,dayFlag);
				Iterator itr = student_name_list_studname_dsc.iterator();
				while(itr.hasNext())
				{
					Log.d("Error from", "getStudentNameStudentDsc");
					System.out.println(itr.next());
				}
			}
		
			//=================END Student name descending=================================
		

	//=====================================================================================================================	
	// END Functions for DB
	//=====================================================================================================================

	//method to show toast message
	public void makeAToast(String str) {
		//yet to implement
		Toast toast = Toast.makeText(this,str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	// getting content from preferences
	public String getFromPreference(String variable_name) {
		String preference_return;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		preference_return = preferences.getString(variable_name, "");

		return preference_return;
	}

	// method to save variable in preference
	public void saveInPreference(String name, String content) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(name, content);
		editor.commit();
	}

}
