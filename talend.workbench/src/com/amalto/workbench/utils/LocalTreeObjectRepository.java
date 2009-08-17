package com.amalto.workbench.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Node;

import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.views.ServerView;

public class LocalTreeObjectRepository implements IXObjectModelListener, ITreeViewerListener{
	private Document doc;
	private ServerView view;
	private boolean internalCheck = false;
	
	private static String config = System.getProperty("user.dir")+"/.treeObjectConfig.xml";
	private static String rootPath = "/root";
	private static String DEFAULT_CATALOG = "System";
	private static String EXPAND_NAME = "Expand";
	private static String EXPAND_VALUE = "true";
	private static String COLLAPSE_VALUE = "false";
	
	private static int XTENTIS_LEVEL = 4;
	
	private Element catalogTreeObj = null;
	private TreeItem itemFocus = null;
	
	private static LocalTreeObjectRepository repository = null;
	
    synchronized public static LocalTreeObjectRepository getInstance()
    {
        if (repository == null)
        {
        	repository = new LocalTreeObjectRepository();
        }

        return repository;
    }
	
	public void startUp(ServerView vw, String ur)
	{
		 view = vw;
		  
		 try {
			  File configFile = new File(config);
			  if (!configFile.exists())
			  {
				  doc = DocumentHelper.createDocument();
				  doc.addElement("root");
		          XMLWriter writer = new XMLWriter(new FileWriter(configFile), OutputFormat.createPrettyPrint());
		          writer.write(doc);
		          writer.flush();
		          writer.close();
			  }
			  else
			  {				  
				  SAXReader saxReader = new SAXReader();
				  doc = saxReader.read(configFile);
			  }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getXPathForElem(Element elem)
	{
		List<String> xpathList = new ArrayList<String>();
		recurseElementForXPath(elem, xpathList);
		
		String xpathVar = "";
		for (int idx = xpathList.size() - 1; idx >= 0; idx--) {
			xpathVar += "/" + xpathList.get(idx);
		}
		
		return xpathVar;
	}
	
	private void recurseElementForXPath(Element elem, List<String> xpathList)
	{
		if (elem != null) {
			xpathList.add(elem.getName());
			recurseElementForXPath(elem.getParent(), xpathList);
		}

	}
		
	
	public void handleEvent(int type, TreeObject parent, TreeObject child)
	{
		if (internalCheck) return;
		
		switch (type) {
		case IXObjectModelListener.ADD:
			addChild(parent, child);
			break;
		case IXObjectModelListener.DELETE:
			removeChild(parent, child);
			break;
		}
	}
	
	private Element getTopElementWithUser(String user)
	{
		List<Element> userRoots = doc.selectNodes(rootPath + "/" + user);
		Element elementUser;
		if (userRoots.isEmpty()) {
			elementUser = doc.getRootElement().addElement(user);
			elementUser.setText("0");	
		} else {
			elementUser = userRoots.get(0);
		}
		
		return elementUser;
	}
	
	private void saveDocument(Document doc)
	{
        File configFile = new File(config);
        StringWriter w = new StringWriter();
        XMLWriter writer = new XMLWriter(w);
        
        try {
			writer.setOutputStream(new FileOutputStream(configFile));
	        writer.write(doc);
	        writer.flush();
	        writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private String filterOutBlank(String input)
	{
		Pattern BLANK = Pattern.compile("([\\s]+)");
		String res = BLANK.matcher(input).replaceAll("");
		res = res.replaceAll("\\[\\w+\\]", "");
		
		return 	res;	
	}
	
    private Element getParentElement(TreeObject treeObj)
    {
		if (!(treeObj instanceof TreeParent))return null;
		
		Element elemFolder = null;
		String xpath = getXPathForTreeObject(treeObj);

		if (doc.selectNodes(xpath).isEmpty()) {
			xpath = xpath.replaceAll("\\[.*\\]", "");
			if (doc.selectNodes(xpath).isEmpty())
			  xpath = getXPathForTreeObject(treeObj.getParent() != null ? treeObj.getParent() : treeObj.getServerRoot());
			if (xpath != null)
			{
				Element elemTop = null;
				if (doc.selectNodes(xpath).isEmpty())
				{
					elemTop = getTopElementWithUser(treeObj.getServerRoot().getUser().getUsername());
				}
				else
				{
					elemTop = (Element)doc.selectNodes(xpath).get(0);
				}
				elemFolder = elemTop.addElement(filterOutBlank(treeObj.getDisplayName()));
				elemFolder.setText(treeObj.getType() + "");	
			}
		}
		else
		{
			elemFolder = (Element)doc.selectNodes(xpath).get(0);
		}
		
			
		
		return elemFolder;
    }
    
    private String dump()
    {
    	try {
			return Util.nodeToString((Node)doc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
    }
    
    
	private void addChild(TreeObject parent, TreeObject child)
	{
		if (parent.getParent() == null && parent.getDisplayName().equals("INVISIBLE ROOT"))
			return;
		String catalog = synchronizeWithElem(child, (TreeParent)parent, true);
		Element elemFolder = getParentElement(parent);
		if (elemFolder != null)
		{
			String xpath = "child::*[name()='" + filterOutBlank(child.getDisplayName()) + "' and text()='" + child.getType() + "']";
			List<Element> list = elemFolder.selectNodes(xpath);
			if (list.isEmpty() && catalog.equals(""))
			{
				Element childElem = elemFolder.addElement(filterOutBlank(child.getDisplayName()));
				childElem.setText(child.getType() + "");
				if (child.getType() == TreeObject.CATEGORY_FOLDER)
				{
					childElem.addAttribute(EXPAND_NAME, COLLAPSE_VALUE);
				}
			}
		}
		saveDocument(doc);
	}
	
	private void removeChild(TreeObject parent, TreeObject child) 
	{
		if (parent.getParent() == null && parent.getDisplayName().equals("INVISIBLE ROOT"))
			return;
		synchronizeWithElem(child, (TreeParent)parent, true);
		Element elemFolder = getParentElement(parent);
		String xpath = "child::*[name()='" + filterOutBlank(child.getDisplayName()) + "' and text()='" + child.getType() + "']";
		List<Element> list = elemFolder.selectNodes(xpath);
		if (!list.isEmpty())
		  elemFolder.remove(list.get(0));
		
		saveDocument(doc);
	}
	
	
	public void synchronizeWithDoc(TreeParent root)
	{
		internalCheck = false;
        TreeObject[] targetChildren = root.getChildren();
        for (TreeObject treeObj: targetChildren)
		{
        	if (treeObj instanceof TreeParent && treeObj.getServerRoot() == treeObj)
        		synchronizeWithDoc((TreeParent)treeObj);
        	
        	synchronizeWithElem(treeObj, root, true);
		}
        
        view.getViewer().refresh(false);
		
	}
	
	private String getXPathForTreeObject(TreeObject theObj)
	{			
		List<String> xpathList = new ArrayList<String>();
		recurseTreeObjectForXPath(theObj, xpathList);
		
		String xpathVar = "";
		for (int idx = xpathList.size() - 1; idx >= 0; idx--) {
			xpathVar += "/" + xpathList.get(idx);
		}
		
		xpathVar += "[text() = '" + theObj.getType() + "']";
		
		return xpathVar;
	}
	
	
	private void recurseTreeObjectForXPath(TreeObject theObj, List<String> list)
	{
		if (theObj.getParent() == null
				&& theObj.getDisplayName().equals("INVISIBLE ROOT")) {
			list.add(rootPath);
			return;
		} else if (theObj instanceof TreeParent
				&& theObj.getServerRoot() == theObj) {
			list.add(theObj.getUser().getUsername());
			list.add("root");
			return;
		} else {
			list.add(filterOutBlank(theObj.getDisplayName()));
		}
		
		recurseTreeObjectForXPath(theObj.getParent() != null ? theObj.getParent() : theObj.getServerRoot(), list);
	}
	
	private String synchronizeWithElem(TreeObject theObj, TreeParent folder, boolean fireEvent)
	{
		internalCheck = fireEvent;
		String catalogPath = "";
		ArrayList<String> catalogs = checkUpCatalogRepositoryForTreeObject(theObj, folder);
		if (catalogs != null && folder.getType() != TreeObject.CATEGORY_FOLDER)
		{
			// create a catalog folder and insert the theObj into it
			TreeParent subFolder = folder;
			TreeParent category = null;
			for (String catalogName: catalogs)
			{
				if (catalogName.equals(filterOutBlank(folder.getDisplayName())))
					continue;
				category = null;
				for (TreeObject child: subFolder.getChildren())
				{
					if (child.getDisplayName().equals(catalogName)
							&& child.getType() == TreeObject.CATEGORY_FOLDER
							&& child instanceof TreeParent)
					{
						category = (TreeParent)child;
						subFolder = category;
						break;
					}
				}
				
				if (category == null) {
					category = new TreeParent(catalogName, folder
							.getServerRoot(), TreeObject.CATEGORY_FOLDER, null,
							null);				
					subFolder.addChild(category);
			   		category.setServerRoot(folder.getServerRoot());
			   		addAttributeToCategoryElem(category, EXPAND_NAME, COLLAPSE_VALUE);
			   		subFolder = category;
				}
			}

	   		boolean exist = false;
	   		for (TreeObject obj: category.getChildren())
	   		{
	   			if (obj.getDisplayName().equals(theObj.getDisplayName()) && obj.getType() == theObj.getType())
	   			{
	   				exist = true;
	   				break;
	   			}
	   		}
	   		
	   		if (!exist)
	   		{
		   		folder.removeChild(theObj);
	   		  category.addChild(theObj);
	   		}
	   		
	   		catalogPath = catalogs.isEmpty() ? "" : catalogs.get(0);
		}
		else
		{
			// check up the system catalog, create it and all system tree objects
			// into it if these objects are not categorized
			if (XSystemObjects.isExist(theObj.getType(), theObj.getDisplayName()))
			{
				TreeParent systemCatalog = null;
				for (TreeObject xobj: folder.getChildren())
				{
					if (DEFAULT_CATALOG.equals(filterOutBlank(xobj.getDisplayName())) && xobj.getType() == TreeObject.CATEGORY_FOLDER)
					{
						systemCatalog = (TreeParent)xobj;
						break;
					}
				}
				
				if (folder.getDisplayName().equals("System") && folder.getType() == TreeObject.CATEGORY_FOLDER)
					return DEFAULT_CATALOG;
				
				if (theObj.getParent() == null) {
					internalCheck = !fireEvent;
					return DEFAULT_CATALOG;
				}
				
	        	String xpath = "//"
					+ theObj.getServerRoot().getUser().getUsername() + "/" + filterOutBlank(folder.getDisplayName()) 
					+ "//child::*[name() = 'System']";
	        	
				if (systemCatalog == null)
				{
	        	    List<Element> elems = doc.selectNodes(xpath);
	        	    if (elems.isEmpty())
	        	    {
						systemCatalog = new TreeParent(DEFAULT_CATALOG,
								folder.getServerRoot(), TreeObject.CATEGORY_FOLDER,
								null, null);
						folder.addChild(systemCatalog);	
						folder.removeChild(theObj);
						systemCatalog.addChild(theObj);
						systemCatalog.setServerRoot(folder.getServerRoot());

						Element elemFolder = getParentElement(folder);
						Element elemSystem = elemFolder.addElement(systemCatalog.getDisplayName());
						elemSystem.setText(TreeObject.CATEGORY_FOLDER + "");
						elemSystem.addAttribute(EXPAND_NAME, COLLAPSE_VALUE);
						Element childElem = elemSystem.addElement(filterOutBlank(theObj.getDisplayName()));
						childElem.setText(theObj.getType() + "");
						
						addAttributeToCategoryElem(systemCatalog, EXPAND_NAME, COLLAPSE_VALUE);
						saveDocument(doc);
	        	    }
				}
				else
				{
					boolean exist = false;
					
					for (TreeObject xbj: systemCatalog.getChildren())
					{
						if (xbj.getDisplayName().equals(filterOutBlank(theObj.getDisplayName())) && xbj.getType() == theObj.getType())
						{
							exist = true;
							break;
						}
							
					}
					if (!exist)
					{
						folder.removeChild(theObj);
					    systemCatalog.addChild(theObj);
					    List<Element> elems = doc.selectNodes(xpath);
					    if (!elems.isEmpty())
					    {
					    	elems.get(0).addElement(filterOutBlank(theObj.getDisplayName())).setText(theObj.getType() + "");
					    	saveDocument(doc);
					    }
					    
					}
				}
				catalogPath = DEFAULT_CATALOG;
			}
		}

		// check up the catalog with none children
		checkUpCatalogHavingNoChildren(theObj, folder);
		
        if (theObj instanceof TreeParent) {
			TreeParent subParent = (TreeParent) theObj;
			for (TreeObject obj : subParent.getChildren()) {
				synchronizeWithElem(obj, subParent, fireEvent);
			}
		}
		
        internalCheck = !fireEvent;
        
        return catalogPath;
	}
	
	
	private void checkUpCatalogHavingNoChildren(TreeObject theObj, TreeParent folder)
	{
		if (folder.getServerRoot() == folder) return;
		
		String xpath = "//" + theObj.getServerRoot().getUser().getUsername()
		+ "/" + filterOutBlank(folder.getDisplayName())
		+ "//child::*[text() = '" + TreeObject.CATEGORY_FOLDER
		+ "' and count(child::*) = 0]";
		

		TreeParent subFolder = folder;
		List<Element> elems = doc.selectNodes(xpath);
		for (Element elem: elems)
		{
			String xpaths = getXPathForElem(elem);
			int modelPos = xpaths.indexOf(filterOutBlank(folder.getDisplayName()));
			if (modelPos == -1)
			{
				System.err.println("elem.getName() is in wrong position !!!");
				return;
			}
			xpaths = xpaths.substring(modelPos + filterOutBlank(folder.getDisplayName()).length() + 1);
			modelPos = xpaths.indexOf("/");
			while(modelPos != -1 || (!xpaths.equals("") && modelPos == -1))
			{
				String nodeName = modelPos != -1 ? xpaths.substring(0, modelPos) : xpaths;
				boolean catalogExist = false;
				for (TreeObject child: subFolder.getChildren())
				{
					if (nodeName.equals(filterOutBlank(child.getDisplayName()))
							&& child.getType() == TreeObject.CATEGORY_FOLDER)
					{
						catalogExist = true;
						subFolder = (TreeParent)child;
						break;
					}
				}
				
				if (!catalogExist)
				{
					TreeParent catalog = new TreeParent(nodeName, folder
							.getServerRoot(), TreeObject.CATEGORY_FOLDER, null, null);
					subFolder.addChild(catalog);
					subFolder = catalog;
				}
				
				if (xpaths.indexOf("/") != -1)
				{
					xpaths = xpaths.substring(modelPos + 1);
					modelPos = xpaths.indexOf("/");
				}
				else
					xpaths = "";

			}

		}
	}
	

	private ArrayList<String> checkUpCatalogRepositoryForTreeObject(TreeObject theObj, TreeObject folder)
	{
		if (theObj.getType() == 0) return null;
		try
		{
			String modelName = getXPathForTreeObject(folder);
			
			String xpath = modelName + "//child::*[text() = '" + TreeObject.CATEGORY_FOLDER + "']//child::*";
			List<Element> elems = doc.selectNodes(xpath);
			for (Element elem : elems)
			{
				if (elem.getName().equals(filterOutBlank(theObj.getDisplayName()))
						&& elem.getData().toString().equals(theObj.getType() + ""))
				{
					ArrayList<String> path = new ArrayList<String>();
					while (isAEXtentisObjects(elem) > XTENTIS_LEVEL)
					{
						path.add(elem.getParent().getName());
						elem = elem.getParent();
					}
                    Collections.reverse(path);
					return path;
				}
			}
		}
		catch(Exception ex)
		{
			return null;
		}	
		return null;
	}
	
	private int isAEXtentisObjects(Element elemXobj)
	{
		if (elemXobj == null)
			return 0;
		
		if (elemXobj == doc.getRootElement())
		     return 1;

		return 1 + isAEXtentisObjects(elemXobj.getParent());
	}
	
	public int receiveUnCertainTreeObjectType(TreeObject xobj)
	{
		String path = this.getXPathForTreeObject(xobj);
		List<Element> elems = doc.selectNodes(path);
		if (!elems.isEmpty())
		{
			Element elem = elems.get(0);
			while (isAEXtentisObjects(elem) >= XTENTIS_LEVEL)
			{
				elem = elem.getParent();
			}
			return Integer.parseInt(elem.getText());
		}
		else
			return -1;
	}
	
	public void switchOnListening()
	{
		internalCheck = false;
	}
	
	public void switchOffListening()
	{
		internalCheck = true;
	}
	
	public TreeObject registerNewTreeObject(TreeObject treeObject)
	{
        if (treeObject.getType() == TreeObject.CATEGORY_FOLDER)
        {
        	String xpath = getXPathForTreeObject(treeObject);
        	List<Element> elems = doc.selectNodes(xpath);
        	if (!elems.isEmpty())
        	{
        		if (catalogTreeObj == null)
        			catalogTreeObj = elems.get(0);
        	}
        	
        	treeObject = treeObject.getParent();
        	treeObject = registerNewTreeObject(treeObject);
        }
        else if (!(treeObject instanceof TreeParent) && treeObject.getParent().getType() == TreeObject.CATEGORY_FOLDER)
        {
        	registerNewTreeObject(treeObject.getParent());
        	treeObject = treeObject.getParent().getParent();
        }
        
        return treeObject;
	}
	
	public void mergeNewTreeObject(TreeObject newTreeObject)
	{
		if (catalogTreeObj != null)
		{
			catalogTreeObj.addElement(newTreeObject.getDisplayName()).setText(newTreeObject.getType() + "");
			saveDocument(doc);
		}
		
		catalogTreeObj = null;
	}
	
	public boolean isInSystemCatalog(TreeObject xobject)
	{
		if (xobject instanceof TreeParent)
		{
			if (xobject.getType() == TreeObject.CATEGORY_FOLDER && xobject.getDisplayName().equals("System"))
			{
				String path = this.getXPathForTreeObject(xobject);
				List<Element> elems = doc.selectNodes(path);
				if (!elems.isEmpty())
				{
					Element elem = elems.get(0);
					if (isAEXtentisObjects(elem) == XTENTIS_LEVEL) {
						return true;
					}
				}
			}
			return false;
		}
		else
		{
			TreeParent parent = (TreeParent)xobject.getParent();
			return isInSystemCatalog(parent);
		}
	}
	
    public void treeCollapsed(TreeExpansionEvent event) {
    	setTreeNodeImage(event, false);
    }

    public void treeExpanded(TreeExpansionEvent event) {
    	setTreeNodeImage(event, true);
    }
    
    private void addAttributeToCategoryElem(TreeParent category, String attrName, String defaultAttrValue)
    {
   		Element elem = locateCategoryElement(category);
   		Attribute attr = elem.attribute(attrName);
   		if (attr == null)
   		{
   			elem.addAttribute(attrName, defaultAttrValue);
   		}
    }
    
    private Element locateCategoryElement(TreeParent category) {
    	if (category.getParent() == category.getServerRoot())
    		return null;
    	
		String xpath = "//" + category.getServerRoot().getUser().getUsername()
				+ "//" + filterOutBlank(category.getParent().getDisplayName())
				+ "//child::*/.[text() = '" + TreeObject.CATEGORY_FOLDER
				+ "' and name()='" + category.getDisplayName() + "']";

		List<Element> elms = doc.selectNodes(xpath);

		return !elms.isEmpty() ? elms.get(0) : null;
	}
    
    private void setTreeNodeImage(TreeExpansionEvent event, boolean expand) {
		Object elem = event.getElement();
		Widget item = view.getViewer().testFindItem(event.getElement());
		if (item == null || (item != itemFocus && itemFocus != null))
			item = itemFocus;
		if (elem instanceof TreeParent && item != null) {
			TreeParent parent = (TreeParent) elem;
			if (parent.getType() == TreeObject.CATEGORY_FOLDER) {
				((TreeItem) item).setImage(expand ? ImageCache
						.getCreatedImage("icons/folder_open.gif") : ImageCache
						.getCreatedImage("icons/folder.gif"));
				
				Element elemCategory = locateCategoryElement(parent);
		        if (elemCategory != null)
		        {
		        	elemCategory.attribute(EXPAND_NAME).setValue(expand ? EXPAND_VALUE : COLLAPSE_VALUE);
		        	saveDocument(doc);
		        }

			}
		}
	}
    
    public boolean getCategoryExpandState(TreeParent category)
    {
    	Element elem = locateCategoryElement(category);
    	if (elem != null)
    	{
    		return Boolean.valueOf(elem.attribute(EXPAND_NAME).getValue());
    	}
    	
    	return false;
    }
    
    private void getTreeItemFromTreeObject(TreeItem[] items,TreeObject xobj, ArrayList<TreeItem> ret)
    {
    	for (TreeItem o: items)
    	{
    		TreeObject treeObj = (TreeObject)o.getData();
    		if (equalTermsWithTreeObject(treeObj, xobj))
    		{
    			ret.add(o);
    			return;
    		}
    	   getTreeItemFromTreeObject(o.getItems(), xobj, ret);
    	}
    }
    
    private boolean equalTermsWithTreeObject(TreeObject theObj, TreeObject otherObj)
    {
    	if (theObj == null || otherObj == null)return false;
       	if (theObj == otherObj) return true;
       	
    	if (theObj.getParent() == theObj.getServerRoot() && otherObj.getParent() == otherObj.getServerRoot())
    	{
    		if (theObj.getDisplayName().equals(otherObj.getDisplayName()))
    			return true;
    		else
    			return false;
    	}

    	
    	if (theObj.getType() != otherObj.getType()
				|| !filterOutBlank(theObj.getDisplayName()).equals(
						filterOutBlank(otherObj.getDisplayName())))
    	{
    		return false;
    	}
    	
    	return equalTermsWithTreeObject(theObj.getParent(), otherObj.getParent());
    }
    
    public void refreshCategoryStateWithinModel(TreeParent model)
    {
    	Element elemCategory = null;
		for (TreeObject xobj : model.getChildren())
		{
			if(xobj instanceof TreeParent)
			{
				refreshCategoryStateWithinModel((TreeParent)xobj);
			}
			else 
				continue;
			
			elemCategory = locateCategoryElement((TreeParent)xobj);
			if (elemCategory == null) continue;
			Attribute attr = elemCategory.attribute(EXPAND_NAME);
			if (attr != null)
			{
				String value = attr.getValue();
				Tree tree = (Tree)view.getViewer().getControl();
				ArrayList<TreeItem> list = new ArrayList<TreeItem>();
				getTreeItemFromTreeObject(tree.getItems(), xobj, list);
				itemFocus = list.isEmpty() ? null : list.get(0);
				if (itemFocus == null)return;
				TreeExpansionEvent event = new TreeExpansionEvent(view.getViewer(), xobj);
				if (value.equals(EXPAND_VALUE))
				{
					//set the category to be closed if it is empty
					if (elemCategory.elements().isEmpty())
					{
						itemFocus.setExpanded(false);
						treeCollapsed(event);
					}
					else
					{
						itemFocus.setExpanded(true);
						treeExpanded(event);
					}
				}
				else
				{
					itemFocus.setExpanded(false);
					treeCollapsed(event);
				}
				itemFocus = null;	
			}
		}
    }
    
    public void resetAllCategoryState(TreeParent root)
    {
    	for (TreeObject xobj : root.getChildren()) {
			if (xobj.getType() == TreeObject.CATEGORY_FOLDER) {
				locateCategoryElement((TreeParent) xobj).attribute(EXPAND_NAME)
						.setValue(COLLAPSE_VALUE);
			}

			if (xobj instanceof TreeParent
					&& xobj.getType() != TreeObject.CATEGORY_FOLDER) {
				resetAllCategoryState((TreeParent) xobj);
			}
		}
    	
    	saveDocument(doc);
    }
    
	public void receiveAllOffsprings(TreeParent parent, ArrayList<TreeObject>list)
	{
		list.addAll(Arrays.asList(parent.getChildren()));
		for(TreeObject obj: parent.getChildren())
		{
			if (obj instanceof TreeParent)
			{
				receiveAllOffsprings((TreeParent)obj, list);
			}
		}
	}
}
