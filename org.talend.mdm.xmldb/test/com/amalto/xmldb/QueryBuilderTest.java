// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.xmldb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.amalto.xmldb.util.PartialXQLPackage;
import com.amalto.xmlserver.interfaces.WhereCondition;

@SuppressWarnings("nls")
public class QueryBuilderTest extends TestCase {

    private QueryBuilder queryBuilder;

    @Override
    protected void setUp() throws Exception {
        queryBuilder = new QueryBuilder() {

            @Override
            public String getFullTextQueryString(String queryStr) {
                return null;
            }
        };
    }

    @Override
    protected void tearDown() throws Exception {
        queryBuilder = null;
    }

    public void testGetXQueryReturn() throws Exception {
        ArrayList<String> viewableFullPaths = new ArrayList<String>();
        viewableFullPaths.add("BrowseItem/CriteriaName");
        LinkedHashMap<String, String> pivotsMap = new LinkedHashMap<String, String>();
        pivotsMap.put("$pivot0", "BrowseItem");
        boolean totalCountOnfirstRow = false;
        String expected = "<CriteriaName>{string($pivot0/CriteriaName)}</CriteriaName>";
        String actual = queryBuilder.getXQueryReturn(viewableFullPaths, pivotsMap, totalCountOnfirstRow);
        assertEquals(expected, actual);
    }

    public void testBuildWhereConditionWhereCondition() throws Exception {
        LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
        pivots.put("$pivot0", "BrowseItem");
        WhereCondition whereCond = new WhereCondition("BrowseItem/ViewPK", "=", "Browse_items_Product", null, false);
        Map<String, ArrayList<String>> metaDataTypes = null;

        String expected = "$pivot0/ViewPK eq \"Browse_items_Product\"";
        String actual = queryBuilder.buildWhereCondition(whereCond, pivots, metaDataTypes);
        assertEquals(expected, actual);
    }

    public void testGetPagingString() {
        boolean withTotalCountOnFirstRow = true;
        PartialXQLPackage partialXQLPackage = new PartialXQLPackage();
        partialXQLPackage.setUseJoin(false);
        partialXQLPackage.setUseSubsequenceFirst(true);
        partialXQLPackage.getForInCollectionMap().put("$pivot0", "collection(\"/Product\")//p/Product");
        partialXQLPackage.setXqWhere("matches(Id, \"123.*\" ,\"i\")");
        long start = 0;
        long limit = 10;
        String rawQuery = "for $pivot0 in subsequence($_leres0_,1,10) return <result>{<Id>{string($pivot0/Id)}</Id>}</result>";

        String expected = "let $_page_ :=\n";
        expected += "for $pivot0 in subsequence($_leres0_,1,10) return <result>{<Id>{string($pivot0/Id)}</Id>}</result>\n";
        expected += "return (<totalCount>{count($_leres0_)}</totalCount>, $_page_)";
        String actual = queryBuilder.getPagingString(withTotalCountOnFirstRow, partialXQLPackage, start, limit, rawQuery);
        assertEquals(expected, actual);
    }
}
