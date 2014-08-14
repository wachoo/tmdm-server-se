package com.amalto.core.objects.menu.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.apache.log4j.Logger;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.util.XtentisException;

/**
 * @author bgrieder
 * 
 * @ejb.bean name="MenuCtrl" display-name="Name for MenuCtrl" description="Description for MenuCtrl"
 * jndi-name="amalto/remote/core/menuctrl" local-jndi-name = "amalto/local/core/menuctrl" type="Stateless"
 * view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission view-type = "remote" role-name = "administration"
 * @ejb.permission view-type = "local" unchecked = "true"
 * 
 */
public class MenuCtrlBean implements SessionBean, Menu {

    private static final long serialVersionUID = 4567895200L;

    private static final Logger LOGGER = Logger.getLogger(MenuCtrlBean.class);

    public MenuCtrlBean() {
    }

    public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    /**
     * Create method
     * 
     * @ejb.create-method view-type = "local"
     */
    public void ejbCreate() throws javax.ejb.CreateException {
    }

    /**
     * Post Create method
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {
    }

    @Override
    public MenuPOJOPK putMenu(MenuPOJO menu) throws XtentisException {
        LOGGER.debug("createMenu()");
        try {
            ObjectPOJOPK pk = menu.store();
            if (pk == null) {
                throw new XtentisException("Unable to create the Menu. Please check the XML Server logs");
            }
            return new MenuPOJOPK(pk);
        } catch (Exception e) {
            String err = "Unable to create/update the menu " + menu.getName() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOGGER.error(err);
            throw new XtentisException(err, e);
        }
    }

    @Override
    public MenuPOJO getMenu(MenuPOJOPK pk) throws XtentisException {
        LOGGER.debug("getMenu() ");
        try {
            MenuPOJO menu = ObjectPOJO.load(MenuPOJO.class, pk);
            if (menu == null) {
                String err = "The Menu " + pk.getUniqueId() + " does not exist.";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            return menu;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to get the Menu " + pk.toString() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOGGER.error(err);
            throw new XtentisException(err, e);
        }
    }

    @Override
    public MenuPOJO existsMenu(MenuPOJOPK pk) throws XtentisException {
        try {
            return ObjectPOJO.load(MenuPOJO.class, pk);
        } catch (XtentisException e) {
            return null;
        } catch (Exception e) {
            String info = "Could not check whether this Menu \"" + pk.getUniqueId() + "\" exists:  " + ": "
                    + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.debug(info, e);
            return null;
        }
    }

    @Override
    public MenuPOJOPK removeMenu(MenuPOJOPK pk) throws XtentisException {
        LOGGER.debug("Removing " + pk.getUniqueId());
        try {
            return new MenuPOJOPK(ObjectPOJO.remove(MenuPOJO.class, pk));
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to remove the Menu " + pk.toString() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOGGER.error(err);
            throw new XtentisException(err, e);
        }
    }

    @Override
    public Collection<MenuPOJOPK> getMenuPKs(String regex) throws XtentisException {
        try {
            Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(MenuPOJO.class, regex);
            ArrayList<MenuPOJOPK> l = new ArrayList<MenuPOJOPK>();
            for (ObjectPOJOPK entry : c) {
                l.add(new MenuPOJOPK(entry));
            }
            return l;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to get the Menu PKs for regular expression \"" + regex + "\"" + ": " + e.getClass().getName()
                    + ": " + e.getLocalizedMessage();
            LOGGER.error(err);
            throw new XtentisException(err, e);
        }
    }

}
