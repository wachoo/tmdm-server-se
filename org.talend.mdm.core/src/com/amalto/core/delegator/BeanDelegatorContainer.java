/**
 * 
 */
package com.amalto.core.delegator;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.talend.mdm.commmon.util.core.MDMConfiguration;


public class BeanDelegatorContainer {

	/** unique instance */
	private static BeanDelegatorContainer sInstance = null;
	/** delegator instances */
	private Map<String,BeanDelegator> delegatorInstancePool = new HashMap<String, BeanDelegator>();

	/** 
	 * Private constuctor
	 */
	private BeanDelegatorContainer() {
		super();
		init();
	}

	/** 
	 * Get the unique instance of this class.
	 */
	public static synchronized BeanDelegatorContainer getUniqueInstance() {

		if (sInstance == null) {
			sInstance = new BeanDelegatorContainer();
		}

		return sInstance;

	}
	
	private void init() {
		
		//get release type from config
		String releaseType=MDMConfiguration.getConfiguration().getProperty(
				"system.release.type", 
				BeanDelegatorConfigReader.OPEN_RELEASE_TYPE
			);
		
		BeanDelegatorConfigReader.init(releaseType);
		Map<String, String> beanImplNamesMap=BeanDelegatorConfigReader.getBeanImplNamesMap();
		for (Iterator<String> iterator = beanImplNamesMap.keySet().iterator(); iterator.hasNext();) {
			String interfaceShortName = iterator.next();
			String implName=beanImplNamesMap.get(interfaceShortName);
			
			//add to pool
			try {
				Object[] instanceParams=new Object[0];
				BeanDelegator beanDelegator=(BeanDelegator) newInstance(implName,instanceParams);
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
	public XtentisWSBeanDelegator getXtentisWSBeanDelegator() {
		return (XtentisWSBeanDelegator) delegatorInstancePool.get("XtentisWSBeanDelegator");
	}


}
