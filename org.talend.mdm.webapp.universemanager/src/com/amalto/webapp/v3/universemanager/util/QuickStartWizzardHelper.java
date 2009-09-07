package com.amalto.webapp.v3.universemanager.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.directwebremoting.WebContext;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.v3.universemanager.bean.RoleEntry;
import com.amalto.webapp.v3.universemanager.bean.RoutingRuleEntry;
import com.amalto.webapp.v3.universemanager.bean.StoredProcedureEntry;
import com.amalto.webapp.v3.universemanager.bean.TransformEntry;
import com.amalto.webapp.v3.universemanager.bean.UniverseQuickStartContext;
import com.amalto.webapp.v3.universemanager.bean.UniverseQuickStartStore;

public class QuickStartWizzardHelper{

	public static final String UNIVERSE_QUICK_START_CONTEXT = "com.amalto.webapp.v3.universemanager.bean.UniverseQuickStartContext";

	public static final String UNIVERSE_QUICK_START_STORE = "com.amalto.webapp.v3.universemanager.bean.UniverseQuickStartStore";

	public static boolean isInTransaction(WebContext ctx) {

		Object statusObj = ctx.getSession().getAttribute(
				QuickStartWizzardHelper.UNIVERSE_QUICK_START_STORE);

		if (statusObj == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public static UniverseQuickStartStore getStore(HttpSession session) {

		UniverseQuickStartStore store = null;
		Object getStore = session.getAttribute(
				QuickStartWizzardHelper.UNIVERSE_QUICK_START_STORE);
		if (getStore != null)
			store = (UniverseQuickStartStore) getStore;

		return store;

	}

	public static UniverseQuickStartStore getStore(WebContext ctx) {

		UniverseQuickStartStore store = null;
		Object getStore = ctx.getSession().getAttribute(
				QuickStartWizzardHelper.UNIVERSE_QUICK_START_STORE);
		if (getStore != null)
			store = (UniverseQuickStartStore) getStore;

		return store;

	}
	
	public static UniverseQuickStartStore initStore(WebContext ctx) {
		
		UniverseQuickStartStore newUniverseQuickStartStore=new UniverseQuickStartStore();
		ctx.getSession().setAttribute(
				QuickStartWizzardHelper.UNIVERSE_QUICK_START_STORE,newUniverseQuickStartStore);
		return newUniverseQuickStartStore;

	}
	
	public static void refreshStore(WebContext ctx,boolean withBasicInfos){

		UniverseQuickStartStore store = (UniverseQuickStartStore) ctx.getSession().getAttribute(
				QuickStartWizzardHelper.UNIVERSE_QUICK_START_STORE);
		store.reset(withBasicInfos);

	}
	
	public static void updateStore(WebContext ctx,String fieldName,String newValue) throws IllegalAccessException, InvocationTargetException {

		UniverseQuickStartStore store = (UniverseQuickStartStore) ctx.getSession().getAttribute(
				QuickStartWizzardHelper.UNIVERSE_QUICK_START_STORE);
		BeanUtils.setProperty(store, fieldName, newValue);

	}
	
	public static void updateStore(WebContext ctx,List<RoleEntry> roleEntries,boolean isDeselectAll){

		UniverseQuickStartStore store = getStore(ctx);
		if(store!=null){
			store.setToCopyRoleEntries(roleEntries);
			store.setDeselectAllRoles(isDeselectAll);
		}

	}
	
	public static void updateStore4RoutingRule(WebContext ctx,List<RoutingRuleEntry> routingRuleEntries,boolean isDeselectAll){

		UniverseQuickStartStore store = getStore(ctx);
		if(store!=null){
			store.setToCopyRoutingRuleEntries(routingRuleEntries);
			store.setDeselectAllRoutingRules(isDeselectAll);
		}

	}
	
	public static void updateStore4Transformer(WebContext ctx,List<TransformEntry> transformEntries,boolean isDeselectAll){

		UniverseQuickStartStore store = getStore(ctx);
		if(store!=null){
			store.setToCopyTransformerEntries(transformEntries);
			store.setDeselectAllTransformers(isDeselectAll);
		}

	}
	
	public static void updateStore(WebContext ctx,StoredProcedureEntry storedProcedureEntry){

		UniverseQuickStartStore store = getStore(ctx);
		if(store!=null)store.setStoredProcedureEntry(storedProcedureEntry);

	}
	
	public static void updateContext(WebContext ctx,String newUniverseName) throws RemoteException, XtentisWebappException, Exception {
		
		UniversePOJO sourceUniverse = UniverseHandler.getUniversePOJO(newUniverseName);
		
		UniverseQuickStartContext context=new UniverseQuickStartContext(sourceUniverse);
		ctx.getSession().setAttribute(QuickStartWizzardHelper.UNIVERSE_QUICK_START_CONTEXT,context);

	}
	
    public static UniverseQuickStartContext getContext(HttpSession session) throws RemoteException, XtentisWebappException, Exception {
    	UniverseQuickStartContext context=null;
    	
		Object getCtx=session.getAttribute(QuickStartWizzardHelper.UNIVERSE_QUICK_START_CONTEXT);
		if(getCtx!=null)context=(UniverseQuickStartContext) getCtx;
		
		return context;
	}
    
    public static String getObjectRevisionFromContext(WebContext ctx,Class clazz) throws RemoteException, XtentisWebappException, Exception {
    	
    	String revision="";
    	UniverseQuickStartContext context=getContext(ctx.getSession());
		if(context!=null)revision=context.getSourceUniverse().getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(clazz));
		return revision;
		
	}
     
    public static void resetStoreAndContext(WebContext ctx) {
		
    	initStore(ctx);
    	ctx.getSession().setAttribute(QuickStartWizzardHelper.UNIVERSE_QUICK_START_CONTEXT,null);

	}
    
    public static String getTargetRevisionName(WebContext ctx) {
    	
		String targetUniverseName="";
    	UniverseQuickStartStore store=getStore(ctx);
    	if(store!=null)targetUniverseName=store.getTargetRevisionName();
    	
    	return targetUniverseName;

	}


}
