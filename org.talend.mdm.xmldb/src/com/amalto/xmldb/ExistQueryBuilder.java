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

import org.apache.commons.lang.StringEscapeUtils;

public class ExistQueryBuilder extends QueryBuilder {

    @Override
    protected boolean useNumberFunction() {
        return true;
    }

    @Override
    public String getFullTextQueryString(String queryStr) {
        StringBuilder ftQueryBuilder = new StringBuilder();
        ftQueryBuilder.append("ft:query(.,\""); //$NON-NLS-1$
        ftQueryBuilder.append(StringEscapeUtils.escapeXml(queryStr.trim()));
        ftQueryBuilder.append("\")"); //$NON-NLS-1$
        return ftQueryBuilder.toString();
    }
}
