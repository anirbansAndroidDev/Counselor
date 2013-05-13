package com.ica.commons;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "counselor_db";

	// Labels table name 
	private static final String TABLE_LABELS = "follow_up";
	private static final String TABLE_CENTER_LIST = "center_list";
	private static final String TABLE_COUNSELOR_LIST = "counselor_list";


	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {

		// all_enquiry_list table create query
		String CREATE_CENTER_TABLE = "CREATE TABLE if not exists all_enquiry_list (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, " +
				"Enquiry_id VARCHAR, StudentName VARCHAR, Phone VARCHAR, Mobile VARCHAR, EnquiryDate VARCHAR, " +
				"Followup_id VARCHAR, WebSource VARCHAR, WebSourceAlias VARCHAR)";
		db.execSQL(CREATE_CENTER_TABLE);

		// counselor_list table create query
		String CREATE_COUNSELOR_TABLE = "CREATE TABLE if not exists counselor_list (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, " +
				"Counselor_id VARCHAR, CounselorName VARCHAR)";
		db.execSQL(CREATE_COUNSELOR_TABLE);

		// follow_ups_list table create query
		String CREATE_FOLLOWUP_TABLE = "CREATE TABLE if not exists follow_ups_list (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, " +
				"StudentName VARCHAR, Course_name VARCHAR, Residence_phone VARCHAR, Mobile VARCHAR, Enquiry_Datetime VARCHAR, " +
				"Probability VARCHAR, LastFollowupDate VARCHAR, NextFollowupDate VARCHAR, CFollowUP VARCHAR, Enquiry_id VARCHAR," +
				" FollowUpRemark VARCHAR, Course_id VARCHAR, PrefCallTimeId VARCHAR, PrefCallTime VARCHAR, DayFlag VARCHAR, WebSource VARCHAR,WebSourceAlias VARCHAR)";
		db.execSQL(CREATE_FOLLOWUP_TABLE);
		
		// course_list table create query
				String CREATE_COURSE_LIST_TABLE = "CREATE TABLE if not exists course_list (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, " +
						"Course_id VARCHAR, CAlias VARCHAR, Course_name VARCHAR)";
				db.execSQL(CREATE_COURSE_LIST_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LABELS);

		// Create tables again
		onCreate(db);
	}


	//=================Time name ascending=================================
	public List<String> getTimeNameTimeAsc(String ProbFlag,String DayFlag){
		
		String selectQuery = null;
		if(DayFlag.equalsIgnoreCase("TODAY"))
		{
			Log.d("Query", "On TOday");
			selectQuery = "SELECT PrefCallTime FROM follow_ups_list  where Probability ='" + ProbFlag + "' and (DayFlag ='" + DayFlag + "' or DayFlag ='OLD') ORDER BY PrefCallTimeId";
		}
		else
		{
			// Select All Query
			selectQuery = "SELECT PrefCallTime FROM follow_ups_list  where Probability ='" + ProbFlag + "' and DayFlag ='" + DayFlag + "' ORDER BY PrefCallTimeId";
		}
		List<String> labels = new ArrayList<String>();


		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}



		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}


	public List<String> getStudentNameTimeAsc(String ProbFlag,String DayFlag){
		String selectQuery = null;
		if(DayFlag.equalsIgnoreCase("TODAY"))
		{
			Log.d("Query", "On TOday");
			selectQuery = "SELECT StudentName,_id FROM follow_ups_list  where Probability ='" + ProbFlag + "' and (DayFlag ='" + DayFlag + "'  or DayFlag ='OLD') ORDER BY PrefCallTimeId";
		}
		else
		{
			// Select All Query
			selectQuery = "SELECT StudentName,_id FROM follow_ups_list  where Probability ='" + ProbFlag + "' and DayFlag ='" + DayFlag + "' ORDER BY PrefCallTimeId";
		}

		List<String> labels = new ArrayList<String>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0) + "," + cursor.getString(1));
			} while (cursor.moveToNext());
		}



		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}

	//=================Time name ascending=================================


	//=================Time name descending=================================

	public List<String> getTimeNameTimeDsc(String ProbFlag,String DayFlag){
		List<String> labels = new ArrayList<String>();
		String selectQuery = null;
		if(DayFlag.equalsIgnoreCase("TODAY"))
		{
			Log.d("Query", "On TOday");
			selectQuery = "SELECT PrefCallTime FROM follow_ups_list  where Probability ='" + ProbFlag + "' and (DayFlag ='" + DayFlag + "' or DayFlag ='OLD') ORDER BY PrefCallTimeId DESC";
		}
		else
		{
			// Select All Query
			selectQuery = "SELECT PrefCallTime FROM follow_ups_list  where Probability ='" + ProbFlag + "' and DayFlag ='" + DayFlag + "' ORDER BY PrefCallTimeId DESC";
		}

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}


	public List<String> getStudentNameTimeDsc(String ProbFlag,String DayFlag){
		String selectQuery = null;
		if(DayFlag.equalsIgnoreCase("TODAY"))
		{
			Log.d("Query", "On TOday");
			selectQuery = "SELECT StudentName,_id FROM follow_ups_list  where Probability ='" + ProbFlag + "' and (DayFlag ='" + DayFlag + "' or DayFlag ='OLD') ORDER BY PrefCallTimeId DESC";
		}
		else
		{
			// Select All Query
			selectQuery = "SELECT StudentName,_id FROM follow_ups_list  where Probability ='" + ProbFlag + "' and DayFlag ='" + DayFlag + "' ORDER BY PrefCallTimeId DESC";
		}

		List<String> labels = new ArrayList<String>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0) + "," + cursor.getString(1));
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}

	//=================Time name descending=================================

	//=================Student name ascending=================================
	public List<String> getTimeNameSyudentAsc(String ProbFlag,String DayFlag){
		String selectQuery = null;
		if(DayFlag.equalsIgnoreCase("TODAY"))
		{
			Log.d("Query", "On TOday");
			selectQuery = "SELECT PrefCallTime FROM follow_ups_list  where Probability ='" + ProbFlag + "' and (DayFlag ='" + DayFlag + "' or DayFlag ='OLD') ORDER BY PrefCallTimeId DESC";
		}
		else
		{
			// Select All Query
			selectQuery = "SELECT PrefCallTime FROM follow_ups_list  where Probability ='" + ProbFlag + "' and DayFlag ='" + DayFlag + "' ORDER BY PrefCallTimeId DESC";
		}

		List<String> labels = new ArrayList<String>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}



		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}


	public List<String> getStudentNameStudentAsc(String ProbFlag,String DayFlag){
		String selectQuery = null;
		if(DayFlag.equalsIgnoreCase("TODAY"))
		{
			Log.d("Query", "On TOday");
			selectQuery = "SELECT StudentName,_id FROM follow_ups_list  where Probability ='" + ProbFlag + "' and (DayFlag ='" + DayFlag + "' or DayFlag ='OLD') ORDER BY StudentName";
		}
		else
		{
			// Select All Query
			selectQuery = "SELECT StudentName,_id FROM follow_ups_list  where Probability ='" + ProbFlag + "' and DayFlag ='" + DayFlag + "' ORDER BY StudentName";
		}

		List<String> labels = new ArrayList<String>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0) + "," + cursor.getString(1));
			} while (cursor.moveToNext());
		}



		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}

	//=================Student name ascending=================================


	//=================Student name descending=================================

	public List<String> getTimeNameStudentDsc(String ProbFlag,String DayFlag){
		String selectQuery = null;
		if(DayFlag.equalsIgnoreCase("TODAY"))
		{
			Log.d("Query", "On TOday");
			selectQuery = "SELECT PrefCallTime FROM follow_ups_list  where Probability ='" + ProbFlag + "' and (DayFlag ='" + DayFlag + "' or DayFlag ='OLD') ORDER BY StudentName DESC";
		}
		else
		{
			// Select All Query
			selectQuery = "SELECT PrefCallTime FROM follow_ups_list  where Probability ='" + ProbFlag + "' and DayFlag ='" + DayFlag + "' ORDER BY StudentName DESC";
		}

		List<String> labels = new ArrayList<String>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}


	public List<String> getStudentNameStudentDsc(String ProbFlag,String DayFlag){
		String selectQuery = null;
		if(DayFlag.equalsIgnoreCase("TODAY"))
		{
			Log.d("Query", "On TOday");
			selectQuery = "SELECT StudentName,_id FROM follow_ups_list  where Probability ='" + ProbFlag + "' and (DayFlag ='" + DayFlag + "' or DayFlag ='OLD') ORDER BY StudentName DESC";
		}
		else
		{
			Log.d("Query", "On TOday");
			// Select All Query
			selectQuery = "SELECT StudentName,_id FROM follow_ups_list  where Probability ='" + ProbFlag + "' and DayFlag ='" + DayFlag + "' ORDER BY StudentName DESC";
		}
		
		List<String> labels = new ArrayList<String>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0) + "," + cursor.getString(1));
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}

	//=================Student name descending=================================

	//====================================================================================================================
	// Query for all_enquiry_list
	//====================================================================================================================
	public void insertCenterValues(String Enquiry_id, String StudentName, String Phone, String Mobile, String EnquiryDate,String Followup_id, String WebSource, String WebSourceAlias)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "INSERT INTO all_enquiry_list ( Enquiry_id, StudentName, Phone, Mobile, EnquiryDate, Followup_id, WebSource, WebSourceAlias) VALUES ('"+Enquiry_id+"', '"+StudentName+"', '"+Phone+"', '"+Mobile+"', '"+EnquiryDate+"', '"+Followup_id+"', '"+WebSource+"', '"+WebSourceAlias+"')";
		Cursor cursor = db.rawQuery(sql, null); //<< execute here 
		cursor.moveToFirst();
		db.close();
	}

	//delete all rows
	public void deleteAllCenterData()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		long ret = db.delete("all_enquiry_list", null, null);
		db.close();
	}

	//=================Student name and _id=================================   

	public List<String> getStudentNameAllEnquiryStudentAsc(){
		List<String> labels = new ArrayList<String>();

		// Select All Query
		String selectQuery = "SELECT StudentName FROM all_enquiry_list ORDER BY StudentName";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}

	public List<String> getIdAllEnquiryStudentAsc(){
		List<String> labels = new ArrayList<String>();

		// Select All Query
		String selectQuery = "SELECT _id FROM all_enquiry_list ORDER BY StudentName";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}


		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}




	public List<String> getStudentNameAllEnquiryStudentDsc(){
		List<String> labels = new ArrayList<String>();

		// Select All Query
		String selectQuery = "SELECT StudentName FROM all_enquiry_list ORDER BY StudentName DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}

	public List<String> getIdAllEnquiryStudentDsc(){
		List<String> labels = new ArrayList<String>();

		// Select All Query
		String selectQuery = "SELECT _id FROM all_enquiry_list ORDER BY StudentName DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				labels.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		// returning lables
		return labels;
	}

	//=================Student name and _id=================================

	//=================Student mobile and source============================ 

	// select statement for getting mobile
	public String selectAllEnqueryMobile(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String str_mobile = "";
		String sql = "SELECT Mobile FROM all_enquiry_list WHERE _id=" + "'"
				+ id + "'";
		// Log.i("select: ",sql);
		Cursor cursor = db.rawQuery(sql, null); // << execute here
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			str_mobile = cursor.getString(cursor.getColumnIndex("Mobile"));
			// Log.i("query result: ",str_answer);
		}
		return str_mobile;
	}
	
	// select statement for getting mobile
		public String selectAllEnquiryEnquiryId(String id) {
			SQLiteDatabase db = this.getWritableDatabase();
			String str_enquiry_id = "";
			String sql = "SELECT Enquiry_id FROM all_enquiry_list WHERE _id='" + id + "'";
			
			Cursor cursor = db.rawQuery(sql, null); // << execute here
			cursor.moveToFirst();
			
			if (cursor.getCount() > 0) {
				str_enquiry_id = cursor.getString(cursor.getColumnIndex("Enquiry_id"));
			}
			return str_enquiry_id;
		}

	// select statement for getting source
	public String selectAllEnquerySource(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String str_source = "";
		String sql = "SELECT WebSourceAlias FROM all_enquiry_list WHERE _id=" + "'"
				+ id + "'";
		// Log.i("select: ",sql);
		Cursor cursor = db.rawQuery(sql, null); // << execute here
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			str_source = cursor.getString(cursor.getColumnIndex("WebSourceAlias"));
			// Log.i("query result: ",str_answer);
		}
		return str_source;
	}

	//=================Student mobile and source============================ 

	//=================Enquiry id======================================

	// select statement for getting Enquiry id
	public String selectAllEnqueryId(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String str_source = "";
		String sql = "SELECT Enquiry_id FROM all_enquiry_list WHERE _id=" + "'"
				+ id + "'";
		// Log.i("select: ",sql);
		Cursor cursor = db.rawQuery(sql, null); // << execute here
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			str_source = cursor.getString(cursor.getColumnIndex("Enquiry_id"));
			// Log.i("query result: ",str_answer);
		}
		return str_source;
	}
	//=================Enquiry id======================================



	//====================================================================================================================
	// Query for counselor_list
	//====================================================================================================================
	public Cursor getCounselorList(){

		// Select All Query
		String selectQuery = "SELECT * FROM counselor_list";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);


		// returning labels
		return cursor;
	}

	public void insertCounselorValues(String Counselor_id, String CounselorName)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "INSERT INTO counselor_list ( Counselor_id, CounselorName) VALUES ('"+Counselor_id+"', '"+CounselorName+"')";
		Cursor cursor = db.rawQuery(sql, null); //<< execute here 
		cursor.moveToFirst();
		db.close();
	}

	//delete all rows
	public void deleteAllCounselorData()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		long ret = db.delete("counselor_list", null, null);
		db.close();
	}

	//====================================================================================================================
	// Query for follow_ups_list
	//====================================================================================================================
	public void insertFollowUpsListsValues(String StudentName, String Course_name, String Residence_phone, String Mobile, String Enquiry_Datetime, String Probability, String LastFollowupDate, String NextFollowupDate, String CFollowUP, String Enquiry_id, String FollowUpRemark, String Course_id, String PrefCallTimeId, String PrefCallTime, String DayFlag, String WebSource, String WebSourceAlias)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "INSERT INTO follow_ups_list ( StudentName, Course_name, Residence_phone, Mobile, Enquiry_Datetime, Probability, LastFollowupDate, NextFollowupDate, CFollowUP, Enquiry_id, FollowUpRemark, Course_id, PrefCallTimeId, PrefCallTime, DayFlag, WebSource, WebSourceAlias) VALUES ('"+StudentName+"', '"+Course_name+"', '"+Residence_phone+"', '"+Mobile+"', '"+Enquiry_Datetime+"', '"+Probability+"', '"+LastFollowupDate+"', '"+NextFollowupDate+"' , '"+CFollowUP+"', '"+Enquiry_id+"', '"+FollowUpRemark+"', '"+Course_id+"', '"+PrefCallTimeId+"', '"+PrefCallTime+"', '"+DayFlag+"', '"+WebSource+"', '"+WebSourceAlias+"')";

		Cursor cursor = db.rawQuery(sql, null); //<< execute here 
		cursor.moveToFirst();
		db.close();
	}

	//delete all rows
	public void deleteFollowUpsListsData()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		long ret = db.delete("follow_ups_list", null, null);
		db.close();
	}

	public int getFollowUpsListsProbabilityNo(String DayFlag, String Probability){

		// Select All Query
		String selectQuery = "SELECT count(*) FROM follow_ups_list where DayFlag = '" + DayFlag + "' and Probability = '" + Probability + "'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		int count = 0;
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do 
			{
				String StrCount = cursor.getString(0);
				count = Integer.parseInt(StrCount);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();
		
		return count;
	}

	public int getFollowUpsListsProbabilityNoForToday(String DayFlagOne, String DayFlagTwo, String Probability){

		// Select All Query
		String selectQuery = "SELECT count(*) FROM follow_ups_list where (DayFlag = '"+ DayFlagOne +"' or  DayFlag = '"+ DayFlagTwo +"') and Probability = '" + Probability + "'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		int count = 0;
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do 
			{
				String StrCount = cursor.getString(0);
				count = Integer.parseInt(StrCount);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		db.close();

		return count;
	}
	
		// select statement for getting mobile
		public String selectFollowUpMobile(String id) {
			SQLiteDatabase db = this.getWritableDatabase();
			String str_mobile = "";
			String sql = "SELECT Mobile FROM follow_ups_list WHERE _id=" + "'"
					+ id + "'";
			// Log.i("select: ",sql);
			Cursor cursor = db.rawQuery(sql, null); // << execute here
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				str_mobile = cursor.getString(cursor.getColumnIndex("Mobile"));
				// Log.i("query result: ",str_answer);
			}
			return str_mobile;
		}

		// select statement for getting source
		public String selectFollowUpSource(String id) {
			SQLiteDatabase db = this.getWritableDatabase();
			String str_source = "";
			String sql = "SELECT WebSourceAlias FROM follow_ups_list WHERE _id=" + "'"
					+ id + "'";
			// Log.i("select: ",sql);
			Cursor cursor = db.rawQuery(sql, null); // << execute here
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				str_source = cursor.getString(cursor.getColumnIndex("WebSourceAlias"));
				// Log.i("query result: ",str_answer);
			}
			return str_source;
		}
		
		
		public String selectFollowUpEnquiryId(String fromPreference) {
			
			SQLiteDatabase db = this.getWritableDatabase();
			String Enquiry_id = "";
			String sql = "SELECT Enquiry_id FROM follow_ups_list WHERE _id= '" + fromPreference + "'";

			Cursor cursor = db.rawQuery(sql, null); // << execute here
			cursor.moveToFirst();
			
			if (cursor.getCount() > 0) {
				Enquiry_id = cursor.getString(cursor.getColumnIndex("Enquiry_id"));
			}
			return Enquiry_id;
		}

	//====================================================================================================================
	// Query for course_list
	//====================================================================================================================
	public Cursor getCourseList(){

		// Select All Query
		String selectQuery = "SELECT * FROM course_list";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);


		// returning labels
		return cursor;
	}

	public void insertCourseValues(String Course_id, String CAlias, String Course_name)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "INSERT INTO course_list ( Course_id, CAlias, Course_name) VALUES ('"+Course_id+"', '"+CAlias+"', '"+Course_name+"')";
		Cursor cursor = db.rawQuery(sql, null); //<< execute here 
		cursor.moveToFirst();
		db.close();
	}

	//delete all rows
	public void deleteAllCourseData()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		long ret = db.delete("course_list", null, null);
		db.close();
	}

	//====================================================================================================================
	// Query for course_list
	//====================================================================================================================
}