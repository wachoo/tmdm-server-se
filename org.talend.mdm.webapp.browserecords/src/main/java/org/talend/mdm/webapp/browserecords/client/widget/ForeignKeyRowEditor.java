package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;


public class ForeignKeyRowEditor extends RowEditor<ItemNodeModel> {

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    TypeModel fkTypeModel;

    public ForeignKeyRowEditor(TypeModel fkTypeModel) {
        this.fkTypeModel = fkTypeModel;
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
            final ItemNodeModel node = grid.getSelectionModel().getSelectedItem();
            ForeignKeyBean fkBean = (ForeignKeyBean) node.getObjectValue();
            Map<String, String> fkValueMap = new HashMap<String, String>();

            for (String foreignKeyInfo : fkTypeModel.getForeignKeyInfo()) {
                fkValueMap.put(foreignKeyInfo, (String) fkBean.get(foreignKeyInfo));
            }

            service.saveFkItem(fkTypeModel.getForeignkey().split("/")[0], fkBean.getId(), fkValueMap, Locale.getLanguage(), //$NON-NLS-1$
                    new SessionAwareAsyncCallback<String>() {

                @Override
                protected void doOnFailure(Throwable caught) {
                    Record record;
                    Store<ItemNodeModel> store = grid.getStore();
                    if (store != null) {
                        record = store.getRecord(node);
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
                    Store<ItemNodeModel> store = grid.getStore();
                    if (store != null) {
                        record = store.getRecord(node);
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

}
