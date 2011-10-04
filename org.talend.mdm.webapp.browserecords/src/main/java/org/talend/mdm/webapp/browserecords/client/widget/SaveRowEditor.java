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
package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

public class SaveRowEditor extends RowEditor<ItemBean> {

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    @Override
    protected void onRowClick(GridEvent<ItemBean> e) {
        // cancel click Editor
    }

    @Override
    public void startEditing(int rowIndex, boolean doFocus) {
        super.startEditing(rowIndex, doFocus);
        grid.getSelectionModel().setLocked(true);

    }

    @Override
    public void stopEditing(boolean saveChanges) {
        super.stopEditing(saveChanges);
        grid.getSelectionModel().setLocked(false);
        if (saveChanges) {
            Document doc = XMLParser.createDocument();
            Map<String, Element> elementSet = new HashMap<String, Element>();

            final ItemBean itemBean = grid.getSelectionModel().getSelectedItem();
            Map<String, TypeModel> metaType = BrowseRecords.getSession().getCurrentEntityModel().getMetaDataTypes();
            for (String index : metaType.keySet()) {
                TypeModel typeModel = metaType.get(index);
                Object value = itemBean.get(typeModel.getXpath());

                if (value instanceof List) {
                    String key = typeModel.getXpath();
                    String parentPath = key.substring(0, key.lastIndexOf('/'));
                    String elName = key.substring(key.lastIndexOf('/') + 1);
                    createElements(parentPath, elName, (List<?>) value, elementSet, doc);
                } else {
                    if (typeModel.getForeignkey() != null) {
                        String str = value.toString();
                        value = str.substring(str.lastIndexOf("-") + 1, str.length()); //$NON-NLS-1$
                    }
                    createElements(typeModel.getXpath(), value == null ? "" : value.toString(), elementSet, doc);//$NON-NLS-1$
                }
            }

            Element el = elementSet.get(itemBean.getConcept());
            doc.appendChild(el);
            itemBean.setItemXml(doc.toString());
            // Window.alert(itemBean.getItemXml());
            service.saveItemBean(itemBean, new SessionAwareAsyncCallback<String>() {

                @Override
                protected void doOnFailure(Throwable caught) {
                    Record record;
                    Store<ItemBean> store = grid.getStore();
                    if (store != null) {
                        record = store.getRecord(itemBean);
                    } else {
                        record = null;
                    }

                    if (record != null) {
                        record.reject(false);
                    }

                    String err = caught.getLocalizedMessage();
                    if (err != null)
                        err.replaceAll("\\[", "{").replaceAll("\\]", "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    MessageBox.alert(MessagesFactory.getMessages().error_title(),
                            Locale.getExceptionString(Locale.getLanguage(), err), null);

                }

                public void onSuccess(String result) {
                    Record record;
                    Store<ItemBean> store = grid.getStore();
                    if (store != null) {
                        record = store.getRecord(itemBean);
                    } else {
                        record = null;
                    }

                    if (record != null) {
                        record.commit(false);
                    }
                    // TODO refreshForm(itemBean);
                    MessageBox.alert(MessagesFactory.getMessages().info_title(),
                            Locale.getExceptionMessageByLanguage(Locale.getLanguage(), result), null);
                }
            });
        }
    }

    private void createElements(String xpath, String value, Map<String, Element> elementSet, Document doc) {
        Element parent = null;
        String[] xps = xpath.split("/"); //$NON-NLS-1$
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        for (String xp : xps) {
            if (isFirst) {
                sb.append(xp);
                isFirst = false;
            } else {
                sb.append("/" + xp); //$NON-NLS-1$
            }
            Element tempEl = elementSet.get(sb.toString());
            if (tempEl == null) {
                tempEl = (Element) doc.createElement(xp);
                elementSet.put(sb.toString(), tempEl);
            }
            if (parent != null) {
                parent.appendChild(tempEl);
            }
            parent = tempEl;
        }
        parent.appendChild(doc.createTextNode(value));
    }

    public void createElements(String xpath, String elName, List<?> value, Map<String, Element> elementSet, Document doc) {
        Element parent = null;
        String[] xps = xpath.split("/");//$NON-NLS-1$
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        for (String xp : xps) {
            if (isFirst) {
                sb.append(xp);
                isFirst = false;
            } else {
                sb.append("/" + xp);//$NON-NLS-1$
            }
            Element tempEl = elementSet.get(sb.toString());
            if (tempEl == null) {
                tempEl = (Element) doc.createElement(xp);
                elementSet.put(sb.toString(), tempEl);
            }
            if (parent != null) {
                parent.appendChild(tempEl);
            }
            parent = tempEl;
        }
        for (Object o : value) {
            Element el = doc.createElement(elName);
            el.appendChild(doc.createTextNode(o.toString()));
            parent.appendChild(el);
        }
    }
}
