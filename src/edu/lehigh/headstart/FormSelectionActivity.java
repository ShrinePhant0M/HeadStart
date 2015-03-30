/*==============================================================================
 Copyright (c) 2013-2014 Li Tian, Lehigh University
 All Rights Reserved.
 ==============================================================================*/

package edu.lehigh.headstart;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class FormSelectionActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_selection_activity);
	}


	public void startSubForms(View v) {
		Intent intent;
		switch(v.getId()) {
		case R.id.family_demographics:
			intent = new Intent(this, FamilyDemographics.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			break;
		case R.id.family_assessment:
			intent = new Intent(this, FamilyAssessment.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			break;
		default:
		}
	}
	
	
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Really Exit?")
        	.setMessage("Are you sure you want to exit? (Unsaved data will be discarded!)")
        	.setNegativeButton(R.string.no, null)
        	.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
        		@Override
        		public void onClick(DialogInterface dialog, int id) {
        			Intent intent = new Intent(FormSelectionActivity.this, FamilySelectionActivity.class);
        			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        			startActivity(intent);
        		}
        	}).create().show();
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
