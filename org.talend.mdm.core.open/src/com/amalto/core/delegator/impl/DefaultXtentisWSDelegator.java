package com.amalto.core.delegator.impl;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.amalto.core.delegator.IXtentisWSDelegator;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.WSDataClusterPK;
import com.amalto.core.webservice.WSItemPK;
import com.amalto.core.webservice.WSUpdateMetadataItem;

public class DefaultXtentisWSDelegator extends IXtentisWSDelegator {

    private static final Logger LOGGER = Logger.getLogger(DefaultXtentisWSDelegator.class);

    /**
     * @ejb.interface-method view-type = "service-endpoint"
     * @ejb.permission role-name = "authenticated" view-type = "service-endpoint"
     */
    public WSItemPK updateItemMetadata(WSUpdateMetadataItem wsUpdateMetadataItem) throws RemoteException {
        try {
            WSItemPK itemPK = wsUpdateMetadataItem.getWsItemPK();
            ItemPOJOPK itemPk = new ItemPOJOPK(new DataClusterPOJOPK(itemPK.getWsDataClusterPK().getPk()),
                    itemPK.getConceptName(), itemPK.getIds());
            ItemPOJO item = Util.getItemCtrl2Local().getItem(itemPk);
            item.setTaskId(wsUpdateMetadataItem.getTaskId());
            ItemPOJOPK itemPOJOPK = Util.getItemCtrl2Local().updateItemMetadata(item);
            return new WSItemPK(new WSDataClusterPK(itemPOJOPK.getDataClusterPOJOPK().getUniqueId()),
                    itemPOJOPK.getConceptName(), itemPOJOPK.getIds());
        } catch (XtentisException e) {
            String err = "ERROR SYSTRACE: " + e.getMessage();
            LOGGER.debug(err, e);
            throw new RemoteException(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            String err = "ERROR SYSTRACE: " + e.getMessage();
            LOGGER.debug(err, e);
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()), e);
        }
    }
}
