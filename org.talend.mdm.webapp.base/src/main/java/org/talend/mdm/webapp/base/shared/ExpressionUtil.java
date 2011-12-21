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
package org.talend.mdm.webapp.base.shared;

import java.util.ArrayList;
import java.util.List;


public class ExpressionUtil {

    private String expression;

    private char peek = ' ';

    private boolean end = false;

    private int charIndex = 0;

    public ExpressionUtil(String expression){
        this.expression = expression;
    }

    public List<String> getDepTypes() {
        charIndex = 0;
        peek = ' ';
        end = false;
        List<String> typePathes = new ArrayList<String>();
        while (!end) {
            String xpath = scan();
            if (xpath != null) {
                String typePath = xpath.replaceAll("\\[\\d+\\]", ""); //$NON-NLS-1$//$NON-NLS-2$
                typePath = typePath.startsWith("/") ? typePath.substring(1) : typePath; //$NON-NLS-1$
                typePathes.add(typePath);
            }
        }
        return typePathes;
    }

    private void readch() {
        peek = expression.charAt(charIndex);
        charIndex++;
        end = (charIndex >= expression.length());
    }

    private boolean readch(char c) {
        readch();
        if (peek != c)
            return false;
        peek = ' ';
        return true;
    }

    public String scan() {
        while (true) {
            readch();
            if (peek == ' ' || peek == '\t' || peek == '\n')
                continue;
            else
                break;
        }

        if (peek == '\"') {
            do {
                readch();
            } while (peek != '\"');
        }

        if (peek == '/') {
            boolean inString = false;
            StringBuffer b = new StringBuffer();
            do {
                b.append(peek);
                readch();
                if (peek == '\"')
                    inString = !inString;
            } while (peek != ',' && peek != ')' && !inString);
            return b.toString();
        }

        if (peek == '.') {
            if (readch('.')) {
                boolean inString = false;
                StringBuffer b = new StringBuffer();
                b.append(".."); //$NON-NLS-1$
                readch();
                while (peek != ',' && peek != ')' && !inString) {
                    b.append(peek);
                    readch();
                    if (peek == '\"')
                        inString = !inString;
                }
                return b.toString();
            } else {
                boolean inString = false;
                StringBuffer b = new StringBuffer();
                b.append("./"); //$NON-NLS-1$
                readch();
                while (peek != ',' && peek != ')' && !inString) {
                    b.append(peek);
                    readch();
                    if (peek == '\"')
                        inString = !inString;
                }
                return b.toString();
            }
        }
        return null;
    }

}
