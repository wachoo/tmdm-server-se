package com.amalto.core.ejb;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

import com.amalto.core.util.XtentisException;

public class AutoCommitToSvnMsg implements Serializable{
	private String versionSystemPk;
	private ItemPOJOPK itemPk;
	private String comment;
	public AutoCommitToSvnMsg(String versionpk,ItemPOJOPK itempk, String comment){
		this.versionSystemPk=versionpk;
		this.itemPk=itempk;
		this.comment=comment;
	}
	public AutoCommitToSvnMsg(){
		
	}
	public String getVersionSystemPk() {
		return versionSystemPk;
	}
	public void setVersionSystemPk(String versionSystemPk) {
		this.versionSystemPk = versionSystemPk;
	}
	
	public ItemPOJOPK getItemPk() {
		return itemPk;
	}
	public void setItemPk(ItemPOJOPK itemPk) {
		this.itemPk = itemPk;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * Marshal the POJO PK to a Castor XML string
	 * @return the marshalled PK
	 * @throws XtentisException
	 */
	 public String  marshal() throws XtentisException {	
	        try {
	    		StringWriter sw = new StringWriter();
	    		Marshaller.marshal(this, sw);
	    		return sw.toString();
		    } catch (Exception e) {
	    	    String err = "Unable to marshal the AutoCommitToSvnMsg:"+e.getLocalizedMessage();
	    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
	    	    throw new XtentisException(err);
		    } 
	 }

	/**
	 * Unmarshal an Item POJO PK from a Castor XML string
	 * @return the ItemPOJOPK
	 * @throws XtentisException
	 */
	 public static AutoCommitToSvnMsg  unmarshal(String marshalledItemPOJOPK) throws XtentisException {	
	        try {
	    		return (AutoCommitToSvnMsg) Unmarshaller.unmarshal(AutoCommitToSvnMsg.class, new StringReader(marshalledItemPOJOPK));
		    } catch (Exception e) {
	    	    String err = "Unable to unmarshal the AutoCommitToSvnMsg:"+e.getLocalizedMessage();	    	   
	    	    throw new XtentisException(err);
		    } 
	 }
	
}
