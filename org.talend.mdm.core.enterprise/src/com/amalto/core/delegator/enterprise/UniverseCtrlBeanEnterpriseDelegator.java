package com.amalto.core.delegator.enterprise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.amalto.core.delegator.UniverseCtrlBeanDefaultDelegator;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.universe.ejb.RevisionItem;
import com.amalto.core.objects.universe.ejb.RevisionPOJO;
import com.amalto.core.objects.universe.ejb.RevisionPOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.WSGetUniverseByRevisionType;
import com.amalto.core.webservice.WSUniversePK;
import com.amalto.core.webservice.WSUniversePKArray;

public class UniverseCtrlBeanEnterpriseDelegator extends
		UniverseCtrlBeanDefaultDelegator {
	private  RevisionPOJO revisePojo = new RevisionPOJO();
	
	public UniversePOJO existsUniverse(UniversePOJOPK pk)
	throws XtentisException {
		 try {
	        	return ObjectPOJO.load(UniversePOJO.class,pk);        	
		    } catch (XtentisException e) {
		    	return null;
		    } catch (Exception e) {
	    	    String info = "Could not check whether this Universe exists:  "+pk.getUniqueId()
	    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
	    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug(info, e);
	    	   return null;
		    }
	}
	
	public Collection<RevisionItem> getAllCreatedRevisions(UniversePOJOPK pk)
		throws XtentisException {
		return revisePojo.getAllCreatedRevisions(pk);
	}
	
	public Collection<RevisionItem> getAllQuotedRevisions(UniversePOJOPK pk)
		throws XtentisException {
		return revisePojo.getAllQuotedRevisions(pk);
	}
	
	public UniversePOJO getUniverse(UniversePOJOPK pk) throws XtentisException {
		try {
        	UniversePOJO sp =  ObjectPOJO.load(UniversePOJO.class,pk);
        	if (sp == null) {
        		String err= "The Universe "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Universe "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
	}
	
	public WSUniversePKArray getUniverseByRevision(String name,
		String revision, String type) throws XtentisException {
		Collection c =
			getUniversePKs(
				""
			);
		
		if (c==null) return null;
		
		List<WSUniversePK> pks=new ArrayList<WSUniversePK>(); 
		for (Iterator iter = c.iterator(); iter.hasNext(); ) {		
			UniversePOJO pojo=getUniverse((UniversePOJOPK) iter.next());				
			if(WSGetUniverseByRevisionType._ITEM.equals(type)){
				for(Map.Entry<String, String> entry:pojo.getItemsRevisionIDs().entrySet()){
					if(Pattern.matches(entry.getKey(), name) && entry.getValue().equalsIgnoreCase(revision)){
						pks.add(new WSUniversePK(pojo.getName()));
					}
				}
			}
			if(WSGetUniverseByRevisionType._OBJECT.equals(type)){
				for(Map.Entry<String, String> entry:pojo.getXtentisObjectsRevisionIDs().entrySet()){
					if(Pattern.matches(entry.getKey(), name) && entry.getValue().equalsIgnoreCase(revision)){
						pks.add(new WSUniversePK(pojo.getName()));
					}
				}
			}				
		}
		return  new WSUniversePKArray(pks.toArray(new WSUniversePK[pks.size()]));
	}
	
	public UniversePOJOPK getUniverseCreator(RevisionPOJOPK pk)
		throws XtentisException {
		return revisePojo.getUniverseCreator(pk);
	}
	
	public Collection<UniversePOJOPK> getUniversePKs(String regex)
		throws XtentisException {
		Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(UniversePOJO.class,regex);
    	ArrayList<UniversePOJOPK> l = new ArrayList<UniversePOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
    		UniversePOJOPK pojoPk = new UniversePOJOPK(iter.next());
    		revisePojo.load(pojoPk.getUniqueId(), null, false);
			l.add(pojoPk);
		}
    	return l;
	}
	
	public Collection<UniversePOJOPK> getUniverseQuoter(RevisionPOJOPK pk)
		throws XtentisException {
		return revisePojo.getUniverseQuoter(pk);
	}
	
	public UniversePOJOPK putUniverse(UniversePOJO universe)
		throws XtentisException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("putUniverse() "+universe.getName());
    	
        try {
            
        	if ("[HEAD]".equals(universe.getName())) {
        		String err = "[HEAD] is a reserved name for Universes";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
            
            if (universe.store() == null)
            	throw new XtentisException("Check the XML Server logs");
            
            revisePojo.load(universe.getPK().getUniqueId(), universe, false);
            universe = revisePojo.addMetaDataIntoUniverse(universe);
            
            ObjectPOJOPK pk = universe.store();
            if (pk == null) throw new XtentisException("Check the XML Server logs");
            return new UniversePOJOPK(pk);
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Universe "+universe.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
	}
	
	public UniversePOJOPK removeUniverse(UniversePOJOPK pk)
		throws XtentisException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("Removing "+pk.getUniqueId());

        try {
        	ObjectPOJOPK universePk = ObjectPOJO.remove(UniversePOJO.class,pk);
            revisePojo.load(pk.getUniqueId(), null, true);
        	return new UniversePOJOPK(universePk);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Universe "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
	}

}
