package com.ica.icacounselor;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
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
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ica.commons.DatabaseHandler;
import com.ica.icacounselor.SetCounselorList.MyData;

public class Login_activity extends Activity {
	private ProgressDialog pgLogin;
	private static String loginStatus = null;
	// checkbox obj
	CheckBox remember_me;
	EditText center_code;
	EditText password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_ica_counselor_main);

		center_code = (EditText)findViewById(R.id.txtCentercode);
		password = (EditText)findViewById(R.id.txtEntryPassword);
		remember_me = (CheckBox) findViewById(R.id.remember_me_checkBox);


		// getting user id and password from preference
		String remember_password = getFromPreference("remember_password");
		String remember_centerCode = getFromPreference("remember_center_code");

		// setting username and password to text box
		password.setText(remember_password);
		center_code.setText(remember_centerCode);
	}
	
	@Override
	public void onBackPressed() {
		
		//Exit from app
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		super.onBackPressed();
	}
	
	
	
	
	//================================================================================================================================
	//Login button
	//================================================================================================================================
	public void login(View v) {
		try {


			if(haveNetworkConnection())
			{
				if(center_code.getText().toString().length()<1 || password.getText().toString().length()<1)
				{
					Toast.makeText(this, "Please provide all information.", Toast.LENGTH_LONG).show();
				}
				else 
				{
					if(center_code.getText().toString().length() < 5)
					{
						pgLogin = new ProgressDialog(Login_activity.this);
						pgLogin.setMessage("Please wait while progress login...");
						pgLogin.setIndeterminate(true);
						pgLogin.setCancelable(true);
						pgLogin.setCanceledOnTouchOutside(false);

						pgLogin.show();

						
						if (remember_me.isChecked()) 
						{
							saveInPreference("remember_password", password.getText().toString());
							saveInPreference("remember_center_code", center_code.getText().toString());
						}
						else
						{
							saveInPreference("remember_password", "");
							saveInPreference("remember_center_code", "");
						}

						new MyAsyncTaskForAllEnquire().execute(center_code.getText().toString(), password.getText().toString());
					}
					else
					{
						Toast.makeText(this, "Please enter a valid center code.", Toast.LENGTH_LONG).show();
					}
				}
			}
			else
			{
				Toast.makeText(this, "Sorry! No internet connection.", Toast.LENGTH_LONG).show();
			}
		} catch (Throwable e) {
			System.out.println(e+"");
		}
	}
	//================================================================================================================================
	//Login button  
	//================================================================================================================================

	
	
	
	public void cancel(View v) {
		//Exit from app
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	
	
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
				Toast.makeText(Login_activity.this, "Not getting proper responce from server.\nCould be wifi problem or server.", Toast.LENGTH_LONG).show();
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

				DatabaseHandler dbhForEnquiryDelete = new DatabaseHandler(Login_activity.this);
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
					DatabaseHandler dbh = new DatabaseHandler(Login_activity.this);
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
				
				new MyAsyncTaskForCourseList().execute(center_code.getText().toString(), password.getText().toString());
				
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

				DatabaseHandler dbhForCourseDelete = new DatabaseHandler(Login_activity.this);
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
					DatabaseHandler dbhCourseObject = new DatabaseHandler(Login_activity.this);
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
				
				new MyAsyncTaskForPendingFollowUps().execute(center_code.getText().toString(), password.getText().toString());

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
	//sending EmailAddress and Password to server for COUNSELOR LIST
	//===================================================================================================================================
	private class MyAsyncTaskForCounselor extends AsyncTask<String, Integer, Double>{

		String responseBody;
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData(params[0],params[1]);
			return null;
		}

		protected void onPostExecute(Double result){
			//Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();
			processResponceForCounselor(responseBody);
		}

		protected void onProgressUpdate(Integer... progress){

		}

		public void postData(String stringLoginUser,String stringLoginPwd) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://203.153.37.4/icaservice/Centre.asmx/counselorList");

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
	//END sending EmailAddress and Password to server for COUNSELOR LIST
	//===================================================================================================================================
	
	
	
	
	//===================================================================================================================================
	//processing the XML got from server for COUNSELOR LIST
	//===================================================================================================================================
	private void processResponceForCounselor(String responceFromServer) 
	{
		try {
			//saving the file as a xml
			FileOutputStream fOut = openFileOutput("counselorListData.xml",MODE_WORLD_READABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(responceFromServer);
			osw.flush();
			osw.close();

			//reading the file as xml
			FileInputStream fIn = openFileInput("counselorListData.xml");
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
				NodeList mb=doc.getElementsByTagName("Counselor");
				
				//==============================================================================
				//Delete all records from counselorTable if record exist 
				//==============================================================================

				DatabaseHandler dbhForDelete = new DatabaseHandler(Login_activity.this);
				Cursor couserForDelete = dbhForDelete.getCounselorList();

				if (couserForDelete.moveToFirst())
				{
					dbhForDelete.deleteAllCounselorData();
				}
				else
				{}
				dbhForDelete.close();
				//==============================================================================
				//Delete all records from counselorTable if record exist 
				//==============================================================================

				for (int i=0;i<mb.getLength();i++) 
				{
					String Counselor_id = "" + ((Element)mb.item(i)).getAttribute("Counseller_id");	
					String CounselorName = "" + ((Element)mb.item(i)).getAttribute("CounselorName");
					//==============================================================================
					//insert to local database
					//==============================================================================
					DatabaseHandler dbhObject = new DatabaseHandler(Login_activity.this);
					ContentValues values = new ContentValues();
					//db.createDataBase();
					values.put("Counselor_id",Counselor_id);
					values.put("CounselorName",CounselorName);

					dbhObject.insertCounselorValues(Counselor_id, CounselorName);
					dbhObject.close();
					//==============================================================================
					//insert to local database
					//==============================================================================
				}
				
				saveInPreference("stringLoginUser", center_code.getText().toString().toUpperCase());
				saveInPreference("stringLoginPwd", password.getText().toString());

				//After all data inserted into counselor table move the activity to "SetCounselorList" Activity
				Intent i = new Intent(this, SetCounselorList.class);
				finish();
				startActivity(i);
			}
			else if(loginStatus.equalsIgnoreCase("F"))
			{
				if (pgLogin.isShowing()) {
					pgLogin.cancel();
					pgLogin.dismiss();
				}
				Toast.makeText( getApplicationContext(),"Not be able to get Counselor List.",Toast.LENGTH_SHORT).show();
			}
		} 
		catch (Throwable t) 
		{
			Log.d("Error On Saving and reading", t+"");
		}
	}
	//===================================================================================================================================
	//processing the XML got from server for COUNSELOR LIST
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

				DatabaseHandler dbhForFollowUpsDelete = new DatabaseHandler(Login_activity.this);
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
					DatabaseHandler dbFollowUpsValue = new DatabaseHandler(Login_activity.this);
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
				
				//getting value from preference variable "counselor"
				String CounselorId = null;
				CounselorId = getFromPreference("CounselorId");
				
				if(CounselorId != null && CounselorId != "")
				{
					saveInPreference("stringLoginUser", center_code.getText().toString());
					saveInPreference("stringLoginPwd",  password.getText().toString());

					saveInPreference("CounselorId",CounselorId);
					Intent i = new Intent(this, Follow_ups_list_first_level.class);
					finish();
					startActivity(i);
				}
				else
				{
					new MyAsyncTaskForCounselor().execute(center_code.getText().toString(), password.getText().toString());
				}

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
