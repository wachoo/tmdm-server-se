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
package com.amalto.xmldb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.amalto.xmlserver.interfaces.*;
import junit.framework.TestCase;

import com.amalto.xmldb.util.PartialXQLPackage;

@SuppressWarnings("nls")
public class QueryBuilderTest extends TestCase {

    private TestQueryBuilder queryBuilder;

    @Override
    protected void setUp() throws Exception {
        queryBuilder = new TestQueryBuilder();
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
        String expected = "let $element := $pivot0/CriteriaName return if (not(empty($element))) then $element else <CriteriaName/>";
        String actual = queryBuilder.getXQueryReturn(viewableFullPaths, pivotsMap, totalCountOnfirstRow);
        assertEquals(expected, actual);

        //
        viewableFullPaths.clear();
        viewableFullPaths.add("Country/label");
        viewableFullPaths.add("Country/../../i");
        pivotsMap.clear();
        pivotsMap.put("$pivot0", "Country");
        totalCountOnfirstRow = false;
        expected = "<result>{let $element := $pivot0/label return if (not(empty($element))) then $element else <label/>}{let $element := $pivot0/../../i return if (not(empty($element))) then $element else <xi/>}</result>";
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
        List<String> types = Arrays.asList("xsd:string");
        metaDataTypes.put("Product/Id", new ArrayList<String>(types));

        String expected = " matches($pivot0/Id, \"231.*\" ,\"i\") ";
        String actual = queryBuilder.buildWhere(pivots, whereItem, metaDataTypes);
        assertEquals(expected, actual);

        // And condition
        whereItem.add(new WhereCondition("Product/Price", WhereCondition.GREATER_THAN, "10", WhereCondition.PRE_NONE, false));
        types = Arrays.asList("xsd:decimal");
        metaDataTypes.put("Product/Price", new ArrayList<String>(types));
        expected = "( matches($pivot0/Id, \"231.*\" ,\"i\") ) and ($pivot0/Price > 10)";

        actual = queryBuilder.buildWhere(pivots, whereItem, metaDataTypes);
        assertEquals(expected, actual);

        queryBuilder.setUseNumberFunction(true);
        expected = "( matches($pivot0/Id, \"231.*\" ,\"i\") ) and (number($pivot0/Price) gt 10)";

        actual = queryBuilder.buildWhere(pivots, whereItem, metaDataTypes);
        assertEquals(expected, actual);

        whereItem = new WhereAnd();
        whereItem.add(new WhereCondition("Product/../*", WhereCondition.CONTAINS, "MyString", WhereCondition.PRE_NONE, false));
        metaDataTypes.clear();
        expected = " matches($pivot0/../*, \"MyString.*\" ,\"i\") ";

        actual = queryBuilder.buildWhere(pivots, whereItem, metaDataTypes);
        assertEquals(expected, actual);

        // case for double number
        whereItem.add(new WhereCondition("Product/Price", WhereCondition.LOWER_THAN, "80d", WhereCondition.PRE_NONE, false));
        types = Arrays.asList("xsd:decimal");
        metaDataTypes.put("Product/Price", new ArrayList<String>(types));
        expected = "( matches($pivot0/../*, \"MyString.*\" ,\"i\") ) and ($pivot0/Price < 80)";

        queryBuilder.setUseNumberFunction(false);
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
        String actual = queryBuilder.getPagingString(withTotalCountOnFirstRow, partialXQLPackage, start, limit, rawQuery,
                Collections.<String> emptyList());
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
        expected += "return <result>{let $element := $pivot0/Id return if (not(empty($element))) then $element else <Id/>}</result>\n";
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
        expected += "return <result>{let $element := $pivot0/Id return if (not(empty($element))) then $element else <Id/>}</result>\n";
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
        expected += "return <result>{let $element := $pivot0/label return if (not(empty($element))) then $element else <label/>}{let $element := $pivot0/../../i return if (not(empty($element))) then $element else <xi/>}</result>";

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
        expected += "return <result>{let $element := $pivot0/Name return if (not(empty($element))) then $element else <Name/>}{let $element := $pivot1/Name return if (not(empty($element))) then $element else <Name/>}</result>";

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
        expected += "return <result>{let $element := $pivot0/Name return if (not(empty($element))) then $element else <Name/>}{let $element := $pivot1/Name return if (not(empty($element))) then $element else <Name/>}</result>\n";
        expected += "return (<totalCount>{count($_page_)}</totalCount>, subsequence($_page_,1,8))";

        actual = queryBuilder.getQuery(isItemQuery, objectRootElementNamesToRevisionID, objectRootElementNamesToClusterName,
                forceMainPivot, viewableFullPaths, whereItem, orderBy, direction, start, limit, withTotalCountOnFirstRow,
                metaDataTypes);
        assertEquals(expected, actual);

        // Using composite FK
        objectRootElementNamesToRevisionID.clear();
        objectRootElementNamesToClusterName.clear();
        objectRootElementNamesToClusterName.put(".*", "DStar");
        forceMainPivot = null;
        viewableFullPaths.clear();
        viewableFullPaths.add("Agent/Id");
        viewableFullPaths.add("Agent/AgencyFK");
        viewableFullPaths.add("Agency/Name");
        whereItem = new WhereAnd();
        ((WhereAnd) whereItem).add(new WhereCondition("Agent/AgencyFK", WhereCondition.JOINS, "Agency/Id1",
                WhereCondition.PRE_NONE, false));
        ((WhereAnd) whereItem).add(new WhereCondition("Agent/AgencyFK", WhereCondition.JOINS, "Agency/Id2",
                WhereCondition.PRE_NONE, false));
        orderBy = null;
        direction = null;
        start = 0;
        limit = 10;
        withTotalCountOnFirstRow = true;
        metaDataTypes = null;

        expected = "let $_leres0_ := collection(\"/DStar\")//p/Agent \n";
        expected += "let $_leres1_ := collection(\"/DStar\")//p/Agency \n";
        expected += "let $_page_ :=\n";
        expected += "for $pivot0 in $_leres0_, $pivot1 in $_leres1_\n";
        expected += " where $pivot0/AgencyFK[not(.) or not(text()) or .]=concat(concat(\"[\",$pivot1/Id1,\"]\")\n";
        expected += ",concat(\"[\",$pivot1/Id2,\"]\")\n";
        expected += ")\n";
        expected += "return <result>{let $element := $pivot0/Id return if (not(empty($element))) then $element else <Id/>}{let $element := $pivot0/AgencyFK return if (not(empty($element))) then $element else <AgencyFK/>}{let $element := $pivot1/Name return if (not(empty($element))) then $element else <Name/>}</result>\n";
        expected += "return (<totalCount>{count($_page_)}</totalCount>, subsequence($_page_,1,10))";

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
        criteria.setKeysKeywords("Agent/AgencyFK[1]");
        criteria.setCompoundKeyKeywords(false);
        criteria.setFromDate(-1L);
        criteria.setToDate(-1L);
        criteria.setMaxItems(20);
        criteria.setSkip(0);
        criteria.setUseFTSearch(false);

        String expected = "let $allres := collection(\"/DStar\")/ii[matches(./i , 'Agent/AgencyFK[1]')][./n eq 'Agent']\n";
        expected += "let $res := for $ii in subsequence($allres, 1,20)\n";
        expected += "return <r>{$ii/t}{$ii/taskId}{$ii/n}<ids>{$ii/i}</ids></r>\n";
        expected += "return (<totalCount>{count($allres)}</totalCount>, $res)";
        String actual = queryBuilder.buildPKsByCriteriaQuery(criteria);
        assertEquals(expected, actual);

        // compound keywords
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

        expected = "let $allres := collection(\"/DStar\")/ii[./p//Agent/AgencyFK eq '[1]'][./n eq 'Agent']\n";
        expected += "let $res := for $ii in subsequence($allres, 1,20)\n";
        expected += "return <r>{$ii/t}{$ii/taskId}{$ii/n}<ids>{$ii/i}</ids></r>\n";
        expected += "return (<totalCount>{count($allres)}</totalCount>, $res)";
        actual = queryBuilder.buildPKsByCriteriaQuery(criteria);
        assertEquals(expected, actual);

        // compound keywords on composite FK
        criteria = new ItemPKCriteria();
        criteria.setRevisionId(null);
        criteria.setClusterName("DStar");
        criteria.setConceptName("Agent");
        criteria.setContentKeywords("");
        criteria.setKeysKeywords("$Agent/AgencyFK$[Id1@Id2@Id3]");
        criteria.setCompoundKeyKeywords(true);
        criteria.setFromDate(-1L);
        criteria.setToDate(-1L);
        criteria.setMaxItems(20);
        criteria.setSkip(0);
        criteria.setUseFTSearch(false);

        expected = "let $allres := collection(\"/DStar\")/ii[./p//Agent/AgencyFK eq '[Id1][Id2][Id3]'][./n eq 'Agent']\n";
        expected += "let $res := for $ii in subsequence($allres, 1,20)\n";
        expected += "return <r>{$ii/t}{$ii/taskId}{$ii/n}<ids>{$ii/i}</ids></r>\n";
        expected += "return (<totalCount>{count($allres)}</totalCount>, $res)";
        actual = queryBuilder.buildPKsByCriteriaQuery(criteria);
        assertEquals(expected, actual);

        // compound keywords on composite FK, has two xpath
        criteria = new ItemPKCriteria();
        criteria.setRevisionId(null);
        criteria.setClusterName("DStar");
        criteria.setConceptName("Agent");
        criteria.setContentKeywords("");
        criteria.setKeysKeywords("$Agent/AgencyFK,Agent/AgencyFK_A$[Id1@Id2@Id3]");
        criteria.setCompoundKeyKeywords(true);
        criteria.setFromDate(-1L);
        criteria.setToDate(-1L);
        criteria.setMaxItems(20);
        criteria.setSkip(0);
        criteria.setUseFTSearch(false);

        expected = "let $allres := collection(\"/DStar\")/ii[./p//Agent/AgencyFK eq '[Id1][Id2][Id3]' or ./p//Agent/AgencyFK_A eq '[Id1][Id2][Id3]'][./n eq 'Agent']\n";
        expected += "let $res := for $ii in subsequence($allres, 1,20)\n";
        expected += "return <r>{$ii/t}{$ii/taskId}{$ii/n}<ids>{$ii/i}</ids></r>\n";
        expected += "return (<totalCount>{count($allres)}</totalCount>, $res)";
        actual = queryBuilder.buildPKsByCriteriaQuery(criteria);
        assertEquals(expected, actual);
    }
    
    public void testQueryBuildWhereCondition() throws Exception {
        LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
        pivots.put("$pivot0", "Update");
        WhereCondition whereCond = new WhereCondition("Update/Key", WhereCondition.CONTAINS, "5000000001", null,
                false);
        Map<String, ArrayList<String>> metaDataTypes = null;

        String expected = " matches($pivot0/Key, \"5000000001.*\" ,\"i\") ";
        String actual = queryBuilder.buildWhereCondition(whereCond, pivots, metaDataTypes);
        assertEquals(expected, actual);
    }
    
    public void testQueryCompletedRoutingOrder() throws Exception {
        boolean isItemQuery = true;
        LinkedHashMap<String, String> objectRootElementNamesToRevisionID = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> objectRootElementNamesToClusterName = new LinkedHashMap<String, String>();
        objectRootElementNamesToClusterName.put(".*", "amaltoOBJECTSCompletedRoutingOrderV2");
        String forceMainPivot = null;
        ArrayList<String> viewableFullPaths = new ArrayList<String>();
        viewableFullPaths.add("completed-routing-order-v2-pOJO/name");
        viewableFullPaths.add("completed-routing-order-v2-pOJO/@status");
        IWhereItem whereItem = new WhereCondition("completed-routing-order-v2-pOJO/@time-last-run-started", WhereCondition.GREATER_THAN_OR_EQUAL, "-1",
                WhereCondition.PRE_NONE, false);
        String orderBy = null;
        String direction = null;
        int start = 0;
        long limit = 20;
        boolean withTotalCountOnFirstRow = true;
        Map<String, ArrayList<String>> metaDataTypes = Collections.emptyMap();

        String expected = "let $_leres0_ := collection(\"/amaltoOBJECTSCompletedRoutingOrderV2\")/completed-routing-order-v2-pOJO [ @time-last-run-started >= -1 ]  \n";
        expected += "let $_page_ :=\n";
        expected += "for $pivot0 in subsequence($_leres0_,1,20)\n";
        expected += "return <result>{let $element := $pivot0/name return if (not(empty($element))) then $element else <name/>}{<status>{string($pivot0/@status)}</status>}</result>\n";
        expected += "return (<totalCount>{count($_leres0_)}</totalCount>, $_page_)";

        String actual = queryBuilder.getQuery(isItemQuery, objectRootElementNamesToRevisionID,
                objectRootElementNamesToClusterName, forceMainPivot, viewableFullPaths, whereItem, orderBy, direction, start,
                limit, withTotalCountOnFirstRow, metaDataTypes);
        assertEquals(expected, actual);
    }

    private static class TestQueryBuilder extends QueryBuilder {

        private boolean useNumberFunction = false;

        @Override
        public String getUriQuery(boolean isItemQuery, Map<String, String> objectRootElementNamesToRevisionID, Map<String, String> objectRootElementNamesToClusterName, String forceMainPivot, ArrayList<String> viewableFullPaths, IWhereItem whereItem, boolean withTotalCountOnFirstRow, Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException {
            return "";
        }

        @Override
        protected boolean useNumberFunction() {
            return useNumberFunction;
        }

        public void setUseNumberFunction(boolean useNumberFunction) {
            this.useNumberFunction = useNumberFunction;
        }

        @Override
        public String getFullTextQueryString(String queryStr) {
            return null;
        }
    }
}
