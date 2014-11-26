package com.amalto.core.util;

import org.apache.commons.lang.StringUtils;

import com.amalto.core.objects.ItemPOJOPK;
import com.amalto.core.objects.ObjectPOJOPK;

public class EntityNotFoundException extends XtentisException {

    private static final long serialVersionUID = -5541352366654335876L;

    private ItemPOJOPK itemPK;

    public EntityNotFoundException(ItemPOJOPK pk) {
        this.itemPK = pk;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public String getMessage() {
        if (itemPK != null) {
            if (StringUtils.isEmpty(itemPK.getDataClusterPOJOPK().getUniqueId())) {
                return "No container specified in id '" + itemPK.getUniqueID() + "'.";
            }
            if (StringUtils.isEmpty(itemPK.getConceptName())) {
                return "No data model specified in '" + itemPK.getUniqueID() + "'.";
            } else {
                return "Could not find item '" + itemPK.getUniqueID() + "'.";
            }
        } else {
            return "Could not find item."; // Generic error message.
        }
    }
}
