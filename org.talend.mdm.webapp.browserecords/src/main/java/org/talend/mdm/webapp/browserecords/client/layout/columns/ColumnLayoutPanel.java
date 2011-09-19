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
package org.talend.mdm.webapp.browserecords.client.layout.columns;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class ColumnLayoutPanel extends ContentPanel {

    public ColumnLayoutPanel(int columnCount) {
        ContentPanel panel = new ContentPanel();
        panel.setHeading("RowLayout: Orientation set to horizontal");
        panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
        panel.setSize(400, 300);
        panel.setFrame(true);
        panel.setCollapsible(true);

        Text label1 = new Text("Test Label 1");
        label1.addStyleName("pad-text"); //$NON-NLS-1$
        label1.setStyleAttribute("backgroundColor", "white"); //$NON-NLS-1$ //$NON-NLS-2$
        label1.setBorders(true);

        Text label2 = new Text("Test Label 2");
        label2.addStyleName("pad-text"); //$NON-NLS-1$
        label2.setStyleAttribute("backgroundColor", "white"); //$NON-NLS-1$ //$NON-NLS-2$
        label2.setBorders(true);

        Text label3 = new Text("Test Label 3");
        label3.addStyleName("pad-text"); //$NON-NLS-1$
        label3.setStyleAttribute("backgroundColor", "white"); //$NON-NLS-1$ //$NON-NLS-2$
        label3.setBorders(true);

        panel.add(label1, new RowData(-1, 1, new Margins(4)));
        panel.add(label2, new RowData(1, 1, new Margins(4, 0, 4, 0)));
        panel.add(label3, new RowData(-1, 1, new Margins(4)));
        this.add(panel);
    }

}
