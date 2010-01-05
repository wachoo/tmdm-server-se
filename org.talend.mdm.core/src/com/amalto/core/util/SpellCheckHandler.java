/*
 * Created on 7 oct. 2005
 */
package com.amalto.core.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;

/**
 * @author bgrieder
 *
 */
public class SpellCheckHandler implements SpellCheckListener{

	private static HashMap<String,String> phonets = new HashMap<String,String>();
	
	private HashMap<String,List<Word>> suggest =new HashMap<String, List<Word>>();
	String errors = null;
	
	
	/**
	 * Factory method to return a phonets reader
	 * @param language
	 * @return
	 */
	public static StringReader getPhonetsReader(String language) throws Exception{
		String ps = null;
		if ((ps=phonets.get(language.toLowerCase()))==null) {
			try {
				ps = getPhonetsString("phonet-utf8."+language.toLowerCase());
			} catch (Exception e) {
				ps =getPhonetsString("phonet-utf8.enfr");
//				language = "en";
			}
			phonets.put(language.toLowerCase(), ps);
		}
		org.apache.log4j.Category.getInstance(SpellChecker.class).debug("Phonets file: "+"phonet-utf8."+language.toLowerCase());
		return new StringReader(ps);
	}
	
	private static String getPhonetsString(String filename) throws Exception{
	    BufferedReader in = null;
        in = new BufferedReader(
        			new InputStreamReader(
        			        SpellCheckHandler.class
        				        	.getResourceAsStream(filename),
							"utf-8"
					)
		);
	
	    String phonetsString="";
        String line;
        while ((line=in.readLine())!=null) phonetsString+=line+"\n";
        return phonetsString;
    }    
	
	public void spellingError(SpellCheckEvent event) {
    	suggest.put(event.getInvalidWord(), event.getSuggestions());
	}
	
	
	public HashMap getSuggestions()	{
	    return suggest;
	}
	
	
	
}
