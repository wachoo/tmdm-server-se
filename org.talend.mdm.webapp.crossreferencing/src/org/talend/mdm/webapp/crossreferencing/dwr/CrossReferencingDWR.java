package org.talend.mdm.webapp.crossreferencing.dwr;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.talend.mdm.webapp.crossreferencing.bean.Configuration;
import org.talend.mdm.webapp.crossreferencing.bean.CrossReferencingTableDescription;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSDeleteBusinessConcept;
import com.amalto.webapp.util.webservices.WSDeleteItem;
import com.amalto.webapp.util.webservices.WSDeleteItems;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetItems;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutBusinessConceptSchema;
import com.amalto.webapp.util.webservices.WSPutItem;



/**
 *
 * @author Bruno Grieder
 *
 */
public class CrossReferencingDWR {


	public TreeMap<String,String> getCrossReferencingTableNames(String value) throws XtentisWebappException {
		try {
			TreeMap<String,String> map = new TreeMap<String,String>();
			String[] result =
				Util.getPort().getBusinessConcepts(
						new WSGetBusinessConcepts(
								new WSDataModelPK(Configuration.datamodel)
						)
				).getStrings();
			for (int i = 0; i < result.length; i++) {
				map.put(result[i], result[i]);
			}
			return map;
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisWebappException("Unable to retrieve the list of cross-referencing tables"+": "+e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}



	public ArrayList<ArrayList<String>> getCrossReferencingContent(String tableName) throws XtentisWebappException {
		try {
			String[] results = Util.getPort().getItems(
					new WSGetItems(
							new WSDataClusterPK(Configuration.datacluster),
							tableName,
							null,
							-1,
							0,
							-1
					)
			).getStrings();


			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			XPath xpath = XPathFactory.newInstance().newXPath();

			ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
			//the first row is totalCount
			for(int i=1;i<results.length;i++){
//				org.apache.log4j.Logger.getLogger(this.getClass()).debug("getCrossReferencingContent() "+results[i]);
				Element lineElement = documentBuilder.parse(new InputSource(new StringReader(results[i]))).getDocumentElement();
				NodeList rowList = (NodeList) xpath.evaluate("./*", lineElement, XPathConstants.NODESET);
				ArrayList<String> fields = new ArrayList<String>();
				for (int j = 0; j < rowList.getLength(); j++) {
					//TODO
					//if(rowList!=null && rowList.item(i)!=null)
					{
						NodeList textNodes = rowList.item(j).getChildNodes();
						String val="";
						//pickup nodes for which there is a text content
						if (textNodes.getLength() > 0) {
							String text = textNodes.item(0).getNodeValue();
							//System.out.println(i+" "+j+" "+text);
							if (text!=null) val =text;
						}
						fields.add(val);
					}
				}
				list.add(fields);
			}
    		if(results.length>0)
    			return list;
    		else
    			return null;
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Update the content of an item/line in the Cross-referencing table
	 * @param concept
	 * @param fields
	 * @param values
	 * @return null
	 * @throws XtentisWebappException
	 * @throws Exception
	 */
	public String updateDocument(String concept, String[] fields,String[] values) throws XtentisWebappException, Exception{

		StringBuffer xml=new StringBuffer();
		xml.append("<"+concept+">");
        for(int i=0;i<fields.length;i++) {
        	xml.append("<"+fields[i]+">");
        	xml.append(values[i].trim());
        	xml.append("</"+fields[i]+">");
        }
		xml.append("</"+concept+">");
		Util.getPort().putItem(
                new WSPutItem(
                		new WSDataClusterPK(Configuration.datacluster),
                        xml.toString(),
                        new WSDataModelPK(Configuration.datamodel),false
                )
        );
		return "OK";
	}

	public String addDocument(String concept,String[] fields,String[] values) throws XtentisWebappException, Exception{
		return updateDocument(concept,fields,values);
	}

	/**
	 * Deletes the content of an item/line in the Cross-referencing table
	 * @param concept
	 * @param keys
	 * @param fields
	 * @param values
	 * @return null
	 * @throws XtentisWebappException
	 * @throws Exception
	 */
	public String deleteDocument(String concept, String[] keys, String[] fields, String[] values) throws XtentisWebappException, Exception{
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < keys.length; i++) {
			//System.out.println("key "+keys[i]);
			for (int j = 0; j < fields.length; j++) {
				if(keys[i].equals(fields[j])) {
					list.add(values[j].trim());
					//System.out.println(values[j]);
				}
			}
		}

		Util.getPort().deleteItem(new WSDeleteItem(
				new WSItemPK(
						new WSDataClusterPK(Configuration.datacluster),
						concept,
						list.toArray(new String[list.size()])
						)
					)
			);
		return null;
	}


	/**
	 * Return the field names in a Tables
	 * @param tableName
	 * @return the cross referencing table description object
	 * @throws XtentisWebappException
	 */
	public CrossReferencingTableDescription getTableDescription(String tableName) throws XtentisWebappException{
		return CrossReferencingTableDescription.getCrossReferencingTableDescription(tableName);
	}



	/**
	 * creates a Xml schema (xsd) to put a concept in the Data Models CROSSREF
	 * @param concept who is the name of concept
	 * @param fields
	 * @return the xml string representing a concept
	 */
	private String createXsd(String concept,String[] fields,String[] keys){
		StringBuffer buffer=new StringBuffer();
		buffer.append("<xsd:element name=\"").append(concept).append("\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
		buffer.append("<xsd:complexType>");
		buffer.append("<xsd:sequence maxOccurs=\"1\" minOccurs=\"0\">");
		for(int i=0;i<fields.length;i++)
			if(!fields[i].equals("")){
				buffer.append("<xsd:element maxOccurs=\"1\" ");
				if(keys[i].equals("true"))
					buffer.append("minOccurs=\"1\" ");
				else
					buffer.append("minOccurs=\"0\" ");
				buffer.append("name=\"").append(fields[i]).append("\" ");
				if(keys[i].equals("false"))
					buffer.append(" nillable=\"true\" ");
				 buffer.append("type=\"xsd:string\"/>");
			}
		buffer.append("</xsd:sequence>");
		buffer.append("</xsd:complexType>");
		buffer.append("<xsd:unique name=\"").append(concept).append("\">");
		buffer.append("<xsd:selector xpath=\".\"/>");
		for(int i=0;i<keys.length;i++)
			if(!fields[i].equals("") && keys[i].equals("true"))
				buffer.append("<xsd:field xpath=\"").append(fields[i]).append("\"/>");
		buffer.append("</xsd:unique>");
		buffer.append("</xsd:element>");

		return buffer.toString();
	}


	/**
	 * gets a grid from a view
	 * @throws Exception
	 */
	public String[] putCrossReferencingTable(String concept,String[] fields,String[] keys) throws Exception {

		concept=concept.replaceAll(" ","_");

		for(int i=0;i<fields.length;i++){
			fields[i]=fields[i].replaceAll(" ","_");
		}

		Util.getPort().putBusinessConceptSchema(new WSPutBusinessConceptSchema(
				new WSDataModelPK(Configuration.datamodel),
				createXsd(concept,fields,keys))
		);

		return null;

	}



		/**
	 * Delete a Cross-Referencing table
	 * @param tableName
	 * @throws Exception
	 * @throws RemoteException
	 */
	public void deleteCrossReferencingTable(String tableName) throws RemoteException, Exception{

		tableName=tableName.replaceAll("\\s+","");

		/*deletes item */
		Util.getPort().deleteItems(
				new WSDeleteItems(
						new WSDataClusterPK(Configuration.datacluster),
						tableName,
						null,
						-1
				)
		);

		/* deletes concept */
		Util.getPort().deleteBusinessConcept(
				new WSDeleteBusinessConcept(
						new WSDataModelPK(Configuration.datamodel),tableName
				)
		);
	}



}