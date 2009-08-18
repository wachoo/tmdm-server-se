package com.amalto.webapp.core.bean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;

//Cator Marshallable Class to use with the new b2box Configuration Mechanism

public class XMLConfiguration_Description {

	private HashMap<String,String> descriptions = new HashMap<String, String>();

	public HashMap<String, String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(HashMap<String, String> descriptions) {
		this.descriptions = descriptions;
	}
	
	/**
	 * Returns an {@link XMLConfiguration_Description} containing Upper Case ISO Codes and descriptions
	 * Expects a Description node 
	 *                    <Description>
     *                         <EN>Purchase Order Number</EN>
     *                          <FR>Numéro de la Commande</FR>
     *                    </Description>
	 * @param the Description {@link Node}
	 */
	public static XMLConfiguration_Description parseXMLConfiguration_Description(Element description) throws XtentisWebappException{
		XMLConfiguration_Description xmlDescription = new XMLConfiguration_Description();
		try {
			NodeList nl = Util.getNodeList(description, "./*");
			if ((nl==null) || (nl.getLength()==0)) return xmlDescription;
			for (int i = 0; i < nl.getLength(); i++) {
				Element desc = (Element) nl.item(i);
				xmlDescription.getDescriptions().put(desc.getLocalName().toUpperCase(), Util.getFirstTextNode(desc, "."));
			}
			return xmlDescription;
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				throw new XtentisWebappException("Unable to read the description from element "+Util.nodeToString(description));
			} catch (Exception ex) {
				throw new XtentisWebappException("Unable to read the description from element "+description.getLocalName());
			}
		}
	}

	/**
	 * Returns an XML description of the description
	 */
	public String toString() {
		String xml ="<Description>";
		Set<String> isoCodes = descriptions.keySet();
		for (Iterator iter = isoCodes.iterator(); iter.hasNext(); ) {
			String isoCode = (String) iter.next();
			xml+="<"+isoCode+">"+descriptions.get(isoCode)+"</"+isoCode+">";
		}
		xml+="</Description>";
		return xml;
	}
	
	
	
}
