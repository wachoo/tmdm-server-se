package com.amalto.core.delegator.impl;

import java.util.ArrayList;

import com.amalto.core.delegator.IItemCtrlDelegator;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.xmlserver.interfaces.IWhereItem;


public class DefaultItemCtrlDelegator extends IItemCtrlDelegator {

	@Override
	protected ArrayList<IWhereItem> getViewWCFromRole(ViewPOJOPK viewPOJOPK)
			throws Exception {
		return null;
	}	
}
