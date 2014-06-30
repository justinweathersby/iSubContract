package com.example.isubcontract;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import calanderdb.Invoice;
import calanderdb.InvoiceContentProvider;
import calanderdb.*;
import android.app.Activity;
import android.R.string;
import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

@SuppressWarnings("unused")
public class DisplayInvoiceActivity extends Activity
{
		EditText nameEt, locationEt, dateEt, startEt, stopEt, rateEt;
		
		Button addBtn, clearBtn, backBtn;
		
		
		private SimpleCursorAdapter myAdapter;
		private Cursor myCursor;
		
		long key;	//The id of the student is passed from the caller.
		
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_display_invoice);
			
			//Record _id is in the Bundle with key studentID
		Intent callersIntent = getIntent();
		key = callersIntent.getLongExtra("idKey", -999);
			
			//Set up the button widgets
			addBtn = (Button) findViewById(R.id.addBTN);
			clearBtn = (Button) findViewById(R.id.clearBTN);
			backBtn = (Button) findViewById(R.id.backBTN);
			
			//Set up the same listener for all buttons
			addBtn.setOnClickListener(myButtonListener);
			clearBtn.setOnClickListener(myButtonListener);
			backBtn.setOnClickListener(myButtonListener);
			
			//
			nameEt = (EditText) findViewById(R.id.nameET);
			dateEt = (EditText) findViewById(R.id.dateET);
			locationEt = (EditText) findViewById(R.id.locationET);
			startEt = (EditText) findViewById(R.id.startET);
			stopEt = (EditText) findViewById(R.id.stopET);
			rateEt = (EditText) findViewById(R.id.rateET);
			
			TimeSheet timeSheet = new TimeSheet();
			
			
		}//END onCreate

		
		//Single click listener for all four buttons
		private OnClickListener myButtonListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId())
				{
					case R.id.saveBTN:		updateRecordToDB();
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
			;
		}
		
		private void updateRecordToDB()
		{
		;
		}
		
		//Format a string date into yyyy-MM-dd HH:mm:ss
		public String MyDateFormatter(String createDate)
		{
			String newDate = null;
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
			long milliSeconds= Long.parseLong(createDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(milliSeconds);
	        newDate = sdf.format(calendar.getTime());
	        
			return newDate;
			
		}
}
