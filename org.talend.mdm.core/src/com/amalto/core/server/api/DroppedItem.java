package com.amalto.core.server.api;

import com.amalto.core.objects.DroppedItemPOJO;
import com.amalto.core.objects.DroppedItemPOJOPK;
import com.amalto.core.objects.ItemPOJOPK;

/**
 *
 */
public interface DroppedItem {
    /**
     * Recover a dropped item
     *
     * @throws com.amalto.core.util.XtentisException
     */
    ItemPOJOPK recoverDroppedItem(DroppedItemPOJOPK droppedItemPOJOPK) throws com.amalto.core.util.XtentisException;

    /**
     * Find all dropped items pks
     *
     * @throws com.amalto.core.util.XtentisException
     */
    java.util.List findAllDroppedItemsPKs(String regex) throws com.amalto.core.util.XtentisException;

    /**
     * Load a dropped item
     *
     * @throws com.amalto.core.util.XtentisException
     */
    DroppedItemPOJO loadDroppedItem(DroppedItemPOJOPK droppedItemPOJOPK) throws com.amalto.core.util.XtentisException;

    /**
     * Remove a dropped item
     *
     * @throws com.amalto.core.util.XtentisException
     */
    DroppedItemPOJOPK removeDroppedItem(DroppedItemPOJOPK droppedItemPOJOPK) throws com.amalto.core.util.XtentisException;
}
