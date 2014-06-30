//FILE : CalanderListActivity.java
//PROG : Justin Weathersby
//PURP : 

package com.example.isubcontract;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import calanderdb.*;
import android.R.string;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

@SuppressWarnings("unused")
public class CalenderListActivity extends ListActivity
								implements LoaderCallbacks<Cursor>
{
	Button exitBtn;
	
	
	private SimpleCursorAdapter myAdapter;
	long idIn;	//The id of the student is passed from the caller.
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calander_list);
		
		//Record _id is in the Bundle with key studentID
		Intent callersIntent = getIntent();
		idIn = callersIntent.getLongExtra("invoiceID", -999);
		
		//Create array to specify the fields to display
		String[] from = new String[]
			{	 
				Invoice.NAME_FIELD,
				Invoice.DATE_FIELD,
				Invoice.LOCATION_FIELD,
				Invoice.TOTAL_FIELD
				
			};
		
		//Create array of layouts to bind those fields to
		int[] to = new int[]	{	R.id.transrowTV1,
									R.id.transrowTV2,
									R.id.transrowTV3,
									R.id.transrowTV4
									
								};
		
		
		//Create simple cursor adapter and set it to display
		myAdapter = new SimpleCursorAdapter(this, R.layout.calander_row, null, from, to, 0);
		
		setListAdapter(myAdapter);
		
		//The args to initLoader:  1 is an id for the loader
		//Can provide a bundle, but here that is null
		//And the context
		getLoaderManager().initLoader(0, null, this);
		
		//Set up for the creation of a context menu for the transcript list
		registerForContextMenu(getListView());
		        
		Toast.makeText(this, "Finished onCreate", Toast.LENGTH_SHORT).show();
	
		exitBtn = (Button) findViewById(R.id.backBTN);
		exitBtn.setOnClickListener
		(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					finish();
				}
			});//END setOnClickListener
	}//END onCreate

	//Handle short clicks on any of the list's items (Views)
		//The clicked record is delineated in DisplayRecordActivity
	@Override
	public void onListItemClick(ListView myList, View myView, int position, long id)
	{
		super.onListItemClick(myList, myView, position, id);
		Intent launchDisplayRec = new Intent(this, DisplayInvoiceActivity.class);
		
		//Start the activity, after first adding the key field id to the Intent
		startActivity(launchDisplayRec.putExtra("idKey", id));
		
	}//END onListItemClick
	//Create the context menu from the array in arrays.xml
	//The array contains entries for Edit, Delete, & Cancel
	@Override
	public void onCreateContextMenu (ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		//This object allows us to gain info from the View that was long-pressed on.
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		
		//Using myAdapter, the SimpleCursorAdapter, you can access the specific record.
		//Its location is specified by the info object
		long recordID = myAdapter.getItemId(info.position);
		
		//Create header for the context menu that lists the adapter item it was created for.
		String menuHeader = "Edit or Delete Record #" + recordID + "?";
		menu.setHeaderTitle(menuHeader);
		
		//Now get the array from arrays.xml and complete the context menu
		String[] menuItems = getResources().getStringArray(R.array.invoices_context_menu);
		for (int ct = 0; ct < menuItems.length; ++ct)
    		menu.add(Menu.NONE, ct, ct, menuItems[ct]);	
		
	}//END onCreateContextMenu
	
	//Respond to an item being long-pressed on in the context menu
	@Override
    public boolean onContextItemSelected(MenuItem myItem)
	{
		//info will contain the position in the adapter view
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) myItem.getMenuInfo();
    	
    	//As in onCreateContextMenu we need to get the record's key field id num.
    	//info.position gets us the position of the item in the adapter that
    	//the menu was created for, but that is not necessarily the key field value.
    	long recordID = myAdapter.getItemId(info.position);
    	
    	//menuItemIndex tells us which menu item was selected in the context menu
    	int menuItemIndex = myItem.getItemId();
		
    	switch (menuItemIndex)
    	{
	    	case 0:	//Edit item
	    			//Currently, no capability to edit a record exists!
	    		Toast.makeText(this, "Editing records is not possible yet.", Toast.LENGTH_SHORT).show();
	    		return true;
	    	
	    	case 1:	//Delete record.Call the method to display a dialog box
	    			//to confirm deletion.
	    			deleteRecord (recordID);
	    		return true;
	    		
	    	default:	return super.onContextItemSelected(myItem);
    	}//END switch
	}//END onContextItemSelected

	//Delete one record.  Note: argument passed is actual key field value in DB.
	//Here we set up a dialog box to confirm the deletion.
	private void deleteRecord (final long idNumInDB)
	{
		//Show an alert dialog to confirm deletion
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setMessage("Are you sure you want to delete record #" + idNumInDB + "?");
		//Possible enhancement:  pass in record's position & display more record data
		
		//Set up two buttons. Left one is BUTTON_POSITIVE
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Delete record",
			new DialogInterface.OnClickListener()
			{	
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					//Delete one record..
	    			//The technique involves getting a ContentResolver,
	    			//which passes the arguments to the ContentProvider.
	    			//Pass in the record's key field value
					getContentResolver().delete(
		    				ContentUris.withAppendedId(InvoiceContentProvider.TABLE_URI, idNumInDB), null, null);
					Toast.makeText(getApplicationContext(), "Invoice #" + idNumInDB + " deleted!", Toast.LENGTH_SHORT).show();
					//Possible add on is call to new activity to handle deletion
				}
			});
		//This is the right button in the dialog box
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
			new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					//Do nothing. Dialog will simply disappear.
				}
			});
		dialog.show();
	}//END deleteRecord
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1)
	{
		CursorLoader cursorLoader = new CursorLoader(this, 
				InvoiceContentProvider.TABLE_URI, 
				null, null, null, null);
		
		return cursorLoader;
	}//END onCreateLoader

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor myCursor)
	{
		myAdapter.swapCursor(myCursor);
		
	}//END onLoadFinished

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		//This is called when the last Cursor provided to onLoadFinished
		//above is about to be closed.  Ensure that the adapter is no
		//longer using the cursor by setting it to null.
		myAdapter.swapCursor(null);
		
	}//END onLoaderReset
	
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
}//END class DisplayRecordActivity
