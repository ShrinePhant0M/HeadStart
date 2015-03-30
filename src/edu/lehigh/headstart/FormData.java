/*==============================================================================
 Copyright (c) 2013-2014 Li Tian, Lehigh University
 All Rights Reserved.
 ==============================================================================*/

package edu.lehigh.headstart;

import java.io.IOException;
import java.util.ArrayList;

import android.os.Handler;
import com.dropbox.sync.android.DbxFile;

public class FormData {
	
	private static FormData demographicsData;
	
	private static FormData assessmentData;
	
	public static FormData getDemographicsData() {
		if (demographicsData == null) {
			throw new NullPointerException("Can't return demographicsData which is null");
		}
		return demographicsData;
	}
	
	public static FormData newDemographicsData() {
		demographicsData = new FormData();
		return demographicsData;
	}
	
	
	
	private ArrayList<String> formArray = new ArrayList<String>();
	
	public void writeToDbx(DbxFile mDbxFile) {
		try {
			mDbxFile.writeString("");
			for(int i=0; i<formArray.size(); i++) {
				mDbxFile.appendString("\"" + formArray.get(i) + "\"\t");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void readFromDbx(DbxFile mDbxFile) {
		try {
			String fullContent = mDbxFile.readString();
			String[] data = fullContent.split("\"\t");
			for(int i=0; i<data.length; i++) {
				addFormData(data[i].substring(1, data[i].length()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Get Functions
	 */
	
	public String getFormData(int i) {
		return formArray.get(i);
	}
	
	
	/**
	 * add Functions
	 */

	public void addFormData(String var) {
		String s = var==null ?  "" : var;
		formArray.add(s);
	}
	
}
