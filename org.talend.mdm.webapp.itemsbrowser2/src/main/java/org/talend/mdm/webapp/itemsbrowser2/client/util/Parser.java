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
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.Criteria;
import org.talend.mdm.webapp.itemsbrowser2.client.model.MultipleCriteria;
import org.talend.mdm.webapp.itemsbrowser2.client.model.SimpleCriterion;
import org.talend.mdm.webapp.itemsbrowser2.shared.OperatorValueConstants;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Parser implements Serializable, IsSerializable {

    private static final long serialVersionUID = 1L;

    public static final char BEGIN_BLOCK = '(';

    public static final char END_BLOCK = ')';

    public static Criteria parse(String input) throws ParserException {
        checkBlocks(input);
        return parse(input, 0, input.length());
    }

    protected static Criteria parse(String input, int beginIndex, int endIndex) throws ParserException {
        char firstChar = input.charAt(beginIndex);
        if (firstChar == ' ') {
            throw new ParserException(MessagesFactory.getMessages().exception_parse_illegalChar(beginIndex));
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
                for (String current : OperatorValueConstants.groupOperatorVales) {
                    final int fromIndex = endBlockIndex - 1;
                    final String searched = END_BLOCK + " " + current + " " + BEGIN_BLOCK;//$NON-NLS-1$//$NON-NLS-2$
                    int indexOf = input.indexOf(searched, fromIndex);
                    if (indexOf >= beginIndex && indexOf <= endIndex) {
                        int foundProf = count(input.substring(beginIndex, indexOf), BEGIN_BLOCK);
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
        throw new ParserException(MessagesFactory.getMessages().exception_parse_unknownOperator(value));
    }

    private static String getOperator(String value) {
        for (String currentOp : OperatorValueConstants.fullOperatorValues) {
            if (value.contains(currentOp)) {
                String[] tmpSplit = value.split(currentOp);
                if (tmpSplit[0].lastIndexOf(" ") == tmpSplit[0].length() - 1 && tmpSplit[1].indexOf(" ") == 0) //$NON-NLS-1$ //$NON-NLS-2$
                    return currentOp;
            }
        }

        for (String currentOp : OperatorValueConstants.fulltextOperatorValues) {
            if (value.contains(currentOp)) {
                String[] tmpSplit = value.split(currentOp);
                if (tmpSplit[0].lastIndexOf(" ") == tmpSplit[0].length() - 1 && tmpSplit[1].indexOf(" ") == 0) //$NON-NLS-1$ //$NON-NLS-2$
                    return currentOp;
            }
        }

        for (String currentOp : OperatorValueConstants.dateOperatorValues) {
            if (value.contains(currentOp)) {
                String[] tmpSplit = value.split(currentOp);
                if (tmpSplit[0].lastIndexOf(" ") == tmpSplit[0].length() - 1 && tmpSplit[1].indexOf(" ") == 0) //$NON-NLS-1$ //$NON-NLS-2$
                    return currentOp;
            }
        }

        for (String currentOp : OperatorValueConstants.numOperatorValues) {
            if (value.contains(currentOp)) {
                String[] tmpSplit = value.split(currentOp);
                if (tmpSplit[0].lastIndexOf(" ") == tmpSplit[0].length() - 1 && tmpSplit[1].indexOf(" ") == 0) //$NON-NLS-1$ //$NON-NLS-2$
                    return currentOp;
            }
        }

        for (String currentOp : OperatorValueConstants.booleanOperatorValues) {
            if (value.contains(currentOp)) {
                String[] tmpSplit = value.split(currentOp);
                if (tmpSplit[0].lastIndexOf(" ") == tmpSplit[0].length() - 1 && tmpSplit[1].indexOf(" ") == 0) //$NON-NLS-1$ //$NON-NLS-2$
                    return currentOp;
            }
        }

        for (String currentOp : OperatorValueConstants.enumOperatorValues) {
            if (value.contains(currentOp)) {
                String[] tmpSplit = value.split(currentOp);
                if (tmpSplit[0].lastIndexOf(" ") == tmpSplit[0].length() - 1 && tmpSplit[1].indexOf(" ") == 0) //$NON-NLS-1$ //$NON-NLS-2$
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
        throw new ParserException(MessagesFactory.getMessages().exception_parse_missEndBlock(END_BLOCK, i));
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
                throw new ParserException(MessagesFactory.getMessages().exception_parse_tooManyEndBlock(END_BLOCK, i));
            }
        }
        if (level < 0) {
            throw new ParserException(MessagesFactory.getMessages().exception_parse_tooManyEndBlock(END_BLOCK, i));
        }
        if (level > 0) {
            throw new ParserException(MessagesFactory.getMessages().exception_parse_tooManyEndBlock(END_BLOCK, i));
        }
    }
}
