package com.example.isubcontract;

import java.text.DateFormat;
import java.util.Date;

import calanderdb.*;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnterInvoiceActivity extends Activity
{
	EditText nameEt, locationEt, dateEt, arrivalEt, departureEt, rateEt;
	
	Button saveBtn, clearBtn, backBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enterinvoice);
	
	
		//Set up all EditText widgets
		nameEt		= (EditText) findViewById(R.id.nameET);
		locationEt 	= (EditText) findViewById(R.id.locationET);
		dateEt 	= (EditText) findViewById(R.id.dateET);
		arrivalEt 	= (EditText) findViewById(R.id.arrivalET);
		departureEt 		= (EditText) findViewById(R.id.departureET);
		rateEt		= (EditText) findViewById(R.id.rateET);
		
		//Set curentDate to correct field
		Date currentDate = new Date(System.currentTimeMillis());
		String curDateTime = DateFormat.getDateTimeInstance().format(currentDate).toString();
		dateEt.setText(curDateTime);
		
		//Set up the button widgets
		saveBtn = (Button) findViewById(R.id.saveBTN);
		clearBtn = (Button) findViewById(R.id.clearBTN);
		backBtn = (Button) findViewById(R.id.backBTN);
		
		//Set up the same listener for all buttons
		saveBtn.setOnClickListener(myButtonListener);
		clearBtn.setOnClickListener(myButtonListener);
		backBtn.setOnClickListener(myButtonListener);
		
	}//END onCreate
	
	//Single click listener for all four buttons
	private OnClickListener myButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.saveBTN:		saveRecordToDB();
											break;
				case R.id.clearBTN:		clearInvoiceForm();
											break;
				case R.id.backBTN:		finish();
			}
		}//END onClick
	};//END myButtonListener
	
	//Clear all data from text fields on new invoice form
	public void clearInvoiceForm()
	{
		nameEt.setText(null);
		locationEt.setText(null);
		dateEt.setText(null); 	
		arrivalEt.setText(null); 
		departureEt.setText(null); 		
		rateEt.setText(null);
	}
	
	//Save one record to the DB. If both values are not entered,
	//displays a toast only. If values are sound, they are saved.
	//Then, all EditText widgets are cleared.
	public void saveRecordToDB()
	{	
		if (recordIsComplete())
		{
			//Set up a ContentValues object, simply a set of key/value pairs.
			ContentValues values = new ContentValues();
			values.put(Invoice.KEY_ID, 0);	//_id of 0 means a new record
			values.put(Invoice.NAME_FIELD, nameEt.getText().toString());
			values.put(Invoice.LOCATION_FIELD, locationEt.getText().toString());
			values.put(Invoice.DATE_FIELD, dateEt.getText().toString());
			values.put(Invoice.START_FIELD, dateEt.getText().toString());
			values.put(Invoice.STOP_FIELD, dateEt.getText().toString());
			values.put(Invoice.RATE_FIELD, dateEt.getText().toString());
			values.put(Invoice.TOTAL_FIELD, "$0.00");
			
			//Get a ContentResolver and call its insert method. It passes this to 
			//the correct ContentProvider, ours.
			Uri recordUri = getContentResolver().insert(InvoiceContentProvider.TABLE_URI, values);
			//The URI returned in recordUri identifies the newly-added row, with the following format:
			//  /transcripttable/#, where # is the new _id
			//To display the URI's value, use uri.getEncodedPath()
			
			//This extracts the new _id from the uri object returned, in case you want to test it.
			long rowId = ContentUris.parseId(recordUri);
			
			//Test display in Log
			Log.d("In EnterGradeActivity/saveRecordToDB", "URI: " + recordUri.getEncodedPath());
			Log.d("In EnterGradeActivity/saveRecordToDB", "Save new rec #" + Long.toString(rowId));
			
			//If the returned id is valid, clear out EditTexts and display a Toast message
			if (rowId >= 0)
			{
				clearInvoiceForm();
				//Tell the user
				Toast.makeText(this, "New record with ID = " + rowId + " saved!", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "Record could not be created!", Toast.LENGTH_SHORT).show();
			}
		}
		else //The record was not completely entered.
		{
			Toast.makeText(this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
		}
			
	}//END saveRecordToDB

	//Test to see that all fields have been entered.
	private boolean recordIsComplete()
	{
		boolean recordOK = true;
		if (	
				nameEt.getText().toString().equals("") ||
				locationEt.getText().toString().equals("") ||
				dateEt.getText().toString().equals("") ||
				departureEt.getText().toString().equals("") ||
				arrivalEt.getText().toString().equals("") ||
				rateEt.getText().toString().equals("")
			)
			recordOK = false;

		return recordOK;
	}//END recordIsComplete

}//END EnterGradeActivity

