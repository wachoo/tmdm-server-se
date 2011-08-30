package org.talend.mdm.webapp.browserecords.client.layout.columns;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class ColumnLayoutPanel extends ContentPanel {

    private ContentPanel panel;


    public ColumnLayoutPanel(int columnCount) {
        ContentPanel panel = new ContentPanel();
        panel.setHeading("RowLayout: Orientation set to horizontal");
        panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
        panel.setSize(400, 300);
        panel.setFrame(true);
        panel.setCollapsible(true);

        Text label1 = new Text("Test Label 1");
        label1.addStyleName("pad-text");
        label1.setStyleAttribute("backgroundColor", "white");
        label1.setBorders(true);

        Text label2 = new Text("Test Label 2");
        label2.addStyleName("pad-text");
        label2.setStyleAttribute("backgroundColor", "white");
        label2.setBorders(true);

        Text label3 = new Text("Test Label 3");
        label3.addStyleName("pad-text");
        label3.setStyleAttribute("backgroundColor", "white");
        label3.setBorders(true);

        panel.add(label1, new RowData(-1, 1, new Margins(4)));
        panel.add(label2, new RowData(1, 1, new Margins(4, 0, 4, 0)));
        panel.add(label3, new RowData(-1, 1, new Margins(4)));
        this.add(panel);
    }

}
