package com.amalto.core.delegator.impl;

import java.util.ArrayList;

import com.amalto.core.delegator.IItemCtrlDelegator;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.xmlserver.interfaces.IWhereItem;


public class DefaultItemCtrlDelegator extends IItemCtrlDelegator {

	/* (non-Javadoc)
	 * @see com.amalto.core.delegator.IItemCtrlDelegator#getViewWCFromRole(com.amalto.core.objects.view.ejb.ViewPOJOPK)
	 */
	@Override
	protected ArrayList<IWhereItem> getViewWCFromRole(ViewPOJOPK viewPOJOPK)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.amalto.core.delegator.IItemCtrlDelegator#getRecordsSecurityFromRole(java.lang.String)
	 */
	@Override
	protected ArrayList<IWhereItem> getRecordsSecurityFromRole(
			String mainPivotName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	
}
