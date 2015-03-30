/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package edu.cmu.pocketsphinx.demo;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.lehigh.headstart.R;

public class PocketSphinxFragment extends DialogFragment implements
        RecognitionListener{

    private static final String DIGITS_SEARCH = "little talk";

    private SpeechRecognizer recognizer;
    //    private String[] textArray;
    
    private View view;
    private EditText content_editText;
    
    private int recognizedNum;
	
    
    public PocketSphinxFragment(View v_editText) {
		content_editText = (EditText)v_editText;
	}
    
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.pocketsphinx_dialog, null);
		
    	builder.setView(view)
			.setTitle("Sphinx Voice Recognition");
		setCancelable(false);
        
		// set the text array for file writing
//		textArray = getResources().getStringArray(R.array.text_array);
		
		
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(getActivity());
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    ((TextView) view.findViewById(R.id.sphinx_status)).setText(R.string.fail_to_init);
                } else {
                	//switchSearch(DIGITS_SEARCH);
                    ((TextView) view.findViewById(R.id.sphinx_status)).setText(R.string.please_speak_sphinx);
                    switchSearch(DIGITS_SEARCH);
                }
            }
        }.execute();
        
        return builder.create();
    }
    
    
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();
        if (text.equals(DIGITS_SEARCH))
            switchSearch(DIGITS_SEARCH);
        else {
        	((TextView) view.findViewById(R.id.sphinx_status)).setText(text);
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        //((TextView) findViewById(R.id.result_text)).setText("");

        if (hypothesis != null) {}
    }

    @Override
    public void onBeginningOfSpeech() {}

    
    @Override
    public void onEndOfSpeech() {
    	recognizer.stop();
    	
    	String prunedText = pruneText(((TextView) view.findViewById(R.id.sphinx_status)).getText().toString());
    	
    	//sync result to content_editTextdialog Here
    	copyToContent(prunedText);
    	
    	PocketSphinxFragment.this.getDialog().cancel();
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
    
    
    public String pruneText(String text) {
//    	String[] recognizedText = ((String)((TextView) findViewById(R.id.result_text)).getText()).split("\\s+");
    	String[] recognizedText = text.split("\\s+");
    	ArrayList<String> list = new ArrayList<String>(Arrays.asList(recognizedText));
    	
    	Iterator<String> iter = list.iterator();
    	String firstWord = iter.next();
    	if(firstWord.equals("allow")) {
    		recognizedNum = 1;
    		boolean flag = false;
    		while(iter.hasNext()) {
				String word = iter.next();
				if(word.equals("choose")) {
					flag = true;
				}
				else if(word.equals("from")) {
					flag = false;
				}
				else if(flag == true) {
					iter.remove();
				}
	    	}
    	}

    	else if(firstWord.equals("parents")) {
    		recognizedNum = 2;
    		boolean flag = false;
    		while(iter.hasNext()) {
				String word = iter.next();
				if(word.equals("completed")) {
					flag = true;
				}
				else if(word.equals("previous")) {
					flag = false;
				}
				else if(flag == true) {
					iter.remove();
				}
	    	}    		
    	}
    	else if(firstWord.equals("begins")) {
    		recognizedNum = 3;
    		boolean flag = true;
    		while(iter.hasNext()) {
				String word = iter.next();
				if(word.equals("associative")) {
					flag = false;
				}
				else if(flag == true) {
					iter.remove();
				}
	    	}    		
    	}
    	else if(firstWord.equals("CDP")) {
    		if(iter.hasNext()) {
    			String word = iter.next();
    			if(word.equals("reviewed")||word.equals("invite")){
    				recognizedNum = 4;
    			}
    			else {
    				recognizedNum = 5;
    			}
    		}
    	}
    	else if(firstWord.equals("reviewed")||firstWord.equals("invite")) {
    		recognizedNum = 4;
    	}
    	else if(firstWord.equals("will")) {
    		recognizedNum = 5;
    	}
    	
    	else if(firstWord.equals("encourage")) {
    		if(iter.hasNext()) {
    			String word = iter.next();
    			if(word.equals("associative")) {
    				recognizedNum = 5;
    			}
    			else {
		    		recognizedNum = 6;
		    		boolean flag = false;
		    		while(iter.hasNext()) {
						String word1 = iter.next();
						if(word1.equals("exploration") || word1.equals("reading")) {
							flag = true;
						}
						else if(word1.equals("during")) {
							flag = false;
						}
						else if(flag == true) {
							iter.remove();
						}
			    	}
    			}
    		}
    	}
    	else if(firstWord.equals("start")||firstWord.equals("talk")) {
    		recognizedNum = 7;
    	}
    	else if(firstWord.equals("they")) {
    		recognizedNum = 8;
    	}
    	else if(firstWord.equals("replaces")) {
    		recognizedNum = 9;
    		boolean flag = true;
    		while(iter.hasNext()) {
				String word = iter.next();
				if(word.equals("where")) {
					flag = true;
				}
				else if(word.equals("some")||word.equals("they")) {
					flag = false;
				}
				else if(flag == true) {
					iter.remove();
				}
	    	}
    	}
    	else if(firstWord.equals("come")) {
    		recognizedNum = 10;
    		while(iter.hasNext()) {
				String word1 = iter.next();
				if(word1.equals("at")) {
					break;
				}
				if(word1.equals("with")) {
					recognizedNum = 12;
					break;
				}
	    	}
    		
    	}
    	else if(firstWord.equals("put")) {
    		recognizedNum = 11;
    	}
    	else {
    		boolean flag = true;
    		iter.remove();
    		while(iter.hasNext()) {
				String word1 = iter.next();
				if(word1.equals("put")) {
					recognizedNum = 11;
					flag = false;
				}
				else if(flag == true) {
					iter.remove();
				}
	    	}
    	}
    	
    	return joinStringArr(list.toArray(new String[list.size()]));
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
    
    
    private void switchSearch(final String searchName) {
        recognizer.startListening(searchName, 6);
    }

    
    private void setupRecognizer(File assetsDir) {
        File modelsDir = new File(assetsDir, "models");
        recognizer = defaultSetup()
        		.setInteger("-vad_postspeech", 100)
        		.setFloat("-vad_threshold", 3.2)
                .setAcousticModel(new File(modelsDir, "hmm/en-us-semi"))
                .setDictionary(new File(modelsDir, "dict/5463.dic"))
                .setRawLogDir(assetsDir)
                .setKeywordThreshold(1e-20f)
                .getRecognizer();
        recognizer.addListener(this);

//        File grammarFile = grammarSearch(modelsDir);
//        recognizer.addGrammarSearch(DIGITS_SEARCH, grammarFile);
        File digitsGrammar = new File(modelsDir, "lm/5463.lm");
        recognizer.addNgramSearch(DIGITS_SEARCH, digitsGrammar);
    }
    
    
    private File grammarSearch(File modelsDir) {
    	File grammarFile = null;
    		
        grammarFile = new File(modelsDir, "grammar/little_talk.gram");
 
    	return grammarFile;
    }
    
}


