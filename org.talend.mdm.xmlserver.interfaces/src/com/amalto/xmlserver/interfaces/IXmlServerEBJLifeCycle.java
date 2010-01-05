package com.amalto.xmlserver.interfaces;


/**
 * This interface allows the Database wrapper to listen to the corresponding EJB Session Objects events
 * @author bgrieder
 *
 */
public interface IXmlServerEBJLifeCycle {

    public void doCreate() throws XmlServerException;
    public void doPostCreate() throws XmlServerException;
    public void doRemove() throws XmlServerException;
    public void doActivate() throws XmlServerException;
    public void doPassivate() throws XmlServerException;
	
}
