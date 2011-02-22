// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.mdm.webapp.itemsbrowser2.client.resources.icon;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author sbessaies
 */
public interface Icons extends ClientBundle {

    Icons INSTANCE = GWT.create(Icons.class);

    @Source("add.png")
    ImageResource add();

    @Source("chart_organisation_add.png")
    ImageResource chart_organisation_add();

    @Source("remove.png")
    ImageResource remove();

    @Source("edit.png")
    ImageResource Edit();

    @Source("valid.png")
    ImageResource Valid();
}
