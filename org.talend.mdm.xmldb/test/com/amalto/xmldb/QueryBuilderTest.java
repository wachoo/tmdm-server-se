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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.amalto.xmldb.util.PartialXQLPackage;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
import com.amalto.xmlserver.interfaces.WhereAnd;
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
        String expected = "if ($pivot0/CriteriaName) then $pivot0/CriteriaName else <CriteriaName/>";
        String actual = queryBuilder.getXQueryReturn(viewableFullPaths, pivotsMap, totalCountOnfirstRow);
        assertEquals(expected, actual);
        
        //
        viewableFullPaths.clear();
        viewableFullPaths.add("Country/label");
        viewableFullPaths.add("Country/../../i");
        pivotsMap.clear();
        pivotsMap.put("$pivot0", "Country");
        totalCountOnfirstRow = false;
        expected = "<result>{if ($pivot0/label) then $pivot0/label else <label/>}{if ($pivot0/../../i) then $pivot0/../../i else <xi/>}</result>";
        actual = queryBuilder.getXQueryReturn(viewableFullPaths, pivotsMap, totalCountOnfirstRow);
        assertEquals(expected, actual);
    }

    public void testBuildWhereCondition() throws Exception {
        LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
        pivots.put("$pivot0", "BrowseItem");
        WhereCondition whereCond = new WhereCondition("BrowseItem/ViewPK", WhereCondition.EQUALS, "Browse_items_Product", null,
                false);
        Map<String, ArrayList<String>> metaDataTypes = null;

        String expected = "$pivot0/ViewPK eq \"Browse_items_Product\"";
        String actual = queryBuilder.buildWhereCondition(whereCond, pivots, metaDataTypes);
        assertEquals(expected, actual);
    }

    public void testBuildWhere() throws Exception {
        LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
        pivots.put("$pivot0", "Product");
        WhereAnd whereItem = new WhereAnd();
        whereItem.add(new WhereCondition("Product/Id", WhereCondition.CONTAINS, "231", WhereCondition.PRE_NONE, false));
        Map<String, ArrayList<String>> metaDataTypes = new HashMap<String, ArrayList<String>>();
        List<String> types = Arrays.asList(new String[] { "xsd:string" });
        metaDataTypes.put("Product/Id", new ArrayList<String>(types));

        String expected = " matches($pivot0/Id, \"231.*\" ,\"i\") ";
        String actual = queryBuilder.buildWhere(pivots, whereItem, metaDataTypes);
        assertEquals(expected, actual);

        // And condition
        whereItem.add(new WhereCondition("Product/Price", WhereCondition.GREATER_THAN, "10", WhereCondition.PRE_NONE, false));
        types = Arrays.asList(new String[] { "xsd:decimal" });
        metaDataTypes.put("Product/Price", new ArrayList<String>(types));
        expected = "( matches($pivot0/Id, \"231.*\" ,\"i\") ) and (number($pivot0/Price) gt 10)";

        actual = queryBuilder.buildWhere(pivots, whereItem, metaDataTypes);
        assertEquals(expected, actual);
    }

    public void testGetPagingString() {
        boolean withTotalCountOnFirstRow = true;
        PartialXQLPackage partialXQLPackage = new PartialXQLPackage();
        partialXQLPackage.setUseJoin(false);
        partialXQLPackage.setUseSubsequenceFirst(true);
        long start = 0;
        long limit = 10;
        String rawQuery = "for $pivot0 in subsequence($_leres0_,1,10) return <result>{<Id>{string($pivot0/Id)}</Id>}</result>";

        String expected = "let $_page_ :=\n";
        expected += "for $pivot0 in subsequence($_leres0_,1,10) return <result>{<Id>{string($pivot0/Id)}</Id>}</result>\n";
        expected += "return (<totalCount>{count($_leres0_)}</totalCount>, $_page_)";
        String actual = queryBuilder.getPagingString(withTotalCountOnFirstRow, partialXQLPackage, start, limit, rawQuery);
        assertEquals(expected, actual);
    }

    public void testGetQuery() throws Exception {
        boolean isItemQuery = true;
        LinkedHashMap<String, String> objectRootElementNamesToRevisionID = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> objectRootElementNamesToClusterName = new LinkedHashMap<String, String>();
        objectRootElementNamesToClusterName.put(".*", "Product");
        String forceMainPivot = null;
        ArrayList<String> viewableFullPaths = new ArrayList<String>();
        viewableFullPaths.add("Product/Id");
        IWhereItem whereItem = null;
        String orderBy = null;
        String direction = null;
        int start = 0;
        long limit = 4;
        boolean withTotalCountOnFirstRow = true;
        Map<String, ArrayList<String>> metaDataTypes = null;

        String expected = "let $_leres0_ := collection(\"/Product\")//p/Product \n";
        expected += "let $_page_ :=\n";
        expected += "for $pivot0 in subsequence($_leres0_,1,4)\n";
        expected += "return <result>{if ($pivot0/Id) then $pivot0/Id else <Id/>}</result>\n";
        expected += "return (<totalCount>{count($_leres0_)}</totalCount>, $_page_)";

        String actual = queryBuilder.getQuery(isItemQuery, objectRootElementNamesToRevisionID,
                objectRootElementNamesToClusterName, forceMainPivot, viewableFullPaths, whereItem, orderBy, direction, start,
                limit, withTotalCountOnFirstRow, metaDataTypes);
        assertEquals(expected, actual);

        // Using where condition
        whereItem = new WhereCondition("Product/Id", WhereCondition.CONTAINS, "231", WhereCondition.PRE_NONE, false);
        expected = "let $_leres0_ := collection(\"/Product\")//p/Product [  matches(Id, \"231.*\" ,\"i\")  ]  \n";
        expected += "let $_page_ :=\n";
        expected += "for $pivot0 in subsequence($_leres0_,1,4)\n";
        expected += "return <result>{if ($pivot0/Id) then $pivot0/Id else <Id/>}</result>\n";
        expected += "return (<totalCount>{count($_leres0_)}</totalCount>, $_page_)";

        actual = queryBuilder.getQuery(isItemQuery, objectRootElementNamesToRevisionID, objectRootElementNamesToClusterName,
                forceMainPivot, viewableFullPaths, whereItem, orderBy, direction, start, limit, withTotalCountOnFirstRow,
                metaDataTypes);
        assertEquals(expected, actual);
        
        //
        isItemQuery = true;
        objectRootElementNamesToRevisionID.clear();
        objectRootElementNamesToClusterName.clear();
        objectRootElementNamesToClusterName.put(".*", "DStar");
        forceMainPivot = null;
        viewableFullPaths.clear();
        viewableFullPaths.add("Country/label");
        viewableFullPaths.add("Country/../../i");
        whereItem = null;
        orderBy = "Country/label";
        direction = null;
        start = 0;
        limit = 20;
        withTotalCountOnFirstRow = false;
        metaDataTypes = null;

        expected = "let $_leres0_ :=  for $r in collection(\"/DStar\")//p/Country order by $r/label return $r  \n";
        expected += "for $pivot0 in subsequence($_leres0_,1,20)\n";
        expected += "return <result>{if ($pivot0/label) then $pivot0/label else <label/>}{if ($pivot0/../../i) then $pivot0/../../i else <xi/>}</result>";

        actual = queryBuilder.getQuery(isItemQuery, objectRootElementNamesToRevisionID, objectRootElementNamesToClusterName,
                forceMainPivot, viewableFullPaths, whereItem, orderBy, direction, start, limit, withTotalCountOnFirstRow,
                metaDataTypes);
        assertEquals(expected, actual);
    }

    public void testGetQueryWithJoin() throws Exception {
        boolean isItemQuery = true;
        LinkedHashMap<String, String> objectRootElementNamesToRevisionID = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> objectRootElementNamesToClusterName = new LinkedHashMap<String, String>();
        objectRootElementNamesToClusterName.put(".*", "Product");
        String forceMainPivot = null;
        ArrayList<String> viewableFullPaths = new ArrayList<String>();
        viewableFullPaths.add("Product/Name");
        viewableFullPaths.add("ProductFamily/Name");
        IWhereItem whereItem = new WhereCondition("Product/Family", WhereCondition.JOINS, "ProductFamily/Id",
                WhereCondition.PRE_NONE, false);
        String orderBy = null;
        String direction = null;
        int start = 0;
        long limit = Integer.MAX_VALUE;
        boolean withTotalCountOnFirstRow = false;
        Map<String, ArrayList<String>> metaDataTypes = null;

        String expected = "let $_leres0_ := collection(\"/Product\")//p/Product \n";
        expected += "let $_leres1_ := collection(\"/Product\")//p/ProductFamily \n";
        expected += "for $pivot0 in $_leres0_, $pivot1 in $_leres1_\n";
        expected += " where $pivot0/Family[not(.) or not(text()) or .]=concat(\"[\",$pivot1/Id,\"]\")\n\n";
        expected += "return <result>{if ($pivot0/Name) then $pivot0/Name else <Name/>}{if ($pivot1/Name) then $pivot1/Name else <Name/>}</result>";

        String actual = queryBuilder.getQuery(isItemQuery, objectRootElementNamesToRevisionID,
                objectRootElementNamesToClusterName, forceMainPivot, viewableFullPaths, whereItem, orderBy, direction, start,
                limit, withTotalCountOnFirstRow, metaDataTypes);
        assertEquals(expected, actual);

        // Using paging
        limit = 8;
        withTotalCountOnFirstRow = true;
        expected = "let $_leres0_ := collection(\"/Product\")//p/Product \n";
        expected += "let $_leres1_ := collection(\"/Product\")//p/ProductFamily \n";
        expected += "let $_page_ :=\n";
        expected += "for $pivot0 in $_leres0_, $pivot1 in $_leres1_\n";
        expected += " where $pivot0/Family[not(.) or not(text()) or .]=concat(\"[\",$pivot1/Id,\"]\")\n\n";
        expected += "return <result>{if ($pivot0/Name) then $pivot0/Name else <Name/>}{if ($pivot1/Name) then $pivot1/Name else <Name/>}</result>\n";
        expected += "return (<totalCount>{count($_page_)}</totalCount>, subsequence($_page_,1,8))";

        actual = queryBuilder.getQuery(isItemQuery, objectRootElementNamesToRevisionID, objectRootElementNamesToClusterName,
                forceMainPivot, viewableFullPaths, whereItem, orderBy, direction, start, limit, withTotalCountOnFirstRow,
                metaDataTypes);
        assertEquals(expected, actual);
    }

    public void testBuildPKsByCriteriaQuery() throws Exception {
        ItemPKCriteria criteria = new ItemPKCriteria();
        criteria.setRevisionId(null);
        criteria.setClusterName("DStar");
        criteria.setConceptName("Agent");
        criteria.setContentKeywords("");
        criteria.setKeysKeywords("$Agent/AgencyFK$[1]");
        criteria.setCompoundKeyKeywords(true);
        criteria.setFromDate(-1L);
        criteria.setToDate(-1L);
        criteria.setMaxItems(20);
        criteria.setSkip(0);
        criteria.setUseFTSearch(false);

        String expected = "let $allres := collection(\"/DStar\")/ii[./p//Agent/AgencyFK eq '[1]'][./n eq 'Agent']\n";
        expected += "let $res := for $ii in subsequence($allres, 1,20)\n";
        expected += "return <r>{$ii/t}{$ii/taskId}{$ii/n}<ids>{$ii/i}</ids></r>\n";
        expected += "return (<totalCount>{count($allres)}</totalCount>, $res)";
        String actual = queryBuilder.buildPKsByCriteriaQuery(criteria);
        assertEquals(expected, actual);
        
        criteria = new ItemPKCriteria();
        criteria.setRevisionId(null);
        criteria.setClusterName("DStar");
        criteria.setConceptName("Agent");
        criteria.setContentKeywords("");
        criteria.setKeysKeywords("Agent/AgencyFK[1]");
        criteria.setCompoundKeyKeywords(false);
        criteria.setFromDate(-1L);
        criteria.setToDate(-1L);
        criteria.setMaxItems(20);
        criteria.setSkip(0);
        criteria.setUseFTSearch(false);

        expected = "let $allres := collection(\"/DStar\")/ii[matches(./i , 'Agent/AgencyFK[1]')][./n eq 'Agent']\n";
        expected += "let $res := for $ii in subsequence($allres, 1,20)\n";
        expected += "return <r>{$ii/t}{$ii/taskId}{$ii/n}<ids>{$ii/i}</ids></r>\n";
        expected += "return (<totalCount>{count($allres)}</totalCount>, $res)";
        actual = queryBuilder.buildPKsByCriteriaQuery(criteria);
        assertEquals(expected, actual);
    }
}
