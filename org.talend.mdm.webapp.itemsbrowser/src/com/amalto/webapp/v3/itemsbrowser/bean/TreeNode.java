package com.amalto.webapp.v3.itemsbrowser.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.util.Util;
import com.sun.xml.xsom.XSAnnotation;

public class TreeNode implements Cloneable {

	public TreeNode() {
		super();
		if(!Util.isEnterprise()){
			readOnly = false;
		}
	}

	private String name;
	private String description;
	private String value;
	private String valueInfo;
	private boolean expandable;
	private String type;
	private int nodeId;
	//add by ymLi 0008917 
	private TreeNode parent;
	


	//browse item specific
	private String typeName;
	private String xmlTag;
	private String documentation;
	private String labelOtherLanguage;
	private boolean readOnly = true;
	private int maxOccurs;
	private int minOccurs;
	private boolean nillable = true;
	private boolean choice;
	private ArrayList<Restriction> restrictions;
	private ArrayList<String> enumeration;
	private boolean retrieveFKinfos = true;
   private String foreignKey;
	private boolean visible;
	private boolean key = false;
	private int keyIndex = -1;
	//add by fliu 0009157
	private HashMap<String, String> facetErrorMsgs  = new HashMap<String, String>();

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.getMessage());
		}
	}

	
	public void fetchAnnotations(XSAnnotation xsa, ArrayList<String> roles, String language) throws Exception{
		org.apache.log4j.Logger.getLogger(this.getClass()).debug(
				"fetchAnnotation() ");
		String X_Label = "X_Label_"+language.toUpperCase();
		try {    		
    		if(xsa!=null && xsa.getAnnotation()!=null){
    			Element el = (Element) xsa.getAnnotation();
    			NodeList annotList = el.getChildNodes();
    			ArrayList<String> fkInfoList = new ArrayList<String>();
    			for (int k = 0; k < annotList.getLength(); k++) {					
    				if("appinfo".equals(annotList.item(k).getLocalName())) {
    					Node source=annotList.item(k).getAttributes().getNamedItem("source");
    					if(source==null) continue;
    					String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue();
    					if(X_Label.equals(appinfoSource)){
    						setName(annotList.item(k).getFirstChild().getNodeValue());
    					}
    					else if(appinfoSource.contains("X_Label")){
    						setLabelOtherLanguage(annotList.item(k).getFirstChild().getNodeValue());
    					}
    					else if("X_Write".equals(appinfoSource)){							
    						if(roles.contains(annotList.item(k).getFirstChild().getNodeValue())){
    							setReadOnly(false);							
    						}
    					}
    					else if("X_Hide".equals(appinfoSource)){
    						if(roles.contains(annotList.item(k).getFirstChild().getNodeValue())){
    							setVisible(false);
    						}							
    					}
    					else if("X_ForeignKey".equals(appinfoSource)){
    						setForeignKey(annotList.item(k).getFirstChild().getNodeValue());
    					}		
    					else if("X_ForeignKeyInfo".equals(appinfoSource)){							
    						fkInfoList.add(annotList.item(k).getFirstChild().getNodeValue());
    					}else if("X_Retrieve_FKinfos".equals(appinfoSource)) {
    					   setRetrieveFKinfos("true".equals(annotList.item(k).getFirstChild().getNodeValue()));
    					}else if(("X_Description_"+language.toUpperCase()).equals(appinfoSource)){		
    					   Node description = annotList.item(k).getFirstChild();
    					   String encodedDESP = description != null ? description.getNodeValue().replaceAll("\"", "&quot;") : "";
    						setDescription(encodedDESP);
    					}
    					else if(appinfoSource.indexOf("X_Facet_") != -1)
    					{
    						  Pattern p = Pattern.compile("X_Facet_(.*?)");  
    						  Matcher matcher = p.matcher(appinfoSource); 
    						  if (matcher.matches())
    						  {
    							  setFacetErrorMsg(matcher.group(1).toLowerCase(), annotList.item(k).getFirstChild().getNodeValue());
    						  }
    					}
    				}
    				if("documentation".equals(annotList.item(k).getLocalName())) {
    					setDocumentation(annotList.item(k).getFirstChild().getNodeValue());
    				}
    			}
    			setForeignKeyInfo(fkInfoList);
    		}			
		}
		catch(Exception e) {
			throw new Exception(e.getClass().getName()+": "+e.getLocalizedMessage());
		}		
	}

	private ArrayList<String> foreignKeyInfo;
	
	public ArrayList<String> getForeignKeyInfo() {
		return foreignKeyInfo;
	}

	public void setForeignKeyInfo(ArrayList<String> foreignKeyInfo) {
		this.foreignKeyInfo = foreignKeyInfo;
	}

	public String getForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}
	
	public boolean isChoice() {
		return choice;
	}

	public void setChoice(boolean choice) {
		this.choice = choice;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public boolean isExpandable() {
		return expandable;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}

	public String getLabelOtherLanguage() {
		return labelOtherLanguage;
	}

	public void setLabelOtherLanguage(String labelOtherLanguage) {
		this.labelOtherLanguage = labelOtherLanguage;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(int maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
	}
	
	
	public boolean isNillable() {
		return nillable;
	}


	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public ArrayList<String> getEnumeration() {
		return enumeration;
	}


	public void setEnumeration(ArrayList<String> enumeration) {
		this.enumeration = enumeration;
	}
	

   public boolean isRetrieveFKinfos() {
      return retrieveFKinfos;
   }

   public void setRetrieveFKinfos(boolean retrieveFKinfos) {
      this.retrieveFKinfos = retrieveFKinfos;
   }

	public ArrayList<Restriction> getRestrictions() {
		// edit by ymli; fix the bug:0011733
		// if there are more than one pattern, connect them to be one( and "|"
		// between them)
		ArrayList<Restriction> newRestrictions = new ArrayList<Restriction>();
		String value = "";
		if (restrictions != null) {
			for (int i = 0; i < restrictions.size(); i++) {
				Restriction re = restrictions.get(i);
				if (re.getName().equals("pattern"))
					value += re.getValue() + "|";
				else
					newRestrictions.add(re);
			}
			if (value.length() > 0) {
				Restriction re1 = new Restriction("pattern", value.substring(0,value.length() - 1));
				newRestrictions.add(re1);
			}

			return newRestrictions;
		}
		return restrictions;
	}


	public void setRestrictions(ArrayList<Restriction> restrictions) {
		this.restrictions = restrictions;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueInfo(){
		return valueInfo;
	}
	
	public void setValueInfo(String info)
	{
		this.valueInfo = info;
	}
	
	public String getXmlTag() {
		return xmlTag;
	}

	public void setXmlTag(String xmlTag) {
		this.xmlTag = xmlTag;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean hidden) {
		this.visible = hidden;
	}


	public boolean isReadOnly() {
		return readOnly;
	}


	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}


	public boolean isKey() {
		return key;
	}


	public void setKey(boolean key) {
		this.key = key;
	}


	public int getKeyIndex() {
		return keyIndex;
	}


	public void setKeyIndex(int keyIndex) {
		this.keyIndex = keyIndex;
	}

	
	public TreeNode getParent() {
		return parent;
	}


	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	
	/**
	 * add by fliu 0009157
	 * @param lang
	 * @param facet
	 */
	public void setFacetErrorMsg(String lang, String facet)
	{
		if (facetErrorMsgs.containsKey(lang))
			facetErrorMsgs.remove(lang);
		facetErrorMsgs.put(lang, facet);
	}
	
	/**
	 * add by fliu 0009157
	 * @return
	 *    the hashMap contains facet error msg
	 */
	public HashMap getFacetErrorMsg()
	{
		return facetErrorMsgs;
	}
}