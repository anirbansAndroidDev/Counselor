package com.ica.allenquiry;

import java.util.Iterator;
import java.util.List;

import com.ica.commons.DatabaseHandler;
import com.ica.icacounselor.Follow_ups_list_first_level;
import com.ica.icacounselor.R;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.widget.TableRow.LayoutParams;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class AllEnquiryTableActivity extends Activity {

	TableLayout allEnquiry_table;
	TableRow allEnquiry_tr_data;

	List<String> student_name_studname_asc;
	List<String> id_studname_asc;

	List<String> student_name_list_studname_dsc;
	List<String> id_studname_dsc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_enquiry_table);

		allEnquiry_table=(TableLayout) findViewById(R.id.all_enquiry_table);

		populateTable("nameAsc");
	}
	@Override
	public void onBackPressed() {
		Log.d("Back Btn Pressed", "Back Btn Pressed From Enquiry level");
		Intent i = new Intent(this, Follow_ups_list_first_level.class);
		startActivity(i);
		super.onBackPressed();
	}

	public void populateTable(final String state)
	{
		if(state.equalsIgnoreCase("nameAsc"))
		{

			getStudentNameStudentAsc();
			getIdStudentAsc();
		}
		else if(state.equalsIgnoreCase("nameDsc"))
		{
			getStudentNameStudentDsc();
			getIdStudentDsc();
		}

		allEnquiry_table.removeAllViews();
		//---------------Table Header-----------------------------------------------
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


		allEnquiry_table.addView(followup_tr_head, new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		//--------------- Table Header-----------------------------------------------
		Iterator itr = null;
		Iterator itr2 = null;

		if(state.equalsIgnoreCase("nameAsc"))
		{

			itr = student_name_studname_asc.iterator();
			itr2 = id_studname_asc.iterator();
		}
		else if(state.equalsIgnoreCase("nameDsc"))
		{

			itr = student_name_list_studname_dsc.iterator();
			itr2 = id_studname_dsc.iterator();
		}

		while(itr.hasNext())
		{

			while(itr2.hasNext())
			{
				//----------------table body------------------------------------------
				allEnquiry_tr_data = new TableRow(this);
				allEnquiry_tr_data.setId(10);
				allEnquiry_tr_data.setBackgroundResource(R.drawable.grey_list_bg);
				allEnquiry_tr_data.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));


				final TextView timeNameText = new TextView(this);
				timeNameText.setId(20);
				timeNameText.setText("  ");
				timeNameText.setTextColor(Color.BLACK);
				timeNameText.setPadding(5,5,5,5);
				timeNameText.setTextSize(10);
				allEnquiry_tr_data.addView(timeNameText);// add the column to the table row here

				final TextView StudentNameText = new TextView(this);
				StudentNameText.setId(20);
				StudentNameText.setText(Html.fromHtml(itr.next().toString()));
				StudentNameText.setTextColor(Color.BLACK);
				StudentNameText.setPadding(5,5,5,5);
				StudentNameText.setTextSize(10);
				allEnquiry_tr_data.addView(StudentNameText);// add the column to the table row here

				final TextView IdText = new TextView(this);
				IdText.setId(20);
				IdText.setText(Html.fromHtml(itr2.next().toString()));
				IdText.setTextColor(Color.BLACK);
				IdText.setPadding(5,5,5,5);
				IdText.setTextSize(10);
				IdText.setVisibility(View.GONE);
				allEnquiry_tr_data.addView(IdText);// add the column to the table row here

				allEnquiry_table.addView(allEnquiry_tr_data, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));

				//----------------------On click table row---------------------------------------

				allEnquiry_tr_data.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						//makeAToast(IdText.getText().toString());
						saveInPreference("allEnqueryId", IdText.getText().toString());
						saveInPreference("allEnqueryName", StudentNameText.getText().toString());
						Intent intent = new Intent(AllEnquiryTableActivity.this, AllEnquiryDetailsActivity.class);
						AllEnquiryTableActivity.this.startActivity(intent);
					}
				});
				//----------------------On click table row---------------------------------------
			}
		}
		//----------------table body------------------------------------------
	}

	//==================================================================================//	
	//=============Functions for DB=====================================================//
	//==================================================================================//	

	//=================Student name ascending=================================


	public void getStudentNameStudentAsc()
	{
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());

		student_name_studname_asc = db.getStudentNameAllEnquiryStudentAsc();
		Iterator itr = student_name_studname_asc.iterator();
		while(itr.hasNext())
		{
			System.out.println(itr.next());
		}
	}

	public void getIdStudentAsc()
	{
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());

		id_studname_asc = db.getIdAllEnquiryStudentAsc();
		Iterator itr = id_studname_asc.iterator();
		while(itr.hasNext())
		{
			System.out.println(itr.next());
		}

	}

	//=================Student name ascending=================================



	//=================Student name descending=================================

	public void getStudentNameStudentDsc()
	{
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());

		student_name_list_studname_dsc = db.getStudentNameAllEnquiryStudentDsc();
		Iterator itr = student_name_list_studname_dsc.iterator();
		while(itr.hasNext())
		{
			System.out.println(itr.next());
		}
	}

	public void getIdStudentDsc()
	{
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());

		id_studname_dsc = db.getIdAllEnquiryStudentDsc();
		Iterator itr = id_studname_dsc.iterator();
		while(itr.hasNext())
		{
			System.out.println(itr.next());
		}

	}

	//=================Student name descending=================================




	//==================================================================================//	
	//=============Functions for DB=====================================================//
	//==================================================================================//

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
