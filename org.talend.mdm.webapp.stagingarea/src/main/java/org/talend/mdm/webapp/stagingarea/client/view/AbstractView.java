package org.talend.mdm.webapp.stagingarea.client.view;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.ContentPanel;

public abstract class AbstractView extends Composite {

    protected final ContentPanel mainPanel;

    public AbstractView() {
        mainPanel = new ContentPanel();
        mainPanel.setHeaderVisible(false);

        initComponents();
        initView();
        initEvent();
        this.initComponent(mainPanel);
    }

    protected void initComponents() {

    }

    protected void initView() {

    }

    protected void initEvent() {

    }

}
