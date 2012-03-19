// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.delegator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.util.XtentisException;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public interface IItemCtrlDelegatorService {
	   public ArrayList<String> runItemsQuery(LinkedHashMap conceptPatternsToRevisionID, LinkedHashMap conceptPatternsToClusterName,
	            String forceMainPivot, ArrayList viewableFullPaths, com.amalto.xmlserver.interfaces.IWhereItem whereItem,
	            String orderBy, String direction, int start, int limit, int spellThreshold, boolean firstTotalCount, Map metaDataTypes, boolean withStartLimit)
	            throws XtentisException;

	   
	    public ArrayList<String> runChildrenItemsQuery(String clusterName, String conceptName, String[] PKXpaths, String FKXpath,
	            String labelXpath, String fatherPK, LinkedHashMap itemsRevisionIDs, String defaultRevisionID,
	            com.amalto.xmlserver.interfaces.IWhereItem whereItem, int start, int limit) throws XtentisException;
	   
	    public ArrayList<String> runPivotIndexQuery(String clusterName, String mainPivotName, LinkedHashMap pivotWithKeys,
	            LinkedHashMap itemsRevisionIDs, String defaultRevisionID, String[] indexPaths,
	            com.amalto.xmlserver.interfaces.IWhereItem whereItem, String[] pivotDirections, String[] indexDirections, int start,
	            int limit) throws XtentisException;
	    
	   public  ILocalUser getLocalUser() throws XtentisException;
	   
	   public ViewPOJO getViewPOJO(ViewPOJOPK viewPOJOPK)throws Exception;
	   
}
