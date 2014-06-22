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

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

/*
 * A StandardTokenizer based on {@link MDMStandardTokenizerImpl}, which supports new token type 
 * for words containing underscore(s).
 */
public class MDMStandardTokenizer extends Tokenizer {

    /** A private instance of the JFlex-constructed scanner */
    private final MDMStandardTokenizerImpl scanner;

    public static final int ALPHANUM = 0;

    public static final int APOSTROPHE = 1;

    public static final int ACRONYM = 2;

    public static final int COMPANY = 3;

    public static final int EMAIL = 4;

    public static final int HOST = 5;

    public static final int NUM = 6;

    public static final int CJ = 7;

    public static final int UNDERSCORE = 9;

    /**
     * @deprecated this solves a bug where HOSTs that end with '.' are identified as ACRONYMs. It is deprecated and will
     * be removed in the next release.
     */
    @Deprecated
    public static final int ACRONYM_DEP = 8;

    /** String token types that correspond to token type int constants */
    public static final String[] TOKEN_TYPES = new String[] { "<ALPHANUM>", "<APOSTROPHE>", "<ACRONYM>", "<COMPANY>", "<EMAIL>",
            "<HOST>", "<NUM>", "<CJ>", "<ACRONYM_DEP>", "<UNDERSCORE>" };

    /** @deprecated Please use {@link #TOKEN_TYPES} instead */
    @Deprecated
    public static final String[] tokenImage = TOKEN_TYPES;

    /**
     * Specifies whether deprecated acronyms should be replaced with HOST type. This is false by default to support
     * backward compatibility.
     * <p/>
     * See http://issues.apache.org/jira/browse/LUCENE-1068
     * 
     * @deprecated this should be removed in the next release (3.0).
     */
    @Deprecated
    private boolean replaceInvalidAcronym;

    private int maxTokenLength = StandardAnalyzer.DEFAULT_MAX_TOKEN_LENGTH;

    /**
     * Set the max allowed token length. Any token longer than this is skipped.
     */
    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
    }

    /** @see #setMaxTokenLength */
    public int getMaxTokenLength() {
        return maxTokenLength;
    }

    @Deprecated
    public MDMStandardTokenizer(Reader input) {
        this(Version.LUCENE_24, input);
    }

    @Deprecated
    public MDMStandardTokenizer(Reader input, boolean replaceInvalidAcronym) {
        super();
        this.scanner = new MDMStandardTokenizerImpl(input);
        init(input, replaceInvalidAcronym);
    }

    public MDMStandardTokenizer(Version matchVersion, Reader input) {
        super();
        this.scanner = new MDMStandardTokenizerImpl(input);
        init(input, matchVersion);
    }

    @Deprecated
    public MDMStandardTokenizer(AttributeSource source, Reader input, boolean replaceInvalidAcronym) {
        super(source);
        this.scanner = new MDMStandardTokenizerImpl(input);
        init(input, replaceInvalidAcronym);
    }

    public MDMStandardTokenizer(Version matchVersion, AttributeSource source, Reader input) {
        super(source);
        this.scanner = new MDMStandardTokenizerImpl(input);
        init(input, matchVersion);
    }

    @Deprecated
    public MDMStandardTokenizer(AttributeFactory factory, Reader input, boolean replaceInvalidAcronym) {
        super(factory);
        this.scanner = new MDMStandardTokenizerImpl(input);
        init(input, replaceInvalidAcronym);
    }

    public MDMStandardTokenizer(Version matchVersion, AttributeFactory factory, Reader input) {
        super(factory);
        this.scanner = new MDMStandardTokenizerImpl(input);
        init(input, matchVersion);
    }

    private void init(Reader input, boolean replaceInvalidAcronym) {
        this.replaceInvalidAcronym = replaceInvalidAcronym;
        this.input = input;
        termAtt = (TermAttribute) addAttribute(TermAttribute.class);
        offsetAtt = (OffsetAttribute) addAttribute(OffsetAttribute.class);
        posIncrAtt = (PositionIncrementAttribute) addAttribute(PositionIncrementAttribute.class);
        typeAtt = (TypeAttribute) addAttribute(TypeAttribute.class);
    }

    private void init(Reader input, Version matchVersion) {
        if (matchVersion.onOrAfter(Version.LUCENE_24)) {
            init(input, true);
        } else {
            init(input, false);
        }
    }

    // this tokenizer generates three attributes:
    // offset, positionIncrement and type
    private TermAttribute termAtt;

    private OffsetAttribute offsetAtt;

    private PositionIncrementAttribute posIncrAtt;

    private TypeAttribute typeAtt;

    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        int posIncr = 1;

        while (true) {
            int tokenType = scanner.getNextToken();

            if (tokenType == MDMStandardTokenizerImpl.YYEOF) {
                return false;
            }

            if (scanner.yylength() <= maxTokenLength) {
                posIncrAtt.setPositionIncrement(posIncr);
                scanner.getText(termAtt);
                final int start = scanner.yychar();
                offsetAtt.setOffset(correctOffset(start), correctOffset(start + termAtt.termLength()));
                // This 'if' should be removed in the next release. For now, it converts
                // invalid acronyms to HOST. When removed, only the 'else' part should
                // remain.
                if (tokenType == MDMStandardTokenizerImpl.ACRONYM_DEP) {
                    if (replaceInvalidAcronym) {
                        typeAtt.setType(MDMStandardTokenizerImpl.TOKEN_TYPES[MDMStandardTokenizerImpl.HOST]);
                        termAtt.setTermLength(termAtt.termLength() - 1); // remove extra '.'
                    } else {
                        typeAtt.setType(MDMStandardTokenizerImpl.TOKEN_TYPES[MDMStandardTokenizerImpl.ACRONYM]);
                    }
                } else {
                    typeAtt.setType(MDMStandardTokenizerImpl.TOKEN_TYPES[tokenType]);
                }
                return true;
            } else {
                // When we skip a too-long term, we still increment the
                // position increment
                posIncr++;
            }
        }
    }

    @Override
    public final void end() {
        // set final offset
        int finalOffset = correctOffset(scanner.yychar() + scanner.yylength());
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        scanner.yyreset(input);
    }

    @Override
    public void reset(Reader reader) throws IOException {
        super.reset(reader);
        reset();
    }

    @Deprecated
    public boolean isReplaceInvalidAcronym() {
        return replaceInvalidAcronym;
    }

    @Deprecated
    public void setReplaceInvalidAcronym(boolean replaceInvalidAcronym) {
        this.replaceInvalidAcronym = replaceInvalidAcronym;
    }
}
