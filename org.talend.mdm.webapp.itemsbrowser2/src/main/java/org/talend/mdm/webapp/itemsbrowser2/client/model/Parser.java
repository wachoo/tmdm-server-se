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
package org.talend.mdm.webapp.itemsbrowser2.client.model;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class Parser {

    public static final char BEGIN_BLOCK = '(';

    public static final char END_BLOCK = ')';

    public static Criteria parse(String input) throws ParserException {
        checkBlocks(input);
        return parse(input, 0, input.length());
    }

    protected static Criteria parse(String input, int beginIndex, int endIndex) throws ParserException {
        char firstChar = input.charAt(beginIndex);
        if (firstChar == ' ') {
            throw new ParserException("Illegal ' '" + " at position " + beginIndex);
        } else if (firstChar == BEGIN_BLOCK) {
            return parseGroupFilter(input, beginIndex, endIndex);
        } else {
            return parseSimpleFilter(input, beginIndex, endIndex);
        }
    }

    protected static Criteria parseGroupFilter(String input, int beginIndex, int endIndex) throws ParserException {
        MultipleCriteria toReturn = null;

        int index = beginIndex;
        int beginBlockIndex = 0;
        int endBlockIndex = 0;
        while (index < endIndex) { // do not search outside of scope
            // find next subFilter begin block
            beginBlockIndex = input.indexOf(BEGIN_BLOCK, index);

            if (beginBlockIndex < 0) {
                // no more block in scope
                break;
            }
            if (beginBlockIndex > endIndex) {
                // if outside of scope then exit
                break;
            }

            endBlockIndex = findEndBlockIndex(input, beginBlockIndex);

            if (toReturn == null) {
                int refProf = -1;
                for (String current : Constants.groupOperators) {
                    final int fromIndex = endBlockIndex - 1;
                    final String searched = END_BLOCK + current + BEGIN_BLOCK;
                    int indexOf = input.indexOf(searched, fromIndex);
                    if (indexOf >= beginIndex && indexOf <= endIndex) {
                        int foundProf = count(input.substring(beginIndex, indexOf), '(');
                        if (foundProf < refProf || refProf == -1) {
                            refProf = foundProf;
                            toReturn = new MultipleCriteria(current);
                        }
                    }
                }
            }

            if (toReturn == null)
                return parse(input, beginBlockIndex + 1, endBlockIndex);
            else
                toReturn.add(parse(input, beginBlockIndex + 1, endBlockIndex));

            // continue after next subFilter end block
            index = endBlockIndex;
        }

        return toReturn;
    }

    private static int count(String source, char c) {
        int i = 0;
        for (char current : source.toCharArray()) {
            if (c == current)
                i++;
        }
        return i;
    }

    protected static SimpleCriterion parseSimpleFilter(String input, int beginIndex, int endIndex) {
        String value = input.substring(beginIndex, endIndex);

        // for (String currentOp : Constants.simpleOperators) {
        // if (value.contains(currentOp)) {
        // String[] split = value.split(currentOp);
        // final SimpleCriterion simpleCriterion = new SimpleCriterion(split[0], currentOp, split[1]);
        // return simpleCriterion;
        // }
        // }

        return null;
    }

    protected static int findEndBlockIndex(String input, int beginBlockIndex) throws ParserException {
        int level = 0;
        int i;
        for (i = beginBlockIndex; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (currentChar == BEGIN_BLOCK) {
                level++;
            } else if (currentChar == END_BLOCK) {
                level--;
            }
            if (level == 0) {
                return i;
            }
        }
        throw new ParserException("Cannot find closing " + END_BLOCK + " at position " + i); // should be already
    }

    protected static void checkBlocks(String input) throws ParserException {
        int level = 0;
        int i;
        for (i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (currentChar == BEGIN_BLOCK) {
                level++;
            } else if (currentChar == END_BLOCK) {
                level--;
            }
            if (level < 0) {
                throw new ParserException("to many " + END_BLOCK + " at position " + i);
            }
        }
        if (level < 0) {
            throw new ParserException("to many " + END_BLOCK + " at position " + i);
        }
        if (level > 0) {
            throw new ParserException("to many " + BEGIN_BLOCK + " at position " + i);
        }
    }
    //
    // public static void main(String[] args) {
    // try {
    // System.out.println("Criteria => " + parse("((version>3.1))"));
    // System.out.println("Criteria => " + parse("((version>3.1)or(type=business_process))"));
    // System.out
    // .println("Criteria => " + parse("((version>3.1)or(type=process)or(((author=nuno)and(status=Production))))"));
    // } catch (ParserException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
}
