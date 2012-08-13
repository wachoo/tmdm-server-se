// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.stagingarea.client.model;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DOC suplch  class global comment. Detailled comment
 */
public class ContextModel implements Serializable, IsSerializable {

    private int refreshIntervals = 1000;

    private List<String> dataContainer;

    private List<String> dataModels;

    public ContextModel() {

    }

    public int getRefreshIntervals() {
        return refreshIntervals;
    }

    public void setRefreshIntervals(int refreshIntervals) {
        this.refreshIntervals = refreshIntervals;
    }

    public List<String> getDataContainer() {
        return dataContainer;
    }

    public void setDataContainer(List<String> dataContainer) {
        this.dataContainer = dataContainer;
    }

    public List<String> getDataModels() {
        return dataModels;
    }

    public void setDataModels(List<String> dataModels) {
        this.dataModels = dataModels;
    }

}
