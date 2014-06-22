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

package org.talend.mdm.webapp.browserecords.client.widget.ForeignKey;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class FKField extends TextField<ForeignKeyBean> implements ReturnCriteriaFK {

    private SplitButton foreignBtn = new SplitButton();

    private FKRelRecordWindow relWindow;

    private boolean retrieveFKinfos = false;

    private String foreignKey;

    private ReturnCriteriaFK returnCriteriaFK;

    public boolean isRetrieveFKinfos() {
        return retrieveFKinfos;
    }

    public void setRetrieveFKinfos(boolean retrieveFKinfos) {
        this.retrieveFKinfos = retrieveFKinfos;
    }

    public FKField() {
        this.setFireChangeEventOnSetValue(true);
    }

    protected void onRender(Element target, int index) {
        El wrap = new El(DOM.createDiv());
        wrap.addStyleName("x-form-field-wrap"); //$NON-NLS-1$
        wrap.addStyleName("x-form-file-wrap"); //$NON-NLS-1$

        input = new El(DOM.createInputText());
        input.addStyleName(fieldStyle);
        input.addStyleName("x-form-file-text"); //$NON-NLS-1$
        input.setId(XDOM.getUniqueId());
        input.setEnabled(false);

        if (GXT.isIE && target.getTagName().equals("TD")) { //$NON-NLS-1$
            input.setStyleAttribute("position", "static"); //$NON-NLS-1$  //$NON-NLS-2$
        }

        foreignBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.link()));
        foreignBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                if (relWindow != null) {
                    relWindow.show();
                } else {
                    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry
                            .get(BrowseRecords.BROWSERECORDS_SERVICE);
                    EntityModel currentEntityModel = BrowseRecords.getSession().getCurrentEntityModel();
                    String concept = currentEntityModel.getTypeModel(foreignKey).getForeignkey().split("/")[0]; //$NON-NLS-1$
                    service.getEntityModel(concept, Locale.getLanguage(), new SessionAwareAsyncCallback<EntityModel>() {

                        public void onSuccess(EntityModel entityModel) {
                            showWindow(entityModel);
                        }

                        protected void doOnFailure(Throwable caught) {
                            super.doOnFailure(caught);
                        }
                    });

                }
            }

        });

        wrap.appendChild(input.dom);
        input.setStyleAttribute("float", "left");//$NON-NLS-1$  //$NON-NLS-2$
        El foreignDiv = new El(DOM.createSpan());
        wrap.appendChild(foreignDiv.dom);

        final SelectionListener<ButtonEvent> closer = new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                if (relWindow != null)
                    relWindow.hide();
            }

        };

        setElement(wrap.dom, target, index);
        foreignBtn.render(foreignDiv.dom);
        super.onRender(target, index);
    }

    private void showWindow(EntityModel entityModel) {
        relWindow = new FKRelRecordWindow();
        relWindow.setEntityModel(entityModel);
        relWindow.setSize(470, 340);
        relWindow.setResizable(false);
        relWindow.setModal(true);
        relWindow.setBlinkModal(true);
        relWindow.setFkKey(foreignKey);
        relWindow.setReturnCriteriaFK(returnCriteriaFK);
        relWindow.setHeading(MessagesFactory.getMessages().fk_RelatedRecord());
        relWindow.show();
    }

    protected void onResize(int width, int height) {
        super.onResize(width, height);
        input.setWidth(width - foreignBtn.getWidth() - 4, true);
    }

    protected void doAttachChildren() {
        super.doAttachChildren();
        ComponentHelper.doAttach(foreignBtn);
    }

    protected void doDetachChildren() {
        super.doDetachChildren();
        ComponentHelper.doDetach(foreignBtn);
    }

    public void Update(String foreignKey, ReturnCriteriaFK returnCriteriaFK) {
        this.foreignKey = foreignKey;
        this.returnCriteriaFK = returnCriteriaFK;
    }

    public void setCriteriaFK(final ForeignKeyBean fk) {
        if (retrieveFKinfos) {
            fk.setShowInfo(true);
        }

        setValue(fk);
    }

    public void setEnabled(boolean enabled) {
        foreignBtn.setEnabled(enabled);
    }

    public void setValue(ForeignKeyBean fk) {
        super.setValue(fk);
    }

    public ForeignKeyBean getValue() {
        return value;
    }
}
