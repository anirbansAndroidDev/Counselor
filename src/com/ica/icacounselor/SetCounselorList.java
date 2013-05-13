package com.ica.icacounselor;

import com.ica.commons.DatabaseHandler;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class SetCounselorList extends Activity {

	String selectedValue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_counselor_list);
		Spinner s = (Spinner)findViewById(R.id.SpinnerUserInfo);

		//---get all Records from database---
		
		DatabaseHandler dbh = new DatabaseHandler(SetCounselorList.this);
		Cursor c = dbh.getCounselorList();

		//create an array of a special data type MyData
		//this class is defined bellow
		//this will help to insert name and corresponding value to a dropDown

		final MyData items[] = new MyData[c.getCount()];
		int i = 0;

		if (c.moveToFirst())
		{
			do 
			{        
				Log.d("Data", c.getString(2) + "  " + c.getString(1));
				//items[i] = new MyData("value", "name");
				items[i] = new MyData( c.getString(2), c.getString(1));
				i++;
			} while (c.moveToNext());
		}
		dbh.close();

		ArrayAdapter<MyData> adapter = new ArrayAdapter<MyData>( this,R.layout.spinner_item,items );
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);

		//on value select from that dropdown it will get that id of Corresponding value
		s.setOnItemSelectedListener
		(
				new AdapterView.OnItemSelectedListener() 
				{
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
					{
						MyData d = items[position];
//						Toast.makeText(SetCounselorList.this,"Selected value is: "+d.getValue() +
//								"\nSelected text is: "+d.getSpinnerText(), 
//								Toast.LENGTH_LONG).show();
						
						selectedValue = d.getValue() + "";
					}

					public void onNothingSelected(AdapterView<?> parent) 
					{

					}
				}
				);
	}

	class MyData 
	{
		public MyData( String spinnerText, String value ) {
			this.spinnerText = spinnerText;
			this.value = value;
		}

		public String getSpinnerText() {
			return spinnerText;
		}

		public String getValue() {
			return value;
		}

		public String toString() {
			return spinnerText;
		}

		String spinnerText;
		String value;
	}
	
	//======================================================================================================================
	//Method for save button
	//======================================================================================================================
	
	public void saveCounselor(View v) {
		saveInPreference("CounselorId", selectedValue);
		
		Toast.makeText( getApplicationContext(),"Counselor name saved.",Toast.LENGTH_SHORT).show();
		
		Intent i = new Intent(this, Follow_ups_list_first_level.class);
		saveInPreference("CounselorId",selectedValue);
		finish();
		startActivity(i);

	}
	
	//======================================================================================================================
	//END Method for save button
	//======================================================================================================================

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
