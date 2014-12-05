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
package org.talend.mdm.webapp.general.server.util;

import java.util.List;

public class GxtProjectModel {

    private String       context;

    private String       application;

    private String       model;

    private List<String> css_addresses;

    public GxtProjectModel() {
    }

	public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<String> getCss_addresses() {
        return css_addresses;
    }

    public void setCss_addresses(List<String> cssAddresses) {
        css_addresses = cssAddresses;
    }
}
