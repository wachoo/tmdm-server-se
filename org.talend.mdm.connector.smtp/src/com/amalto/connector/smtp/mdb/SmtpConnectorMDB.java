package com.amalto.connector.smtp.mdb;

import javax.resource.ResourceException;
import javax.resource.cci.Record;

import com.amalto.connector.mdb.ConnectorMDB;


/**
 * 
 * @author jfeltesse
 */
public class SmtpConnectorMDB extends ConnectorMDB {
	
	private static final long serialVersionUID = -1403186159735122741L;

	public Record onMessage(Record inputData) throws ResourceException {
		try  {
			return super.onMessage(inputData);
		} catch (ResourceException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	
}

