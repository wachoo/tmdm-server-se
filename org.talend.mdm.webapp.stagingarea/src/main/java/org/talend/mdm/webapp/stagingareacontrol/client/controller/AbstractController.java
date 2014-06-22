// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.stagingareacontrol.client.controller;

import org.talend.mdm.webapp.stagingareacontrol.client.view.AbstractView;


public abstract class AbstractController {

    protected AbstractView bindingView;

    public AbstractView getBindingView() {
        return bindingView;
    }

    public void setBindingView(AbstractView bindingView) {
        this.bindingView = bindingView;
    }


}
