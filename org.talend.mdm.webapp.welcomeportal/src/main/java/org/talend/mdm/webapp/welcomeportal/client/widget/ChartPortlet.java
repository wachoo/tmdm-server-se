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
package org.talend.mdm.webapp.welcomeportal.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.talend.mdm.webapp.welcomeportal.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.options.GlobalSeriesOptions;
import com.googlecode.gflot.client.options.PlotOptions;

public abstract class ChartPortlet extends BasePortlet {

    protected SimplePlot plot;

    public ChartPortlet(String name, Portal portal) {
        super(name, portal);
    }

    protected void initPlot() {
        PlotOptions plotOptions = PlotOptions.create();
        plotOptions.setGlobalSeriesOptions(GlobalSeriesOptions.create());
        plot = new SimplePlot(plotOptions);
        plot.setWidth(400);
        plot.setHeight(300);
    }

    public void refreshPlot() {
        updatePlot();
        plot.redraw();
        FieldSet set = (FieldSet) this.getItemByItemId(portletName + "Set"); //$NON-NLS-1$
        set.layout(true);
    }

    protected List<String> sort(Set<String> names) {
        List<String> appnamesSorted = new ArrayList<String>(names);
        Collections.sort(appnamesSorted);

        return appnamesSorted;
    }

    @Override
    protected void setIcon() {
        this.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.chart()));
    }

    abstract protected void updatePlot();
}
