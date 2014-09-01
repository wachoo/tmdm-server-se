// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

/*
This file is an updated grammar file for jflex from original grammar file  {@link StandardTokenizerImpl.jflex}
Original grammar file is updated to add a new lexical type 'UNDERSCORE' to support to generate
new token type for words containing underscore(s).

NOTE: if you change MDMStandardTokenizerImpl.flex and need to regenerate
      the tokenizer, remember to use JRE 1.4 to run jflex (before
      Lucene 3.0).  This grammar now uses constructs (eg :digit:,
      :letter:) whose meaning can vary according to the JRE used to
      run jflex.  See
      https://issues.apache.org/jira/browse/LUCENE-1126 for details.
*/

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

%%

%class MDMStandardTokenizerImpl
%unicode
%integer
%function getNextToken
%pack
%char

%{

public static final int ALPHANUM          = MDMStandardTokenizer.ALPHANUM;
public static final int UNDERSCORE          = MDMStandardTokenizer.UNDERSCORE;
public static final int APOSTROPHE        = MDMStandardTokenizer.APOSTROPHE;
public static final int ACRONYM           = MDMStandardTokenizer.ACRONYM;
public static final int COMPANY           = MDMStandardTokenizer.COMPANY;
public static final int EMAIL             = MDMStandardTokenizer.EMAIL;
public static final int HOST              = MDMStandardTokenizer.HOST;
public static final int NUM               = MDMStandardTokenizer.NUM;
public static final int CJ                = MDMStandardTokenizer.CJ;
/**
 * @deprecated this solves a bug where HOSTs that end with '.' are identified
 *             as ACRONYMs. It is deprecated and will be removed in the next
 *             release.
 */
public static final int ACRONYM_DEP       = MDMStandardTokenizer.ACRONYM_DEP;

public static final String [] TOKEN_TYPES = MDMStandardTokenizer.TOKEN_TYPES;

public final int yychar()
{
    return yychar;
}

/**
 * Fills Lucene token with the current token text.
 */
final void getText(Token t) {
  t.setTermBuffer(zzBuffer, zzStartRead, zzMarkedPos-zzStartRead);
}

/**
 * Fills TermAttribute with the current token text.
 */
final void getText(TermAttribute t) {
  t.setTermBuffer(zzBuffer, zzStartRead, zzMarkedPos-zzStartRead);
}

%}

THAI       = [\u0E00-\u0E59]

// basic word: a sequence of digits & letters (includes Thai to enable ThaiAnalyzer to function)
ALPHANUM   = ({LETTER}|{THAI}|[:digit:])+

//word contains underscores in it
UNDERSCORE = {ALPHANUM} ("_")+ {ALPHANUM} | {ALPHANUM} (("_")+ {ALPHANUM})+ |("_")+ ({ALPHANUM} ("_")*)+ | ({ALPHANUM} ("_")*) ("_")+

// internal apostrophes: O'Reilly, you're, O'Reilly's
// use a post-filter to remove possessives
APOSTROPHE =  {ALPHA} ("'" {ALPHA})+

// acronyms: U.S.A., I.B.M., etc.
// use a post-filter to remove dots
ACRONYM    =  {LETTER} "." ({LETTER} ".")+

ACRONYM_DEP	= {ALPHANUM} "." ({ALPHANUM} ".")+

// company names like AT&T and Excite@Home.
COMPANY    =  {ALPHA} ("&"|"@") {ALPHA}

// email addresses
EMAIL      =  {ALPHANUM} (("."|"-"|"_") {ALPHANUM})* "@" {ALPHANUM} (("."|"-") {ALPHANUM})+

// hostname
HOST       =  {ALPHANUM} ((".") {ALPHANUM})+

// floating point, serial, model numbers, ip addresses, etc.
// every other segment must have at least one digit
NUM        = ({ALPHANUM} {P} {HAS_DIGIT}
           | {HAS_DIGIT} {P} {ALPHANUM}
           | {ALPHANUM} ({P} {HAS_DIGIT} {P} {ALPHANUM})+
           | {HAS_DIGIT} ({P} {ALPHANUM} {P} {HAS_DIGIT})+
           | {ALPHANUM} {P} {HAS_DIGIT} ({P} {ALPHANUM} {P} {HAS_DIGIT})+
           | {HAS_DIGIT} {P} {ALPHANUM} ({P} {HAS_DIGIT} {P} {ALPHANUM})+)

// punctuation
P	         = ("_"|"-"|"/"|"."|",")

// at least one digit
HAS_DIGIT  = ({LETTER}|[:digit:])* [:digit:] ({LETTER}|[:digit:])*

ALPHA      = ({LETTER})+

// From the JFlex manual: "the expression that matches everything of <a> not matched by <b> is !(!<a>|<b>)"
LETTER     = !(![:letter:]|{CJ})

// Chinese and Japanese (but NOT Korean, which is included in [:letter:])
CJ         = [\u3100-\u312f\u3040-\u309F\u30A0-\u30FF\u31F0-\u31FF\u3300-\u337f\u3400-\u4dbf\u4e00-\u9fff\uf900-\ufaff\uff65-\uff9f]

WHITESPACE = \r\n | [ \r\n\t\f]

%%

{ALPHANUM}                                                     { return ALPHANUM; }
{UNDERSCORE}                                                   { return UNDERSCORE; }
{APOSTROPHE}                                                   { return APOSTROPHE; }
{ACRONYM}                                                      { return ACRONYM; }
{COMPANY}                                                      { return COMPANY; }
{EMAIL}                                                        { return EMAIL; }
{HOST}                                                         { return HOST; }
{NUM}                                                          { return NUM; }
{CJ}                                                           { return CJ; }
{ACRONYM_DEP}                                                  { return ACRONYM_DEP; }

/** Ignore the rest */
. | {WHITESPACE}                                               { /* ignore */ }
