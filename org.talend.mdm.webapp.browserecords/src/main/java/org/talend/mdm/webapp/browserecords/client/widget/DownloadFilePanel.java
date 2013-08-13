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
package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.util.PostDataUtil;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.base.shared.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.model.RecordsPagingConfig;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.ViewUtil;
import org.talend.mdm.webapp.browserecords.shared.Constants;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FormData;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class DownloadFilePanel extends FormPanel {

    private TextField<String> fileName;

    private CheckBox includeXmlContent;

    private QueryModel queryModel;

    private Button exportBtn;

    private Window window;

    private ViewBean viewBean = BrowseRecords.getSession().getCurrentView();

    public DownloadFilePanel(QueryModel queryModel, Window window) {
        this.queryModel = queryModel;
        this.window = window;

        this.setFrame(false);
        this.setHeaderVisible(false);
        this.setWidth("100%"); //$NON-NLS-1$

        fileName = new TextField<String>();
        fileName.setFieldLabel(MessagesFactory.getMessages().picture_field_label());
        fileName.setAllowBlank(false);
        fileName.setValue(viewBean.getBindingEntityModel().getConceptName());
        this.add(fileName, new FormData("90%")); //$NON-NLS-1$

        includeXmlContent = new CheckBox();
        includeXmlContent.setFieldLabel(MessagesFactory.getMessages().includeXmlContent_field_label());
        includeXmlContent.setLabelStyle("windth:90px"); //$NON-NLS-1$
        this.add(includeXmlContent, new FormData("90%")); //$NON-NLS-1$

        exportBtn = new Button(MessagesFactory.getMessages().export_btn());
        exportBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (!DownloadFilePanel.this.isValid()) {
                    return;
                }

                try {
                    Map<String, String> param = buildExportParameter();
                    PostDataUtil.postData("/browserecords/download", param); //$NON-NLS-1$
                } catch (Exception e) {
                    MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages().export_error(),
                            null);
                }
                DownloadFilePanel.this.window.hide();
            }
        });
        this.add(exportBtn);
    }

    private Map<String, String> buildExportParameter() {

        Grid<ItemBean> grid = ItemsListPanel.getInstance().getGrid();

        List<String> selectItemXmlList = new ArrayList<String>();
        if (grid != null) {
            List<ItemBean> selectItemList = grid.getSelectionModel().getSelectedItems();
            for (int i = 0; i < selectItemList.size(); i++) {
                selectItemXmlList.add(selectItemList.get(i).getItemXml());
            }
        }

        Map<String, String> param = new HashMap<String, String>();
        queryModel.getModel();
        queryModel.getCriteria();
        queryModel.getLanguage();

        param.put("fileName", fileName.getValue()); //$NON-NLS-1$
        param.put("dataCluster", queryModel.getDataClusterPK()); //$NON-NLS-1$
        param.put("viewPk", queryModel.getView().getViewPK()); //$NON-NLS-1$
        param.put("criteria", queryModel.getCriteria()); //$NON-NLS-1$
        param.put("language", queryModel.getLanguage()); //$NON-NLS-1$
        param.put("includeXmlContent", includeXmlContent.getValue().toString()); //$NON-NLS-1$

        EntityModel entityModel = viewBean.getBindingEntityModel();
        Map<String, TypeModel> dataTypes = entityModel.getMetaDataTypes();
        List<String> headerList = new LinkedList<String>();
        List<String> viewableXpathList = viewBean.getViewableXpaths();
        for (String viewableXpath : viewableXpathList) {
            TypeModel typeModel = dataTypes.get(viewableXpath);
            headerList.add(typeModel == null ? viewableXpath : ViewUtil.getViewableLabel(Locale.getLanguage(), typeModel));
        }
        param.put(
                "header", org.talend.mdm.webapp.base.shared.util.CommonUtil.convertListToString(headerList, Constants.FILE_EXPORT_IMPORT_SEPARATOR)); //$NON-NLS-1$
        param.put(
                "viewableXpath", org.talend.mdm.webapp.base.shared.util.CommonUtil.convertListToString(viewableXpathList, Constants.FILE_EXPORT_IMPORT_SEPARATOR)); //$NON-NLS-1$
        if (selectItemXmlList.size() > 0) {
            selectItemXmlList.add(0, ""); //$NON-NLS-1$
            param.put("itemXmlString", CommonUtil.convertListToString(selectItemXmlList, Constants.FILE_EXPORT_IMPORT_SEPARATOR)); //$NON-NLS-1$
        } else {
            param.put("itemXmlString", ""); //$NON-NLS-1$//$NON-NLS-2$
        }

        RecordsPagingConfig pagingLoad = queryModel.getPagingLoadConfig();
        String sortDir = null;
        if (SortDir.ASC.equals(SortDir.findDir(pagingLoad.getSortDir()))) {
            sortDir = "ascending"; //$NON-NLS-1$
        }
        if (SortDir.DESC.equals(SortDir.findDir(pagingLoad.getSortDir()))) {
            sortDir = "descending"; //$NON-NLS-1$
        }
        Map<String, TypeModel> types = queryModel.getModel().getMetaDataTypes();
        TypeModel typeModel = types.get(pagingLoad.getSortField());
        if (typeModel != null) {
            if (DataTypeConstants.INTEGER.getTypeName().equals(typeModel.getType().getBaseTypeName())
                    || DataTypeConstants.INT.getTypeName().equals(typeModel.getType().getBaseTypeName())
                    || DataTypeConstants.LONG.getTypeName().equals(typeModel.getType().getBaseTypeName())
                    || DataTypeConstants.DECIMAL.getTypeName().equals(typeModel.getType().getBaseTypeName())
                    || DataTypeConstants.FLOAT.getTypeName().equals(typeModel.getType().getBaseTypeName())
                    || DataTypeConstants.DOUBLE.getTypeName().equals(typeModel.getType().getBaseTypeName())) {
                sortDir = "NUMBER:" + sortDir; //$NON-NLS-1$
            }
        }

        if (pagingLoad.getSortField() != null) {
            param.put("sortField", pagingLoad.getSortField()); //$NON-NLS-1$
        }
        if (sortDir != null) {
            param.put("sortDir", sortDir); //$NON-NLS-1$
        }
        param.put("offset", Integer.toString(pagingLoad.getOffset())); //$NON-NLS-1$
        param.put("limit", Integer.toString(pagingLoad.getLimit())); //$NON-NLS-1$
        return param;
    }
}
