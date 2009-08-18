package com.amalto.webapp.core.bean;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Util2;
import com.amalto.webapp.core.util.XtentisWebappException;

//Castor Marshallable Class to use with the new b2box Configuration Mechanism

public class XMLConfiguration_Document {

	private String name=null;
	private XMLConfiguration_Description description = new XMLConfiguration_Description();
	private String conceptName=null;
	private ArrayList<XMLConfiguration_XPath> keys = new ArrayList<XMLConfiguration_XPath>();
	private ArrayList<XMLConfiguration_Searchable> viewables = new ArrayList<XMLConfiguration_Searchable>();
	private ArrayList<XMLConfiguration_Searchable> searchables = new ArrayList<XMLConfiguration_Searchable>();
	

	public XMLConfiguration_Description getDescription() {
		return description;
	}
	public void setDescription(XMLConfiguration_Description description) {
		this.description = description;
	}
	public ArrayList<XMLConfiguration_XPath> getKeys() {
		return keys;
	}
	public void setKeys(ArrayList<XMLConfiguration_XPath> keys) {
		this.keys = keys;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<XMLConfiguration_Searchable> getViewables() {
		return viewables;
	}
	public void setViewables(ArrayList<XMLConfiguration_Searchable> viewables) {
		this.viewables = viewables;
	}
	public String getConceptName() {
		return conceptName;
	}
	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}
	/**
	 * Returns an {@link XMLConfiguration_Document} 
	 *        <Document>
                    <Name>Order</Name>
                    <Description>
                        <EN>Purchase Order</EN>
                        <FR>Commande</FR>
                    </Description>
                    <ConceptName>Order</ConceptName>
                    <ListOfKeys>
                        <Key>
                            <Name>Buyer Order Number</Name>
                            <Description>
                                <EN>Purchase Order Number</EN>
                                <FR>Numéro de la Commande</FR>
                            </Description>
                            <XPath>Order/OrderHeader/OrderNumber/BuyerOrderNumber</XPath>
                        </Key>
                        <Key>
                            <Name>Buyer Party ID</Name>
                            <Description>
                                <EN>Buyer Party Identification</EN>
                                <EN>Identifiant de l'acheteur</EN>
                            </Description>
                            <XPath>Order/OrderHeader/OrderParty/BuyerParty/Party/PartyID/Identifier/Ident</XPath>
                        </Key>
                    </ListOfKeys>
                    <ListOfViewables>
                        <Viewable>
                            <Name>Order Date</Name>
                            <Description>
                                <EN>Purchase Order Date</EN>
                                <FR>Date de la Commande</FR>
                            </Description>
                            <XPath>Order/OrderHeader/OrderIssueDate</XPath>
                        </Viewable>
                    </ListOfViewables>
                </Document>
	 * Expects an xPath Document
	 * @param the Document {@link Node}
	 */
	public static XMLConfiguration_Document parseXMLConfiguration_Document(Element document) throws XtentisWebappException{
		

		
		XMLConfiguration_Document xmlDocument = new XMLConfiguration_Document();
		try {
			xmlDocument.setName(Util.getFirstTextNode(document, "Name"));
			xmlDocument.setDescription(XMLConfiguration_Description.parseXMLConfiguration_Description(Util2.getFirstElement(document, "Description")));
			xmlDocument.setConceptName(Util.getFirstTextNode(document, "ConceptName"));
			xmlDocument.setKeys(getXPaths(document, "ListOfKeys/Key"));
			if (xmlDocument.getKeys().size()==0) {
				throw new XtentisWebappException("Unable to find Keys for the Document"+Util.nodeToString(document));
			}
			
			ArrayList al1  = getSearchable(document, "ListOfViewables/Viewable");
			xmlDocument.setViewables(al1);
			
			ArrayList al2  = getSearchable(document, "ListOfSearchables/Searchable");
			xmlDocument.setSearchables(al2);			
			
			return xmlDocument;
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisWebappException("Unable to read the Document from element "+document.getLocalName());
		}
	}

	
	private static ArrayList<XMLConfiguration_Searchable> getSearchable(Element document, String path) throws XtentisWebappException, Exception{
		ArrayList<XMLConfiguration_Searchable> list = new ArrayList<XMLConfiguration_Searchable>();		
		NodeList nl2 = Util.getNodeList(document, path);
		

		
		// System.err.println("*i*i*i*i*i>" + nl2.getLength());
		
		if ((nl2 != null) && nl2.getLength() > 0) {
			for (int i = 0; i < nl2.getLength(); i++) {
				Element xPath = (Element)nl2.item(i);
				list.add(
					XMLConfiguration_Searchable.parseXMLConfiguration_Searchable(xPath)	
				);
			}
		}
		
		// System.err.println("**********>" + list.size());
		
		return list;
		
	}
	
	private static ArrayList<XMLConfiguration_XPath> getXPaths(Element document, String path) throws XtentisWebappException, Exception{
		ArrayList<XMLConfiguration_XPath> list = new ArrayList<XMLConfiguration_XPath>();		
		NodeList nl2 = Util.getNodeList(document, path);
		if ((nl2 != null) && nl2.getLength() > 0) {
			for (int i = 0; i < nl2.getLength(); i++) {
				Element xPath = (Element)nl2.item(i);
				list.add(
					XMLConfiguration_XPath.parseXMLConfiguration_XPath(xPath)	
				);
			}
		}
		return list;
		
	}
	
	/**
	 * Returns and XML String of the Document
	 */
	public String toString() {
		String xml ="<Document>";
		xml+="<Name>"+name+"</Name>";
		xml += description.toString();
		xml+="<ConceptName>"+conceptName+"</ConceptName>";
		xml+="<ListOfKeys>";
		for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
			XMLConfiguration_XPath key = (XMLConfiguration_XPath) iter.next();
			xml+=key.toString("Key");
		}
		xml+="</ListOfKeys>";
		xml+="<ListOfViewables>";
		for (Iterator iter = viewables.iterator(); iter.hasNext(); ) {
			XMLConfiguration_Searchable key = (XMLConfiguration_Searchable) iter.next();
			xml+=key.toString("Viewable");
		}
		xml+="</ListOfViewables>";
		xml+="<ListOfSearchables>";
		for (Iterator iter = searchables.iterator(); iter.hasNext(); ) {
			XMLConfiguration_Searchable search = (XMLConfiguration_Searchable) iter.next();
			xml+=search.toString("Searchable");
		}
		xml+="</ListOfSearchables>";
		xml+="</Document>";
		return xml;
	}
	
	public void setSearchables(ArrayList<XMLConfiguration_Searchable> searchables) {
		this.searchables = searchables;
	}
	
	public ArrayList<XMLConfiguration_Searchable> getSearchables() {
		return searchables;
	}
	
	
	
}
