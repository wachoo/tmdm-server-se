package com.amalto.webapp.v3.itemstrash.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDroppedItem;

public class ItemsTrashItem {
	protected String itemPK;
    protected java.lang.String uniqueId;
    protected java.lang.String conceptName;
    protected java.lang.String ids;
    protected java.lang.String partPath;
    protected java.lang.String insertionUserName;
    protected java.lang.String insertionTime;
    protected java.lang.String projection;
    protected java.lang.String revisionID;
    
    
    public ItemsTrashItem() {
		super();
	}
	public ItemsTrashItem(String conceptName, String ids, String insertionTime,
			String insertionUserName, String itemPK, String partPath,
			String projection, String revisionID, String uniqueId) {
		super();
		this.conceptName = conceptName;
		this.ids = ids;
		this.insertionTime = insertionTime;
		this.insertionUserName = insertionUserName;
		this.itemPK = itemPK;
		this.partPath = partPath;
		this.projection = projection;
		this.revisionID = revisionID;
		this.uniqueId = uniqueId;
	}
	public java.lang.String getRevisionID() {
		return revisionID;
	}
	public void setRevisionID(java.lang.String revisionID) {
		this.revisionID = revisionID;
	}
	public String getItemPK() {
		return itemPK;
	}
	public void setItemPK(String itemPK) {
		this.itemPK = itemPK;
	}
	public java.lang.String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(java.lang.String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public java.lang.String getConceptName() {
		return conceptName;
	}
	public void setConceptName(java.lang.String conceptName) {
		this.conceptName = conceptName;
	}
	public java.lang.String getIds() {
		return ids;
	}
	public void setIds(java.lang.String ids) {
		this.ids = ids;
	}
	public java.lang.String getPartPath() {
		return partPath;
	}
	public void setPartPath(java.lang.String partPath) {
		this.partPath = partPath;
	}
	public java.lang.String getInsertionUserName() {
		return insertionUserName;
	}
	public void setInsertionUserName(java.lang.String insertionUserName) {
		this.insertionUserName = insertionUserName;
	}
	public String getInsertionTime() {
		return insertionTime;
	}
	public void setInsertionTime(String insertionTime) {
		this.insertionTime = insertionTime;
	}
	public java.lang.String getProjection() {
		return projection;
	}
	public void setProjection(java.lang.String projection) {
		this.projection = projection;
	}
	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	public ItemsTrashItem WS2POJO(WSDroppedItem item){
		ItemsTrashItem pojo = new ItemsTrashItem(
				item.getConceptName(),
				Util.joinStrings(item.getIds(), "."),
				df.format(new Date(item.getInsertionTime())),
				item.getInsertionUserName(),
				item.getWsDataClusterPK().getPk(),
				item.getPartPath(),
				item.getProjection(),
				item.getRevisionID(),
				item.getUniqueId()
				);
		
//		pojo.setConceptName(item.getConceptName());
//		pojo.setIds(Util.joinStrings(item.getIds(), "."));
//		pojo.setInsertionTime(item.getInsertionTime());
//		pojo.setInsertionUserName(item.getInsertionUserName());
//		pojo.setItemPK(new ItemPK(item.getWsDataClusterPK().getPk()));
//		pojo.setPartPath(item.getPartPath());
//		pojo.setProjection(item.getProjection());
//		pojo.setRevisionID(item.getRevisionID());
//		pojo.setUniqueId(item.getUniqueId());
		return pojo;
	}
	
}
