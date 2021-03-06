/*
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package com.amalto.core.webservice;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="WSGetItemsSort")
public class WSGetItemsSort {
    protected com.amalto.core.webservice.WSDataClusterPK wsDataClusterPK;
    protected java.lang.String conceptName;
    protected com.amalto.core.webservice.WSWhereItem whereItem;
    protected int spellTreshold;
    protected int skip;
    protected int maxItems;
    protected java.lang.Boolean totalCountOnFirstResult;
    protected java.lang.String sort;
    protected java.lang.String dir;
    
    public WSGetItemsSort() {
    }
    
    public WSGetItemsSort(com.amalto.core.webservice.WSDataClusterPK wsDataClusterPK, java.lang.String conceptName, com.amalto.core.webservice.WSWhereItem whereItem, int spellTreshold, int skip, int maxItems, java.lang.Boolean totalCountOnFirstResult, java.lang.String sort, java.lang.String dir) {
        this.wsDataClusterPK = wsDataClusterPK;
        this.conceptName = conceptName;
        this.whereItem = whereItem;
        this.spellTreshold = spellTreshold;
        this.skip = skip;
        this.maxItems = maxItems;
        this.totalCountOnFirstResult = totalCountOnFirstResult;
        this.sort = sort;
        this.dir = dir;
    }
    
    public com.amalto.core.webservice.WSDataClusterPK getWsDataClusterPK() {
        return wsDataClusterPK;
    }
    
    public void setWsDataClusterPK(com.amalto.core.webservice.WSDataClusterPK wsDataClusterPK) {
        this.wsDataClusterPK = wsDataClusterPK;
    }
    
    public java.lang.String getConceptName() {
        return conceptName;
    }
    
    public void setConceptName(java.lang.String conceptName) {
        this.conceptName = conceptName;
    }
    
    public com.amalto.core.webservice.WSWhereItem getWhereItem() {
        return whereItem;
    }
    
    public void setWhereItem(com.amalto.core.webservice.WSWhereItem whereItem) {
        this.whereItem = whereItem;
    }
    
    public int getSpellTreshold() {
        return spellTreshold;
    }
    
    public void setSpellTreshold(int spellTreshold) {
        this.spellTreshold = spellTreshold;
    }
    
    public int getSkip() {
        return skip;
    }
    
    public void setSkip(int skip) {
        this.skip = skip;
    }
    
    public int getMaxItems() {
        return maxItems;
    }
    
    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }
    
    public java.lang.Boolean getTotalCountOnFirstResult() {
        return totalCountOnFirstResult;
    }
    
    public void setTotalCountOnFirstResult(java.lang.Boolean totalCountOnFirstResult) {
        this.totalCountOnFirstResult = totalCountOnFirstResult;
    }
    
    public java.lang.String getSort() {
        return sort;
    }
    
    public void setSort(java.lang.String sort) {
        this.sort = sort;
    }
    
    public java.lang.String getDir() {
        return dir;
    }
    
    public void setDir(java.lang.String dir) {
        this.dir = dir;
    }
}
