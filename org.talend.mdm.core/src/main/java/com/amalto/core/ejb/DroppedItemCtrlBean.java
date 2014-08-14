package com.amalto.core.ejb;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.util.XtentisException;
import org.apache.log4j.Logger;

/**
 * @author Starkey Shu
 * 
 * @ejb.bean name="DroppedItemCtrl"
 *          display-name="Name for DroppedItemCtrl"
 *          description="Description for DroppedItemCtrl"
 *          jndi-name="amalto/remote/core/droppeditemctrl"
 * 		  	local-jndi-name = "amalto/local/core/droppeditemctrl"
 *          type="Stateless"
 *          view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 * 
 */

@SuppressWarnings("deprecation")
public class DroppedItemCtrlBean implements SessionBean {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(DroppedItemCtrlBean.class);

    public DroppedItemCtrlBean() {
    }

    public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbCreate() throws javax.ejb.CreateException {
    }

    public void ejbPostCreate() throws javax.ejb.CreateException {
    }

    /**
     * Recover a dropped item
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ItemPOJOPK recoverDroppedItem(DroppedItemPOJOPK droppedItemPOJOPK) throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recovering " + droppedItemPOJOPK.getUniquePK());
        }
        try {
            return DroppedItemPOJO.recover(droppedItemPOJOPK);
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to recover the dropped item " + droppedItemPOJOPK.getUniquePK()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Find all dropped items pks
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public List<DroppedItemPOJOPK> findAllDroppedItemsPKs(String regex) throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("finding all dropped items pks ");
        }
        try {
            return DroppedItemPOJO.findAllPKs(regex);
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to find all dropped items pks  "
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Load a dropped item
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public DroppedItemPOJO loadDroppedItem(DroppedItemPOJOPK droppedItemPOJOPK) throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading " + droppedItemPOJOPK.getUniquePK());
        }
        try {
            return DroppedItemPOJO.load(droppedItemPOJOPK);
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to load the dropped item " + droppedItemPOJOPK.getUniquePK()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Remove a dropped item
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public DroppedItemPOJOPK removeDroppedItem(DroppedItemPOJOPK droppedItemPOJOPK) throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removing " + droppedItemPOJOPK.getUniquePK());
        }
        try {
            return DroppedItemPOJO.remove(droppedItemPOJOPK);
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to remove the dropped item " + droppedItemPOJOPK.getUniquePK()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err);
            throw new XtentisException(err, e);
        }
    }
}