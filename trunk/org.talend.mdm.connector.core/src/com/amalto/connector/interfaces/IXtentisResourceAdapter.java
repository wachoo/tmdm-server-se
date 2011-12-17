package com.amalto.connector.interfaces;

import javax.resource.cci.MappedRecord;

import com.amalto.connector.jca.XtentisConnectorException;


/**
 * 
 * @author bgrieder
 *
 */
public interface IXtentisResourceAdapter  {

	public String getVersion();
	
	/*********************************************************************************************
	 * Functions of the Resource Adapter called by the application server
	 *********************************************************************************************/
	
	/**
	 * Called by the Application server to start the EIS/Connector using the MappedRecord parameters as configuration
	 * @param recordIn - the parameters to use to start the connector
	 * @return a MappedRecord containing a status and message
	 * @throws XtentisConnectorException
	 */
	public MappedRecord startConnector(MappedRecord recordIn)  throws XtentisConnectorException ;
   
	/**
	 * Called by the Application server to start/restart the EIS/Connector based on the saved configuration
	 * Some additional parameters may be passed in the MappedRecord
	 * This method is typically called at the end of the Application Server start/restart
	 * @param recordIn - optional additional parameters used to start the connector
	 * @return a MappedRecord containing a status and message
	 * @throws XtentisConnectorException
	 */
	public MappedRecord startFromConfiguration(MappedRecord recordIn)  throws XtentisConnectorException ;
	
	/**
	 * Called by the Application server to stop the EIS/Connector
	 * @param recordIn
	 * @return a MappedRecord containing a status and message
	 * @throws XtentisConnectorException
	 */
	public MappedRecord stopConnector(MappedRecord recordIn)  throws XtentisConnectorException ;
   
	/**
	 * Called by the Application server to pull a message from the EIS/Connector
	 * @param recordIn
	 * @return a MappedRecord containing a status and the content pulled
	 * @throws XtentisConnectorException
	 */
	public MappedRecord pull(MappedRecord recordIn)  throws XtentisConnectorException ;
   
	/**
	 * Called by the Application server to push a message to the EIS/Connector
	 * @param recordIn
	 * @return a MappedRecord containing a status and message
	 * @throws XtentisConnectorException
	 */
	public MappedRecord push(MappedRecord recordIn)  throws XtentisConnectorException ;
   
	/**
	 * Called by the Application server to retrieve the EIS/Connector status
	 * @param recordIn
	 * @return a MappedRecord containing a status and message
	 * @throws XtentisConnectorException
	 */
	public MappedRecord getConnectorStatus(MappedRecord recordIn)  throws XtentisConnectorException ;
	
	
	/*********************************************************************************************
	 * Functions of the Resource Adapter called by another connector
	 *********************************************************************************************/

	/**
	 * Called by another connector trying to chain this one in a processing chain
	 * @param recordIn
	 * @return a Mapped record that can be processed by the calling connector
	 * @throws XtentisConnectorException
	 */
	public MappedRecord receiveFromAnotherConnector(MappedRecord recordIn)  throws XtentisConnectorException ;
	
	/*********************************************************************************************
	 * Functions called by the Resource adapter towards the application server
	 *********************************************************************************************/

	/**
	 * is XML server running?
 	 * @return the status: OK running - ERROR stopped
	 * @throws XtentisConnectorException
	 */
	public MappedRecord isXMLServerRunning()  throws XtentisConnectorException ;
	
	/**
	 * Get the EIS/Connector configuration saved by the application server
 	 * @return the configuration
	 * @throws XtentisConnectorException
	 */
	public MappedRecord getConfiguration()  throws XtentisConnectorException ;
	
	/**
	 * The EIS/Connector wants the Application Server to save its configuration
	 * @throws XtentisConnectorException
	 */
	public void saveConfiguration()  throws XtentisConnectorException ;
	
	/**
	 * The EIS/Connector wants to push a message to the application server
	 * Notes: 
	 * 	The EIS/Connector must implement XAResource
	 * 	Calls to this method may be made by multiple threads
	 * 	Implementation of the message is application specific
	 * 	Whether this method should return an actual object is also implemenatation specific
	 * @param callingConnector  the calling Connector/EIS
	 * @param message  the actual message
	 * @return a message or null, as expected
	 * @throws XtentisConnectorException
	 */
	public Object onMessage(Object callingConnector, Object message)  throws XtentisConnectorException ;
   

}

