package calanderdb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressWarnings("unused")
public class DBHelper extends SQLiteOpenHelper
{
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	String pathToDB = Invoice.DB_PATH + "/" + Invoice.DB_NAME;
	
	public DBHelper(Context context)
	{
		super(context, Invoice.DB_NAME, null, Invoice.DB_VERSION);
		this.myContext = context;
	}//END constructor

	//Following methods are used to create a brand new DB file & then
	//source it from a DB file contained in the assets folder of the project
	//Create an empty DB and rewrite it with the file in assets directory
	public SQLiteDatabase createDatabase() throws IOException
	{
		boolean dbExists = false;
		dbExists = checkIfDataBaseExists();
		Log.d("In createDatabase", "Value of dbExists is " + dbExists);
		
		myDataBase = getReadableDatabase();
		if (!dbExists)
		{
			try
			{
				Log.d("In createDatabase/try", "Calling to copy DB from assets");
				copyDataBaseFromAssets();
			}
			catch(IOException ex)
			{
				Log.d("DataBaseHelper", "Could not create new DB");
				throw new Error("Error copying DB!");
 			}
		}//END DB does not exist

		return myDataBase;
	}//END createDatabase

	//Check to see if the DB already exists: returns true or false
	private boolean checkIfDataBaseExists()
	{
		Log.d("In checkDBExists", "Checking DB");
		//Check to see if there is a DB file in the correct path
		File dbFile = myContext.getDatabasePath(Invoice.DB_NAME);
		return dbFile.exists();
		 
	}//END checkDataBaseExists
			
	//Copies DB file from assets folder to the databases/DB_NAME path
	private void copyDataBaseFromAssets() throws IOException
	{
		//First check to make sure the databases directory exists
		//If the DB has not yet been created, this directory will not be there!
		File myDirectory = new File(Invoice.DB_PATH);
		if (!myDirectory.exists())
		{
			try {
				myDirectory.mkdir();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Log.d("copyDataBaseFromAssets method", "Start db copying");
		InputStream myInput = myContext.getAssets().open(Invoice.DB_NAME);
		String outFileName = Invoice.DB_PATH + "/" + Invoice.DB_NAME;
		//Open the empty DB as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		//Transfer bytes from the input to output file
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0)
		{
			myOutput.write(buffer, 0, length);
		}
		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
		
		Log.d("copyDataBaseFromAssets method", "Done with db copying");
	}//END copyDataBaseFromAssets	

	//onCreate is necessary for an SQLiteOpenHelper, but here is blank
	//The DB is created by a call from the Content Provider to a method above
	@Override
	public void onCreate(SQLiteDatabase db)
	{
	}//Empty

	//onUpgrade method is required by the super class, however it
	//is overridden by the ContentProvider, so it can't be executed.
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		//From Dummies Listing 12-2
		throw new UnsupportedOperationException();
		
	}//END onUpgrade

	 // Getting single contact
    TimeSheet getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(Invoice.TABLE_NAME, null, Invoice.KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        TimeSheet timeSheet = new TimeSheet();
        // return contact
        return timeSheet;
    }
}
