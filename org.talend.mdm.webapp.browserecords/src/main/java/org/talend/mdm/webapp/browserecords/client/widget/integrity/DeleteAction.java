package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;

/**
 * Wraps a delete action for a single item.
 *
 * <ul>
 *     <li>{@link #PHYSICAL}: for physical deletes.</li>
 *     <li>{@link LogicalDeleteAction}: for logical deletes ("move to trash" deletes).</li>
 * </ul>
 */
public interface DeleteAction {

    /**
     * The 'physical delete' operation. Please note that logical deletes requires additional parameters that makes this
     * type of delete not eligible for singleton.
     */
    DeleteAction PHYSICAL = new PhysicalDeleteAction();

    /**
     * Delete a single item.
     *
     * @param item     The item to be deleted.
     * @param service  The service to be used for communication with MDM server.
     * @param override <code>true</code> if user chose to override FK integrity (when applicable only! see
     *                 {@link org.talend.mdm.webapp.browserecords.shared.FKIntegrityResult}), <code>false</code> otherwise.
     * @see BrowseRecordsServiceAsync#deleteItemBean(org.talend.mdm.webapp.browserecords.client.model.ItemBean, boolean, com.google.gwt.user.client.rpc.AsyncCallback)
     * @see BrowseRecordsServiceAsync#logicalDeleteItem(org.talend.mdm.webapp.browserecords.client.model.ItemBean, String, boolean, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    void delete(ItemBean item, BrowseRecordsServiceAsync service, boolean override);
}

