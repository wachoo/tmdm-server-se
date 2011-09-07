package org.talend.mdm.webapp.browserecords.client.model;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;


public class ItemNodeModel extends BaseTreeModel implements Cloneable {

    private static final long serialVersionUID = 1L;

    private static int ID = 0;

    private String description;

    private String value;

    private String bindingPath;

    private Object objectValue;

    private boolean isKey;

    public boolean isKey() {
        return isKey;
    }

    public void setKey(boolean isKey) {
        this.isKey = isKey;
    }

    public Object getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(Object objectValue) {
        this.objectValue = objectValue;
    }
    public ItemNodeModel() {
        set("id", ID++); //$NON-NLS-1$

    }
    public ItemNodeModel(String name) {
        set("id", ID++); //$NON-NLS-1$
        set("name", name); //$NON-NLS-1$
    }
    public ItemNodeModel(String name, BaseTreeModel[] children) {
        this(name);
        for (int i = 0; i < children.length; i++) {
            add(children[i]);
        }
    }

    public String toString() {
        return getName();
    }

    public String toValue() {
        StringBuffer sb = new StringBuffer();
        sb.append(objectValue != null ? objectValue.toString() : ""); //$NON-NLS-1$
        for (ModelData model : this.getChildren()) {
            ItemNodeModel node = (ItemNodeModel) model;
            sb.append("+\r\n"); //$NON-NLS-1$
            sb.append(node.toValue() + "---"); //$NON-NLS-1$
        }
        return sb.toString();
    }

    public Integer getId() {
        return (Integer) get("id"); //$NON-NLS-1$
    }

    public String getName() {
        return get("name"); //$NON-NLS-1$
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getBindingPath() {
        // StringBuffer xp = new StringBuffer();
        // List<String> paths = new ArrayList<String>();
        // TreeModel parent = this;
        //
        // while (parent != null) {
        //            paths.add((String) parent.get("name")); //$NON-NLS-1$
        // parent = parent.getParent();
        // }
        //
        // for (int i = paths.size() - 1; i >= 0; i--) {
        // if (i != paths.size() - 1)
        //                xp.append("/"); //$NON-NLS-1$
        // xp.append(paths.get(i));
        // }
        // return xp.toString();
        return bindingPath;
    }

    public void setBindingPath(String bindingPath) {
        this.bindingPath = bindingPath;
    }

    public void setName(String name) {
        set("name", name); //$NON-NLS-1$
    }

    private ArrayList<String> primaryKeyInfo;

    public ArrayList<String> getPrimaryKeyInfo() {
        return primaryKeyInfo;
    }

    public void setPrimaryKeyInfo(ArrayList<String> primaryKeyInfo) {
        this.primaryKeyInfo = primaryKeyInfo;
    }

    private ArrayList<String> foreignKeyInfo;

    public ArrayList<String> getForeignKeyInfo() {
        return foreignKeyInfo;
    }

    public void setForeignKeyInfo(ArrayList<String> foreignKeyInfo) {
        this.foreignKeyInfo = foreignKeyInfo;
    }

    public void setChildNodes(List<ItemNodeModel> defaultTreeModel) {
        removeAll();
        if (defaultTreeModel != null) {
            for (ModelData child : defaultTreeModel) {
                add(child);
            }
        }
    }
}