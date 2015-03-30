/*==============================================================================
 Copyright (c) 2013-2014 Li Tian, Lehigh University
 All Rights Reserved.
 ==============================================================================*/

package edu.lehigh.headstart;

import java.io.IOException;
import java.util.ArrayList;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxPath.InvalidPathException;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

public class FamilySelectionActivity extends Activity {
	
    private static String chosenName;
    
    private DbxAccountManager mDbxAcctMgr;
    
	private DbxFileSystem dbxFs;
	
	private DbxPath mDbxPath = new DbxPath("Supervisor/HomeVisitor1/familylist.txt");
	
	private DbxFile familyNameFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.family_selection_activity);
		
		/*
         * Set the number of sentence
         */
        Spinner spinner = (Spinner) findViewById(R.id.family_spinner);
        
		mDbxAcctMgr = LogInActivity.getDbxAccountManager();
		if(mDbxAcctMgr.hasLinkedAccount()) {
			try {
				dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
			} catch (Unauthorized e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        
		String nameList = null;
		try {
			if(dbxFs.isFile(mDbxPath)) {
				familyNameFile = dbxFs.open(mDbxPath);
				nameList = familyNameFile.readString();
				if(TextUtils.isEmpty(nameList)) {
					nameList = "None";
				}
			}
			else {
				familyNameFile = dbxFs.create(mDbxPath);
				nameList = "None";
			}
		} catch (InvalidPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			familyNameFile.close();
		}
        
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, nameList.split("\n"));
		
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        // An item was selected. You can retrieve the selected item using
		        // parent.getItemAtPosition(pos)
		    	chosenName = (String)parent.getItemAtPosition(pos);
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parent) {
		        // Another interface callback
		    }
		});
		
	}
	
	
	public void addFamily(View v) {
		String newName = ((EditText) findViewById(R.id.add_family_edittext)).getText().toString();
		
		if (TextUtils.isEmpty(newName)) {
    		Toast.makeText(this, "New name can't be blank", Toast.LENGTH_SHORT).show();
    		return;
    	}
		
		try {
			if(dbxFs.isFile(mDbxPath)) {
				familyNameFile = dbxFs.open(mDbxPath);
				familyNameFile.appendString(newName + "\n");
			}
			else {
				familyNameFile = dbxFs.create(mDbxPath);
			}
		} catch (InvalidPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			familyNameFile.close();
		}
		
		//restart this activity
		this.recreate();
	}
	
	
	public void onEnterForms(View v) {

		if (chosenName == "None") {
    		Toast.makeText(this, "you need to create a family/child name first", Toast.LENGTH_SHORT).show();
    		return;
    	}
		
		Intent intent = new Intent(this, FormSelectionActivity.class);
//		intent.putExtra("chosenName", chosenName);
		startActivity(intent);
	}
	
	
	public static String getChosenName() {
		return chosenName;
	}
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_visit_summary_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.action_settings) {

		}
		return true;
	}

}
