// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.schema.manage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class AppinfoSourceHolder {

    private AppinfoSourceHolderPK owner;

    private List<AppinfoSourceTypeHolder> appinfoSourceTypeHolders;

    public AppinfoSourceHolder(AppinfoSourceHolderPK pk) {
        this.owner = pk;
        this.appinfoSourceTypeHolders = new ArrayList<AppinfoSourceTypeHolder>();
    }

    public void addSource(String sourceType, String xpath, String value) {

        int mark = appinfoSourceTypeHolders.indexOf(new AppinfoSourceTypeHolder(sourceType));
        if (mark == -1) {
            AppinfoSourceTypeHolder appinfoSourceTypeHolder = new AppinfoSourceTypeHolder(sourceType,
                    getBindingAlgorithm(sourceType));
            appinfoSourceTypeHolder.addRole(value, xpath);
            appinfoSourceTypeHolders.add(appinfoSourceTypeHolder);
        } else {
            AppinfoSourceTypeHolder appinfoSourceTypeHolder = appinfoSourceTypeHolders.get(mark);
            appinfoSourceTypeHolder.addRole(value, xpath);
        }

    }

    private AppinfoAddPathAbstractAlgorithm getBindingAlgorithm(String sourceType) {
        // just like simple factory
        if (sourceType.equals(BusinessConcept.APPINFO_X_HIDE)) {
            return new AppinfoAddPathParentFirstAlgorithm();
        } else if (sourceType.equals(BusinessConcept.APPINFO_X_WRITE)) {
            return new AppinfoAddPathParentFirstAlgorithm();
        }

        return null;

    }

    public List<String> getResult(String sourceType, String role) {
        List<String> xpaths = null;

        int mark = appinfoSourceTypeHolders.indexOf(new AppinfoSourceTypeHolder(sourceType));
        if (mark != -1) {
            AppinfoSourceTypeHolder appinfoSourceTypeHolder = appinfoSourceTypeHolders.get(mark);
            xpaths = appinfoSourceTypeHolder.getResult(sourceType, role);
        }

        if (xpaths == null)
            return new ArrayList<String>();

        return xpaths;

    }

    @Override
    public String toString() {

        StringBuffer print = new StringBuffer();
        print.append("-").append(owner).append("\n");
        for (Iterator iterator = appinfoSourceTypeHolders.iterator(); iterator.hasNext();) {

            AppinfoSourceTypeHolder appinfoSourceTypeHolder = (AppinfoSourceTypeHolder) iterator.next();
            print.append(appinfoSourceTypeHolder);

        }

        return print.toString();
    }

}
