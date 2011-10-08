package org.talend.mdm.webapp.browserecords.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.google.gwt.user.client.rpc.IsSerializable;


public class ItemNodeModel extends BaseTreeModel implements IsSerializable {

    private static final long serialVersionUID = 1L;

    private static int ID = 0;

    private String description;

    private String label;

    private String bindingPath;

    private Serializable objectValue;

    private boolean isKey;
    
    private String dynamicLabel;
    
    private String realType;

    private boolean visible = true;
    
    private boolean valid = false;	

    private boolean hasVisiblueRule = false;
    
    private boolean isCloned = false;

    public boolean isHasVisiblueRule() {
    	if(hasVisiblueRule) {
    		return true;
    	}
    	
    	List<ModelData> child = getChildren();
    	
    	for(ModelData model : child) {
    		if(((ItemNodeModel) model).isHasVisiblueRule()) {
    			return true;
    		}
    	}
    	
		return hasVisiblueRule;
	}

	public void setHasVisiblueRule(boolean hasVisiblueRule) {
		this.hasVisiblueRule = hasVisiblueRule;
	}

	public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

    private boolean isChangeValue;

    public boolean isChangeValue() {
        return isChangeValue;
    }

    public void setChangeValue(boolean isChangeValue) {
        this.isChangeValue = isChangeValue;
    }

    public String getDynamicLabel() {
		return dynamicLabel;
	}

	public void setDynamicLabel(String dynamicLabel) {
		this.dynamicLabel = dynamicLabel;
	}

	public boolean isKey() {
        return isKey;
    }
	
    public void setKey(boolean isKey) {
        this.isKey = isKey;
    }
    
    public Serializable getObjectValue() {
        return objectValue;
    }
    
    public void setObjectValue(Serializable objectValue) {
        this.objectValue = objectValue;
    }
    
    public ItemNodeModel() {
        set("id", ID++); //$NON-NLS-1$
    }
    
    public ItemNodeModel(String name) {
        set("id", ID++); //$NON-NLS-1$
        set("name", name); //$NON-NLS-1$
    }

    public String toString() {
        return _toString(""); //$NON-NLS-1$
    }

    private String _toString(String pre) {
        StringBuffer sb = new StringBuffer();
        sb.append(pre + getName() + "\n"); //$NON-NLS-1$
        List<ModelData> children = this.getChildren();
        if (children != null) {
            for (ModelData child : children) {
                ItemNodeModel childModel = (ItemNodeModel) child;
                sb.append(childModel._toString(pre + "    ")); //$NON-NLS-1$
            }
        }
        return sb.toString();
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

    public String getBindingPath() {
        if (this.bindingPath == null) {
            StringBuffer xp = new StringBuffer();
            List<String> paths = new ArrayList<String>();
            TreeModel parent = this;

            while (parent != null) {
                paths.add((String) parent.get("name")); //$NON-NLS-1$
                parent = parent.getParent();
            }

            for (int i = paths.size() - 1; i >= 0; i--) {
                if (i != paths.size() - 1)
                    xp.append("/"); //$NON-NLS-1$
                xp.append(paths.get(i));
            }
            bindingPath = xp.toString();
        }
        return bindingPath;

    }

    public void setBindingPath(String bindingPath) {
        this.bindingPath = bindingPath;
    }

    public void setName(String name) {
        set("name", name); //$NON-NLS-1$
    }

    public void setChildNodes(List<ItemNodeModel> defaultTreeModel) {
        removeAll();
        if (defaultTreeModel != null) {
            for (ModelData child : defaultTreeModel) {
                add(child);
            }
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getRealType() {
        return realType;
    }

    public void setRealType(String realType) {
        this.realType = realType;
    }

    public boolean isCloned() {
        return isCloned;
    }

    public void setCloned(boolean isCloned) {
        this.isCloned = isCloned;
    }


    public ItemNodeModel clone(boolean withValue) {
        ItemNodeModel clonedModel = new ItemNodeModel(get("name").toString()); //$NON-NLS-1$
        clonedModel.setBindingPath(getBindingPath());
        List<ItemNodeModel> clonedList = new ArrayList<ItemNodeModel>();
        for (ModelData data : this.getChildren()) {
            ItemNodeModel clonedData = ((ItemNodeModel) data).clone(withValue);
            clonedList.add(clonedData);
        }
        clonedModel.setChildNodes(clonedList);
        clonedModel.setLabel(this.label);
        clonedModel.setDescription(description);
        clonedModel.setDynamicLabel(dynamicLabel);
        clonedModel.setParent(this.getParent());
        clonedModel.setCloned(true);
        if (this.getRealType() != null)
            clonedModel.setRealType(this.getRealType());
        if (withValue)
            clonedModel.setObjectValue(objectValue);
        return clonedModel;
    }

}