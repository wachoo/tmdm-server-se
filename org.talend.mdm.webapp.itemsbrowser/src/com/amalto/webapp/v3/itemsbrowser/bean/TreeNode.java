package com.amalto.webapp.v3.itemsbrowser.bean;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.xsom.XSAnnotation;

public class TreeNode implements Cloneable {

	public TreeNode() {
		super();
	}

	private String name;
	private String description;
	private String value;
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
	private String foreignKey;
	private boolean visible;
	private boolean key = false;
	private int keyIndex = -1;
	
	

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
    					}else if(("X_Description_"+language.toUpperCase()).equals(appinfoSource)){						
    						setDescription(annotList.item(k).getFirstChild().getNodeValue());
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



	public ArrayList<Restriction> getRestrictions() {
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
}