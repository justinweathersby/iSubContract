package calanderdb;

	//FILE : InvoiceContentProvider.java
	//PROG : Justin Weathersby


	import java.io.IOException;

	import android.content.ContentProvider;
	import android.content.ContentResolver;
	import android.content.ContentUris;
	import android.content.ContentValues;
	import android.content.UriMatcher;
	import android.database.Cursor;
	import android.database.SQLException;
	import android.database.sqlite.SQLiteDatabase;
	import android.net.Uri;
	import android.util.Log;

public class InvoiceContentProvider extends ContentProvider
	{
		//DB constants are in the Constants class
		//The AUTHORITY should always be the package name plus the class name
		public static String AUTHORITY = "com.example.isubcontract.InvoiceContentProvider";
		
		//Following URI used to get all records. Ends up as:
		// content://edu.jones.trackstudentgrades.GradesContentProvider/transcripttable
		public static final Uri TABLE_URI = Uri.parse("content://" + AUTHORITY +
														"/" + Invoice.TABLE_NAME);
		//Generate a single record URI for that query. Ends up as:
		// content://edu.jones.trackstudentgrades.GradesContentProvider/transcripttable/row
		public static final Uri ONE_REC_URI	= Uri.parse("content://" + AUTHORITY +
														"/" + Invoice.TABLE_NAME + "/row");
		
		//MIME types used for searching for all records or looking up a single record
		public static final String MULTIPLE_RECORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
															+ "/vnd.com.example.android.invoicemgr.invoicetable";
		
		public static final String SINGLE_RECORD_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
															+ "/vnd.edu.jones.android.invoicemgr.invoicetable";
		
		//Database columns are listed in Constants:  
		//KEY_ID, SEMESTER_FIELD, CRSPREFIX_FIELD, COURSENUMBER_FIELD,
		//SECTIONNUMBER_FIELD, GRADE_FIELD, CREDITS_FIELD, DATE_FIELD
		
		//Database related constants also listed in Constants class
		//DB_VERSION, DB_NAME, TABLE_NAME
		
		//The DB is created in the DataBaseHelper object, not here, so this string not used.
		//This included simply for documenting the DB and in case the app is written
		//differently for other applications.
	   
		
		
		//The DB helper we'll use to create the DB.
		//Instantiated in the method initializeDB.
		private DBHelper myHelper;
		//SQLiteDatabase object instantiated in initializeDB
		private SQLiteDatabase myDB;
		
		//UriMatcher stuff
		private static final int INVOICE_LIST = 0;
		private static final int INVOICE_RECORD = 1;
		private static final UriMatcher myURIMatcher = buildUriMatcher();
		
		//Build the UriMatcher for search suggestion and shortcut refresh queries
		private static UriMatcher buildUriMatcher()
		{
			UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH); //NO_MATCH: -1
			matcher.addURI(AUTHORITY, Invoice.TABLE_NAME, INVOICE_LIST);
			matcher.addURI(AUTHORITY, Invoice.TABLE_NAME + "/row", INVOICE_RECORD);
			
			return matcher;
		}//END buildUriMatcher
		
		//This method is needed to query the supported types. Also, is useful
		//in the query method to determine the type of URI received.
		@Override
		public String getType(Uri uri)
		{
			switch (myURIMatcher.match(uri))
			{
				case INVOICE_LIST: return MULTIPLE_RECORDS_MIME_TYPE;
				case INVOICE_RECORD: return SINGLE_RECORD_MIME_TYPE;
				default:
					throw new IllegalArgumentException("Unknown Uri: " + uri);
			}
		}//END getType
		
		@Override
		public boolean onCreate()
		{
			//NOTE:  most texts and online references state that it is a good idea
			//to set up the DB tables with calls here in the provider's onCreate
			//method. But the developer docs hint otherwise.  The provider's
			//onCreate is called from the main activity's thread when it starts:
			//all providers are instantiated by calling their onCreate at that time.
			//That could mean a significant lag in the main activity getting going,
			//if the providers all do DB creation/maintenance, etc.
			//So, I have transferred the following code to a separate method, 
			//called by the CRUD methods if the DB does not yet exist.
			//That means that DB creation does not happen for the first time
			//until the DB is actually needed.
			
			return true;
		}//END onCreate
		
		//Query for a complete list of records or only one record.
		//projectionIn is the list of fields/columns to return...null means all
		//Method returns a Cursor pointing to the result set
		@Override
		public Cursor query(Uri uri, String[] projectionIn, String selection,
							String[] selectionArgs, String sortOrder)
		{	
			//First, check to see if the DB helper has been instantiated and DB created
			//If not, then create the DB now.
			if (myHelper == null)
			{
				initializeDB();
			}//END DB initialization
			
			//Use UriMatcher to see the query type and format the DB query.
			//Actual query executed by the SQLiteDatabase object, myDB.
			Cursor myCursor = null;
			switch (myURIMatcher.match(uri))
			{
				case INVOICE_LIST:
					myCursor = myDB.query(Invoice.TABLE_NAME, projectionIn, null, null, null, null, null);
					Log.d("In GCP", "Executing List_Transcript");
					break;
				case INVOICE_RECORD:
					myCursor = myDB.query(	Invoice.TABLE_NAME,
											projectionIn,
											Invoice.KEY_ID + "=?",
											selectionArgs,
											null, null, null, null);
					if (myCursor != null && myCursor.getCount() > 0)
					{
						myCursor.moveToFirst();
					}
					//Log.d("In GCP", "Executing ITEM_TRANSCRIPT");
					break;
				default:
					throw new IllegalArgumentException("Unknown Uri:  " + uri);
			}
			
			//This call is required in order to have any changes reported and cursor updated
			myCursor.setNotificationUri(getContext().getContentResolver(), uri);
			
			return myCursor;
		}//END query

		//Delete one record.  Table and row number are passed in the Uri
		//Actual deletion done in the SQLiteDatabase object, myDB.
		@Override
		public int delete(Uri uri, String selection, String[] selectionArgs)
		{
			//First, check to see if the DB helper has been instantiated and DB created
			//If not, then create the DB now.
			if (myHelper == null)
			{
				initializeDB();
			}//END DB initialization
			
			int count = myDB.delete(Invoice.TABLE_NAME, Invoice.KEY_ID + "=?", 
									new String[]
									{
										Long.toString(ContentUris.parseId(uri))
					
									});
			if (count > 0)
			{
				getContext().getContentResolver().notifyChange(uri, null);
			}
			return count;
		}//END delete

		//Called by EnterGradeActivity.java to insert a new record
		//The ContentValues object has all fields, including the key id,
		//which is passed as 0 in case we want to test for that.
		@Override
		public Uri insert(Uri uri, ContentValues values)
		{
			//First, check to see if the DB helper has been instantiated and DB created
			//If not, then create the DB now.
			if (myHelper == null)
			{
				initializeDB();
			}//END DB initialization
			
			long id = 0;
			//Remove the key id, since the SQLite object will create a new one
			values.remove(Invoice.KEY_ID);
			//Call the SQLiteDatabase to perform the insert
			//The method returns the new ID or -1 if an error occurred.
			//Strong note:  any direct interaction with a DB should always be coded
			//inside a try/catch block!!!
			try
			{
				id = myDB.insertOrThrow(Invoice.TABLE_NAME, null, values);
			}
			catch (SQLException ex)
			{
				Log.d("InvoiceContentProvider", "Unable to insert record. "+ ex.getStackTrace());
			}
			//Get a ContentResolver to notify the provider to update any Views
			//if the id is valid.
			if (id >= 0)
			{
				getContext().getContentResolver().notifyChange(uri, null);
			}
			//The complete Uri is returned with the new ID attached
			return ContentUris.withAppendedId(uri, id);
		}//END insert

		//Currently, the app does not support updates to records, so this
		//mandatory method is not used.
		@Override
		public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
		{
			//First, check to see if the DB helper has been instantiated and DB created
			//If not, then create the DB now.
			if (myHelper == null)
			{
				initializeDB();
			}//END DB initialization
			
			int count = myDB.update(Invoice.TABLE_NAME, values, 
									Invoice.KEY_ID + "=?",
									new String[]
									{
										Long.toString(ContentUris.parseId(uri))
					
									});
			if (count > 0)
			{
				getContext().getContentResolver().notifyChange(uri, null);
			}
			return count;
		}//END update
		
		//Method to set up the DB initially.  This is called to create the DB
		//by any of the CRUD methods, which ensures that it does happen until
		//the first use of the DB.  Therefore, no activity waits for this to happen.
		public void initializeDB()
		{
			//First, instantiate the DB helper, then create the DB.
			myHelper = new DBHelper(getContext());
			try
			{
				//Use the DataBaseHelper object to create the actual DB.
				//If the DB does not exist, it is created and sourced from
				//an assets file. If it does, it is just instantiated and returned.
				myDB = myHelper.createDatabase();
			}
			catch (IOException e)
			{
				Log.d("In ContentProvider", "Call to createDatabase failed");
				e.printStackTrace();
			}//END DB initialization
			
			
		}//END initializeDB

        
        
 }
		

