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

package org.talend.mdm.webapp.itemsbrowser2.client.resources.icon;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

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

    @Source("add-element.gif")
    ImageResource add_element();

    @Source("image_add.png")
    ImageResource image_add();

    @Source("no_image.gif")
    ImageResource no_image();

    @Source("clear-icon.gif")
    ImageResource clear_icon();

    @Source("save.png")
    ImageResource Save();

    @Source("display.png")
    ImageResource Display();

    @Source("create.png")
    ImageResource Create();

    @Source("delete.png")
    ImageResource Delete();

    @Source("send_to_trash.png")
    ImageResource Send_to_trash();

    @Source("duplicate.png")
    ImageResource Duplicate();

    @Source("journal.png")
    ImageResource Journal();

    @Source("save_and_close.png")
    ImageResource SaveClose();

    @Source("refresh.png")
    ImageResource Refresh();
    
    @Source("drop-add.gif")
    ImageResource drop_add();
    
    @Source("drop-no.gif")
    ImageResource drop_no();
}
