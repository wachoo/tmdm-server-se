/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package com.amalto.core.objects.view;

import java.util.List;
import java.util.Set;

import com.amalto.core.objects.ObjectPOJO;
import com.amalto.core.objects.ObjectPOJOPK;
import com.amalto.core.util.ArrayListHolder;
import com.amalto.xmlserver.interfaces.IWhereItem;

/**
 * @author bgrieder
 * 
 */
public class ViewPOJO extends ObjectPOJO {

    private String name;

    private String description;

    private Set<String> roles;

    private List<String> noAccessRoles;

    private ArrayListHolder<String> searchableBusinessElements;

    private ArrayListHolder<String> viewableBusinessElements;

    private ArrayListHolder<IWhereItem> whereConditions;

    private String transformerPK;

    private boolean isTransformerActive;

    private boolean isAsc;

    private String sortField;

    private String customForm;

    public ViewPOJO(String name) {
        this.name = name;
    }

    public ViewPOJO() {
        this.searchableBusinessElements = new ArrayListHolder<String>();
        this.viewableBusinessElements = new ArrayListHolder<String>();
        this.whereConditions = new ArrayListHolder<IWhereItem>();
    }

    public String getTransformerPK() {
        return transformerPK;
    }

    public void setTransformerPK(String transformerPK) {
        this.transformerPK = transformerPK;
    }

    public boolean isTransformerActive() {
        return isTransformerActive;
    }

    public void setTransformerActive(boolean isTransformerActive) {
        this.isTransformerActive = isTransformerActive;
    }

    /**
     * @return Returns the Name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the Description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayListHolder<String> getSearchableBusinessElements() {
        return searchableBusinessElements;
    }

    public void setSearchableBusinessElements(ArrayListHolder<String> searchableBusinessElements) {
        this.searchableBusinessElements = searchableBusinessElements;
    }

    public ArrayListHolder<String> getViewableBusinessElements() {
        return viewableBusinessElements;
    }

    public void setViewableBusinessElements(ArrayListHolder<String> viewableBusinessElements) {
        this.viewableBusinessElements = viewableBusinessElements;
    }

    public ArrayListHolder<IWhereItem> getWhereConditions() {
        return whereConditions;
    }

    public void setWhereConditions(ArrayListHolder<IWhereItem> whereConditions) {
        this.whereConditions = whereConditions;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public List<String> getNoAccessRoles() {
        return noAccessRoles;
    }

    public void setNoAccessRoles(List<String> noAccessRoles) {
        this.noAccessRoles = noAccessRoles;
    }

    public boolean getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(boolean isAsc) {
        this.isAsc = isAsc;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getCustomForm() {
        return customForm;
    }

    public void setCustomForm(String customForm) {
        this.customForm = customForm;
    }

    @Override
    public ObjectPOJOPK getPK() {
        if (getName() == null) {
            return null;
        }
        return new ObjectPOJOPK(new String[] { name });
    }
}
