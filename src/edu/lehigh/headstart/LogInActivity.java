/*==============================================================================
 Copyright (c) 2013-2014 Li Tian, Lehigh University
 All Rights Reserved.
 ==============================================================================*/

package edu.lehigh.headstart;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFileSystem;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class LogInActivity extends Activity {
	
	private static DbxAccountManager mDbxAcctMgr;
	
	private DbxFileSystem dbxFs;
	
	static final int REQUEST_LINK_TO_DBX = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		//set the imeOption
		EditText password_editText = (EditText) findViewById(R.id.password);
		password_editText.setOnEditorActionListener(new OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		    	boolean handled = false;
		    	if (actionId == EditorInfo.IME_ACTION_GO) {
		            login(v);
		            handled = true;
		        }
		        return handled;
		    }
		});
		
		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), "cnsu65ezmsgck0e", "wqh8hn2d17m457u");
		
		if(mDbxAcctMgr.hasLinkedAccount()) {
			((Button) findViewById(R.id.login_button)).setEnabled(true);
			Toast.makeText(this, "Dropbox connected", Toast.LENGTH_SHORT).show();
			((Button) findViewById(R.id.link_to_dropbox_button)).setText(R.string.switch_dropbox_account);
		}
	}
	
	
	public void login(View V) {
		//verify account here
		
		Intent intent = new Intent(this, FamilySelectionActivity.class);
		startActivity(intent);
	}
	
	
	public static DbxAccountManager getDbxAccountManager() {
		return mDbxAcctMgr;
	}
	
	
	
	public void onClickLinkToDropbox(View view) {
		if(mDbxAcctMgr.hasLinkedAccount()) {
			new AlertDialog.Builder(this).setTitle("Really unlink the current account?")
	    	.setMessage(R.string.unlink_message)
	    	.setNegativeButton(R.string.no, null)
	    	.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	    		@Override
	    		public void onClick(DialogInterface dialog, int id) {
	    			linkToDropbox();
	    		}
	    	}).create().show();
		}
		else {
			linkToDropbox();
		}
	}
	
	
	public void linkToDropbox() {
		mDbxAcctMgr.unlink();
	    mDbxAcctMgr.startLink(LogInActivity.this, REQUEST_LINK_TO_DBX);
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_LINK_TO_DBX) {
	        if (resultCode == Activity.RESULT_OK) {
	            // ... Start using Dropbox files.
	        	((Button) findViewById(R.id.link_to_dropbox_button)).setText(R.string.switch_dropbox_account);
	        	((Button) findViewById(R.id.login_button)).setEnabled(true);
	        	Toast.makeText(this, "Dropbox connected", Toast.LENGTH_SHORT).show();
	        } else {
	            // ... Link failed or was cancelled by the user.
	        	((Button) findViewById(R.id.link_to_dropbox_button)).setText(R.string.link_to_dropbox);
	        	((Button) findViewById(R.id.login_button)).setEnabled(false);
	        	Toast.makeText(this, "Dropbox can't be connected", Toast.LENGTH_SHORT).show();
	        }
	    } 
	    else {
	        super.onActivityResult(requestCode, resultCode, data);
	    }
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
