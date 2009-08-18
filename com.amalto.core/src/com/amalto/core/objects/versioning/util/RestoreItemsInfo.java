package com.amalto.core.objects.versioning.util;

import java.io.Serializable;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJOPK;

/**
 * Used to tag objects in Background Jobs
 * @author bgrieder
 *
 */
public class RestoreItemsInfo implements Serializable {
	private VersioningSystemPOJOPK versioningSystemPOJOPK;
	private String tag;
	private String username;
	private ItemPOJOPK[] itemPKs;
	
	public RestoreItemsInfo() {
		super();
	}

	
	
	public RestoreItemsInfo(VersioningSystemPOJOPK versioningSystemPOJOPK, String tag, String username, ItemPOJOPK[] itemPKs) {
		super();
		this.versioningSystemPOJOPK = versioningSystemPOJOPK;
		this.tag = tag;
		this.username = username;
		this.itemPKs = itemPKs;
	}


	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public VersioningSystemPOJOPK getVersioningSystemPOJOPK() {
		return versioningSystemPOJOPK;
	}

	public void setVersioningSystemPOJOPK(
			VersioningSystemPOJOPK versioningSystemPOJOPK) {
		this.versioningSystemPOJOPK = versioningSystemPOJOPK;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ItemPOJOPK[] getItemPKs() {
		return itemPKs;
	}

	public void setItemPKs(ItemPOJOPK[] itemPKs) {
		this.itemPKs = itemPKs;
	}
	
	
	
}