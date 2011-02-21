package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.io.Serializable;
import java.util.List;

public class TreeNode implements Serializable {

	
	private TreeNode parent;
	
	
	private String name;
	
	private String description;
	
	private String value;
	
	private String valueInfo;
	
	private boolean expandable;
	
	private String type;
	
	private int nodeId;
	
	private String taskId;
	
	private String typeName;
	
	private String xmlTag;
	
	private String documentation;
	
	private String labelOtherLanguage;
	
	private boolean readOnly = true;
	
	private int maxOccurs;
	
	private int minOccurs;
	
	private boolean nullable = true;
	
	private boolean choice;
	
	private boolean retrieveFKinfos = false;
	
	private String fkFilter;
	
	private String foreignKey;
	
	private String usingforeignKey;
	
	private boolean visible;
	
	private boolean key = false;
	
	private int keyIndex = -1;
	
	private String realValue;
	
	private String bindingPath;
	
	private boolean polymiorphise;
	
	private String realType;
	
	private List<Restriction> restrictions;
	
	private List<String> enumeration;
	
	private List<String> subTypes;
	
	
	public TreeNode(){}
	
	
}
