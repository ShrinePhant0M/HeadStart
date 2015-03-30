/*==============================================================================
 Copyright (c) 2013-2014 Li Tian, Lehigh University
 All Rights Reserved.
 ==============================================================================*/

package edu.lehigh.headstart;

import java.util.ArrayList;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import edu.cmu.pocketsphinx.demo.PocketSphinxFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditTextDialogFragment extends DialogFragment {
	
	private static final int RC_VOICE_RECOGNITION = 0;
	
	private boolean isTestMode = false;
	
	private TextView v_root;
	private View view;
	private EditText content_editText;
	
	private Button googleVoice_Button;
	private Button sphinxVoice_Button;
	
	public EditTextDialogFragment(View v, boolean isTest) {
		v_root = (TextView) v;
		isTestMode = isTest;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.edit_text_dialog, null);
		setCancelable(false);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
			.setTitle(R.string.edit_the_text)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		         @Override
		         public void onClick(DialogInterface dialog, int id) {
		        	 v_root.setText(content_editText.getText());
		         }
		     }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		    	 @Override
		         public void onClick(DialogInterface dialog, int id) {
		             EditTextDialogFragment.this.getDialog().cancel();
		         }
		     }).setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
		    	 @Override
		         public void onClick(DialogInterface dialog, int id) {
		    		 
		    	 }
		     });
		
		
		//init content_editText
		content_editText = (EditText) view.findViewById(R.id.content_editText);
		content_editText.setText(v_root.getText());
		content_editText.setSelection(content_editText.length());
		
		//init google Voice button
		googleVoice_Button = (Button) view.findViewById(R.id.button_voiceRecord);
		googleVoice_Button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				invokeSpeechToText();
			}
		});
		
		//init sphinxVoice button
		sphinxVoice_Button = (Button) view.findViewById(R.id.button_sphinx);
		sphinxVoice_Button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				sphinxSpeechToText();
			}
		});
		
//		if(v_root.getId()==R.id.send_to_parent || v_root.getId()==R.id.send_to_staff) {
//			content_editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//			googleVoice_Button.setEnabled(false);
//			sphinxVoice_Button.setEnabled(false);
//		}
		
		return builder.create();
	}
	
	
	@Override
	public void onStart()
	{
	    super.onStart();
	    AlertDialog dialog = (AlertDialog) getDialog();
	    
        dialog.getButton(Dialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
            	content_editText.setText("");
            }
        });
	}
	
	
	
	/*
	 * The following are for google speech recognition
	 */
	private void invokeSpeechToText()
	{
		Intent stt = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		stt.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		stt.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.please_speak_google));
		stt.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 4000);	//reported bug
		startActivityForResult(stt, RC_VOICE_RECOGNITION);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RC_VOICE_RECOGNITION && resultCode == Activity.RESULT_OK) {
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			Log.i("RecResult", matches.toString());

			ProgressDialog pd = ProgressDialog.show(getActivity(), getString(R.string.analyzing_speech),
					getString(R.string.please_wait_while_the_speech_is_being_analyzed), true, false);

			//analysis starts
			
			String match;
			Log.w("MATCH", matches.get(0));
			match = matches.get(0);

			//analysis finishes
			
			pd.dismiss();
			
			if(isTestMode) {
				view.findViewById(R.id.test_view).setVisibility(View.VISIBLE);
				compareWithContent(matches);
			}
			else {
				//view.findViewById(R.id.test_view).setVisibility(View.GONE);
				copyToContent(match);
			}
		}
	
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	/*
	 * In test mode, test the best returned match from Speech Recognizer
	 */
	private void compareWithContent(ArrayList<String> matches) {
		
		if(TextUtils.isEmpty( content_editText.getText() )) {
			content_editText.setText("");			
	   	}
		
		//stemClass = Class.forName("org.tartarus.snowball.ext." + "englishStemmer");
		SnowballStemmer stemmer = (SnowballStemmer) new englishStemmer();		
		
		//Target Array
		String[] targetArr = content_editText.getText().toString().toLowerCase().split("\\s+");
		
		String bestMatch = "";
		int similarity_max = 0;
		for (String match : matches) {
			String[] matchArr = match.toLowerCase().split("\\s+");
			int similarity = similarityCompute(matchArr, targetArr, stemmer);
			
			if (similarity > similarity_max) {
				similarity_max = similarity;
				bestMatch = joinStringArr(matchArr);
			}
		}
		
		//display the result on screen
		TextView target_textView = (TextView) view.findViewById(R.id.test_target_string);
		TextView ratio_textView = (TextView) view.findViewById(R.id.test_ratio);
		
		target_textView.setText(bestMatch);
		ratio_textView.setText(similarity_max + "/" + targetArr.length);
		
	}
	
	private int similarityCompute(String[] matchArr, String[] targetArr, SnowballStemmer stemmer) {
		int similarity = 0;
		for (int i=0; i<matchArr.length; i++) {
			for (int j=0; j<targetArr.length; j++) {
				
				String matchStemmed = stem(matchArr[i], stemmer);
				String targetStemmed = stem(targetArr[j], stemmer);
				
				if (matchStemmed.equals(targetStemmed)) {
					matchArr[i] = targetArr[j];
					similarity ++;
					break;
				}
			}
		}
		return similarity;
	}
	
	private String joinStringArr(String[] strArr) {
		int k = strArr.length;
		if ( k == 0 )
		{
			return null;
		}
		StringBuilder out = new StringBuilder();
		out.append(strArr[0]);
		for ( int x=1; x < k; ++x )
		{
			out.append(" ").append(strArr[x]);
		}
		return out.toString();
	}
	
	private String stem(String current, SnowballStemmer stemmer) {
		stemmer.setCurrent(current);
		stemmer.stem();
		return stemmer.getCurrent();
	}
	
	/*
	 * In work mode, get the best result and copy it to content
	 */
	private void copyToContent(String match) {
		if(TextUtils.isEmpty( content_editText.getText() )) {
			content_editText.setText(match + ". ");			
	   	}
		else {
			content_editText.setText(content_editText.getText().toString() + match + ". ");
		}
		content_editText.setSelection(content_editText.length());
	}
	
	
	
	
	/*
	 * The following are for sphinx speech recogniton
	 */
	private void sphinxSpeechToText() {
		PocketSphinxFragment sphinx = new PocketSphinxFragment(content_editText);
		sphinx.show(getFragmentManager(), "sphinx");
	}
	
	
}



