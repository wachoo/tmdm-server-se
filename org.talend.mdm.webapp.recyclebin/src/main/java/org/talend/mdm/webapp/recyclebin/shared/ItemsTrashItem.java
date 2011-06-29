package org.talend.mdm.webapp.recyclebin.shared;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;


public class ItemsTrashItem extends BaseModelData implements IsSerializable {

    private String itemPK;

	private String uniqueId;

	private String conceptName;

	private String ids;

	private String partPath;

	private String insertionUserName;

	private String insertionTime;

	private String projection;

	private String revisionID;
    
    
    public ItemsTrashItem() {
		super();
	}

    public ItemsTrashItem(String conceptName, String ids, String insertionTime, String insertionUserName, String itemPK,
            String partPath, String projection, String revisionID, String uniqueId) {
		this.conceptName = conceptName;
		this.ids = ids;
		this.insertionTime = insertionTime;
		this.insertionUserName = insertionUserName;
		this.itemPK = itemPK;
		this.partPath = partPath;
		this.projection = projection;
		this.revisionID = revisionID;
		this.uniqueId = uniqueId;
        set("conceptName", conceptName);
        set("ids",ids);
        set("insertionTime",insertionTime);
        set("insertionUserName",insertionUserName);
        set("itemPK", itemPK);
        set("partPath", partPath);
        set("projection", projection);
        set("revisionID", revisionID);
        set("uniqueId", uniqueId);
	}

    public String getRevisionID() {
		return revisionID;
	}

    public void setRevisionID(String revisionID) {
		this.revisionID = revisionID;
	}

	public String getItemPK() {
		return itemPK;
	}

	public void setItemPK(String itemPK) {
		this.itemPK = itemPK;
	}

    public String getUniqueId() {
		return uniqueId;
	}

    public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

    public String getConceptName() {
		return conceptName;
	}

    public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

    public String getIds() {
        return ids;
	}

    public void setIds(String ids) {
		this.ids = ids;
	}

    public String getPartPath() {
		return partPath;
	}

    public void setPartPath(String partPath) {
		this.partPath = partPath;
	}

    public String getInsertionUserName() {
		return insertionUserName;
	}

    public void setInsertionUserName(String insertionUserName) {
		this.insertionUserName = insertionUserName;
	}

	public String getInsertionTime() {
		return insertionTime;
	}

	public void setInsertionTime(String insertionTime) {
		this.insertionTime = insertionTime;
	}

    public String getProjection() {
		return projection;
	}

    public void setProjection(String projection) {
		this.projection = projection;
    }
}
