package org.talend.mdm.webapp.browserecords.client.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

public class CommonUtil {

    public static final String XMLNS_TMDM = "xmlns:tmdm"; //$NON-NLS-1$

    public static final String XMLNS_TMDM_VALUE = "http://www.talend.com/mdm"; //$NON-NLS-1$

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
        Document doc = XMLParser.createDocument();
        Element root = _toXML(doc, nodeModel, viewBean, nodeModel);
        if (nodeModel.get(XMLNS_TMDM) != null)
            root.setAttribute(XMLNS_TMDM, XMLNS_TMDM_VALUE);
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$//$NON-NLS-2$
        doc.appendChild(root);
        return doc.toString();
    }

    private static Element _toXML(Document doc, ItemNodeModel nodeModel, ViewBean viewBean, ItemNodeModel rootModel) {
        Element root = doc.createElement(nodeModel.getName());
        TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(nodeModel.getTypePath());
        Serializable value = nodeModel.getObjectValue();
        if (typeModel.isSimpleType() && value != null && nodeModel.getParent() != null) {
            if (value instanceof ForeignKeyBean)
                root.appendChild(doc.createTextNode(((ForeignKeyBean) value).getId()));
            else
                root.appendChild(doc.createTextNode(value.toString()));
        }

        if (nodeModel.getRealType() != null) {
            root.setAttribute("xsi:type", nodeModel.getRealType()); //$NON-NLS-1$
        }

        if (nodeModel.getTypeName() != null) {
            root.setAttribute("tmdm:type", nodeModel.getTypeName()); //$NON-NLS-1$
            rootModel.set(XMLNS_TMDM, XMLNS_TMDM_VALUE);
        }

        List<ModelData> children = nodeModel.getChildren();
        if (children != null) {
            for (ModelData child : children) {
                Element el = _toXML(doc, (ItemNodeModel) child, viewBean, rootModel);
                root.appendChild(el);
            }
        }
        return root;
    }

    public static List<ItemNodeModel> getDefaultTreeModel(TypeModel model, String language) {
        List<ItemNodeModel> itemNodes = new ArrayList<ItemNodeModel>();

        if (model.getMinOccurs() > 1 && model.getMaxOccurs() > model.getMinOccurs()) {
            for (int i = 0; i < model.getMaxOccurs() - model.getMinOccurs(); i++) {
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
            node.setBindingPath(model.getXpath());
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

    public static boolean isLetter(char c) {
        boolean result = ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');

        return result;
    }

    public static String pickOutISOMessage(String errorString) {

        return pickOutISOMessage(errorString, Locale.getLanguage());
    }
    
    /**
     * Parse a multiple language string and return the message corresponding to the current language.
     * 
     * errorString is expected to be in the following format:
     * 
     * [en:...][fr:...][zh:...]
     * 
     * Characters ] and \ can be escaped in these using backslash escapes, for example
     * 
     * [en: a message with a \] character in the middle]
     * 
     * A message for a language can also be embedded anywhere in the string, for example
     * 
     * abcd[en:...]abcd[fr:...]abcd
     * 
     * If a message for the current language exists, then it is returned.
     * 
     * If a message for the current language doesn't exist, but an english message exists, then it is returned.
     * 
     * If neither of the above are true, then no parsing takes place, and the original errorString is returned.
     * 
     * @param errorString Multiple language message string to be parsed
     * @param lang Language code of the desired message
     * @return Message corresponding to the current language
     */
    public static String pickOutISOMessage(String errorString, String lang) {

        if (errorString != null && lang != null) {
            // Parse states
            final byte PARSE_ERROR = 0;
            final byte LOOKING_FOR_OPENING_BRACKET = 1;
            final byte LOOKING_FOR_COUNTRY_CODE_FIRST_CHAR = 2;
            final byte LOOKING_FOR_COUNTRY_CODE_SECOND_CHAR = 3;
            final byte LOOKING_FOR_COLON = 4;
            final byte LOOKING_FOR_CLOSING_BRACKET = 5;
            final byte ENCOUNTERED_FIRST_BACKSLASH = 6;

            byte parseState = LOOKING_FOR_OPENING_BRACKET;
            // string buffer for constructing current country code
            StringBuffer countryCodeBuffer = new StringBuffer();
            // string buffer for constructing current error message
            StringBuffer errorMessageBuffer = new StringBuffer();
            // map between country code and message
            Map<String, String> errorMessageHash = new HashMap<String, String>();

            int i = 0;
            int errorStringLen = errorString.length();
            for (i = 0; i < errorStringLen && parseState != PARSE_ERROR; ++i) {
                char c = errorString.charAt(i);

                switch (parseState) {
                case LOOKING_FOR_OPENING_BRACKET:
                    if (c == '[') {
                        parseState = LOOKING_FOR_COUNTRY_CODE_FIRST_CHAR;
                    }
                    break;
                case LOOKING_FOR_COUNTRY_CODE_FIRST_CHAR:
                    if (isLetter(c)) {
                        countryCodeBuffer.append(c);
                        parseState = LOOKING_FOR_COUNTRY_CODE_SECOND_CHAR;
                    } else {
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    }
                    break;
                case LOOKING_FOR_COUNTRY_CODE_SECOND_CHAR:
                    if (isLetter(c)) {
                        countryCodeBuffer.append(c);
                        parseState = LOOKING_FOR_COLON;
                    } else {
                        countryCodeBuffer = new StringBuffer();
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    }
                    break;
                case LOOKING_FOR_COLON:
                    if (c == ':') {
                        parseState = LOOKING_FOR_CLOSING_BRACKET;
                    } else {
                        countryCodeBuffer = new StringBuffer();
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    }
                    break;
                case LOOKING_FOR_CLOSING_BRACKET:
                    if (c == ']') {
                        errorMessageHash.put(countryCodeBuffer.toString().toLowerCase(), errorMessageBuffer.toString());
                        countryCodeBuffer = new StringBuffer();
                        errorMessageBuffer = new StringBuffer();
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    } else if (c == '\\') {
                        parseState = ENCOUNTERED_FIRST_BACKSLASH;
                    } else {
                        errorMessageBuffer.append(c);
                    }
                    break;
                case ENCOUNTERED_FIRST_BACKSLASH:
                    if (c == '\\' || c == ']') {
                        errorMessageBuffer.append(c);
                    }
                    parseState = LOOKING_FOR_CLOSING_BRACKET;
                    break;
                default:
                    parseState = PARSE_ERROR;
                }
            }

            String resultingErrorMessage = errorString;
            String langCode = lang.toLowerCase();
            if (errorMessageHash.containsKey(langCode)) {
                resultingErrorMessage = errorMessageHash.get(langCode);
            } else if (errorMessageHash.containsKey("en")) { //$NON-NLS-1$
                resultingErrorMessage = errorMessageHash.get("en"); //$NON-NLS-1$
            }

            return resultingErrorMessage;

        } else {

            return null;
        }
    }

    public static boolean isSimpleCriteria(String criteria) {
        if (criteria.indexOf(" AND ") == -1 && criteria.indexOf(" OR ") == -1) { //$NON-NLS-1$//$NON-NLS-2$
            return true;
        }

        return false;
    }
}
