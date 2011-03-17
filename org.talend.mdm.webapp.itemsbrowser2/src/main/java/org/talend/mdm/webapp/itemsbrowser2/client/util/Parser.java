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
package org.talend.mdm.webapp.itemsbrowser2.client.util;

import java.io.Serializable;

import org.talend.mdm.webapp.itemsbrowser2.client.exception.ParserException;
import org.talend.mdm.webapp.itemsbrowser2.client.model.Criteria;
import org.talend.mdm.webapp.itemsbrowser2.client.model.MultipleCriteria;
import org.talend.mdm.webapp.itemsbrowser2.client.model.OperatorConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.model.SimpleCriterion;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class Parser implements Serializable, IsSerializable {

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
                for (String current : OperatorConstants.groupOperators) {
                    final int fromIndex = endBlockIndex - 1;
                    final String searched = END_BLOCK + " " + current + " " + BEGIN_BLOCK;
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

    protected static SimpleCriterion parseSimpleFilter(String input, int beginIndex, int endIndex) throws ParserException {
        String value = input.substring(beginIndex, endIndex);
        String realOp = getOperator(value);
        if (realOp != null) {
            String[] split = value.split(realOp);
            final SimpleCriterion simpleCriterion = new SimpleCriterion(split[0].trim(), realOp, split[1].trim());
            return simpleCriterion;
        }
        throw new ParserException("Cannot find correct operator in " + value);
    }

    private static String getOperator(String value) {
        for (String currentOp : OperatorConstants.fullOperators.keySet()) {
            if (value.contains(currentOp)) {
                return currentOp;
            }
        }

        for (String currentOp : OperatorConstants.fulltextOperators.keySet()) {
            if (value.contains(currentOp)) {
                return currentOp;
            }
        }

        for (String currentOp : OperatorConstants.dateOperators.keySet()) {
            if (value.contains(currentOp)) {
                return currentOp;
            }
        }

        for (String currentOp : OperatorConstants.numOperators.keySet()) {
            if (value.contains(currentOp)) {
                return currentOp;
            }
        }

        for (String currentOp : OperatorConstants.booleanOperators.keySet()) {
            if (value.contains(currentOp)) {
                return currentOp;
            }
        }

        for (String currentOp : OperatorConstants.enumOperators.keySet()) {
            if (value.contains(currentOp)) {
                return currentOp;
            }
        }
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
        throw new ParserException("Cannot find closing " + END_BLOCK + " at position " + i);
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
}
