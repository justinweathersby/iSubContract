package com.example.isubcontract;

import java.text.DateFormat;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainMenuActivity extends Activity 
{
	EditText locationEt, dateEt;
	
	TextView locationTV;

	Button startBtn, calanderBtn, optionsBtn, exitBtn;
	
	String curDateTime;
	
	Date currentDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		currentDate = new Date(System.currentTimeMillis());
		curDateTime = DateFormat.getDateTimeInstance().format(currentDate).toString();
		
		setUpMainButtons();
	}
		
	private void setUpMainButtons()
	{
		//Set up all buttons
		startBtn = (Button) findViewById(R.id.roundStartBTN);
		calanderBtn = (Button) findViewById(R.id.calanderBTN);
		optionsBtn = (Button) findViewById(R.id.optionsBTN);
		exitBtn = 	(Button) findViewById(R.id.exitBTN);
		
		//Set up edit Text fields
		locationEt = (EditText) findViewById(R.id.locationET);
		dateEt = (EditText) findViewById(R.id.dateET);
		
		//Set up the same listener for all buttons
		startBtn.setOnClickListener(myButtonListener);
		optionsBtn.setOnClickListener(myButtonListener);
		calanderBtn.setOnClickListener(myButtonListener);
		exitBtn.setOnClickListener(myButtonListener);
		
		dateEt.setText(curDateTime);
		
	}//END setUpMainButtons	
		
	//Single click listener for all four buttons
	private OnClickListener myButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
				switch (v.getId())
				{
				
					case R.id.calanderBTN:			startActivity(new Intent(getBaseContext(), CalenderListActivity.class));
													break;
					case R.id.roundStartBTN:		startActivity(new Intent(getBaseContext(), EnterInvoiceActivity.class));
													break;
					case R.id.exitBTN:				finish();
				}
		}//END onClick
	};//END myButtonListener
}
