package com.amalto.core.server.api;

/**
 *
 */
public interface DroppedItem {
    /**
     * Recover a dropped item
     *
     * @throws com.amalto.core.util.XtentisException
     */
    com.amalto.core.ejb.ItemPOJOPK recoverDroppedItem(com.amalto.core.ejb.DroppedItemPOJOPK droppedItemPOJOPK) throws com.amalto.core.util.XtentisException;

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
    com.amalto.core.ejb.DroppedItemPOJO loadDroppedItem(com.amalto.core.ejb.DroppedItemPOJOPK droppedItemPOJOPK) throws com.amalto.core.util.XtentisException;

    /**
     * Remove a dropped item
     *
     * @throws com.amalto.core.util.XtentisException
     */
    com.amalto.core.ejb.DroppedItemPOJOPK removeDroppedItem(com.amalto.core.ejb.DroppedItemPOJOPK droppedItemPOJOPK) throws com.amalto.core.util.XtentisException;
}
