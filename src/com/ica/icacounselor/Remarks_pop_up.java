package com.ica.icacounselor;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ica.allenquiry.AllEnquiryDetailsActivity;
import com.ica.allenquiry.AllEnquiryTableActivity;
import com.ica.commons.DatabaseHandler;

public class Remarks_pop_up extends Activity {

	Spinner probability_spinner;
	Spinner call_time_spinner;

	Button save;

	EditText remarksEditText;
	EditText dateEditText;

	String remarksResponseBody;
	String remarksResponseCode;

	String loginUserStr;
	String loginPwdStr;
	String enquiryIdStr;
	String nextFollowUpDateStr;
	String probablityStr;
	String callTimeStr;
	String remarksStr;
	String mobileStr;
	String courseIdStr;
	String counselorId;
	String CourseValue;
	private static String loginStatus = null;
	private ProgressDialog pgLogin;

	final CharSequence[] dateOptions={"Tomorrow","Day after tomorrow","After 1 week","After 15 days","After 1 month","Specific Date"};

	private int myYear, myMonth, myDay;
	static final int ID_DATEPICKER = 0;


	//For date checking
	Date todayDate;
	Date tomorrowDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// remove title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.remarks_pop_up);


		probability_spinner = (Spinner)findViewById(R.id.probability_spinner);
		//adding items to probability_spinner spinner
		addItemsInProbabilitySpinner();

		call_time_spinner = (Spinner)findViewById(R.id.call_time_spinner);
		//adding items to probability_spinner spinner
		addItemsInCallTimeSpinner();

		remarksEditText=(EditText)findViewById(R.id.remarks_editText);
		dateEditText=(EditText)findViewById(R.id.date_editText);


		//--------------------------------------------------------------------------------------------------------------
		// For course spinner
		//--------------------------------------------------------------------------------------------------------------
		Spinner CourseSpinner = (Spinner)findViewById(R.id.SpinnerCourse);

		//---get all Records from database---
		DatabaseHandler db = new DatabaseHandler(this);
		Cursor c = db.getCourseList();

		final MyData items[] = new MyData[c.getCount()];
		int i = 0;

		if (c.moveToFirst())
		{
			do 
			{          
				//items[i] = new MyData("value", "name");
				items[i] = new MyData( c.getString(3), c.getString(1));
				i++;
			} while (c.moveToNext());
		}
		db.close();

		ArrayAdapter<MyData> adapter = new ArrayAdapter<MyData>( Remarks_pop_up.this,R.layout.spinner_item,items );
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		CourseSpinner.setAdapter(adapter);

		//on value select from that dropdown it will get that id of Corresponding value
		CourseSpinner.setOnItemSelectedListener
		(
				new AdapterView.OnItemSelectedListener() 
				{
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
					{
						MyData d = items[position];
						CourseValue = d.getValue();
					}

					public void onNothingSelected(AdapterView<?> parent) 
					{

					}
				}
				);

		//--------------------------------------------------------------------------------------------------------------
		//END Course spinner
		//--------------------------------------------------------------------------------------------------------------

		//setting current tomorrows to edittext
		final Calendar cal = Calendar.getInstance();
		myYear = cal.get(Calendar.YEAR);
		myMonth = cal.get(Calendar.MONTH)+1;
		myDay = cal.get(Calendar.DAY_OF_MONTH)+1;

		String dayStr=""+myDay;
		String monthStr=""+myMonth;

		if ((myMonth)<10)
		{
			monthStr="0"+myMonth;
		}
		if (myDay<10)
		{
			dayStr="0"+myDay;
		}


		dateEditText.setText(dayStr+"/"+monthStr+"/"+myYear);

		//saving tomorrow's date to variable

		//===================================================================================================
		// onclick date edit text
		//===================================================================================================
		dateEditText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				ContextThemeWrapper cw = new ContextThemeWrapper(Remarks_pop_up.this, R.style.AlertDialogTheme );

				AlertDialog.Builder dateList=new AlertDialog.Builder(cw);
				dateList.setTitle(null).setSingleChoiceItems(dateOptions,0, new DialogInterface.OnClickListener() {

					@Override

					public void onClick(DialogInterface dialog, int position) {

						if(dateOptions[position].toString().equalsIgnoreCase("Specific Date"))
						{
							final Calendar c = Calendar.getInstance();
							myYear = c.get(Calendar.YEAR);
							myMonth = c.get(Calendar.MONTH);
							myDay = c.get(Calendar.DAY_OF_MONTH);
							showDialog(ID_DATEPICKER);
						}

						if(dateOptions[position].toString().equalsIgnoreCase("Tomorrow"))
						{
							final Calendar c = Calendar.getInstance();
							myYear = c.get(Calendar.YEAR);
							myMonth = c.get(Calendar.MONTH)+1;
							myDay = c.get(Calendar.DAY_OF_MONTH)+1;

							String dayStr=""+myDay;
							String monthStr=""+myMonth;

							if ((myMonth)<10)
							{
								monthStr="0"+myMonth;
							}
							if (myDay<10)
							{
								dayStr="0"+myDay;
							}


							dateEditText.setText(dayStr+"/"+monthStr+"/"+myYear);
						}

						if(dateOptions[position].toString().equalsIgnoreCase("Day after tomorrow"))
						{
							final Calendar c = Calendar.getInstance();
							myYear = c.get(Calendar.YEAR);
							myMonth = c.get(Calendar.MONTH)+1;
							myDay = c.get(Calendar.DAY_OF_MONTH)+2;

							String dayStr=""+myDay;
							String monthStr=""+myMonth;

							if ((myMonth)<10)
							{
								monthStr="0"+myMonth;
							}
							if (myDay<10)
							{
								dayStr="0"+myDay;
							}


							dateEditText.setText(dayStr+"/"+monthStr+"/"+myYear);
						}

						if(dateOptions[position].toString().equalsIgnoreCase("After 1 week"))
						{
							final Calendar c = Calendar.getInstance();
							myYear = c.get(Calendar.YEAR);
							myMonth = c.get(Calendar.MONTH)+1;
							myDay = c.get(Calendar.DAY_OF_MONTH)+7;

							String dayStr=""+myDay;
							String monthStr=""+myMonth;

							if ((myMonth)<10)
							{
								monthStr="0"+myMonth;
							}
							if (myDay<10)
							{
								dayStr="0"+myDay;
							}


							dateEditText.setText(dayStr+"/"+monthStr+"/"+myYear);
						}

						if(dateOptions[position].toString().equalsIgnoreCase("After 15 days"))
						{
							final Calendar c = Calendar.getInstance();
							myYear = c.get(Calendar.YEAR);
							myMonth = c.get(Calendar.MONTH)+1;
							myDay = c.get(Calendar.DAY_OF_MONTH)+15;

							String dayStr=""+myDay;
							String monthStr=""+myMonth;

							if ((myMonth)<10)
							{
								monthStr="0"+myMonth;
							}
							if (myDay<10)
							{
								dayStr="0"+myDay;
							}


							dateEditText.setText(dayStr+"/"+monthStr+"/"+myYear);
						}

						if(dateOptions[position].toString().equalsIgnoreCase("After 1 month"))
						{
							final Calendar c = Calendar.getInstance();
							myYear = c.get(Calendar.YEAR);
							myMonth = c.get(Calendar.MONTH)+2;
							myDay = c.get(Calendar.DAY_OF_MONTH);

							String dayStr=""+myDay;
							String monthStr=""+myMonth;

							if ((myMonth)<10)
							{
								monthStr="0"+myMonth;
							}
							if (myDay<10)
							{
								dayStr="0"+myDay;
							}


							dateEditText.setText(dayStr+"/"+monthStr+"/"+myYear);
						}

						dialog.cancel();
					}

				});

				dateList.show();
			}

		});

		// onclick save button
		save = (Button) findViewById(R.id.save_button);
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (remarksEditText.getText().length()>0)
				{

					Date cDate = new Date();
					String fDate = new SimpleDateFormat("dd/MM/yyyy").format(cDate);
					String toDate=fDate.toString();

					String formatString = "dd/MM/yyyy"; // for example
					SimpleDateFormat df = new SimpleDateFormat(formatString);
					Date date1 = null;
					try 
					{
						date1 = df.parse(dateEditText.getText().toString());
					} 
					catch (ParseException e1) 
					{
						e1.printStackTrace();
					}

					myYear = cal.get(Calendar.YEAR);
					myMonth = cal.get(Calendar.MONTH)+1;
					myDay = cal.get(Calendar.DAY_OF_MONTH)+1;

					String dayStr=""+myDay;
					String monthStr=""+myMonth;

					if ((myMonth)<10)
					{
						monthStr="0"+myMonth;
					}
					if (myDay<10)
					{
						dayStr="0"+myDay;
					}



					Date date2 = null;
					try 
					{
						date2 = df.parse(dayStr+"/"+monthStr+"/"+myYear);
					} 
					catch (ParseException e1) 
					{
						e1.printStackTrace();
					}


					if (date1.before(date2)) 
					{
						makeAToast("Date should be atlease tomorrow");
					}

					else
					{
						if(haveNetworkConnection())
						{
							Log.d("stringLoginUser: ",getFromPreference("stringLoginUser"));
							Log.d("stringLoginPwd: ",getFromPreference("stringLoginPwd"));
							Log.d("Enquiry_id: ",getFromPreference("Enquiry_id"));
							Log.d("Follow up: ",dateEditText.getText().toString());
							Log.d("Probability: ",probability_spinner.getSelectedItem()+"");
							Log.d("Call time: ",call_time_spinner.getSelectedItem()+"");
							Log.d("Remarks: ",remarksEditText.getText().toString());
							Log.d("Mobile: ",getFromPreference("Mobile"));
							Log.d("Course id: ",CourseValue);
							Log.d("CounselorId: ",getFromPreference("CounselorId"));
	
							pgLogin = new ProgressDialog(Remarks_pop_up.this);
							pgLogin.setMessage("Please wait while saving ...");
							pgLogin.setIndeterminate(true);
							pgLogin.setCancelable(true);
							pgLogin.setCanceledOnTouchOutside(false);
	
							pgLogin.show();
	
							try
							{
								new MyAsyncTaskForSaving().execute(""+getFromPreference("stringLoginUser"),""+getFromPreference("stringLoginPwd"),""+getFromPreference("Enquiry_id"),""+dateEditText.getText().toString(),""+probability_spinner.getSelectedItem(),""+call_time_spinner.getSelectedItem(),""+remarksEditText.getText().toString(),""+getFromPreference("Mobile"),""+CourseValue,""+getFromPreference("CounselorId"));
							}
							catch (Throwable e) {
								Log.d("Error", e+"");
							}
						}
						else
						{
							Toast.makeText(Remarks_pop_up.this, "Sorry! No internet connection.", Toast.LENGTH_LONG).show();
						}
					}
				}
				else
				{
					makeAToast("Please insert Remarks!");
				}
			}

		});

	}

	@Override
	public void onBackPressed() {
		// do something on back.

		finish();
		return;
	}

	//==============Date Picker============================

	@Override
	protected Dialog onCreateDialog(int id) {

		switch(id){
		case ID_DATEPICKER:

			return new DatePickerDialog(this,myDateSetListener,myYear, myMonth, myDay);
		default:
			return null;
		}
	}

	private DatePickerDialog.OnDateSetListener myDateSetListener = new DatePickerDialog.OnDateSetListener()
	{
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		{
			String dayStr=String.valueOf(dayOfMonth);
			String monthStr=String.valueOf(monthOfYear+1);

			if ((monthOfYear+1)<10)
			{
				monthStr="0"+String.valueOf(monthOfYear+1);
			}
			if (dayOfMonth<10)
			{
				dayStr="0"+String.valueOf(dayOfMonth);
			}

			String date = dayStr+"/"+ monthStr +"/"+ String.valueOf(year);
			dateEditText.setText(date);
		} 
	};



	//==============Date Picker============================


	//method to add values to probability_spinner spinner
	public void addItemsInProbabilitySpinner()
	{
		List<String> list = new ArrayList<String>();
		list.add("Hot");
		list.add("Warm");
		list.add("Cold");
		list.add("Not Interested");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		probability_spinner.setAdapter(dataAdapter);
	}

	//method to add values to call time spinner
	public void addItemsInCallTimeSpinner()
	{
		List<String> list = new ArrayList<String>();

		list.add("Morning");
		list.add("Afternoon");
		list.add("Evening");
		list.add("Night");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		call_time_spinner.setAdapter(dataAdapter);
	}



	//method to getEnquiryId from database
	void getEnquiryId()
	{
		// getting status from database
		DatabaseHandler db = new DatabaseHandler(
				getApplicationContext());

		enquiryIdStr= db.selectAllEnqueryMobile(getFromPreference("allEnqueryId"));

	}

	//method to show toast message
	public void makeAToast(String str) {
		//yet to implement
		Toast toast = Toast.makeText(this,str, Toast.LENGTH_LONG);
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

	//===================================================================================================================================
	//sending EmailAddress and Password to server
	//===================================================================================================================================
	private class MyAsyncTaskForSaving extends AsyncTask<String, Integer, Double>{

		String responseBody;
		int responseCode;
		@Override
		protected Double doInBackground(String... params) {
			postData(params[0],params[1],params[2],params[3],params[4],params[5],params[6],params[7],params[8],params[9]);
			return null;
		}

		protected void onPostExecute(Double result)
		{
			processResponceAfterSaving(responseBody);
		}

		protected void onProgressUpdate(Integer... progress){

		}

		public void postData(String stringLoginUser,String stringLoginPwd,String Enquiry_id,String Next_followup_datetime,String Probability,String PrefCallTime,String FollowupRemarks,String Mobile,String Course_id,String CounselorId) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://203.153.37.4/icaservice/Centre.asmx/followupEntry");

			try {
				// Data that I am sending
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("stringLoginUser", stringLoginUser));
				nameValuePairs.add(new BasicNameValuePair("stringLoginPwd", stringLoginPwd));
				nameValuePairs.add(new BasicNameValuePair("Enquiry_id", Enquiry_id));
				nameValuePairs.add(new BasicNameValuePair("Next_followup_datetime", Next_followup_datetime));
				nameValuePairs.add(new BasicNameValuePair("Probability", Probability));
				nameValuePairs.add(new BasicNameValuePair("PrefCallTime", PrefCallTime));
				nameValuePairs.add(new BasicNameValuePair("FollowupRemarks", FollowupRemarks));
				nameValuePairs.add(new BasicNameValuePair("Mobile", Mobile));
				nameValuePairs.add(new BasicNameValuePair("Course_id", Course_id));
				nameValuePairs.add(new BasicNameValuePair("CounselorId", CounselorId));


				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				responseBody = EntityUtils.toString(response.getEntity());

				Log.d("Saving result", responseBody);
			} 
			catch (Throwable t ) {
				//Toast.makeText( getApplicationContext(),""+t,Toast.LENGTH_LONG).show();
				Log.d("Error Time of Login",t+"");
			} 
		}
	}
	//===================================================================================================================================
	//END sending EmailAddress and Password to server 
	//===================================================================================================================================


	//===================================================================================================================================
	//processing the XML got from server for ENQUIRY LIST
	//===================================================================================================================================
	private void processResponceAfterSaving(String responceFromServer) 
	{
		try {
			//saving the file as a xml
			FileOutputStream fOut = openFileOutput("replyAfterSaving.xml",MODE_WORLD_READABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(responceFromServer);
			osw.flush();
			osw.close();

			//reading the file as xml
			FileInputStream fIn = openFileInput("replyAfterSaving.xml");
			InputStreamReader isr = new InputStreamReader(fIn);
			char[] inputBuffer = new char[responceFromServer.length()];
			isr.read(inputBuffer);
			String readString = new String(inputBuffer);

			//getting the xml Value as per child node form the saved xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(readString.getBytes("UTF-8"));
			Document doc = db.parse(is);

			NodeList root=doc.getElementsByTagName("root");

			String loginStatus =null;
			for (int i=0;i<root.getLength();i++) 
			{
				loginStatus = "" + ((Element)root.item(i)).getAttribute("status");
			}

			//If Email and Pass match with server
			if(loginStatus.equalsIgnoreCase("Y"))
			{
				new MyAsyncTaskForAllEnquire().execute(""+getFromPreference("stringLoginUser"),""+getFromPreference("stringLoginPwd"));
			}
			else if(loginStatus.equalsIgnoreCase("F"))
			{
				if (pgLogin.isShowing()) {
					pgLogin.cancel();
					pgLogin.dismiss();
				}
				finish();
				Toast.makeText( getApplicationContext(),"Information not saved.\nPlease contact to administrator",Toast.LENGTH_SHORT).show();
			}
		} 
		catch (Throwable t) 
		{
			Log.d("Error On Saving and reading", t+"");
		}
	}
	//===================================================================================================================================
	//processing the XML got from server for ENQUIRY LIST
	//===================================================================================================================================

	
	
	
	//===================================================================================================================================
	//Sending center code and Password to server and getting ALL ENQUIRY INFO
	//===================================================================================================================================
	private class MyAsyncTaskForAllEnquire extends AsyncTask<String, Integer, Double>{

		String responseBody;
		int responseCode;
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData(params[0],params[1]);
			return null;
		}

		protected void onPostExecute(Double result){
			
			if(responseCode == 200)
			{
				processResponceAllEnquiry(responseBody);
			}
			
			else
			{
				if (pgLogin.isShowing()) 
				{
					pgLogin.cancel();
					pgLogin.dismiss();
				}
				Toast.makeText(Remarks_pop_up.this, "Not getting responce from server.", Toast.LENGTH_LONG).show();
			}
			
		}

		protected void onProgressUpdate(Integer... progress){

		}

		public void postData(String center_code,String passwrd) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://203.153.37.4/icaservice/Centre.asmx/centreLogin");

			try {
				// Data that I am sending
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("stringLoginUser", center_code));
				nameValuePairs.add(new BasicNameValuePair("stringLoginPwd", passwrd));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);

				responseCode = response.getStatusLine().getStatusCode();
				responseBody = EntityUtils.toString(response.getEntity());
			} 
			catch (Throwable t ) {
				Log.d("Error Time of Login",t+"");
			} 
		}
	}
	//===================================================================================================================================
	//END sending EmailAddress and Password to server and getting ENQUIRY INFO
	//===================================================================================================================================

	
	
	
	//===================================================================================================================================
	//processing the XML got from server for ENQUIRY LIST
	//===================================================================================================================================
	private void processResponceAllEnquiry(String responceFromServer) 
	{
		try {
			//saving the file as a xml
			FileOutputStream fOut = openFileOutput("enquiryListData.xml",MODE_WORLD_READABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(responceFromServer);
			osw.flush();
			osw.close();

			//reading the file as xml
			FileInputStream fIn = openFileInput("enquiryListData.xml");
			InputStreamReader isr = new InputStreamReader(fIn);
			char[] inputBuffer = new char[responceFromServer.length()];
			isr.read(inputBuffer);
			String readString = new String(inputBuffer);

			//getting the xml Value as per child node form the saved xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(readString.getBytes("UTF-8"));
			Document doc = db.parse(is);

			NodeList root=doc.getElementsByTagName("root");

			for (int i=0;i<root.getLength();i++) 
			{
				loginStatus 		= "" + ((Element)root.item(i)).getAttribute("status");
				String noOfEnquiry  = "" + ((Element)root.item(i)).getAttribute("cEnquiry");
				saveInPreference("noOfEnquiry", noOfEnquiry);
			}

			//If Email and Pass match with server
			if(loginStatus.equalsIgnoreCase("Y"))
			{
				//==============================================================================
				//Delete all records from counselorTable if record exist 
				//==============================================================================

				DatabaseHandler dbhForEnquiryDelete = new DatabaseHandler(Remarks_pop_up.this);
				Cursor couserForDelete = dbhForEnquiryDelete.getCounselorList();

				if (couserForDelete.moveToFirst())
				{
					dbhForEnquiryDelete.deleteAllCenterData();
				}
				else
				{}
				dbhForEnquiryDelete.close();
				
				//==============================================================================
				//Delete all records from counselorTable if record exist 
				//==============================================================================

				NodeList mb=doc.getElementsByTagName("enquiry");

				for (int i=0;i<mb.getLength();i++) 
				{
					String Enquiry_id   	= "" + ((Element)mb.item(i)).getAttribute("Enquiry_id");
					String StudentName  	= "" + ((Element)mb.item(i)).getAttribute("StudentName");
					String Phone  			= "" + ((Element)mb.item(i)).getAttribute("Phone");
					String Mobile 			= "" + ((Element)mb.item(i)).getAttribute("Mobile");
					String EnquiryDate  	= "" + ((Element)mb.item(i)).getAttribute("EnquiryDate");
					String Followup_id  	= "" + ((Element)mb.item(i)).getAttribute("Followup_id");
					String WebSource  		= "" + ((Element)mb.item(i)).getAttribute("WebSource");
					String WebSourceAlias 	= "" + ((Element)mb.item(i)).getAttribute("WebSourceAlias");

					//==============================================================================
					//insert to local database
					//==============================================================================
					DatabaseHandler dbh = new DatabaseHandler(Remarks_pop_up.this);
					ContentValues values = new ContentValues();
					//db.createDataBase();
					values.put("Enquiry_id",Enquiry_id);
					values.put("StudentName",StudentName);
					values.put("Phone",Phone);
					values.put("Mobile",Mobile);
					values.put("EnquiryDate",EnquiryDate);
					values.put("Followup_id",Followup_id);
					values.put("WebSource",WebSource);
					values.put("WebSourceAlias",WebSourceAlias);

					dbh.insertCenterValues(Enquiry_id, StudentName, Phone, Mobile, EnquiryDate, Followup_id, WebSource, WebSourceAlias);
					dbh.close();
					//==============================================================================
					//insert to local database
					//==============================================================================
				}
				
				new MyAsyncTaskForCourseList().execute(""+getFromPreference("stringLoginUser"),""+getFromPreference("stringLoginPwd"));
				
			}
			else if(loginStatus.equalsIgnoreCase("F"))
			{
				if (pgLogin.isShowing()) {
					pgLogin.cancel();
					pgLogin.dismiss();
				}
				Toast.makeText( getApplicationContext(),"Login failed. Please check your credentials and try again.",Toast.LENGTH_LONG).show();
			}
		} 
		catch (Throwable t) 
		{
			Log.d("Error On Saving and reading", t+"");
		}
	}
	//===================================================================================================================================
	//processing the XML got from server for ENQUIRY LIST
	//===================================================================================================================================

	
	
	
	//===================================================================================================================================
	//sending EmailAddress and Password to server for COURSE lIST
	//===================================================================================================================================
	private class MyAsyncTaskForCourseList extends AsyncTask<String, Integer, Double>{

		String responseBody;
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData(params[0],params[1]);
			return null;
		}

		protected void onPostExecute(Double result){
			processResponceForCourseList(responseBody);
		}

		protected void onProgressUpdate(Integer... progress){

		}

		public void postData(String stringLoginUser,String stringLoginPwd) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://203.153.37.4/icaservice/Centre.asmx/courseList");

			try {
				// Data that I am sending
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("stringLoginUser", stringLoginUser));
				nameValuePairs.add(new BasicNameValuePair("stringLoginPwd", stringLoginPwd));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				responseBody = EntityUtils.toString(response.getEntity());

				Log.d("result", responseBody);
			} 
			catch (Throwable t ) {
				Log.d("Error Time of Login",t+"");
			} 
		}
	}
	//===================================================================================================================================
	//END sending EmailAddress and Password to server for COURSE lIST
	//===================================================================================================================================

	
	
	
	//===================================================================================================================================
	//processing the XML got from server for COURSE LIST
	//===================================================================================================================================
	private void processResponceForCourseList(String responceFromServer) 
	{
		try {
			//saving the file as a xml
			FileOutputStream fOut = openFileOutput("courseListData.xml",MODE_WORLD_READABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(responceFromServer);
			osw.flush();
			osw.close();

			//reading the file as xml
			FileInputStream fIn = openFileInput("courseListData.xml");
			InputStreamReader isr = new InputStreamReader(fIn);
			char[] inputBuffer = new char[responceFromServer.length()];
			isr.read(inputBuffer);
			String readString = new String(inputBuffer);

			//getting the xml Value as per child node form the saved xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(readString.getBytes("UTF-8"));
			Document doc = db.parse(is);

			NodeList root=doc.getElementsByTagName("root");

			for (int i=0;i<root.getLength();i++) 
			{
				loginStatus = "" + ((Element)root.item(i)).getAttribute("status");
			}

			//If Email and Pass match with server
			if(loginStatus.equalsIgnoreCase("Y"))
			{
				NodeList mb=doc.getElementsByTagName("Course");
				
				//==============================================================================
				//Delete all records from courseTable if record exist 
				//==============================================================================

				DatabaseHandler dbhForCourseDelete = new DatabaseHandler(Remarks_pop_up.this);
				Cursor couserForDelete = dbhForCourseDelete.getCounselorList();

				if (couserForDelete.moveToFirst())
				{
					dbhForCourseDelete.deleteAllCourseData();
				}
				else
				{}
				dbhForCourseDelete.close();
				//==============================================================================
				//Delete all records from courseTable if record exist 
				//==============================================================================

				for (int i=0;i<mb.getLength();i++) 
				{
				    String Course_id 		= "" + ((Element)mb.item(i)).getAttribute("Course_id");	
					String CAlias    		= "" + ((Element)mb.item(i)).getAttribute("CAlias");
					String Course_name      = "" + ((Element)mb.item(i)).getAttribute("Course_name");
					//==============================================================================
					//insert to local database
					//==============================================================================
					DatabaseHandler dbhCourseObject = new DatabaseHandler(Remarks_pop_up.this);
					ContentValues values = new ContentValues();
					//db.createDataBase();
					values.put("Course_id",Course_id);
					values.put("CAlias",CAlias);
					values.put("Course_name",Course_name);

					dbhCourseObject.insertCourseValues(Course_id, CAlias, Course_name);
					dbhCourseObject.close();
					//==============================================================================
					//insert to local database
					//==============================================================================
				}
				
				new MyAsyncTaskForPendingFollowUps().execute(""+getFromPreference("stringLoginUser"),""+getFromPreference("stringLoginPwd"));

			}
			else if(loginStatus.equalsIgnoreCase("F"))
			{
				if (pgLogin.isShowing()) {
					pgLogin.cancel();
					pgLogin.dismiss();
				}
				Toast.makeText( getApplicationContext(),"Not be able to get Course List.",Toast.LENGTH_SHORT).show();
			}
		} 
		catch (Throwable t) 
		{
			Log.d("Error On Saving and reading", t+"");
		}
	}
	//===================================================================================================================================
	//processing the XML got from server for COURSE LIST
	//===================================================================================================================================

	
	
	
	//===================================================================================================================================
	//sending center_code and Password to server for Pending Follow
	//===================================================================================================================================
	private class MyAsyncTaskForPendingFollowUps extends AsyncTask<String, Integer, Double>{

		String responseBody;
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData(params[0],params[1]);
			return null;
		}

		protected void onPostExecute(Double result){
			//Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();
			processPendingFollowUpsResponce(responseBody);
		}

		protected void onProgressUpdate(Integer... progress){

		}

		public void postData(String center_code,String passwrd) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://203.153.37.4/icaservice/Centre.asmx/pendingFollowupList");

			try {
				// Data that I am sending
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("stringLoginUser", center_code));
				nameValuePairs.add(new BasicNameValuePair("stringLoginPwd", passwrd));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				responseBody = EntityUtils.toString(response.getEntity());

				Log.d("result", responseBody);
			} 
			catch (Throwable t ) {
				//Toast.makeText( getApplicationContext(),""+t,Toast.LENGTH_LONG).show();
				Log.d("Error Time of Login",t+"");
			} 
		}
	}
	//===================================================================================================================================
	//END sending center_code and Password to server for Pending Follow
	//===================================================================================================================================

	
	
	
	//===================================================================================================================================
	//processing the XML got from server for Pending Follow
	//===================================================================================================================================
	private void processPendingFollowUpsResponce(String responceFromServer) 
	{
		try {
			//saving the file as a xml
			FileOutputStream fOut = openFileOutput("followUpData.xml",MODE_WORLD_READABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(responceFromServer);
			osw.flush();
			osw.close();

			//reading the file as xml
			FileInputStream fIn = openFileInput("followUpData.xml");
			InputStreamReader isr = new InputStreamReader(fIn);
			char[] inputBuffer = new char[responceFromServer.length()];
			isr.read(inputBuffer);
			String readString = new String(inputBuffer);

			//getting the xml Value as per child node form the saved xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(readString.getBytes("UTF-8"));
			Document doc = db.parse(is);

			NodeList root=doc.getElementsByTagName("root");

			for (int i=0;i<root.getLength();i++) 
			{
				loginStatus = "" + ((Element)root.item(i)).getAttribute("status");
				
			    String cToday  	  			= "" + ((Element)root.item(i)).getAttribute("cToday");
				String cTomorrow  			= "" + ((Element)root.item(i)).getAttribute("cTomorrow");
				String cDayAfterTomorrow  	= "" + ((Element)root.item(i)).getAttribute("cDayAfterTomorrow");
				String cRest				= "" + ((Element)root.item(i)).getAttribute("cRest");
				String cOld					= "" + ((Element)root.item(i)).getAttribute("cOld");

				saveInPreference("cToday", cToday);
				saveInPreference("cTomorrow", cTomorrow);
				saveInPreference("cDayAfterTomorrow", cDayAfterTomorrow);
				saveInPreference("cRest", cRest);
				saveInPreference("cOld", cOld);

			}

			//If Email and Pass match with server
			if(loginStatus.equalsIgnoreCase("Y"))
			{
				//==============================================================================
				//Delete all records from counselorTable if record exist 
				//==============================================================================

				DatabaseHandler dbhForFollowUpsDelete = new DatabaseHandler(Remarks_pop_up.this);
				Cursor couserForDelete = dbhForFollowUpsDelete.getCounselorList();

				if (couserForDelete.moveToFirst())
				{
					dbhForFollowUpsDelete.deleteFollowUpsListsData();
				}
				else
				{}
				dbhForFollowUpsDelete.close();
				
				//==============================================================================
				//Delete all records from counselorTable if record exist 
				//==============================================================================

				NodeList mb=doc.getElementsByTagName("followup");

				for (int i=0;i<mb.getLength();i++) 
				{
					String StudentName   	= "" + ((Element)mb.item(i)).getAttribute("StudentName");
					String Course_name  	= "" + ((Element)mb.item(i)).getAttribute("Course_name");
					String Residence_phone	= "" + ((Element)mb.item(i)).getAttribute("Residence_phone");
					String Mobile 			= "" + ((Element)mb.item(i)).getAttribute("Mobile");
					String Enquiry_Datetime = "" + ((Element)mb.item(i)).getAttribute("Enquiry_Datetime");
					String Probability 		= "" + ((Element)mb.item(i)).getAttribute("Probability");
					String LastFollowupDate = "" + ((Element)mb.item(i)).getAttribute("LastFollowupDate");
					String NextFollowupDate	= "" + ((Element)mb.item(i)).getAttribute("NextFollowupDate");
					String CFollowUP  	 	= "" + ((Element)mb.item(i)).getAttribute("CFollowUP");
					String Enquiry_id 		= "" + ((Element)mb.item(i)).getAttribute("Enquiry_id");
					String FollowUpRemark   = "" + ((Element)mb.item(i)).getAttribute("FollowUpRemark");
					String Course_id		= "" + ((Element)mb.item(i)).getAttribute("Course_id");
					String PrefCallTimeId	= "" + ((Element)mb.item(i)).getAttribute("PrefCallTimeId");
					String PrefCallTime	 	= "" + ((Element)mb.item(i)).getAttribute("PrefCallTime");
					String DayFlag	 		= "" + ((Element)mb.item(i)).getAttribute("DayFlag");
					String WebSource	 	= "" + ((Element)mb.item(i)).getAttribute("WebSource");
					String WebSourceAlias	= "" + ((Element)mb.item(i)).getAttribute("WebSourceAlias");

					//==============================================================================
					//insert to local database
					//==============================================================================
					DatabaseHandler dbFollowUpsValue = new DatabaseHandler(Remarks_pop_up.this);
					ContentValues values = new ContentValues();
					//db.createDataBase();
					values.put("StudentName",StudentName);
					values.put("Course_name",Course_name);
					values.put("Residence_phone",Residence_phone);
					values.put("Mobile",Mobile);
					values.put("Enquiry_Datetime",Enquiry_Datetime);
					values.put("Probability",Probability);
					values.put("LastFollowupDate",LastFollowupDate);
					values.put("NextFollowupDate",NextFollowupDate);
					values.put("CFollowUP",CFollowUP);
					values.put("Enquiry_id",Enquiry_id);
					values.put("FollowUpRemark",FollowUpRemark);
					values.put("Course_id",Course_id);
					values.put("PrefCallTimeId",PrefCallTimeId);
					values.put("PrefCallTime",PrefCallTime);
					values.put("DayFlag",DayFlag);
					values.put("WebSource",WebSource);
					values.put("WebSourceAlias",WebSourceAlias);

					dbFollowUpsValue.insertFollowUpsListsValues(StudentName, Course_name, Residence_phone, Mobile, Enquiry_Datetime, Probability, 
								LastFollowupDate,NextFollowupDate, CFollowUP, Enquiry_id, FollowUpRemark, Course_id, PrefCallTimeId, PrefCallTime, DayFlag, WebSource, WebSourceAlias);
					dbFollowUpsValue.close();
					//==============================================================================
					//insert to local database
					//==============================================================================
				}
				
				if (pgLogin.isShowing()) 
				{
					pgLogin.cancel();
					pgLogin.dismiss();
				}

				Toast.makeText( getApplicationContext(),"Your information saved.",Toast.LENGTH_SHORT).show();
				finish();
				
				//===========================================================================================================
				//Getting data from first activity
				//===========================================================================================================
					Bundle extras = getIntent().getExtras();
					if (extras == null) 
					{
						return;
					}
					String from = extras.getString("from");
					
					if(from.equalsIgnoreCase("followUp"))
					{
						Log.d("From", "followUp");
						try {
							String DayFlag   = getFromPreference("DayFlagForBackBtnFromForthLevel");
							String comStr    = getFromPreference("SendOnBackBtnFromForth");
							
								//Need to send dayFlag as OLD
								if(comStr.equalsIgnoreCase("Hot"))
								{
									Log.d("probability", "Hot");
									Intent showList = new Intent(Remarks_pop_up.this, Follow_ups_list_third_level.class);
									showList.putExtra("probFlag", "Hot");
									showList.putExtra("dayFlag", DayFlag);
									startActivity(showList);
								}
								else if(comStr.equalsIgnoreCase("Warm"))
								{
									Log.d("probability", "Warm");
									Intent showList = new Intent(Remarks_pop_up.this, Follow_ups_list_third_level.class);
									showList.putExtra("probFlag", "Warm");
									showList.putExtra("dayFlag", DayFlag);
									startActivity(showList);
								}

								else if(comStr.equalsIgnoreCase("Cold"))
								{
									Log.d("probability", "Cold");
									Intent showList = new Intent(Remarks_pop_up.this, Follow_ups_list_third_level.class);
									showList.putExtra("probFlag", "Cold");
									showList.putExtra("dayFlag", DayFlag);
									startActivity(showList);
								}
						} catch (Throwable e) {
							Log.d("Error", e+"");
						}
					}
					else if(from.equalsIgnoreCase("enquery"))
					{
						Log.d("From", "enquiry");
						Intent intent = new Intent(Remarks_pop_up.this, AllEnquiryTableActivity.class);
						Remarks_pop_up.this.startActivity(intent);
					}
				//===========================================================================================================
				//END Getting data from first activity
				//===========================================================================================================

			}
			else if(loginStatus.equalsIgnoreCase("F"))
			{
				if (pgLogin.isShowing()) {
					pgLogin.cancel();
					pgLogin.dismiss();
				}
				Toast.makeText( getApplicationContext(),"Not Getting Follow up list from server.",Toast.LENGTH_SHORT).show();
			}
		} 
		catch (Throwable t) 
		{
			Log.d("Error On Saving and reading", t+"");
		}
	}
	//===================================================================================================================================
	//processing the XML got from server for Pending Follow
	//===================================================================================================================================

	
	
	
	
	//===================================================================================================================================
	//check packet data and wifi
	//===================================================================================================================================
	private boolean haveNetworkConnection() 
	{
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) 
		{
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
	//====================================================================================================================================
	//checking packet data and wifi END
	//====================================================================================================================================

}