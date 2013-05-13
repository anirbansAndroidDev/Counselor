package com.ica.icacounselor;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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

import android.widget.TableRow.LayoutParams;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ica.commons.DatabaseHandler;

public class Follow_ups_list_fourth_level extends Activity {

	TextView name;
	TextView mobile;
	TextView source;

	Button remarks;
	Button call;

	TableLayout prev_details_table;
	TableRow prev_details_data;
	
	String stringLoginUser;
	String stringLoginPwd;
	String Enquiry_id;
	String Next_followup_datetime;
	String Probability;
	String PrefCallTime;
	String FollowupRemarks;
	String Mobile;
	String Course_id;
	String CounselorId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follow_ups_list_fourth_level);


		// assigning objects to layout
		name    = (TextView) findViewById(R.id.follow_fourth_name_textView);
		mobile  = (TextView) findViewById(R.id.follow_fourth_mobile_textView);
		source  = (TextView) findViewById(R.id.follow_fourth_source_textView);
		remarks = (Button)findViewById(R.id.follow_fourth_remarks_button);
		call	= (Button)findViewById(R.id.follow_fourth_call_button);
		
		//----------------------------------------------------------
		//Setting up information for upper table
		//----------------------------------------------------------
		source.setText(getSource());
		name.setText(getFromPreference("followUpName"));
		mobile.setText(getMobile());
		saveInPreference("Mobile",getMobile());
		//----------------------------------------------------------
		//END Setting up information for upper table
		//----------------------------------------------------------

		stringLoginUser = getFromPreference("stringLoginUser");
		stringLoginPwd  = getFromPreference("stringLoginPwd");
		Enquiry_id      = getEnquiryId();
		saveInPreference("Enquiry_id",getEnquiryId());
		
		Log.d("Sending to server", stringLoginUser + "  " + stringLoginPwd + "  " + Enquiry_id);
		new MyAsyncTaskForFollowUpHistory().execute(stringLoginUser,stringLoginPwd,Enquiry_id);
		
		
		//remarks button onclick
		remarks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Follow_ups_list_fourth_level.this, Remarks_pop_up.class);
				intent.putExtra("from", "followUp");
				Follow_ups_list_fourth_level.this.startActivity(intent);
			}
		});

		//call button onclick
		call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				call();
			}
		});


		//==========================Previous Details table=================================================

		prev_details_table=(TableLayout) findViewById(R.id.previous_details_table);
		//---------------Table Header-----------------------------------------------
		TableRow prev_details_head = new TableRow(this);
		prev_details_head.setId(10);
		prev_details_head.setBackgroundResource(R.drawable.blue_border);
		prev_details_head.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));


		final TextView date_title = new TextView(this);
		date_title.setId(20);
		date_title.setText("Followup date");
		date_title.setTextColor(Color.CYAN);
		date_title.setPadding(10,5,5,5);
		prev_details_head.addView(date_title);// add the column to the table row here
		date_title.setTextSize(13);    

		final TextView probability_title = new TextView(this);
		probability_title.setId(20);
		probability_title.setText("Probability");
		probability_title.setTextColor(Color.CYAN);
		probability_title.setPadding(5,5,5,5);
		prev_details_head.addView(probability_title);// add the column to the table row here
		probability_title.setTextSize(13);    

		final TextView remarks_title = new TextView(this);
		remarks_title.setId(20);
		remarks_title.setText("Remarks");
		remarks_title.setTextColor(Color.CYAN);
		remarks_title.setPadding(5,5,5,5);
		prev_details_head.addView(remarks_title);// add the column to the table row here
		remarks_title.setTextSize(13);    



		prev_details_table.addView(prev_details_head, new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		//--------------- Table Header-----------------------------------------------
	}
	
	@Override
	public void onBackPressed() {
		
		try {
			String DayFlag   = getFromPreference("DayFlagForBackBtnFromForthLevel");
			String comStr    = getFromPreference("SendOnBackBtnFromForth");
			
				//Need to send dayFlag as OLD
				if(comStr.equalsIgnoreCase("Hot"))
				{
					Intent showList = new Intent(Follow_ups_list_fourth_level.this, Follow_ups_list_third_level.class);
					showList.putExtra("probFlag", "Hot");
					showList.putExtra("dayFlag", DayFlag);
					startActivity(showList);
				}
				else if(comStr.equalsIgnoreCase("Warm"))
				{
					Intent showList = new Intent(Follow_ups_list_fourth_level.this, Follow_ups_list_third_level.class);
					showList.putExtra("probFlag", "Warm");
					showList.putExtra("dayFlag", DayFlag);
					startActivity(showList);
				}

				else if(comStr.equalsIgnoreCase("Cold"))
				{
					Intent showList = new Intent(Follow_ups_list_fourth_level.this, Follow_ups_list_third_level.class);
					showList.putExtra("probFlag", "Cold");
					showList.putExtra("dayFlag", DayFlag);
					startActivity(showList);
				}
		} catch (Throwable e) {
			Log.d("Error", e+"");
		}
		super.onBackPressed();
	}

	//method to get mobile number from database
	String getMobile()
	{
		// getting status from database
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());

		String mobileStr= db.selectFollowUpMobile(getFromPreference("followUpId"));
		return mobileStr;
	}
	
	//method to get Enquire Id from database
	String getEnquiryId()
	{
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());

		String EnquiryId = db.selectFollowUpEnquiryId(getFromPreference("followUpId"));
		return EnquiryId;

	}

	//method to get source from database
	String getSource()
	{
		// getting status from database
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());

		String sourceStr= db.selectFollowUpSource(getFromPreference("followUpId"));
		return sourceStr;
	}

	private void call() {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"+ getMobile()));
			startActivity(callIntent);
		} catch (ActivityNotFoundException e) {
			Log.e("helloandroid dialing example", "Call failed", e);
		}
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
	
	
	
	
	//===================================================================================================================================
	//sending EmailAddress and Password to server
	//===================================================================================================================================
	private class MyAsyncTaskForFollowUpHistory extends AsyncTask<String, Integer, Double>{

		String responseBody = null;
		int responseCode;
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData(params[0],params[1],params[2]);
			return null;
		}

		protected void onPostExecute(Double result){
			processResponceForFollowUpHistory(responseBody);
		}
		
		protected void onProgressUpdate(Integer... progress){
		}

		public void postData(String stringLoginUser,String stringLoginPwd,String EnquiryId) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://203.153.37.4/icaservice/Centre.asmx/FollowupHistory");

			try {
				// Data that I am sending
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("stringLoginUser", stringLoginUser));
				nameValuePairs.add(new BasicNameValuePair("stringLoginPwd", stringLoginPwd));
				nameValuePairs.add(new BasicNameValuePair("EnquiryId", EnquiryId));
				
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
	//END sending EmailAddress and Password to server 
	//===================================================================================================================================


	
	//===================================================================================================================================
	//processing the XML got from server for COUNSELOR LIST
	//===================================================================================================================================
	private void processResponceForFollowUpHistory(String responceFromServer) 
	{
		try {
			//saving the file as a xml
			FileOutputStream fOut = openFileOutput("followUpListHistoryData.xml",MODE_WORLD_READABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(responceFromServer);
			osw.flush();
			osw.close();

			//reading the file as xml
			FileInputStream fIn = openFileInput("followUpListHistoryData.xml");
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

			String loginStatus = null;
			for (int i=0;i<root.getLength();i++) 
			{
				loginStatus = "" + ((Element)root.item(i)).getAttribute("status");
			}

			//If Email and Pass match with server
			if(loginStatus.equalsIgnoreCase("Y"))
			{
				NodeList mb=doc.getElementsByTagName("history");
				

				for (int i=0;i<mb.getLength();i++) 
				{
					String Followup_datetime = "" + ((Element)mb.item(i)).getAttribute("Followup_datetime");	
					String Probability 		 = "" + ((Element)mb.item(i)).getAttribute("Probability");
					String FollowUpRemark    = "" + ((Element)mb.item(i)).getAttribute("FollowUpRemark");
					
					//----------------table body------------------------------------------
					prev_details_data = new TableRow(this);
					prev_details_data.setId(10);
					prev_details_data.setBackgroundResource(R.drawable.purple_border);
					prev_details_data.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT));


					final TextView DateText = new TextView(this);
					DateText.setId(20);
					DateText.setText(Followup_datetime);
					DateText.setTextColor(Color.WHITE);
					DateText.setPadding(10,5,5,5);
					DateText.setTextSize(10);
					prev_details_data.addView(DateText);// add the column to the table row here

					final TextView ProbablityText = new TextView(this);
					ProbablityText.setId(20);
					ProbablityText.setText(Probability);
					ProbablityText.setTextColor(Color.WHITE);
					ProbablityText.setPadding(5,5,5,5);
					ProbablityText.setTextSize(10);
					prev_details_data.addView(ProbablityText);// add the column to the table row here

					final TextView RemarksText = new TextView(this);
					RemarksText.setId(20);
					RemarksText.setText(FollowUpRemark);
					RemarksText.setTextColor(Color.WHITE);
					RemarksText.setPadding(5,5,5,5);
					RemarksText.setTextSize(10);
					RemarksText.setWidth(10);
					prev_details_data.addView(RemarksText);// add the column to the table row here

					prev_details_table.addView(prev_details_data, new TableLayout.LayoutParams(
							LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT));

				}
				
			}
			else if(loginStatus.equalsIgnoreCase("F"))
			{
//				if (pgLogin.isShowing()) {
//					pgLogin.cancel();
//					pgLogin.dismiss();
//				}
				Toast.makeText( getApplicationContext(),"Not be able to get Follow up history.",Toast.LENGTH_SHORT).show();
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

	
}
