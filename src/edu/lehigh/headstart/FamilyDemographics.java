/*==============================================================================
 Copyright (c) 2013-2014 Li Tian, Lehigh University
 All Rights Reserved.
 ==============================================================================*/

package edu.lehigh.headstart;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.primitives.Ints;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class FamilyDemographics extends Activity {

	public static final int SIGNATURE_ACTIVITY = 1;
	
	private boolean isTestMode = false;
	
	private TextView signatureView;
	
	private SeekBar progress_seekBar;
	private TextView value_seekBar;		//show the value of SeekBar
	private TextView text_seekBar;		//show the text of SeekBar
	
	private RangeMap<Integer, String> rangeMap_text;
	private RangeMap<Integer, Integer> rangeMap_value;
	private String[] text_progress;
	private final int[] value_progress = new int[] {0, 1, 2, 3, 4, 5};
	private final int[] percent_progress = new int[] {0, 10, 20, 30, 40, 50, 60};
	
	
    private File testFile;
    private String fileName;
    
    private String chosenName = FamilySelectionActivity.getChosenName();

    private DbxAccountManager mDbxAcctMgr;
    
	private DbxFileSystem dbxFs;
	
	private DbxPath mDbxPath;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.family_demographics_activity);
		
		mDbxAcctMgr = LogInActivity.getDbxAccountManager();
		if(mDbxAcctMgr.hasLinkedAccount()) {
			try {
				dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
			} catch (Unauthorized e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		mDbxPath = new DbxPath("Supervisor/HomeVisitor1/" + chosenName + "/form1.txt");
		
		//prepopulate the child's name
		((TextView) findViewById(views[0])).setText(chosenName);
	}
	
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, FormSelectionActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	
	
	public void editTextDialog(View v) {
		EditTextDialogFragment editContentDialog = new EditTextDialogFragment(v, isTestMode);
		editContentDialog.show(getFragmentManager(), "edit_content");
	}
	
	
	
	public void editDateTime(View v) {
		
		DateTimeDialogFragment dateTimeDialog;
		
		switch(v.getId()) {
		case R.id.start_date:
			dateTimeDialog = new DateTimeDialogFragment(v, DateTimeDialogFragment.DATE_TIME_PICKER);
			dateTimeDialog.show(getFragmentManager(), "dateTimePicker");
			break;
		case R.id.birthday:
			dateTimeDialog = new DateTimeDialogFragment(v, DateTimeDialogFragment.DATE_PICKER);
			dateTimeDialog.show(getFragmentManager(), "dateTimePicker");
			break;
		default:
		}
	}
	
	
	public void startSignatureCapture(View v) {
		signatureView = (TextView)v;
		Intent intent = new Intent(this, SignatureCaptureActivity.class); 
        startActivityForResult(intent,SIGNATURE_ACTIVITY);
	}
	
	
	//save form
	public void onDoneForm(View v) {
    	
		DbxFile mDbxFile = null;
		try {
			if(dbxFs.isFile(mDbxPath)) {
				mDbxFile = dbxFs.open(mDbxPath);
			}
			else {
				mDbxFile = dbxFs.create(mDbxPath);
			}
			
			FormData data = FormData.newDemographicsData();
			setFormData(data);
			data.writeToDbx(mDbxFile);
			
		} catch (InvalidPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			mDbxFile.close();
		}
        
        Toast.makeText(this, "Form saved successfully", Toast.LENGTH_SHORT).show();
        
        onBackPressed();
	}
	
	//pass data to FormData class
	public void setFormData(FormData data) {
		for (int i=0; i<views.length; i++) {
			View v = findViewById(views[i]);
			if (v instanceof RadioGroup) {
				String s = ((RadioButton) findViewById(((RadioGroup) v).getCheckedRadioButtonId())).getTag().toString() + " - " +
						((RadioButton) findViewById(((RadioGroup) v).getCheckedRadioButtonId())).getText().toString();
				data.addFormData(s);
			}
			else {
				data.addFormData(((TextView) v).getText().toString());
			}
		}
	}
	
	
	//popluate form from FormData class
    public void populateForm() {
    	
		DbxFile mDbxFile = null;
		try {
			if(dbxFs.isFile(mDbxPath)) {
				mDbxFile = dbxFs.open(mDbxPath);
			}
			else {
				mDbxFile = dbxFs.create(mDbxPath);
			}
			
			FormData data = FormData.newDemographicsData();
			data.readFromDbx(mDbxFile);
			getFormData(data);
			
		} catch (InvalidPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			mDbxFile.close();
		}
    }
    
    
    public void getFormData(FormData data) {
    	for(int i=0; i<views.length; i++) {
    		View v = findViewById(views[i]);
			if (v instanceof RadioGroup) {
				String rbTag = data.getFormData(i).split(" - ")[0];
				((RadioGroup) v).check(((RadioButton) ((RadioGroup) v).findViewWithTag(rbTag)).getId());
			}
			else {
				((TextView) findViewById(views[i])).setText(data.getFormData(i));
			}
    	}
    }
    
    
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
        	return true;
        }
        return false;
    }
    
    
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory. 

		File file = new File(Environment.getExternalStorageDirectory(), albumName);
        if (!file.mkdirs()) {
            Log.e("mkdir", "Directory not created");
            if (file.isDirectory()) {
            	Log.e("mkdir", "Directory existed");
            }
        }

        return file;
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
		switch (item.getItemId()) {
		case R.id.populate_form:
			populateForm();
			break;
		case R.id.test_mode:
			if(isTestMode == false) {
				item.setIcon(R.drawable.ic_action_test_enabled);
				Toast.makeText(this, R.string.test_mode_enabled, Toast.LENGTH_SHORT).show();
				isTestMode=true;
			}
			else {
				item.setIcon(R.drawable.ic_action_test_disabled);
				Toast.makeText(this, R.string.test_mode_disabled, Toast.LENGTH_SHORT).show();
				isTestMode=false;
			}
			break;
		case R.id.action_settings:
			
			break;
		default:
			
		}

		return true;
	}

	
	//signature captured result
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
        case SIGNATURE_ACTIVITY: 
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String status  = bundle.getString("status");
                if(status.equalsIgnoreCase("done")){
                	Toast.makeText(this, "Signature captured successful!", Toast.LENGTH_SHORT).show();
                	signatureView.setText("Captured successful");
                }
            }
            break;
            
        default:
        }
    }
	
	
	//all the views
	private int[] views = {
		R.id.childs_name, R.id.birthday, R.id.start_date, R.id.address, R.id.temporary_living_condition, 
		R.id.transportation, R.id.primary_contact_person, R.id.primary_phone, R.id.primary_contact_person_relationship, 
		R.id.primary_contact_person_relationship_textview, R.id.primary_contact_person_preferred_method_of_contact, 
		R.id.primary_contact_person_preferred_method_of_contact_textview, 
		R.id.primary_contact_person_best_availability, R.id.primary_contact_person_best_availability_textview,
		R.id.primary_contact_person_access_to_a_computer, R.id.secondary_contact_person, R.id.secondary_phone,
		R.id.secondary_contact_person_relationship, R.id.secondary_contact_person_relationship_textview, 
		R.id.secondary_contact_person_preferred_method_of_contact, 
		R.id.secondary_contact_person_preferred_method_of_contact_textview,
		R.id.secondary_contact_person_best_availability, R.id.secondary_contact_person_best_availability_textview,
		R.id.secondary_contact_person_access_to_a_computer, R.id.primary_language_spoken_at_home,
		R.id.primary_language_spoken_at_home_textview, R.id.is_either_parent_an_active_member, R.id.circle_resources, 
		R.id.do_you_receive_child_care_subsidy, R.id.is_this_child_in_foster_or_kin_care, R.id.open_cy_case,
		R.id.are_there_any_written_custody_arrangements, 
		R.id.pfa, R.id.against_whom_textview, R.id.pc_rep, R.id.are_there_pets_in_the_home, R.id.if_yes_textview, 
		R.id.num_of_adults_living_in_household, 
		R.id.num_of_children_living_in_household, R.id.num_of_years_parent_in_hs_ehs_program, R.id.sibling_in_HS_EHS,
		R.id.sibling_in_HS_EHS_textview, R.id.does_child_receive_any_special_services, 
		R.id.does_child_receive_any_special_services_textview
	};
	
}


