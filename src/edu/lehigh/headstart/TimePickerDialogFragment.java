/*==============================================================================
 Copyright (c) 2013-2014 Li Tian, Lehigh University
 All Rights Reserved.
 ==============================================================================*/

package edu.lehigh.headstart;

import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;


public class TimePickerDialogFragment extends DialogFragment
							implements TimePickerDialog.OnTimeSetListener {

	private TextView v_root;
	
	public TimePickerDialogFragment(View v) {
		v_root = (TextView)v;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, false);
	}
	
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// Do something with the time chosen by the user
		if(hourOfDay<12) {
			v_root.setText(hourOfDay + ":" + minute+ "AM");
		}
		else {
			v_root.setText(hourOfDay + ":" + minute+ "PM");
		}
	}
}