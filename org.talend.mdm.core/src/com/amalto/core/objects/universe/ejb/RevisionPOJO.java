package com.amalto.core.objects.universe.ejb;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amalto.core.server.api.XmlServer;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class RevisionPOJO implements Serializable{

    private static final Logger LOGGER = Logger.getLogger(ItemPOJO.class);

    private List<RevisionItem> revisionItems = new ArrayList<RevisionItem>();

	private Map<String, UniversePOJO> universeXMLMap = new HashMap<String, UniversePOJO>();
	
	public RevisionPOJO() {
	}
	
	public List<RevisionItem> getRevisionItems() {
		return revisionItems;
	}
	
	public void setRevisionItems(List<RevisionItem> lst) {
		revisionItems = lst;
	}
	
	/**
	 * add meta data into pojo, the meta data is stored into the universeXMLMap
	 *  every adding action must follow up the corresponding info in the map
	 * @throws XtentisException
	 */
	public UniversePOJO addMetaDataIntoUniverse(UniversePOJO pojo) throws XtentisException {
		UniversePOJO one = universeXMLMap.get(pojo.getName());
		if (one == null) {
            return pojo;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss, SSS").format(new Date(System.currentTimeMillis())); //$NON-NLS-1$
        if (one.getBirthTimeStamp() == null) {
        	one.setBirthTimeStamp(timeStamp);
        	one.setModifyTimeStamp(""); //$NON-NLS-1$
		} else {
        	one.setModifyTimeStamp(timeStamp);
		}
    	ILocalUser user = LocalUser.getLocalUser();
        if (one.getCreator() == null) {
        	one.setCreator(user.getUsername());
		} else {
			pojo.setCreator(one.getCreator());
		}
		List<String> modifyList = one.getModifierList();
		if (!one.getModifyTimeStamp().equals("") && !modifyList.contains(user.getUsername())) { //$NON-NLS-1$
			modifyList.add(user.getUsername());
		}
		if (modifyList.isEmpty()) {
            modifyList.add(""); //$NON-NLS-1$
        } else {
			modifyList.remove(""); //$NON-NLS-1$
        }
		pojo.setBirthTimeStamp(one.getBirthTimeStamp());
		pojo.setModifyTimeStamp(one.getModifyTimeStamp());
		pojo.setCreator(one.getCreator());
		pojo.setModifierList(modifyList);
        return pojo;
	}

    /**
     * Loads all the revisions from Universe entry
     * 
     * 	the {@link RevisionPOJO}
     * @throws XtentisException
     */
    public void load(String universePk, UniversePOJO pojo, boolean del) throws XtentisException {
        String revisionEntry = "UNIVERSE-REVISION"; //$NON-NLS-1$
        if (universePk != null && universePk.equals(revisionEntry)) {
            return;
        }
        //get the universe and revision ID
    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
    	if (universe == null) {
    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
    		LOGGER.error(err);
    		throw new XtentisException(err);
    	}
    	String invokerObjName = "Universe"; //$NON-NLS-1$
    	String clusterName = "amaltoOBJECTSUniverse"; //$NON-NLS-1$
        XmlServer server = Util.getXmlServerCtrlLocal();
		if (pojo != null && universeXMLMap.get(universePk) == null && !del) {
            universeXMLMap.put(universePk, pojo);
        } else if (pojo == null && del) {
			universeXMLMap.remove(universePk);
		}
		String revisionID = universe.getXtentisObjectsRevisionIDs().get(invokerObjName);
        String[] contents = server.getAllDocumentsUniqueID(revisionID, clusterName);
        List<String> revisionsAvailable = new ArrayList<String>();
        List<String> revisionsForCurrent = new ArrayList<String>();
        try {
            for (String uniqueID: contents) {
                String xmlData = server.getDocumentAsString(revisionID, clusterName, uniqueID, null);
            	if (!uniqueID.equals(revisionEntry)) {
            		String[] pathSlices = new String[]{"items-revision-iDs", "xtentis-objects-revision-iDs"}; //$NON-NLS-1$ //$NON-NLS-2$
                    for (String pathSlice : pathSlices) {
                        Document doc = Util.parse(xmlData);
                        NodeList rvs = Util.getNodeList(doc.getDocumentElement(), ".//" + pathSlice + "/value[text()!= '']"); //$NON-NLS-1$ //$NON-NLS-2$
                        for (int idx = 0; idx < rvs.getLength(); idx++) {
                        	Node item = rvs.item(idx);
                        	if (!revisionsAvailable.contains(item.getTextContent())) {
                            	revisionsAvailable.add(item.getTextContent());	
                        	}
                        	if(universePk != null && uniqueID.equals(universePk)) {
                        		revisionsForCurrent.add(item.getTextContent());
                        	}
                        }
                    }
            	} else {
            		RevisionPOJO ro = unmarshal(xmlData);
            		revisionItems = ro.getRevisionItems();
            	}
            }
            for (String revisionAvail : revisionsAvailable) {
            	RevisionItem revisionItem = getRevisionItemFromRecordList(revisionAvail);
            	if (revisionItem == null && universePk != null) {
            		RevisionItem item = new RevisionItem();
            		item.setName(revisionAvail);
            		item.setCreator(universePk);
            		List<String> quoters = item.getQuoterList();
            		quoters.add(universePk);
            		revisionItems.add(item);
            	} else {
            		// set modifier for the revision item
            		List<String> quoters  = revisionItem.getQuoterList();
            		if (revisionsForCurrent.contains(revisionAvail)
							&& universePk != null
							&& !quoters.contains(universePk)) {
						quoters.add(universePk);
					} else if (!revisionsForCurrent.contains(revisionAvail) && quoters.contains(universePk)) {
            			// delete the revision from the quoters
            			quoters.remove(universePk);
            		}
            	}
            }
            removeUnlessRevisionFromRecordList(revisionsAvailable);
            // store the RevisionPOJO info into xdb
            server.start("Revision"); //$NON-NLS-1$
            try {
                server.putDocumentFromString(this.toString(), revisionEntry, "Revision", revisionID); //$NON-NLS-1$
                server.commit("Revision"); //$NON-NLS-1$
            } catch (XtentisException e) {
                server.rollback("Revision"); //$NON-NLS-1$
                throw new XtentisException("Could not write revision information.", e);
            }
            if (server.getDocumentAsString(revisionID, clusterName, revisionEntry) != null) {
				server.deleteDocument(revisionID, clusterName, revisionEntry);
			}
        }
        catch(Exception e) {
    	    String err = "Unable to create/update the Revision " +": "+e.getClass().getName()+": "+e.getLocalizedMessage();
			LOGGER.error(err);
			throw new XtentisException(err, e);
        }
    }
    
    public Collection<RevisionItem> getAllCreatedRevisions(UniversePOJOPK pk) {
		List<RevisionItem> result = new ArrayList<RevisionItem>();
		for (RevisionItem item : revisionItems) {
			if (item.getCreator().equals(pk.getUniqueId()))
				result.add(item);
		}
		return result;
	}
    
    public Collection<RevisionItem> getAllQuotedRevisions(UniversePOJOPK pk) {
		List<RevisionItem> result = new ArrayList<RevisionItem>();
		for (RevisionItem item : revisionItems) {
			if (item.getQuoterList().contains(pk.getUniqueId()))
				result.add(item);
		}
		return result;
	}
    
    public UniversePOJOPK getUniverseCreator(RevisionPOJOPK pk) {
		for (RevisionItem item : revisionItems) {
			if (item.getName().equals(pk.getUniqueId())) {
                return new UniversePOJOPK(item.getCreator());
            }
        }
		return null;
    }
    
    public Collection<UniversePOJOPK> getUniverseQuoter(RevisionPOJOPK pk) {
		List<UniversePOJOPK> result = new ArrayList<UniversePOJOPK>();
		for (RevisionItem item : revisionItems) {
			if (item.getName().equals(pk.getUniqueId())) {
				for (String universe : item.getQuoterList()) {
                    UniversePOJOPK universePOJOPK = new UniversePOJOPK(universe);
                    if (!result.contains(universePOJOPK)) {
                        result.add(universePOJOPK);
                    }
                }
			}

		}
		return result;
	}
    
    private RevisionItem getRevisionItemFromRecordList(String revisionItemName) {
    	for (RevisionItem item : revisionItems) {
			if (item.getName().equals(revisionItemName))
				return item;
		}
		return null;
    }
    
    private void removeUnlessRevisionFromRecordList(List<String> revisionsAvailable) {
    	List<RevisionItem> toDelList = new ArrayList<RevisionItem>();
    	for (RevisionItem item : revisionItems) {
			if (!revisionsAvailable.contains(item.getName())) {
				toDelList.add(item);
			}
		}
    	revisionItems.removeAll(toDelList);
    }

    /**
     * Marshal the POJO to a Castor XML string
     *
     * @return the marshalled value
     * @throws XtentisException
     */
    public String marshal() throws XtentisException {
        try {
            StringWriter sw = new StringWriter();
            Marshaller.marshal(this, sw);
            return sw.toString();
        } catch (Exception e) {
            LOGGER.error(e);
            throw new XtentisException(e.getMessage());
        }
    }

    /**
     * Unmarshal an Item POJO PK from a Castor XML string
     *
     * @return the ItemPOJOPK
     * @throws XtentisException
     */
    public static RevisionPOJO unmarshal(String marshalledRevision) throws XtentisException {
        try {
            return (RevisionPOJO) Unmarshaller.unmarshal(RevisionPOJO.class, new StringReader(marshalledRevision));
        } catch (Exception e) {
            LOGGER.error(e);
            throw new XtentisException(e.getMessage());
        }
    }

    public String toString() {
        try {
	        return marshal();
        } catch (XtentisException e) {
	        return null;
        }
	}
}
