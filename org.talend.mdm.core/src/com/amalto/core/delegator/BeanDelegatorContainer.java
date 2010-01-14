/**
 * 
 */
package com.amalto.core.delegator;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class BeanDelegatorContainer {

	/** unique instance */
	private static BeanDelegatorContainer sInstance = null;
	/** delegator instances */
	private Map<String,IBeanDelegator> delegatorInstancePool = new HashMap<String, IBeanDelegator>();

	/** 
	 * Private constuctor
	 */
	private BeanDelegatorContainer() {
		super();
		init();
	}

	/** 
	 * Get the unique instance of this class.
	 * In order to improve the performance, removed synchronized, using pseudo singleton mode
	 */
	public static BeanDelegatorContainer getUniqueInstance() {

		if (sInstance == null) {
			sInstance = new BeanDelegatorContainer();
		}

		return sInstance;

	}
	
	private void init() {
		
		BeanDelegatorConfigReader.init();
		Map<String, String> beanImplNamesMap=BeanDelegatorConfigReader.getBeanImplNamesMap();
		for (Iterator<String> iterator = beanImplNamesMap.keySet().iterator(); iterator.hasNext();) {
			String interfaceShortName = iterator.next();
			String implName=beanImplNamesMap.get(interfaceShortName);
			
			//add to pool
			try {
				Object[] instanceParams=new Object[0];
				IBeanDelegator beanDelegator=(IBeanDelegator) newInstance(implName,instanceParams);
				delegatorInstancePool.put(interfaceShortName, beanDelegator);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			org.apache.log4j.Logger.getLogger(this.getClass()).info("Init instance:"+implName);
		}

	}
	
	private Object newInstance(String className, Object[] args) throws Exception {
        Class newoneClass = Class.forName(className);
        Class[] argsClass = new Class[args.length];
        for (int i = 0, j = args.length; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }
        Constructor cons = newoneClass.getConstructor(argsClass);
        return cons.newInstance(args);
    }
	
	//TODO add more delegator get method
	public ILocalUser getLocalUserDelegator() {
		return (ILocalUser) delegatorInstancePool.get("LocalUser");
	}
	public IValidation getValidationDelegator() {
		return (IValidation) delegatorInstancePool.get("Validation");
	}
	public IItemCtrlDelegator getItemCtrlDelegator() {
		return (IItemCtrlDelegator) delegatorInstancePool.get("ItemCtrl");
	}
	public IXtentisWSDelegator getXtentisWSDelegator() {
		return (IXtentisWSDelegator) delegatorInstancePool.get("XtentisWS");
	}
}
