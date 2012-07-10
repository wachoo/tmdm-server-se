package org.talend.mdm.webapp.browserecords.client.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class CommonUtil {

    public static final String XMLNS_TMDM = "xmlns:tmdm"; //$NON-NLS-1$

    public static final String XMLNS_TMDM_VALUE = "http://www.talend.com/mdm"; //$NON-NLS-1$

    
    private static final String BEFORE_DOMAIN = "\\b((https?|ftp)://)"; //$NON-NLS-1$
    private static final String PATH = "(/[-a-z0-9A-Z_:@&?=+,.!/~*'%#$]*)*"; //$NON-NLS-1$

    
    public static String getHost(String url) {
        String host = url.replaceAll(BEFORE_DOMAIN, "").replaceAll(PATH, ""); //$NON-NLS-1$ //$NON-NLS-2$
        return host;
    }

    public static String getXpathSuffix(String xpath) {
        return xpath.substring(xpath.lastIndexOf('/') + 1);
    }

    public static String getElementFromXpath(String xpath) {
        String[] arr = xpath.split("/");//$NON-NLS-1$
        for (int i = arr.length - 1; i > -1; i--) {
            if (arr[i] != "")//$NON-NLS-1$
                return arr[i];
        }
        return xpath;
    }

    public static String getRealTypePath(ItemNodeModel nodeModel) {
        String realXPath = ""; //$NON-NLS-1$
        ItemNodeModel current = nodeModel;
        boolean isFirst = true;
        while (current != null) {
            String name;
            if (current.getRealType() != null) {
                name = current.getName() + ":" + current.getRealType(); //$NON-NLS-1$
            } else {
                name = current.getName();
            }

            current = (ItemNodeModel) current.getParent();
            if (isFirst) {
                realXPath = name;
                isFirst = false;
                continue;
            }
            realXPath = name + "/" + realXPath; //$NON-NLS-1$

        }
        return realXPath;
    }

    public static String getRealXPath(ItemNodeModel nodeModel) {
        String realXPath = ""; //$NON-NLS-1$
        ItemNodeModel current = nodeModel;
        boolean isFirst = true;
        while (current != null) {
            String name = getNodeNameWithIndex(current);
            current = (ItemNodeModel) current.getParent();
            if (isFirst) {
                realXPath = name;
                isFirst = false;
                continue;
            }
            realXPath = name + "/" + realXPath; //$NON-NLS-1$

        }
        return realXPath;
    }

    private static String getNodeNameWithIndex(ItemNodeModel nodeModel) {
        ItemNodeModel parent = (ItemNodeModel) nodeModel.getParent();
        if (parent != null) {
            List<ModelData> children = parent.getChildren();
            int index = 0;
            for (ModelData child : children) {
                ItemNodeModel childModel = (ItemNodeModel) child;
                if (childModel.getName().equals(nodeModel.getName())) {
                    index++;
                    if (childModel == nodeModel) {
                        return nodeModel.getName() + "[" + index + "]"; //$NON-NLS-1$//$NON-NLS-2$
                    }
                }
            }
        }
        return nodeModel.getName();
    }

    public static String toXML(ItemNodeModel nodeModel, ViewBean viewBean) {
        return toXML(nodeModel, viewBean, false);
    }

    public static String toXML(ItemNodeModel nodeModel, ViewBean viewBean, boolean isAll) {
        if (nodeModel == null)
            return null;
        Document doc = XMLParser.createDocument();
        Element root = _toXML(doc, nodeModel, viewBean, nodeModel, isAll);
        if (nodeModel.get(XMLNS_TMDM) != null)
            root.setAttribute(XMLNS_TMDM, XMLNS_TMDM_VALUE);
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$//$NON-NLS-2$
        doc.appendChild(root);
        return doc.toString();
    }

    private static Element _toXML(Document doc, ItemNodeModel nodeModel, ViewBean viewBean, ItemNodeModel rootModel, boolean isAll) {
        Element root = doc.createElement(nodeModel.getName());
        TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(nodeModel.getTypePath());
        if (!isAll) {
            if (nodeModel.getParent() != null) {
                if (!nodeModel.isKey()) {
                    if (!typeModel.isVisible()) {
                        return null;
                    }
                }
            }
        }
        Serializable value = nodeModel.getObjectValue();

        if (typeModel.isSimpleType()) {
            if (value != null && nodeModel.getParent() != null) {
                String elValue = null;
                if (value instanceof ForeignKeyBean) {
                    elValue = ((ForeignKeyBean) value).getId();
                } else {
                    elValue = value.toString();
                }
                root.appendChild(doc.createTextNode(elValue));
                return root;
            }
        }

        if (nodeModel.getRealType() != null) {
            root.setAttribute("xsi:type", nodeModel.getRealType()); //$NON-NLS-1$
        }

        if (nodeModel.getTypeName() != null) {
            root.setAttribute("tmdm:type", nodeModel.getTypeName()); //$NON-NLS-1$
            rootModel.set(XMLNS_TMDM, XMLNS_TMDM_VALUE);
        }

        List<ModelData> children = nodeModel.getChildren();
        if (children != null && children.size() > 0) {
            for (ModelData child : children) {
                Element el = _toXML(doc, (ItemNodeModel) child, viewBean, rootModel, isAll);
                if (el != null) {
                    root.appendChild(el);
                }
            }
            if (!isAll) {
                if (childrenEl(root).size() == 0) {
                    return null;
                }
                mergerChildrenWhenEmpty(root);
            }
        }
        return root;
    }

    public static int getCountOfBrotherOfTheSameName(ItemNodeModel nodeModel) {
        int count = 0;
        String name = nodeModel.getName();
        ItemNodeModel parentModel = (ItemNodeModel) nodeModel.getParent();
        if (parentModel == null)
            return 1;
        for (int i = 0; i < parentModel.getChildCount(); i++) {
            ItemNodeModel childModel = (ItemNodeModel) parentModel.getChild(i);
            if (name.equals(childModel.getName())) {
                count++;
            }
        }
        return count;
    }

    public static boolean hasChildrenValue(ItemNodeModel parentModel) {
        List<ModelData> childs = parentModel.getChildren();
        if (childs != null && childs.size() > 0) {
            for (int i = 0; i < childs.size(); i++) {
                ItemNodeModel child = (ItemNodeModel) childs.get(i);
                if (hasChildrenValue(child)) {
                    return true;
                } else {
                    continue;
                }
            }
        } else {
            Serializable value = parentModel.getObjectValue();
            if (value != null) {
                if (value instanceof ForeignKeyBean) {
                    ForeignKeyBean fkBean = (ForeignKeyBean) value;
                    return fkBean.getId() != null && fkBean.getId().trim().length() > 0;
                } else {
                    return !"".equals(value); //$NON-NLS-1$
                }
            }
        }
        return false;
    }

    public static String getRealXpathWithoutLastIndex(ItemNodeModel nodeModel) {
        return getRealXPath(nodeModel).replaceAll("\\[\\d+\\]$", ""); //$NON-NLS-1$//$NON-NLS-2$
    }

    public static String getRealXpathWithoutLastIndex(String realPath) {
        return realPath.replaceAll("\\[\\d+\\]$", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static void mergerChildrenWhenEmpty(Element el) {
        Map<String, List<Element>> childrenGroup = childrenElGroup(el);
        Set<String> keySet = childrenGroup.keySet();
        for (String key : keySet) {
            List<Element> group = childrenGroup.get(key);
            if (isEmptyValueEls(group)) {
                removeRedundantChildren(group);
            }
        }
    }

    private static void removeRedundantChildren(List<Element> childrenEl) {
        for (int i = 1; i < childrenEl.size(); i++) {
            Element el = childrenEl.get(i);
            el.getParentNode().removeChild(el);
        }
    }

    private static boolean isEmptyValueEls(List<Element> els) {
        for (Element el : els) {
            if (!isEmptyValueEl(el)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isEmptyValueEl(Element el) {
        List<Element> childrenEl = childrenEl(el);
        if (childrenEl.size() == 0) {
            Node node = el.getFirstChild();
            return node == null || node.getNodeValue() == null || node.getNodeValue().trim().length() == 0;
        }

        for (Element childEl : childrenEl) {
            if (!isEmptyValueEl(childEl)) {
                return false;
            }
        }
        return true;
    }

    private static List<Element> childrenEl(Element el) {
        List<Element> els = new ArrayList<Element>();
        NodeList childNodes = el.getChildNodes();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    els.add((Element) child);
                }
            }
        }
        return els;
    }

    private static Map<String, List<Element>> childrenElGroup(Element el) {
        Map<String, List<Element>> childrenGroup = new LinkedHashMap<String, List<Element>>();
        List<Element> childrenEl = childrenEl(el);
        for (Element childEl : childrenEl) {
            List<Element> group = childrenGroup.get(childEl.getNodeName());
            if (group == null) {
                group = new ArrayList<Element>();
                childrenGroup.put(childEl.getNodeName(), group);
            }
            group.add(childEl);
        }
        return childrenGroup;
    }

    public static List<ItemNodeModel> getDefaultTreeModel(TypeModel model, String language) {
        List<ItemNodeModel> itemNodes = new ArrayList<ItemNodeModel>();

        if (model.getMinOccurs() > 1) {
            for (int i = 0; i < model.getMinOccurs(); i++) {
                ItemNodeModel itemNode = new ItemNodeModel();

                itemNodes.add(itemNode);
                if (model.getForeignkey() != null)
                    break;
            }
        } else {
            ItemNodeModel itemNode = new ItemNodeModel();
            itemNodes.add(itemNode);
        }

        for (ItemNodeModel node : itemNodes) {
            if (model.getMinOccurs() > 0) {
                node.setMandatory(true);
            }
            if (model.isSimpleType()) {
                setDefaultValue(model, node);
            } else {
                ComplexTypeModel complexModel = (ComplexTypeModel) model;
                List<TypeModel> children = complexModel.getSubTypes();
                List<ItemNodeModel> list = new ArrayList<ItemNodeModel>();
                for (TypeModel typeModel : children) {
                    list.addAll(getDefaultTreeModel(typeModel, language));
                }
                node.setChildNodes(list);
            }
            node.setName(model.getName());
            node.setTypePath(model.getTypePath());
            node.setDescription(model.getDescriptionMap().get(language));
            node.setLabel(model.getLabel(language));
        }
        return itemNodes;
    }

    private static void setDefaultValue(TypeModel model, ItemNodeModel node) {

        if (model.getDefaultValueExpression() != null && model.getDefaultValueExpression().trim().length() > 0) {
            if (!"".equals(model.getForeignkey()) && model.getForeignkey() != null) { //$NON-NLS-1$
                ForeignKeyBean foreignKeyBean = new ForeignKeyBean();
                foreignKeyBean.setId(model.getDefaultValue());
                foreignKeyBean.setForeignKeyPath(model.getForeignkey());
                node.setObjectValue(foreignKeyBean);
            } else {
                node.setObjectValue(model.getDefaultValue());
            }
            node.setChangeValue(true);
        } else {
            if (model.getType().getTypeName().equals(DataTypeConstants.BOOLEAN.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.BOOLEAN.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DATE.getTypeName())) {
                String dateStr = DateUtil.getDate((Date) DataTypeConstants.DATE.getDefaultValue());
                node.setObjectValue(dateStr);
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DATETIME.getTypeName())) {
                String dateStr = DateUtil.getDateTime((Date) DataTypeConstants.DATETIME.getDefaultValue());
                node.setObjectValue(dateStr);
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DECIMAL.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.DECIMAL.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DOUBLE.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.DOUBLE.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.FLOAT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.FLOAT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.INT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.INT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.INTEGER.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.INTEGER.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.LONG.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.LONG.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.SHORT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.SHORT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.STRING.getTypeName())) {
                if (model.getForeignkey() != null && model.getForeignkey().trim().length() > 0) {
                    ForeignKeyBean foreignKeyBean = new ForeignKeyBean();
                    foreignKeyBean.setId(""); //$NON-NLS-1$
                    foreignKeyBean.setForeignKeyPath(model.getForeignkey());
                    node.setObjectValue(foreignKeyBean);
                } else {
                    node.setObjectValue((Serializable) DataTypeConstants.STRING.getDefaultValue());
                }
            } else if (model.getType().getTypeName().equals(DataTypeConstants.UUID.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.UUID.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.AUTO_INCREMENT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.AUTO_INCREMENT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.PICTURE.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.PICTURE.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.URL.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.URL.getDefaultValue());
            }
        }
    }

    public static ItemNodeModel recrusiveRoot(ItemNodeModel node) {
        ItemNodeModel parent = (ItemNodeModel) node.getParent();
        ItemNodeModel root = null;

        if (parent != null && parent.getParent() != null) {
            root = recrusiveRoot(parent);
        } else {
            root = parent;
        }

        return root;
    }

    public static boolean isSimpleCriteria(String criteria) {
        if (criteria.indexOf(" AND ") == -1 && criteria.indexOf(" OR ") == -1) { //$NON-NLS-1$//$NON-NLS-2$
            return true;
        }

        return false;
    }

    public static boolean validateSearchValue(Map<String, TypeModel> metaDataTypeMap, String value) {
        if (!value.contains("/")) //$NON-NLS-1$
        {
            return true;
        } else if (isString(value)) {
            return true;
        } else if (isPath(metaDataTypeMap, value)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isString(String value) {
        if (((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\"")))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            return true;
        } else {
            return false;
        }
    }

    private static boolean isPath(Map<String, TypeModel> metaDataTypeMap, String value) {
        if (!value.endsWith("/")) //$NON-NLS-1$
        {
            value = (value.startsWith("/") ? value.substring(1) : value); //$NON-NLS-1$
            if (metaDataTypeMap.get(value) != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Parsed a file name to a String array, the first object is the short file name, the second one is extension name
     * Return null if argument illegal
     * 
     * @param longFilePath
     * @return
     */
    public static String[] parseFileName(String longFilePath) {

        if (longFilePath == null || longFilePath.trim().length() == 0)
            return null;

        String shortFileName = "";
        String fileExtensioName = "";
        String shortpath = "";

        String[] tokenizer = longFilePath.split("[\\\\|/]");

        if (tokenizer != null && tokenizer.length > 0)
            shortpath = tokenizer[tokenizer.length - 1];

        if (shortpath.indexOf(".") != -1) {
            int pos = shortpath.lastIndexOf(".");
            shortFileName = shortpath.substring(0, pos);
            fileExtensioName = shortpath.substring(pos + 1);
        } else {
            shortFileName = shortpath;
        }

        String[] parsedResult = { shortFileName, fileExtensioName };
        return parsedResult;
    }

    public static String typePathToXpath(String typePath) {
        String[] paths = typePath.split("/"); //$NON-NLS-1$
        StringBuffer result = new StringBuffer();
        boolean isFirst = true;
        for (String path : paths) {
            String[] part = path.split(":"); //$NON-NLS-1$
            String xpart;
            if (part.length == 1) {
                xpart = part[0];
            } else {
                xpart = part[0] + "[@xsi:type='" + part[1] + "']"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (isFirst) {
                result.append(xpart);
                isFirst = false;
            } else {
                result.append("/" + xpart); //$NON-NLS-1$
            }

        }
        return result.toString();
    }

    public static boolean isReadOnlyOnUI(TypeModel typeModel) {
        TypeModel currentType = typeModel;
        boolean isReadOnly = false;
        while (currentType != null) {
            if (currentType.isReadOnly()) {
                isReadOnly = true;
                break;
            }
            currentType = currentType.getParentTypeModel();
        }
        return isReadOnly;
    }

    public static String[] extractIDs(ItemNodeModel model) {

        List<String> keys = new Vector<String>();

        if (model != null) {
            List<ModelData> modelChildren = model.getChildren();

            if (modelChildren != null) {

                for (ModelData data : modelChildren) {

                    if (data instanceof ItemNodeModel) {

                        ItemNodeModel childModel = (ItemNodeModel) data;
                        if (childModel.isKey()) {

                            Object val = childModel.getObjectValue();
                            if (val != null) {

                                String valString = val.toString();
                                if (valString != null && !valString.isEmpty()) {
                                    keys.add(valString);
                                }
                            }
                        }
                    }
                }
            }
        }

        String[] keyArray = keys.toArray(new String[] {});

        return keyArray;
    }
}
