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
package org.talend.mdm.webapp.journal.client;

import org.talend.mdm.webapp.journal.client.mvc.JournalController;
import org.talend.mdm.webapp.journal.client.util.TimelineUtil;
import org.talend.mdm.webapp.journal.client.widget.JournalSearchPanel;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class Journal implements EntryPoint {

    public static final String JOURNAL_SERVICE = "JournalService"; //$NON-NLS-1$

    public static final String JOURNAL_ID = "Journal"; //$NON-NLS-1$

    public static final String SEARCH_CRITERIA = "SearchCriteria"; //$NON-NLS-1$

    private native void registerJournalService()/*-{

        $wnd.amalto.journal.Journal.browseJournalWithCriteria = function(ids,
                concept, isItemsBroswer) {
            @org.talend.mdm.webapp.journal.client.Journal::initJournalfromBrowseRecord(Ljava/lang/String;Ljava/lang/String;)(ids, concept);
            var tabPanel = $wnd.amalto.core.getTabPanel();
            var panel = tabPanel.getItem("Journal");
            if (panel == undefined) {
                $wnd.amalto.journal.Journal.init();
                @org.talend.mdm.webapp.journal.client.Journal::setSearchField(Ljava/lang/String;Ljava/lang/String;)(ids, concept);
            } else {
                @org.talend.mdm.webapp.journal.client.Journal::onSearchWithCriteria()();
                @org.talend.mdm.webapp.journal.client.Journal::setSearchField(Ljava/lang/String;Ljava/lang/String;)(ids, concept);
                tabPanel.setSelection("Journal");
            }
        }
    }-*/;

    @Override
    public void onModuleLoad() {
        XDOM.setAutoIdPrefix(GWT.getModuleName() + "-" + XDOM.getAutoIdPrefix()); //$NON-NLS-1$
        registerPubService();
        TimelineUtil.regLoadDate();
        TimelineUtil.regShowDialog();
        Log.setUncaughtExceptionHandler();
        Registry.register(JOURNAL_SERVICE, GWT.create(JournalService.class));
        Registry.register(SEARCH_CRITERIA, new JournalSearchCriteria());

        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.addController(new JournalController());
        registerJournalService();
    }

    private native void registerPubService()/*-{
        var instance = this;
        $wnd.amalto.journal = {};
        $wnd.amalto.journal.Journal = function() {

            function initUI() {
                instance.@org.talend.mdm.webapp.journal.client.Journal::initUI()();
            }

            return {
                init : function() {
                    initUI();
                }
            }
        }();
    }-*/;

    private native void _initUI()/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();
        var panel = tabPanel.getItem("Journal");
        if (panel == undefined) {
            @org.talend.mdm.webapp.journal.client.GenerateContainer::generateContentPanel()();
            panel = this.@org.talend.mdm.webapp.journal.client.Journal::createPanel()();
            tabPanel.add(panel);
        } else {
            @org.talend.mdm.webapp.journal.client.Journal::onSearchWithCriteria()();
        }
        tabPanel.setSelection(panel.getItemId());
    }-*/;

    native JavaScriptObject createPanel()/*-{
        var instance = this;
        var panel = {
            render : function(el) {
                instance.@org.talend.mdm.webapp.journal.client.Journal::renderContent(Ljava/lang/String;)(el.id);
            },
            setSize : function(width, height) {
                var cp = @org.talend.mdm.webapp.journal.client.GenerateContainer::getContentPanel()();
                cp.@com.extjs.gxt.ui.client.widget.ContentPanel::setSize(II)(width, height);
            },
            getItemId : function() {
                var cp = @org.talend.mdm.webapp.journal.client.GenerateContainer::getContentPanel()();
                return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getItemId()();
            },
            getEl : function() {
                var cp = @org.talend.mdm.webapp.journal.client.GenerateContainer::getContentPanel()();
                var el = cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getElement()();
                return {
                    dom : el
                };
            },
            doLayout : function() {
                var cp = @org.talend.mdm.webapp.journal.client.GenerateContainer::getContentPanel()();
                return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::doLayout()();
            },
            title : function() {
                var cp = @org.talend.mdm.webapp.journal.client.GenerateContainer::getContentPanel()();
                return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getHeading()();
            }
        };
        return panel;
    }-*/;

    public void renderContent(final String contentId) {
        onModuleRender();

        final ContentPanel content = GenerateContainer.getContentPanel();

        if (GWT.isScript()) {
            RootPanel panel = RootPanel.get(contentId);
            panel.add(content);
        } else {
            final Element element = DOM.getElementById(contentId);
            SimplePanel panel = new SimplePanel() {

                @Override
                protected void setElement(Element elem) {
                    super.setElement(element);
                }
            };
            RootPanel rootPanel = RootPanel.get();
            rootPanel.clear();
            rootPanel.add(panel);
            panel.add(content);
        }
    }

    public void initUI() {
        _initUI();
    }

    private void onModuleRender() {
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.dispatch(JournalEvents.InitFrame);
    }

    public static void initJournalfromBrowseRecord(String ids, String concept) {
        resetSearchCondition();
        JournalSearchCriteria criteria = Registry.get(Journal.SEARCH_CRITERIA);
        criteria.setEntity(concept);
        criteria.setKey(ids);
    }

    public static void setSearchField(String ids, String concept) {
        JournalSearchPanel.getInstance().setKeyFieldValue(ids);
        JournalSearchPanel.getInstance().setEntityFieldValue(concept);
    }

    public static void onSearchWithCriteria() {
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.dispatch(JournalEvents.DoSearch);
    }

    public static void resetSearchCondition() {
        JournalSearchCriteria criteria = Registry.get(Journal.SEARCH_CRITERIA);
        criteria.setStrict(true);
        criteria.setEntity(null);
        criteria.setKey(null);
        criteria.setOperationType(null);
        criteria.setSource(null);
        criteria.setStartDate(null);
        criteria.setEndDate(null);
        JournalSearchPanel.getInstance().initPanel();
    }
}