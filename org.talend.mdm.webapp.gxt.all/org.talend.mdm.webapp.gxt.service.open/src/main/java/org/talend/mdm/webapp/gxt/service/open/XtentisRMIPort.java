package org.talend.mdm.webapp.gxt.service.open;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.amalto.core.objects.menu.ejb.MenuEntryPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJOPK;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocal;
import com.amalto.core.util.Util;
import org.talend.mdm.webapp.gxt.service.open.webservices.*;

public class XtentisRMIPort implements XtentisPort_PortType {
    
    /****************************************************************************
     * Ping - test that we can authenticate by getting a server response
     * **************************************************************************/
    
    public WSString ping(WSPing wsPing) throws RemoteException {
        try {
            return new WSString(wsPing.getEcho());
        } catch (Exception e) {
            throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
        }
    }

        
    /***************************************************************************
     * Menu
     * **************************************************************************/

    public WSMenuPK deleteMenu(WSDeleteMenu wsMenuDelete) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
            return
                new WSMenuPK(
                    ctrl.removeMenu(
                        new MenuPOJOPK(
                                wsMenuDelete.getWsMenuPK().getPk()
                        )
                    ).getUniqueId()
                );
    
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
        }
    }
    
    public WSMenu getMenu(WSGetMenu wsGetMenu) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
            MenuPOJO pojo =
                ctrl.getMenu(
                    new MenuPOJOPK(
                            wsGetMenu.getWsMenuPK().getPk()
                    )
                );
            return POJO2WS(pojo);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
        }
    }

    public WSBoolean existsMenu(WSExistsMenu wsExistsMenu) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
            MenuPOJO pojo =
                ctrl.existsMenu(
                    new MenuPOJOPK(
                            wsExistsMenu.getWsMenuPK().getPk()
                    )
                );
            return new WSBoolean(pojo!=null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
        }
    }
    
    public WSMenuPKArray getMenuPKs(WSGetMenuPKs regex) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
            Collection c =
                ctrl.getMenuPKs(
                    regex.getRegex()
                );
            if (c==null) return null;
            WSMenuPK[] pks = new WSMenuPK[c.size()];
            int i=0;
            for (Iterator iter = c.iterator(); iter.hasNext(); ) {
                pks[i++] = new WSMenuPK(
                        ((MenuPOJOPK) iter.next()).getUniqueId()
                );
            }
            return new WSMenuPKArray(pks);
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
        }
    }

    public WSMenuPK putMenu(WSPutMenu wsMenu) throws RemoteException {
        try {
            MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
            MenuPOJOPK pk =
                ctrl.putMenu(
                        WS2POJO(wsMenu.getWsMenu())
                );
            return new WSMenuPK(pk.getUniqueId());
        } catch (Exception e) {
            throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
        }
    }
    
    public  WSMenu POJO2WS(MenuPOJO pojo) throws Exception{
        WSMenu ws = new WSMenu();
        ws.setName(pojo.getName());
        ws.setDescription(pojo.getDescription());
        if (pojo.getMenuEntries()!=null) {
            WSMenuEntry[] wsSubMenus = new WSMenuEntry[pojo.getMenuEntries().size()];
            int i=0;
            for (Iterator iter = pojo.getMenuEntries().iterator(); iter.hasNext(); ) {
                MenuEntryPOJO menuEntry = (MenuEntryPOJO) iter.next();
                wsSubMenus[i++] = POJO2WS(menuEntry);
            }
            ws.setMenuEntries(wsSubMenus);
        }
        return ws;
    }
    
    public  MenuPOJO WS2POJO(WSMenu ws) throws Exception{
        MenuPOJO pojo = new MenuPOJO();
        pojo.setName(ws.getName());
        pojo.setDescription(ws.getDescription());
        ArrayList<MenuEntryPOJO> menuEntries = new ArrayList<MenuEntryPOJO>();
        if (ws.getMenuEntries()!=null) {
            for (int i = 0; i < ws.getMenuEntries().length; i++) {
                menuEntries.add(WS2POJO(ws.getMenuEntries()[i]));
            }
        }
        pojo.setMenuEntries(menuEntries);
        return pojo;
    }   
    
    public static WSMenuEntry POJO2WS(MenuEntryPOJO pojo) throws Exception{
        WSMenuEntry ws = new WSMenuEntry();
        ws.setId(pojo.getId());
        Set<String> languages = pojo.getDescriptions().keySet();
        WSMenuEntryDescriptions[] wsDescriptions = new WSMenuEntryDescriptions[languages.size()];
        int i=0;
        for (Iterator iter = languages.iterator(); iter.hasNext(); ) {
            String language = (String) iter.next();
            wsDescriptions[i] = new WSMenuEntryDescriptions();
            wsDescriptions[i].setLanguage(language);
            wsDescriptions[i].setLabel(pojo.getDescriptions().get(language));
            i++;
        }
        ws.setDescriptions(wsDescriptions);
        ws.setContext(pojo.getContext());
        ws.setApplication(pojo.getApplication());
        ws.setIcon(pojo.getIcon());
        if (pojo.getSubMenus()!=null) {
            WSMenuEntry[] wsSubMenus = new WSMenuEntry[pojo.getSubMenus().size()];
            i=0;
            for (Iterator iter = pojo.getSubMenus().iterator(); iter.hasNext(); ) {
                MenuEntryPOJO menuEntry = (MenuEntryPOJO) iter.next();
                wsSubMenus[i++] = POJO2WS(menuEntry);
            }
            ws.setSubMenus(wsSubMenus);
        }
        return ws;
    }

    public static MenuEntryPOJO WS2POJO(WSMenuEntry ws) throws Exception{
        MenuEntryPOJO pojo = new MenuEntryPOJO();
        pojo.setId(ws.getId());
        HashMap<String, String> descriptions = new HashMap<String, String>();
        if (ws.getDescriptions()!=null) {
            for (int i = 0; i < ws.getDescriptions().length; i++) {
                descriptions.put(
                        ws.getDescriptions()[i].getLanguage(),
                        ws.getDescriptions()[i].getLabel()
                );
            }
        }
        pojo.setDescriptions(descriptions);
        pojo.setContext(ws.getContext());
        pojo.setApplication(ws.getApplication());
        pojo.setIcon(ws.getIcon());
        ArrayList<MenuEntryPOJO> subMenus = new ArrayList<MenuEntryPOJO>();
        if (ws.getSubMenus()!=null) {
            for (int i = 0; i < ws.getSubMenus().length; i++) {
                subMenus.add(WS2POJO(ws.getSubMenus()[i]));
            }
        }
        pojo.setSubMenus(subMenus);
        return pojo;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#checkSchema(com.amalto.webapp.util.webservices.WSCheckSchema)
     */
    @Override
    public WSString checkSchema(WSCheckSchema wsSchema) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#checkServiceConfiguration(com.amalto.webapp.util.webservices.WSCheckServiceConfigRequest)
     */
    @Override
    public WSCheckServiceConfigResponse checkServiceConfiguration(WSCheckServiceConfigRequest serviceName) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#connectorInteraction(com.amalto.webapp.util.webservices.WSConnectorInteraction)
     */
    @Override
    public WSConnectorInteractionResponse connectorInteraction(WSConnectorInteraction wsConnectorInteraction)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#count(com.amalto.webapp.util.webservices.WSCount)
     */
    @Override
    public WSString count(WSCount wsCount) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#countItemsByCustomFKFilters(com.amalto.webapp.util.webservices.WSCountItemsByCustomFKFilters)
     */
    @Override
    public WSString countItemsByCustomFKFilters(WSCountItemsByCustomFKFilters wsCountItemsByCustomFKFilters)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteBusinessConcept(com.amalto.webapp.util.webservices.WSDeleteBusinessConcept)
     */
    @Override
    public WSString deleteBusinessConcept(WSDeleteBusinessConcept wsDeleteBusinessConcept) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteDataCluster(com.amalto.webapp.util.webservices.WSDeleteDataCluster)
     */
    @Override
    public WSDataClusterPK deleteDataCluster(WSDeleteDataCluster wsDeleteDataCluster) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteDataModel(com.amalto.webapp.util.webservices.WSDeleteDataModel)
     */
    @Override
    public WSDataModelPK deleteDataModel(WSDeleteDataModel wsDeleteDataModel) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteItem(com.amalto.webapp.util.webservices.WSDeleteItem)
     */
    @Override
    public WSItemPK deleteItem(WSDeleteItem wsDeleteItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteItems(com.amalto.webapp.util.webservices.WSDeleteItems)
     */
    @Override
    public WSInt deleteItems(WSDeleteItems wsDeleteItems) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteMDMJob(com.amalto.webapp.util.webservices.WSDELMDMJob)
     */
    @Override
    public WSBoolean deleteMDMJob(WSDELMDMJob deleteMDMJobRequest) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteRoutingOrderV2(com.amalto.webapp.util.webservices.WSDeleteRoutingOrderV2)
     */
    @Override
    public WSRoutingOrderV2PK deleteRoutingOrderV2(WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteRoutingRule(com.amalto.webapp.util.webservices.WSDeleteRoutingRule)
     */
    @Override
    public WSRoutingRulePK deleteRoutingRule(WSDeleteRoutingRule wsRoutingRuleDel) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteStoredProcedure(com.amalto.webapp.util.webservices.WSDeleteStoredProcedure)
     */
    @Override
    public WSStoredProcedurePK deleteStoredProcedure(WSDeleteStoredProcedure wsStoredProcedureDelete) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteTransformer(com.amalto.webapp.util.webservices.WSDeleteTransformer)
     */
    @Override
    public WSTransformerPK deleteTransformer(WSDeleteTransformer wsTransformerDelete) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteTransformerV2(com.amalto.webapp.util.webservices.WSDeleteTransformerV2)
     */
    @Override
    public WSTransformerV2PK deleteTransformerV2(WSDeleteTransformerV2 wsDeleteTransformerV2) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#deleteView(com.amalto.webapp.util.webservices.WSDeleteView)
     */
    @Override
    public WSViewPK deleteView(WSDeleteView wsViewDel) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#dropItem(com.amalto.webapp.util.webservices.WSDropItem)
     */
    @Override
    public WSDroppedItemPK dropItem(WSDropItem wsDropItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#executeRoutingOrderV2Asynchronously(com.amalto.webapp.util.webservices.WSExecuteRoutingOrderV2Asynchronously)
     */
    @Override
    public WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(
            WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#executeRoutingOrderV2Synchronously(com.amalto.webapp.util.webservices.WSExecuteRoutingOrderV2Synchronously)
     */
    @Override
    public WSString executeRoutingOrderV2Synchronously(WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#executeStoredProcedure(com.amalto.webapp.util.webservices.WSExecuteStoredProcedure)
     */
    @Override
    public WSStringArray executeStoredProcedure(WSExecuteStoredProcedure wsExecuteStoredProcedure) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#executeTransformerV2(com.amalto.webapp.util.webservices.WSExecuteTransformerV2)
     */
    @Override
    public WSTransformerContext executeTransformerV2(WSExecuteTransformerV2 wsExecuteTransformerV2) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#executeTransformerV2AsJob(com.amalto.webapp.util.webservices.WSExecuteTransformerV2AsJob)
     */
    @Override
    public WSBackgroundJobPK executeTransformerV2AsJob(WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#existsDBDataCluster(com.amalto.webapp.util.webservices.WSExistsDBDataCluster)
     */
    @Override
    public WSBoolean existsDBDataCluster(WSExistsDBDataCluster wsExistsDBDataCluster) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#existsDataCluster(com.amalto.webapp.util.webservices.WSExistsDataCluster)
     */
    @Override
    public WSBoolean existsDataCluster(WSExistsDataCluster wsExistsDataCluster) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#existsDataModel(com.amalto.webapp.util.webservices.WSExistsDataModel)
     */
    @Override
    public WSBoolean existsDataModel(WSExistsDataModel wsDataModelExists) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#existsItem(com.amalto.webapp.util.webservices.WSExistsItem)
     */
    @Override
    public WSBoolean existsItem(WSExistsItem wsExistsItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#existsRoutingOrderV2(com.amalto.webapp.util.webservices.WSExistsRoutingOrderV2)
     */
    @Override
    public WSRoutingOrderV2 existsRoutingOrderV2(WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#existsRoutingRule(com.amalto.webapp.util.webservices.WSExistsRoutingRule)
     */
    @Override
    public WSBoolean existsRoutingRule(WSExistsRoutingRule wsExistsRoutingRule) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#existsStoredProcedure(com.amalto.webapp.util.webservices.WSExistsStoredProcedure)
     */
    @Override
    public WSBoolean existsStoredProcedure(WSExistsStoredProcedure wsExistsStoredProcedure) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#existsTransformer(com.amalto.webapp.util.webservices.WSExistsTransformer)
     */
    @Override
    public WSBoolean existsTransformer(WSExistsTransformer wsExistsTransformer) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#existsTransformerPluginV2(com.amalto.webapp.util.webservices.WSExistsTransformerPluginV2)
     */
    @Override
    public WSBoolean existsTransformerPluginV2(WSExistsTransformerPluginV2 wsExistsTransformerPluginV2) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#existsTransformerV2(com.amalto.webapp.util.webservices.WSExistsTransformerV2)
     */
    @Override
    public WSBoolean existsTransformerV2(WSExistsTransformerV2 wsExistsTransformerV2) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#existsView(com.amalto.webapp.util.webservices.WSExistsView)
     */
    @Override
    public WSBoolean existsView(WSExistsView wsViewPK) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#extractThroughTransformerV2(com.amalto.webapp.util.webservices.WSExtractThroughTransformerV2)
     */
    @Override
    public WSTransformerContext extractThroughTransformerV2(WSExtractThroughTransformerV2 wsExtractThroughTransformerV2)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#extractUsingTransformer(com.amalto.webapp.util.webservices.WSExtractUsingTransformer)
     */
    @Override
    public WSPipeline extractUsingTransformer(WSExtractUsingTransformer wsExtractUsingTransformer) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#extractUsingTransformerThruView(com.amalto.webapp.util.webservices.WSExtractUsingTransformerThruView)
     */
    @Override
    public WSPipeline extractUsingTransformerThruView(WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#findAllDroppedItemsPKs(com.amalto.webapp.util.webservices.WSFindAllDroppedItemsPKs)
     */
    @Override
    public WSDroppedItemPKArray findAllDroppedItemsPKs(WSFindAllDroppedItemsPKs regex) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#findBackgroundJobPKs(com.amalto.webapp.util.webservices.WSFindBackgroundJobPKs)
     */
    @Override
    public WSBackgroundJobPKArray findBackgroundJobPKs(WSFindBackgroundJobPKs status) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getAutoIncrement(com.amalto.webapp.util.webservices.WSAutoIncrement)
     */
    @Override
    public WSAutoIncrement getAutoIncrement(WSAutoIncrement wsAutoIncrementRequest) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getBackgroundJob(com.amalto.webapp.util.webservices.WSGetBackgroundJob)
     */
    @Override
    public WSBackgroundJob getBackgroundJob(WSGetBackgroundJob wsGetBackgroundJob) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getBusinessConceptKey(com.amalto.webapp.util.webservices.WSGetBusinessConceptKey)
     */
    @Override
    public WSConceptKey getBusinessConceptKey(WSGetBusinessConceptKey wsGetBusinessConceptKey) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getBusinessConceptValue(com.amalto.webapp.util.webservices.WSGetBusinessConceptValue)
     */
    @Override
    public WSString getBusinessConceptValue(WSGetBusinessConceptValue wsGetBusinessConceptValue) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getBusinessConcepts(com.amalto.webapp.util.webservices.WSGetBusinessConcepts)
     */
    @Override
    public WSStringArray getBusinessConcepts(WSGetBusinessConcepts wsGetBusinessConcepts) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getChildrenItems(com.amalto.webapp.util.webservices.WSGetChildrenItems)
     */
    @Override
    public WSStringArray getChildrenItems(WSGetChildrenItems wsGetChildrenItems) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getComponentVersion(com.amalto.webapp.util.webservices.WSGetComponentVersion)
     */
    @Override
    public WSVersion getComponentVersion(WSGetComponentVersion wsGetComponentVersion) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getConceptsInDataCluster(com.amalto.webapp.util.webservices.WSGetConceptsInDataCluster)
     */
    @Override
    public WSStringArray getConceptsInDataCluster(WSGetConceptsInDataCluster wsGetConceptsInDataCluster) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getConceptsInDataClusterWithRevisions(com.amalto.webapp.util.webservices.WSGetConceptsInDataClusterWithRevisions)
     */
    @Override
    public WSConceptRevisionMap getConceptsInDataClusterWithRevisions(
            WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getCurrentUniverse(com.amalto.webapp.util.webservices.WSGetCurrentUniverse)
     */
    @Override
    public WSUniverse getCurrentUniverse(WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getDataCluster(com.amalto.webapp.util.webservices.WSGetDataCluster)
     */
    @Override
    public WSDataCluster getDataCluster(WSGetDataCluster wsDataClusterPK) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getDataClusterPKs(com.amalto.webapp.util.webservices.WSRegexDataClusterPKs)
     */
    @Override
    public WSDataClusterPKArray getDataClusterPKs(WSRegexDataClusterPKs regexp) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getDataModel(com.amalto.webapp.util.webservices.WSGetDataModel)
     */
    @Override
    public WSDataModel getDataModel(WSGetDataModel wsDataModelget) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getDataModelPKs(com.amalto.webapp.util.webservices.WSRegexDataModelPKs)
     */
    @Override
    public WSDataModelPKArray getDataModelPKs(WSRegexDataModelPKs regexp) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getFullPathValues(com.amalto.webapp.util.webservices.WSGetFullPathValues)
     */
    @Override
    public WSStringArray getFullPathValues(WSGetFullPathValues wsGetFullPathValues) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getItem(com.amalto.webapp.util.webservices.WSGetItem)
     */
    @Override
    public WSItem getItem(WSGetItem wsGetItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getItemPKsByCriteria(com.amalto.webapp.util.webservices.WSGetItemPKsByCriteria)
     */
    @Override
    public WSItemPKsByCriteriaResponse getItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getItemPKsByFullCriteria(com.amalto.webapp.util.webservices.WSGetItemPKsByFullCriteria)
     */
    @Override
    public WSItemPKsByCriteriaResponse getItemPKsByFullCriteria(WSGetItemPKsByFullCriteria wsGetItemPKsByFullCriteria)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getItems(com.amalto.webapp.util.webservices.WSGetItems)
     */
    @Override
    public WSStringArray getItems(WSGetItems wsGetItems) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getItemsByCustomFKFilters(com.amalto.webapp.util.webservices.WSGetItemsByCustomFKFilters)
     */
    @Override
    public WSStringArray getItemsByCustomFKFilters(WSGetItemsByCustomFKFilters wsGetItemsByCustomFKFilters)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getItemsPivotIndex(com.amalto.webapp.util.webservices.WSGetItemsPivotIndex)
     */
    @Override
    public WSStringArray getItemsPivotIndex(WSGetItemsPivotIndex wsGetItemsPivotIndex) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getMDMCategory(com.amalto.webapp.util.webservices.WSCategoryData)
     */
    @Override
    public WSCategoryData getMDMCategory(WSCategoryData wsCategoryDataRequest) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getMDMConfiguration()
     */
    @Override
    public WSMDMConfig getMDMConfiguration() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getMDMJob(com.amalto.webapp.util.webservices.WSMDMNULL)
     */
    @Override
    public WSMDMJobArray getMDMJob(WSMDMNULL mdmJobRequest) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getRoutingOrderV2(com.amalto.webapp.util.webservices.WSGetRoutingOrderV2)
     */
    @Override
    public WSRoutingOrderV2 getRoutingOrderV2(WSGetRoutingOrderV2 wsGetRoutingOrderV2) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getRoutingOrderV2PKsByCriteria(com.amalto.webapp.util.webservices.WSGetRoutingOrderV2PKsByCriteria)
     */
    @Override
    public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(
            WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getRoutingOrderV2SByCriteria(com.amalto.webapp.util.webservices.WSGetRoutingOrderV2SByCriteria)
     */
    @Override
    public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getRoutingRule(com.amalto.webapp.util.webservices.WSGetRoutingRule)
     */
    @Override
    public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRulePK) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getRoutingRulePKs(com.amalto.webapp.util.webservices.WSGetRoutingRulePKs)
     */
    @Override
    public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regexp) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getServiceConfiguration(com.amalto.webapp.util.webservices.WSServiceGetConfiguration)
     */
    @Override
    public WSString getServiceConfiguration(WSServiceGetConfiguration wsGetConfiguration) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getServiceDocument(com.amalto.webapp.util.webservices.WSString)
     */
    @Override
    public WSServiceGetDocument getServiceDocument(WSString serviceName) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getServicesList(com.amalto.webapp.util.webservices.WSGetServicesList)
     */
    @Override
    public WSServicesList getServicesList(WSGetServicesList wsGetServicesList) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getStoredProcedure(com.amalto.webapp.util.webservices.WSGetStoredProcedure)
     */
    @Override
    public WSStoredProcedure getStoredProcedure(WSGetStoredProcedure wsGetStoredProcedure) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getStoredProcedurePKs(com.amalto.webapp.util.webservices.WSRegexStoredProcedure)
     */
    @Override
    public WSStoredProcedurePKArray getStoredProcedurePKs(WSRegexStoredProcedure regex) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getTransformer(com.amalto.webapp.util.webservices.WSGetTransformer)
     */
    @Override
    public WSTransformer getTransformer(WSGetTransformer wsGetTransformer) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getTransformerPKs(com.amalto.webapp.util.webservices.WSGetTransformerPKs)
     */
    @Override
    public WSTransformerPKArray getTransformerPKs(WSGetTransformerPKs regex) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getTransformerPluginV2Configuration(com.amalto.webapp.util.webservices.WSTransformerPluginV2GetConfiguration)
     */
    @Override
    public WSString getTransformerPluginV2Configuration(WSTransformerPluginV2GetConfiguration wsGetConfiguration)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getTransformerPluginV2Details(com.amalto.webapp.util.webservices.WSGetTransformerPluginV2Details)
     */
    @Override
    public WSTransformerPluginV2Details getTransformerPluginV2Details(
            WSGetTransformerPluginV2Details wsGetTransformerPluginV2Details) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getTransformerPluginV2SList(com.amalto.webapp.util.webservices.WSGetTransformerPluginV2SList)
     */
    @Override
    public WSTransformerPluginV2SList getTransformerPluginV2SList(WSGetTransformerPluginV2SList wsGetTransformerPluginV2SList)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getTransformerV2(com.amalto.webapp.util.webservices.WSGetTransformerV2)
     */
    @Override
    public WSTransformerV2 getTransformerV2(WSGetTransformerV2 wsGetTransformerV2) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getTransformerV2PKs(com.amalto.webapp.util.webservices.WSGetTransformerV2PKs)
     */
    @Override
    public WSTransformerV2PKArray getTransformerV2PKs(WSGetTransformerV2PKs regex) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getView(com.amalto.webapp.util.webservices.WSGetView)
     */
    @Override
    public WSView getView(WSGetView wsViewPK) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#getViewPKs(com.amalto.webapp.util.webservices.WSGetViewPKs)
     */
    @Override
    public WSViewPKArray getViewPKs(WSGetViewPKs regexp) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#initMDM(com.amalto.webapp.util.webservices.WSInitData)
     */
    @Override
    public WSInt initMDM(WSInitData initData) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#isItemModifiedByOther(com.amalto.webapp.util.webservices.WSIsItemModifiedByOther)
     */
    @Override
    public WSBoolean isItemModifiedByOther(WSIsItemModifiedByOther wsItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#loadDroppedItem(com.amalto.webapp.util.webservices.WSLoadDroppedItem)
     */
    @Override
    public WSDroppedItem loadDroppedItem(WSLoadDroppedItem wsLoadDroppedItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#logout(com.amalto.webapp.util.webservices.WSLogout)
     */
    @Override
    public WSString logout(WSLogout wsLogout) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#processBytesUsingTransformer(com.amalto.webapp.util.webservices.WSProcessBytesUsingTransformer)
     */
    @Override
    public WSPipeline processBytesUsingTransformer(WSProcessBytesUsingTransformer wsProcessBytesUsingTransformer)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#processBytesUsingTransformerAsBackgroundJob(com.amalto.webapp.util.webservices.WSProcessBytesUsingTransformerAsBackgroundJob)
     */
    @Override
    public WSBackgroundJobPK processBytesUsingTransformerAsBackgroundJob(
            WSProcessBytesUsingTransformerAsBackgroundJob wsProcessBytesUsingTransformerAsBackgroundJob) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#processFileUsingTransformer(com.amalto.webapp.util.webservices.WSProcessFileUsingTransformer)
     */
    @Override
    public WSPipeline processFileUsingTransformer(WSProcessFileUsingTransformer wsProcessFileUsingTransformer)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#processFileUsingTransformerAsBackgroundJob(com.amalto.webapp.util.webservices.WSProcessFileUsingTransformerAsBackgroundJob)
     */
    @Override
    public WSBackgroundJobPK processFileUsingTransformerAsBackgroundJob(
            WSProcessFileUsingTransformerAsBackgroundJob wsProcessFileUsingTransformerAsBackgroundJob) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putBackgroundJob(com.amalto.webapp.util.webservices.WSPutBackgroundJob)
     */
    @Override
    public WSBackgroundJobPK putBackgroundJob(WSPutBackgroundJob wsPutBackgroundJob) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putBusinessConcept(com.amalto.webapp.util.webservices.WSPutBusinessConcept)
     */
    @Override
    public WSString putBusinessConcept(WSPutBusinessConcept wsPutBusinessConcept) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putBusinessConceptSchema(com.amalto.webapp.util.webservices.WSPutBusinessConceptSchema)
     */
    @Override
    public WSString putBusinessConceptSchema(WSPutBusinessConceptSchema wsPutBusinessConceptSchema) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putDBDataCluster(com.amalto.webapp.util.webservices.WSPutDBDataCluster)
     */
    @Override
    public WSBoolean putDBDataCluster(WSPutDBDataCluster wsDataCluster) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putDataCluster(com.amalto.webapp.util.webservices.WSPutDataCluster)
     */
    @Override
    public WSDataClusterPK putDataCluster(WSPutDataCluster wsDataCluster) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putDataModel(com.amalto.webapp.util.webservices.WSPutDataModel)
     */
    @Override
    public WSDataModelPK putDataModel(WSPutDataModel wsDataModel) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putItem(com.amalto.webapp.util.webservices.WSPutItem)
     */
    @Override
    public WSItemPK putItem(WSPutItem wsPutItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putItemArray(com.amalto.webapp.util.webservices.WSPutItemArray)
     */
    @Override
    public WSItemPKArray putItemArray(WSPutItemArray wsPutItemArray) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putItemWithCustomReport(com.amalto.webapp.util.webservices.WSPutItemWithCustomReport)
     */
    @Override
    public WSItemPK putItemWithCustomReport(WSPutItemWithCustomReport wsPutItemWithCustomReport) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putItemWithReport(com.amalto.webapp.util.webservices.WSPutItemWithReport)
     */
    @Override
    public WSItemPK putItemWithReport(WSPutItemWithReport wsPutItemWithReport) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putItemWithReportArray(com.amalto.webapp.util.webservices.WSPutItemWithReportArray)
     */
    @Override
    public WSItemPKArray putItemWithReportArray(WSPutItemWithReportArray wsPutItemWithReportArray) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putMDMJob(com.amalto.webapp.util.webservices.WSPUTMDMJob)
     */
    @Override
    public WSBoolean putMDMJob(WSPUTMDMJob putMDMJobRequest) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putRoutingRule(com.amalto.webapp.util.webservices.WSPutRoutingRule)
     */
    @Override
    public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putServiceConfiguration(com.amalto.webapp.util.webservices.WSServicePutConfiguration)
     */
    @Override
    public WSString putServiceConfiguration(WSServicePutConfiguration wsPutConfiguration) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putStoredProcedure(com.amalto.webapp.util.webservices.WSPutStoredProcedure)
     */
    @Override
    public WSStoredProcedurePK putStoredProcedure(WSPutStoredProcedure wsStoredProcedure) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putTransformer(com.amalto.webapp.util.webservices.WSPutTransformer)
     */
    @Override
    public WSTransformerPK putTransformer(WSPutTransformer wsTransformer) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putTransformerPluginV2Configuration(com.amalto.webapp.util.webservices.WSTransformerPluginV2PutConfiguration)
     */
    @Override
    public WSString putTransformerPluginV2Configuration(WSTransformerPluginV2PutConfiguration wsPutConfiguration)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putTransformerV2(com.amalto.webapp.util.webservices.WSPutTransformerV2)
     */
    @Override
    public WSTransformerV2PK putTransformerV2(WSPutTransformerV2 wsTransformerV2) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#putView(com.amalto.webapp.util.webservices.WSPutView)
     */
    @Override
    public WSViewPK putView(WSPutView wsView) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#quickSearch(com.amalto.webapp.util.webservices.WSQuickSearch)
     */
    @Override
    public WSStringArray quickSearch(WSQuickSearch wsQuickSearch) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#recoverDroppedItem(com.amalto.webapp.util.webservices.WSRecoverDroppedItem)
     */
    @Override
    public WSItemPK recoverDroppedItem(WSRecoverDroppedItem wsRecoverDroppedItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#removeDroppedItem(com.amalto.webapp.util.webservices.WSRemoveDroppedItem)
     */
    @Override
    public WSDroppedItemPK removeDroppedItem(WSRemoveDroppedItem wsRemoveDroppedItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#routeItemV2(com.amalto.webapp.util.webservices.WSRouteItemV2)
     */
    @Override
    public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#routingEngineV2Action(com.amalto.webapp.util.webservices.WSRoutingEngineV2Action)
     */
    @Override
    public WSRoutingEngineV2Status routingEngineV2Action(WSRoutingEngineV2Action wsRoutingEngineAction) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#runQuery(com.amalto.webapp.util.webservices.WSRunQuery)
     */
    @Override
    public WSStringArray runQuery(WSRunQuery wsRunQuery) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#serviceAction(com.amalto.webapp.util.webservices.WSServiceAction)
     */
    @Override
    public WSString serviceAction(WSServiceAction wsServiceAction) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#viewSearch(com.amalto.webapp.util.webservices.WSViewSearch)
     */
    @Override
    public WSStringArray viewSearch(WSViewSearch wsViewSearch) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.amalto.webapp.util.webservices.XtentisPort_PortType#xPathsSearch(com.amalto.webapp.util.webservices.WSXPathsSearch)
     */
    @Override
    public WSStringArray xPathsSearch(WSXPathsSearch wsXPathsSearch) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
        
}
