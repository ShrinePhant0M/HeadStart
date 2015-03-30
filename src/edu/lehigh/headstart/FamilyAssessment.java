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

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

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
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class FamilyAssessment extends Activity {

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
    
    
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.family_assessment_activity);
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
		dateTimeDialog = new DateTimeDialogFragment(v, DateTimeDialogFragment.DATE_PICKER);
		dateTimeDialog.show(getFragmentManager(), "dateTimePicker");
	}
	
	
	public void startSignatureCapture(View v) {
		signatureView = (TextView)v;
		Intent intent = new Intent(this, SignatureCaptureActivity.class); 
        startActivityForResult(intent,SIGNATURE_ACTIVITY);
	}
	
	
	
	public void onDoneForm(View v) {
		//edit file name to open here
    	fileName = "test";
    	
    	if (TextUtils.isEmpty(fileName)) {
    		Toast.makeText(this, "Username can't be blank", Toast.LENGTH_SHORT).show();
    		return;
    	}

    	testFile = new File(getAlbumStorageDir("LittleTalk"), fileName);
		
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(testFile));
        sendBroadcast(intent);
        
        Toast.makeText(this, "Form saved successfully", Toast.LENGTH_SHORT).show();
        
        onBackPressed();
	}
	
	
	
    public void populateForm() {
    	
		//edit file name to open here
    	fileName = "test";
    	
    	if (TextUtils.isEmpty(fileName)) {
    		Toast.makeText(this, "Username can't be blank", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	testFile = new File(getAlbumStorageDir("LittleTalk"), fileName);
    	
//    	if (testFile.isFile()) {
//        	BufferedReader br;
//    		try {
//    			br = new BufferedReader(new FileReader(testFile));
//    			read(br);
//    			br.close();
//    		} catch (FileNotFoundException e) {
//    			// TODO Auto-generated catch block
//    			e.printStackTrace();
//    		} catch (NumberFormatException e) {
//    			// TODO Auto-generated catch block
//    			e.printStackTrace();
//    		} catch (IOException e) {
//    			// TODO Auto-generated catch block
//    			e.printStackTrace();
//    		}
//    		Toast.makeText(this, "Form populated successfully", Toast.LENGTH_SHORT).show();
//    	}
//    	else {
//    		Toast.makeText(this, "No such file to pre-populate", Toast.LENGTH_SHORT).show();
//    	}
    	
    }
    
    
//    public void read(BufferedReader br) throws IOException {
//    	char[] buffer = new char[3000];
//    	br.read(buffer);
//    	String[] str_arr = (new String(buffer)).split(";\n\n");
//    	
//    	((TextView) findViewById(R.id.date_time)).setText(str_arr[0]);
//    	((TextView) findViewById(R.id.family_name)).setText(str_arr[1]);
//    	((TextView) findViewById(R.id.childs_name)).setText(str_arr[2]);
//    	((TextView) findViewById(R.id.individual_child_plan_goal)).setText(str_arr[3]);
//    	
//    }
    
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
	
}


