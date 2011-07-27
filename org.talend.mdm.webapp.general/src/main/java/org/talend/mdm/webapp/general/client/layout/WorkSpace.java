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
package org.talend.mdm.webapp.general.client.layout;

import org.talend.mdm.webapp.general.client.layout.AccordionMenus.HTMLMenuItem;
import org.talend.mdm.webapp.general.model.MenuBean;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Frame;

public class WorkSpace extends LayoutContainer {

    private static WorkSpace instance;

    private TabPanel workTabPanel = new TabPanel();

    private WorkSpace() {
        super();
        this.setLayout(new FitLayout());
        workTabPanel.setMinTabWidth(115);
        workTabPanel.setResizeTabs(true);
        workTabPanel.setAnimScroll(true);
        workTabPanel.setTabScroll(true);
        this.add(workTabPanel);
        registerTabPanel();
    }

    public static WorkSpace getInstance() {
        if (instance == null) {
            instance = new WorkSpace();
        }
        return instance;
    }

    public void addWorkTab(final HTMLMenuItem menuItem, String url) {
        MenuBean menuBean = menuItem.getMenuBean();
        TabItem item = workTabPanel.getItemByItemId(menuBean.getName());
        if (item == null) {
            item = new TabItem(menuBean.getName());
            item.addListener(Events.Select, new Listener<BaseEvent>() {

                public void handleEvent(BaseEvent be) {
                    AccordionMenus.getInstance().selectedItem(menuItem);
                }
            });
            item.setItemId(menuBean.getName());
            item.setClosable(true);
            item.setLayout(new FitLayout());
            Frame frame = new Frame(url);
//            Frame frame = new Frame("/" + menuBean.getContext()); //$NON-NLS-1$
            frame.getElement().getStyle().setBorderWidth(0.0D, Unit.PX);
            item.add(frame);
            workTabPanel.add(item);
        }
        workTabPanel.setSelection(item);
    }
    
    public void addWorkTab(String url, final String text, final String id, final JavaScriptObject panel) {

        TabItem item = workTabPanel.getItemByItemId(id);
        Frame frame = null;
        if (item == null) {
            item = new TabItem(text);
            item.setItemId(id);
            item.setClosable(true);
            item.setLayout(new FitLayout());
            frame = new Frame();
            item.add(frame);
            workTabPanel.add(item);
        }
        workTabPanel.setSelection(item);
        if (frame != null) {
            renderToFrame(panel, frame.getElement());
        }

    }


    public native void renderToFrame(JavaScriptObject panel, Element iframeEl)/*-{
        var doc = iframeEl.contentWindow.document;
        var html = [
        "<html>",
        "<head>",
        "<link href='/core/secure/ext-2.2/resources/css/ext-all.css' type='text/css' rel='stylesheet'>",
        "<link href='/core/secure/ext.ux/editablecolumntree/editable-column-tree.css' type='text/css' rel='stylesheet'>",
        "<link href='/core/secure/css/firefox3-fix.css' type='text/css' rel='stylesheet'>",
        "<link href='/core/secure/css/webapp-core.css' type='text/css' rel='stylesheet'>",
        "<link href='/core/secure/css/amalto-menus.css' type='text/css' rel='stylesheet'>",
        "<link type='text/css' href='/core/secure/timeline/timeline_js/timeline-bundle.css' rel='stylesheet'>",
        "<link type='text/css' href='/core/secure/timeline/timeline_ajax/styles/graphics.css' rel='stylesheet'>",
        "<link type='text/css' href='/core/secure/timeline/css/default.css' rel='stylesheet'>",
        "<link href='/core/secure/yui-2.4.0/build/logger/assets/logger.css' rel='stylesheet' type='text/css'>",
        "<link href='/core/secure/yui-2.4.0/build/container/assets/container.css' rel='stylesheet' type='text/css'>",

        //        "<script type='text/javascript' src='/core/secure/yui-2.4.0/build/utilities/utilities.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/yui-2.4.0/build/yuiloader/yuiloader-beta.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/ext-2.2/adapter/yui/ext-yui-adapter.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/ext-2.2/ext-all-debug.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/ext.ux/editablecolumntree/ColumnNodeUI.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/ext.ux/editablecolumntree/treeSerializer.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/ext.ux/MultiSelectTreePanel.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/js/core.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/dwr/interface/LayoutInterface.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/ext.ux/DWRAction.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/ext.ux/DWRProxy.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/js/bgutil.js'></script>",
        //        "<script type='text/javascript' src='/core/secure/js/raphael-min.js'></script>",
        //        "<script language='javascript1.2' type='text/javascript' src='/core/secure/dwr/engine.js'></script>",
        //        "<script language='javascript1.2' type='text/javascript' src='/core/secure/dwr/util.js'></script>",
        //        "<script src='/core/secure/timeline/timeline_js/timeline-api.js' type='text/javascript'></script>",
        //        "<script src='/core/secure/timeline/timeline_ajax/simile-ajax-api.js' type='text/javascript'></script>",
        //        "<script type='text/javascript' src='/itemsbrowser/secure/dwr/interface/ItemsBrowserInterface.js'></script>",
        //        "<script type='text/javascript' src='/itemsbrowser/secure/js/ItemsBrowser.js'></script>",
        //        "<script type='text/javascript' src='/talendmdm/secure/js/conf.js'></script>",
        //        "<script type='text/javascript' src='/talendmdm/secure/js/actions.js'></script>",
        //        "<script type='text/javascript' src='/talendmdm/secure/dwr/interface/ActionsInterface.js'></script>",
        //        "<script type='text/javascript' src='/talendmdm/secure/dwr/interface/WidgetInterface.js'></script>",

        "</head>",
        "<body>",
        "</body>",
        "</html>"
        ];
        doc.write(html.join(""));
        panel.render(doc.body);
    }-*/;

    public native void registerTabPanel()/*-{
        var instance = this;
        $wnd.addWorkTabPanel = function(url, text, id, jsPanel){
        instance.@org.talend.mdm.webapp.general.client.layout.WorkSpace::addWorkTab(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(url, text, id, jsPanel);
        };
    }-*/;

    public void clearTabs(){
        workTabPanel.removeAll();
    }
}
