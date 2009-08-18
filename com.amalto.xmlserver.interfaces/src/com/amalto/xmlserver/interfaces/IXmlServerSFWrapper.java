package com.amalto.xmlserver.interfaces;

/**
 * All applications must call the methods of this wrapper only
 * They never directly call the underlying API
 *
 *@deprecated Stateful wrappers have been replaced by stateless wrappers
 */
public interface IXmlServerSFWrapper {

    /**
     * initializes a query
     * @throws XmlServerException
     * 
     */    
    public long initializeQuery(String cluster, String query) throws XmlServerException ;

    /**
     * Skip a number of results
     * @throws XmlServerException
     * 
     */    
    public void skip(int count) throws XmlServerException;
    
    /**
     * Fetches the next item
     * @throws XmlServerException
     *  
     */    
    public String fetchNext() throws XmlServerException;
    
    /**
     * Close the query
     * @throws XmlServerException
     *  
     */    
    public long closeQuery() throws XmlServerException;
	
    /**
     * Get the number of results on a query
     * If query is null, returns the number of results on the current query
     * Returns -1 if unknown
     * @throws XmlServerException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public long getNumberOfResults(String cluster, String query) throws XmlServerException;
     
   
}