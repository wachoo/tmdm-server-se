// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.storage.hibernate;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

@SuppressWarnings("nls")
public class MDMStandardAnalyzerTest extends TestCase {

    private IndexSearcher searcher;

    private List<String> dataStrings = null;

    QueryParser parser = null;

    @Override
    public void setUp() throws Exception {
        dataStrings = Arrays.asList("text1 text2", "text2 text1", "test_123", "test_abc", "Test_Abc", "testab_c", "_testabc",
                "testabc_", "__testabc", "testabc__", "A_B_C", "a_b_c", "A__BC", "a__bc");

        Directory directory = new RAMDirectory();

        IndexWriter writer = new IndexWriter(directory, new MDMStandardAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);

        for (String valueStr : dataStrings) {
            Document doc = new Document();
            doc.add(new Field("entityField", valueStr, Field.Store.NO, Field.Index.ANALYZED));
            writer.addDocument(doc);
        }
        writer.close();

        searcher = new IndexSearcher(directory, true);

        parser = new QueryParser(Version.LUCENE_29, "entityField", new MDMStandardAnalyzer(Version.LUCENE_29));
    }

    public void testTermQuery() throws Exception {
        Query query = new TermQuery(new Term("entityField", "text1"));

        assertEquals("note text1 -> text1", "entityField:text1", query.toString());
        assertEquals("Expected hit number is not equal to actual hit number", 2, searcher.search(query, 10).totalHits);
    }

    public void testQueryParser() throws Exception {
        Query query = parser.parse("entityField:text2 text1");
        assertEquals("Wrong tokenization for -> text2 text1", "text2 text1", query.toString("entityField")); // 5
        assertEquals("Expected hit number is not equal to actual hit number", 2, searcher.search(query, 10).totalHits);
    }

    public void testQueryParser_undscore_in_mid1() throws Exception {
        Query query = parser.parse("entityField:test_abc");
        assertEquals("Wrong tokenization for -> test_abc", "test_abc", query.toString("entityField"));
        assertEquals("Expected hit number is not equal to actual hit number", 2, searcher.search(query, 10).totalHits);
    }

    public void testQueryParser_abcX_bug() throws Exception {
        parser.setLowercaseExpandedTerms(false);
        Query query = parser.parse("entityField:Test_Abc*");
        assertEquals("Wrong tokenization for -> Test_Abc*", "Test_Abc*", query.toString("entityField"));
        assertEquals("No doc expected", 0, searcher.search(query, 10).totalHits);
    }

    public void testQueryParser_abcX_bug_fixed() throws Exception {
        parser.setLowercaseExpandedTerms(true);
        Query query = parser.parse("entityField:Test_Abc*");
        assertEquals("Wrong tokenization for -> Test_Abc*", "test_abc*", query.toString("entityField"));
        assertEquals("Expected hit number is not equal to actual hit number", 2, searcher.search(query, 10).totalHits);
    }

    public void testQueryParser_undscore_in_mid1_wildcard() throws Exception {
        parser.setLowercaseExpandedTerms(true);
        Query query = parser.parse("entityField:t*abc");
        assertEquals("Wrong tokenization for -> t*abc", "t*abc", query.toString("entityField"));
        assertEquals("Expected hit number is not equal to actual hit number", 2, searcher.search(query, 10).totalHits);
    }

    public void testQueryParser_undscore_in_mid2() throws Exception {
        Query query = parser.parse("entityField:test_123");
        assertEquals("Wrong tokenization for -> test_123", "test_123", query.toString("entityField"));
        assertEquals("No doc found!", 1, searcher.search(query, 10).totalHits);
    }

    public void testQueryParser_c() throws Exception {
        Query query = parser.parse("entityField:testab_c");
        assertEquals("Wrong tokenization for -> testab_c", "testab_c", query.toString("entityField"));
        assertEquals("No doc found!", 1, searcher.search(query, 10).totalHits);
    }

    public void testQueryParser_cwithWildcard() throws Exception {
        Query query = parser.parse("entityField:testab_*");
        assertEquals("Wrong tokenization for -> testab_*", "testab_*", query.toString("entityField"));
        assertEquals("No doc found!", 1, searcher.search(query, 10).totalHits);
    }

    public void testQueryParserForTrailingUnderscores() throws Exception {
        Query query = parser.parse("entityField:__testabc");
        assertEquals("Wrong tokenization for -> __testabc", "__testabc", query.toString("entityField"));
        assertEquals("Expected hit number is not equal to actual hit number", 1, searcher.search(query, 10).totalHits);

        query = parser.parse("entityField:testabc__");
        assertEquals("Wrong tokenization for -> testabc__", "testabc__", query.toString("entityField"));
        assertEquals("Expected hit number is not equal to actual hit number", 1, searcher.search(query, 10).totalHits);
    }

    public void testQueryParserMulitUnderscores() throws Exception {
        Query query = parser.parse("entityField:A_B_C");
        assertEquals("Wrong tokenization for -> A_B_C", "a_b_c", query.toString("entityField"));
        assertEquals("Expected hit number is not equal to actual hit number", 2, searcher.search(query, 10).totalHits);

        query = parser.parse("entityField:A_*_C");
        assertEquals("Wrong tokenization for -> A_*_C", "a_*_c", query.toString("entityField"));
        assertEquals("Expected hit number is not equal to actual hit number", 2, searcher.search(query, 10).totalHits);

        query = parser.parse("entityField:A__BC");
        assertEquals("Wrong tokenization for -> A__BC", "a__bc", query.toString("entityField"));
        assertEquals("Expected hit number is not equal to actual hit number", 2, searcher.search(query, 10).totalHits);

        query = parser.parse("entityField:A_*BC");
        assertEquals("Wrong tokenization for -> A_*BC", "a_*bc", query.toString("entityField"));
        assertEquals("Expected hit number is not equal to actual hit number", 2, searcher.search(query, 10).totalHits);

        query = parser.parse("entityField:A_*C");
        assertEquals("Wrong tokenization for -> A_*C", "a_*c", query.toString("entityField"));
        assertEquals("Expected hit number is not equal to actual hit number", 4, searcher.search(query, 10).totalHits);
    }

}