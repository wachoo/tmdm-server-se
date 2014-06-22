package com.amalto.webapp.core.bean;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Util2;
import com.amalto.webapp.core.util.XtentisWebappException;

//Castor Marshallable Class to use with the new b2box Configuration Mechanism

public class XMLConfiguration_XPath {

	private String name;
	private XMLConfiguration_Description description = new XMLConfiguration_Description();
	private String xPath;

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getXPath() {
		return xPath;
	}
	public void setXPath(String path) {
		xPath = path;
	}
	public XMLConfiguration_Description getDescription() {
		return description;
	}
	public void setDescription(XMLConfiguration_Description description) {
		this.description = description;
	}
	/**
	 * Returns an {@link XMLConfiguration_XPath} 
	 * Expects an xPath node
	 * 
	 *					<XPathNode> 
	 *                     <Name>Buyer Order Number</Name>
     *                       <Description>
     *                          <EN>Purchase Order Number</EN>
     *                          <FR>Numéro de la Commande</FR>
     *                       </Description>
     *                       <XPath>Order/OrderHeader/OrderNumber/BuyerOrderNumber</XPath>
	 *  				<XPathNode>
	 *  
	 * @param the XPathNode {@link Node}
	 */
	public static XMLConfiguration_XPath parseXMLConfiguration_XPath(Element xPath) throws XtentisWebappException{
		XMLConfiguration_XPath xmlXPath = new XMLConfiguration_XPath();
		try {
			xmlXPath.setName(Util.getFirstTextNode(xPath, "Name"));
			xmlXPath.setXPath(Util.getFirstTextNode(xPath, "XPath"));
			Element desc = Util2.getFirstElement(xPath, "Description");
			if (desc !=null) {
				xmlXPath.setDescription(XMLConfiguration_Description.parseXMLConfiguration_Description(desc));
			} else {
				org.apache.log4j.Logger.getLogger(XMLConfiguration_XPath.class).info("parseXMLConfiguration_XPath() No description for XPath "+xmlXPath.getName());
			} 
			return xmlXPath;
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			try {
				throw new XtentisWebappException("Unable to read the XPath from element "+Util.nodeToString(xPath));
			} catch (Exception ex) {
				throw new XtentisWebappException("Unable to read the XPath from element "+xPath.getLocalName());
			}
		}
	}

	/**
	 * Return the XMLized version of the xPath withthe propoer nodeName as Root
	 * @param nodeName - the root node name
	 * @return the XML
	 */
	public String toString(String nodeName) {
		String xml = "<"+nodeName+">";
		xml+= "	<Name>"+name+"</Name>";
		xml+= description.toString();
		xml+= "	<XPath>"+xPath+"</XPath>";
		xml+="</"+nodeName+">";
		return xml;
	}
	
	
	
}
