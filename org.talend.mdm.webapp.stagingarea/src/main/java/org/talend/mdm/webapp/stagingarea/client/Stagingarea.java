package org.talend.mdm.webapp.stagingarea.client;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Stagingarea implements EntryPoint {


    public static final String STAGINGAREA_ID = "Stagingarea.task"; //$NON-NLS-1$

    public void onModuleLoad() {
        if (GWT.isScript()) {
            XDOM.setAutoIdPrefix(GWT.getModuleName() + "-" + XDOM.getAutoIdPrefix()); //$NON-NLS-1$
            registerPubService();
            Log.setUncaughtExceptionHandler();
        }
    }

    private native void registerPubService()/*-{
        var instance = this;
        $wnd.amalto.stagingarea = {};
        $wnd.amalto.stagingarea.Stagingarea = function(){

            function initUI(){
                instance.@org.talend.mdm.webapp.stagingarea.client.Stagingarea::initUI()();
            }

            return {
                init : function(){initUI();}
            }
        }();
    }-*/;

    private native void _initUI()/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();
        var panel = tabPanel.getItem("Stagingarea"); 
        if (panel == undefined){
            @org.talend.mdm.webapp.stagingarea.client.GenerateContainer::generateContentPanel()();
            panel = this.@org.talend.mdm.webapp.stagingarea.client.Stagingarea::createPanel()();
            tabPanel.add(panel);
        }
        tabPanel.setSelection(panel.getItemId());
    }-*/;

    native JavaScriptObject createPanel()/*-{
        var instance = this;
        var panel = {
            render : function(el){
                instance.@org.talend.mdm.webapp.stagingarea.client.Stagingarea::renderContent(Ljava/lang/String;)(el.id);
            },
            setSize : function(width, height){
                var cp = @org.talend.mdm.webapp.stagingarea.client.GenerateContainer::getContentPanel()();
                cp.@com.extjs.gxt.ui.client.widget.ContentPanel::setSize(II)(width, height);
            },
            getItemId : function(){
                var cp = @org.talend.mdm.webapp.stagingarea.client.GenerateContainer::getContentPanel()();
                return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getItemId()();
            },
            getEl : function(){
                var cp = @org.talend.mdm.webapp.stagingarea.client.GenerateContainer::getContentPanel()();
                var el = cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getElement()();
                return {dom : el};
            },
            doLayout : function(){
                var cp = @org.talend.mdm.webapp.stagingarea.client.GenerateContainer::getContentPanel()();
                return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::doLayout()();
            },
            title : function(){
                var cp = @org.talend.mdm.webapp.stagingarea.client.GenerateContainer::getContentPanel()();
                return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getHeading()();
            }
        };
        return panel;
    }-*/;

    public void renderContent(final String contentId) {
        onModuleRender();
        RootPanel panel = RootPanel.get(contentId);
        panel.add(GenerateContainer.getContentPanel());
    }

    public void initUI() {
        _initUI();
    }

    private void onModuleRender() {
        ContentPanel contentPanel = GenerateContainer.getContentPanel();
        Button button = new Button("welcome"); //$NON-NLS-1$
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                // TODO do some thing...
            }
        });
        contentPanel.add(button);
    }

}
