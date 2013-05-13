package com.ica.icacounselor;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ica.activity_util.MyCustomAdapter;
import com.ica.activity_util.Parent;
import com.ica.commons.DatabaseHandler;

public class Follow_ups_list_second_level extends Activity {
	private ExpandableListView mExpandableList;
	String DayFlag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follow_ups_list_second_level);
		mExpandableList = (ExpandableListView)findViewById(R.id.expandable_list);

		ArrayList<Parent> arrayParents = new ArrayList<Parent>();
		ArrayList<String> arrayChildren = new ArrayList<String>();

		//===========================================================================================================
		//Getting data from first activity
		//===========================================================================================================
				Bundle extras = getIntent().getExtras();
				if (extras == null) 
				{
					return;
				}
				DayFlag = extras.getString("DayFlag");
				saveInPreference("DayFlagForBackBtnFromForthLevel",DayFlag);
		//===========================================================================================================
		//END Getting data from first activity
		//===========================================================================================================

        
		int hotProbability  = 0;
		int warmProbability = 0;
		int coldProbability = 0;
		
		Parent parent1 = new Parent();
		//==============================================================================
		//Get all records from follow_ups_list
		//==============================================================================
		
		DatabaseHandler dbForProbability = new DatabaseHandler(Follow_ups_list_second_level.this);

		if(DayFlag.equalsIgnoreCase("TODAY"))
		{
			parent1.setTitle("Today");
			hotProbability  = dbForProbability.getFollowUpsListsProbabilityNoForToday(DayFlag, "OLD", "Hot");
			warmProbability = dbForProbability.getFollowUpsListsProbabilityNoForToday(DayFlag, "OLD", "Warm");
			coldProbability = dbForProbability.getFollowUpsListsProbabilityNoForToday(DayFlag, "OLD", "Cold");
		}
		else if(DayFlag.equalsIgnoreCase("TOMORROW"))
		{
			parent1.setTitle("Tomorrow");
			hotProbability  = dbForProbability.getFollowUpsListsProbabilityNo(DayFlag, "Hot");
			warmProbability = dbForProbability.getFollowUpsListsProbabilityNo(DayFlag, "Warm");
			coldProbability = dbForProbability.getFollowUpsListsProbabilityNo(DayFlag, "Cold");
		}
		else if(DayFlag.equalsIgnoreCase("DAY AFTER TOMORROW"))
		{
			parent1.setTitle("Day After Tomorrow");
			hotProbability  = dbForProbability.getFollowUpsListsProbabilityNo(DayFlag, "Hot");
			warmProbability = dbForProbability.getFollowUpsListsProbabilityNo(DayFlag, "Warm");
			coldProbability = dbForProbability.getFollowUpsListsProbabilityNo(DayFlag, "Cold");
		}
		else if(DayFlag.equalsIgnoreCase("REST"))
		{
			parent1.setTitle("Rest");
			hotProbability  = dbForProbability.getFollowUpsListsProbabilityNo(DayFlag, "Hot");
			warmProbability = dbForProbability.getFollowUpsListsProbabilityNo(DayFlag, "Warm");
			coldProbability = dbForProbability.getFollowUpsListsProbabilityNo(DayFlag, "Cold");
		}
		else
		{}
			
		dbForProbability.close();
		
		//==============================================================================
		//Get all records from follow_ups_list
		//==============================================================================
        
        arrayChildren = new ArrayList<String>();
        arrayChildren.add("Hot (" + hotProbability + ")");
        arrayChildren.add("Warm (" + warmProbability + ")");
        arrayChildren.add("Cold (" + coldProbability + ")");
        
        
        parent1.setArrayChildren(arrayChildren);
        
        arrayParents.add(parent1);
        
		//sets the adapter that provides data to the list.
		mExpandableList.setAdapter(new MyCustomAdapter(Follow_ups_list_second_level.this,arrayParents));

		// onclick child list

		mExpandableList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {

				/* You must make use of the View v, find the view by id and extract the text as below*/

				TextView tv= (TextView) v.findViewById(R.id.list_item_text_child);
				String data= tv.getText().toString();   
				
				
				String comStr = data.substring(0, data.indexOf("(")-1 );
				
				saveInPreference("SendOnBackBtnFromForth",comStr);
				
					//Need to send dayFlag as OLD
					if(comStr.equalsIgnoreCase("Hot"))
					{
						Intent showList = new Intent(Follow_ups_list_second_level.this, Follow_ups_list_third_level.class);
						showList.putExtra("probFlag", "Hot");
						showList.putExtra("dayFlag", DayFlag);
						Log.d("Send",DayFlag);
						startActivity(showList);
					}
					if(comStr.equalsIgnoreCase("Warm"))
					{
						Intent showList = new Intent(Follow_ups_list_second_level.this, Follow_ups_list_third_level.class);
						showList.putExtra("probFlag", "Warm");
						showList.putExtra("dayFlag", DayFlag);
						Log.d("Send",DayFlag);
						startActivity(showList);
					}

					if(comStr.equalsIgnoreCase("Cold"))
					{
						Intent showList = new Intent(Follow_ups_list_second_level.this, Follow_ups_list_third_level.class);
						showList.putExtra("probFlag", "Cold");
						showList.putExtra("dayFlag", DayFlag);
						Log.d("Send",DayFlag);
						startActivity(showList);
					}

				return true;  // i missed this
			}
		});

	}
	@Override
	public void onBackPressed() {
		Log.d("Back Btn Pressed", "Back Btn Pressed From Second level");
		Intent i = new Intent(this, Follow_ups_list_first_level.class);
		startActivity(i);
		super.onBackPressed();
	}

	//method to show toast message
	public void makeAToast(String str) {
		//yet to implement
		Toast toast = Toast.makeText(this,str, Toast.LENGTH_SHORT);
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
