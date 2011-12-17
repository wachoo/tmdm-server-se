package com.amalto.core.objects.datacluster.ejb;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.util.JazzyConfiguration;
import com.amalto.core.util.SpellCheckHandler;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;



/**
 * @author Bruno Grieder
 * 
 */
public class DataClusterPOJO extends ObjectPOJO{
   
	
    private String name;
    private String description;
    private String vocabulary;
    
    
    /**
     * 
     */
    public DataClusterPOJO() {
    }    
	public DataClusterPOJO(String revisionID, String name) {
		super();
		this.name = name;
	}
	public DataClusterPOJO(String name, String description, String vocabulary) {
		super();
		this.name = name;
		this.description = description;
		this.vocabulary = vocabulary;
	}

	/**
	 * @return Returns the Name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * @return Returns the Description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 
	 * @return the xsd Vocabulary
	 */
	public String getVocabulary() {
		return vocabulary;
	}
	
	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}
	
	
	@Override
	public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
	}
	

	private static Pattern xmlPattern = Pattern.compile("<.*?>", Pattern.DOTALL); 
	private static Pattern specialCharsPattern = Pattern.compile("['|\\.|,|\\(|\\)|\\r|\\n]", Pattern.DOTALL);
	private static Pattern words2Pattern = Pattern.compile("\\p{Space}+([^\\p{Punct}\\p{Space}]{2,}?)\\p{Space}+");
	
	
	   /**
     * Add this string words to the vocabulary - ignore xml tags
     * @return the number of words added
     * 
     * @ejb.interface-method   view-type="local"
     */
    public int addToVocabulary(String string) throws XtentisException{
       	try {
       		int number = 0;
       		String newVocabulary = "";
       		String currentVocabulary = getVocabulary();
       		currentVocabulary = (currentVocabulary == null ? "" : currentVocabulary);
       		
       		//remove all xml tags
       		string = xmlPattern.matcher(string).replaceAll("");
//       	remove special chars
       		string = specialCharsPattern.matcher(string).replaceAll(" ");

       		//extract words of at least 2 characters  		
    		Matcher m = words2Pattern.matcher(string);
    		int pos = 0;
    		while (m.find(pos)) {
    			pos = m.end()-1;
    			String word = m.group(1).trim(); //.toLowerCase();
//    			do not index numeric values
    			if (word.matches("[-|+]?[1-9][0-9]*(\\.[0-9]+)?")) continue; 
    			word = word.toLowerCase();  //if (!word.matches("\\p{Lu}{1,2}"))  
    			if (!"".equals(word)) {
	    			//Check if word is part of vocabulary and if not add it	
    				if (	(newVocabulary.indexOf(word+"\n")==-1) &&
    						(currentVocabulary.indexOf(word+"\n")==-1)) {
						newVocabulary+=word+"\n";
						number++;
					}
    			}
    		}//while
    		setVocabulary(currentVocabulary+newVocabulary);
    		
    		//reinit spell checker to use the new vocabulary/dictionary
    		if (spellHandler!=null) {
    	        spellChecker = new SpellChecker(getDictionary(),spellTreshold);    	        
    	        spellChecker.addSpellCheckListener(spellHandler);
    		}
    		
    		org.apache.log4j.Logger.getLogger(this.getClass()).debug(
    				"addToVocabulary() Added "+number+" words to vocabulary of cluster "+getName()
    		);
    		return number;
	    } catch (Exception e) {
		    String err = "Unable to add the words of the following string to the vocabulary:  "+string
		    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
		    throw new XtentisException(err);
	    }    	
    }
    
    /**
     * Loads the dictionary
     * Called by spellcheck; keep the dictionnary on a transient variable
     * @throws XtentisException
     */
    private SpellDictionary getDictionary() throws XtentisException{
    	try {
		    return 
		    	new SpellDictionaryHashMap(
		        		new StringReader(getVocabulary()), 
						SpellCheckHandler.getPhonetsReader("enfr")
				);
	    } catch (Exception e) {
		    String err = "Unable to get the dictionary for cluster "+getName()
		    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
		    throw new XtentisException(err);
	    }
    }

    
    
    /**
     * Caching of the spellchecker
     */
    private transient SpellChecker spellChecker = null;
    private transient SpellCheckHandler spellHandler = null;
    private transient int spellTreshold = 150;
	Pattern wordsPattern = Pattern.compile("\\p{Space}*([^\\p{Space}]{1,}?)\\p{Space}+");
    
    private SpellCheckHandler getSpellHandler(int treshold) throws XtentisException{
    	if (
    			(spellHandler != null) 
    			&& (treshold == spellTreshold)
    		)
    			return spellHandler;
    	
    	System.setProperty("jazzy.config", "com.amalto.core.util.JazzyConfiguration" );
      	JazzyConfiguration jc = new JazzyConfiguration();
    	if (treshold>jc.getMaxTreshold()) 
    		spellTreshold = jc.getAverageTreshold();
    	else if (treshold<jc.getMinTreshold()) 
    		spellTreshold = jc.getMinTreshold();
    	else
    		spellTreshold = treshold;
    	
        spellChecker = new SpellChecker(getDictionary(),spellTreshold);
        
        spellHandler = new SpellCheckHandler();
        spellChecker.addSpellCheckListener(spellHandler);
        
        return spellHandler;
        
    }
    /**
     * Returns a collection of spell checked possible sentences 
     * or an empty collection if no suggestions are found
     * @return A list of spell-checked sentences or an empty collection if nothing is found
     * @throws XtentisException
     */
    public Collection<String> spellCheck(
			String sentence, 
			int treshold,
			boolean ignoreNonExistentWords) 
    	throws XtentisException{
    	
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("spellCheck() "+sentence);
    	
    	System.setProperty("jazzy.config", "com.amalto.core.util.JazzyConfiguration" );
    	    	
        try {
        	
        	if ((getVocabulary()==null) || "".equals(getVocabulary()))
        			return new ArrayList<String>();
        	               	
	        SpellCheckHandler handler = getSpellHandler(treshold);
	        
	    	int errors = spellChecker.checkSpelling(new StringWordTokenizer(sentence)); //.toLowerCase()));
	        if (errors ==0) {
	        	return new ArrayList<String>();
	        }
	       
	        
	        
//	        //Debugging
//	        HashMap map = handler.getSuggestions();
//	        Set keys = map.keySet();
//	        for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
//				String key = (String) iter.next();
//				System.out.println("KEY "+key);
//				Collection results = (Collection)handler.getSuggestions().get(key);
//				for (Iterator iterator = results.iterator(); iterator.hasNext(); ) {
//					Word sugg = (Word) iterator.next();
//					System.out.println("     SUG: "+sugg.toString());
//				}
//			}
//	        //debugging
	        
	        ArrayList<ArrayList<Word>> suggestions = new ArrayList<ArrayList<Word>>();
			Matcher m = wordsPattern.matcher(" "+sentence+" ");
			int pos = 0;
			while (m.find(pos)) {
				pos = m.end()-1;
				String word = m.group(1).trim(); //.toLowerCase();
				// we ignore the spell check suggestions for words of 3 characters or less 
				if (word.length()<2) {
					ArrayList<Word> results = new ArrayList<Word>();
					results.add(new Word(word,0));
					suggestions.add(new ArrayList<Word>(results));	
				} else {
					//word of more than 1 character
					//check if we ignore non existent words
					if (ignoreNonExistentWords) {
						if (handler.getSuggestions().containsKey(word)) {
							//the spell checker did not ignore the word
							Collection<Word> results = (Collection<Word>)handler.getSuggestions().get(word);
							if (results.size()>0) {
								suggestions.add(new ArrayList<Word>(results));
							} // else we ignore the word because no suggestion
						} else {
							//the word exists (or has been  ignored on purpose by the spell checker)
							//we suggest the original word
							ArrayList<Word> results = new ArrayList<Word>();
							results.add(new Word(word,0));
							suggestions.add(new ArrayList<Word>(results));
						}
					} else {		
						Collection<Word> results = (Collection<Word>)handler.getSuggestions().get(word);
						if ((results==null) || (results.size()==0)) { 
							results = new ArrayList<Word>();
							results.add(new Word(word,0));
						}
						suggestions.add(new ArrayList<Word>(results));
					}
				}
			}//while words
			
			ArrayList<String> proposals = new ArrayList<String>();
			for (Iterator<ArrayList<Word>> iter = suggestions.iterator(); iter.hasNext();) {
				ArrayList<Word> sug = iter.next();
				ArrayList<String> newProposals = new ArrayList<String>();
				for (Iterator<Word> iterator = sug.iterator(); iterator.hasNext(); ) {
					Word suggestion = iterator.next();
					if (proposals.size()==0) { // first run
						newProposals.add(suggestion.getWord());
					} else {
						for (Iterator<String> iterator2 = proposals.iterator(); iterator2.hasNext(); ) {
							String proposal = iterator2.next();
							newProposals.add(proposal+" "+suggestion.getWord());
						}
					}
				} //for suggestions
				proposals = newProposals;
			}//for words
			
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
				"spellCheck() RESULTS for '"+sentence+"': "+Util.joinStrings(
					proposals.toArray(new String[proposals.size()]), 
					" -- "
				)
			);
			
			return proposals;
	    } catch (XtentisException e) {
	    	throw (e);
	    } catch (Exception e) {
		    String err = "Unable to spell check in cluster "+getName()+" the sentence: "+sentence
		    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
		    throw new XtentisException(err);
	    }
	}
    

}
