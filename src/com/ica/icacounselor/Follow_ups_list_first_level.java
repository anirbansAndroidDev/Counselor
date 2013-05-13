package com.ica.icacounselor;

import java.util.ArrayList;

import com.ica.activity_util.MyCustomAdapter;
import com.ica.activity_util.Parent;
import com.ica.allenquiry.AllEnquiryTableActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

public class Follow_ups_list_first_level extends Activity{
	private ExpandableListView mExpandableList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follow_ups_list_first_level);
		mExpandableList = (ExpandableListView)findViewById(R.id.expandable_list);
		
		ArrayList<Parent> arrayParents = new ArrayList<Parent>();
		ArrayList<String> arrayChildren = new ArrayList<String>();

        Parent parent1 = new Parent();
        parent1.setTitle("Pending Follow-ups");
        
		// getting user No of All Enquire from preference
		String cToday 	 		 = getFromPreference("cToday");
		String cTomorrow 		 = getFromPreference("cTomorrow");
		String cDayAfterTomorrow = getFromPreference("cDayAfterTomorrow");
		String cRest 			 = getFromPreference("cRest");
		String cOld				 = getFromPreference("cOld");
		
		int today 		 = Integer.parseInt(cToday);
		int old    		 = Integer.parseInt(cOld);
		int todayPlusOld = today + old;
		
        arrayChildren = new ArrayList<String>();
        arrayChildren.add("Today ("+ todayPlusOld +")");
        arrayChildren.add("Tomorrow ("+ cTomorrow +")");
        arrayChildren.add("Day after tomorrow ("+ cDayAfterTomorrow +")");
        arrayChildren.add("Rest ("+ cRest +")");
        
        parent1.setArrayChildren(arrayChildren);
        
        arrayParents.add(parent1);
        
        Parent parent2 = new Parent();
        parent2.setTitle("Enqueries (Not Yet Called)");
        
        
		// getting user No of All Enquire from preference
		String noOfEnquiry = getFromPreference("noOfEnquiry");

        arrayChildren = new ArrayList<String>();
        arrayChildren.add("Enquery ("+ noOfEnquiry +")");
        
        parent2.setArrayChildren(arrayChildren);
        arrayParents.add(parent2);


		//sets the adapter that provides data to the list.
		mExpandableList.setAdapter(new MyCustomAdapter(Follow_ups_list_first_level.this,arrayParents));

		// onclick child list

		mExpandableList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {

				/* You must make use of the View v, find the view by id and extract the text as below*/

				TextView tv= (TextView) v.findViewById(R.id.list_item_text_child);
				String data= tv.getText().toString();   
				
				String comStr = data.substring(0, data.indexOf("(")-1 );
				
				saveInPreference("SendOnBackBtnFromThird",comStr);
				
				if(comStr.equalsIgnoreCase("Today"))
				{
					Intent showList = new Intent(Follow_ups_list_first_level.this, Follow_ups_list_second_level.class);
					showList.putExtra("DayFlag", "TODAY");
					startActivity(showList);
				}
				else if(comStr.equalsIgnoreCase("Tomorrow"))
				{
					Intent showList = new Intent(Follow_ups_list_first_level.this, Follow_ups_list_second_level.class);
					showList.putExtra("DayFlag", "TOMORROW");
					startActivity(showList);
				}
				else if(comStr.equalsIgnoreCase("Day after tomorrow"))
				{
					Intent showList = new Intent(Follow_ups_list_first_level.this, Follow_ups_list_second_level.class);
					showList.putExtra("DayFlag", "DAY AFTER TOMORROW");
					startActivity(showList);
				}
				else if(comStr.equalsIgnoreCase("Rest"))
				{
					Intent showList = new Intent(Follow_ups_list_first_level.this, Follow_ups_list_second_level.class);
					showList.putExtra("DayFlag", "REST");
					startActivity(showList);
				}
				else if(comStr.equalsIgnoreCase("Enquery"))
				{
					Intent intent = new Intent(Follow_ups_list_first_level.this, AllEnquiryTableActivity.class);
                	Follow_ups_list_first_level.this.startActivity(intent);
				}
				
				return true;  // i missed this
			}
		});

	}

	//method to show toast message
	public void makeAToast(String str) {
		//yet to implement
		Toast toast = Toast.makeText(this,str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	@Override
	public void onBackPressed() {
		Intent showList = new Intent(Follow_ups_list_first_level.this, Login_activity.class);
		startActivity(showList);

		super.onBackPressed();
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
