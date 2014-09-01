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
package org.talend.mdm.storage.hibernate;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.util.Version;

/*
 * A StandardAnalyzer based on {@link MDMStandardTokenizerImpl}, which supports new token type 
 * for words containing underscore(s).
 */
public final class MDMStandardAnalyzer extends StandardAnalyzer {

    private Version luceneVersion = null;

    static StandardAnalyzer STANDARD = null;

    public MDMStandardAnalyzer() {
        new MDMStandardAnalyzer(Version.LUCENE_29);
    }

    public MDMStandardAnalyzer(Version version) {
        super(version);
        this.luceneVersion = version;
        STANDARD = new StandardAnalyzer(luceneVersion) {

            @Override
            public TokenStream tokenStream(String fieldName, Reader reader) {
                MDMStandardTokenizer tokenStream = new MDMStandardTokenizer(luceneVersion, reader);
                tokenStream.setMaxTokenLength(STANDARD.getMaxTokenLength());
                TokenStream result = new StandardFilter(tokenStream);
                result = new LowerCaseFilter(result);
                result = new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(luceneVersion), result,
                        StandardAnalyzer.STOP_WORDS_SET);
                return result;
            }
        };
    }

    public MDMStandardAnalyzer(Version matchVersion, final Set<String> stopWords) {
        super(matchVersion, stopWords);
        this.luceneVersion = matchVersion;
        STANDARD = new StandardAnalyzer(luceneVersion) {

            @Override
            public TokenStream tokenStream(String fieldName, Reader reader) {
                MDMStandardTokenizer tokenStream = new MDMStandardTokenizer(luceneVersion, reader);
                tokenStream.setMaxTokenLength(STANDARD.getMaxTokenLength());
                TokenStream result = new StandardFilter(tokenStream);
                result = new LowerCaseFilter(result);
                result = new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(luceneVersion), result, stopWords);
                return result;
            }
        };
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return STANDARD.tokenStream(fieldName, reader);
    }

    @Override
    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        return STANDARD.reusableTokenStream(fieldName, reader);
    }

}
