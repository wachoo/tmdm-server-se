/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query.processor;

import com.amalto.core.query.QizxQueryModelConverter;
import com.amalto.core.query.QueryExecutor;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.xquery.Expression;
import com.amalto.core.query.xquery.ExpressionVisitorContext;
import com.amalto.core.query.xquery.QizxQueryTextConverter;
import com.amalto.xmldb.XmldbSLWrapper;
import com.amalto.xmlserver.interfaces.XmlServerException;

/**
 *
 */
class XQueryAction implements QueryExecutor {

    private final Select select;

    private final XmldbSLWrapper server;

    public XQueryAction(Select select, XmldbSLWrapper server) {
        this.select = select;
        this.server = server;
    }

    public int getResultsCount() {
        return 0;
    }

    public int getCount() {
        return 0;
    }

    public Iterable<String> getResults() {
        String dataContainerName = "MDM";
        QizxQueryModelConverter userToXQuery = new QizxQueryModelConverter(dataContainerName);
        Expression expression = select.accept(userToXQuery);

        // DEBUG CODE
        /*
        System.out.println("==============================");
        System.out.println("XQUERY QUERY (Object model representation)"); //NON-NLS
        System.out.println("==============================");
        expression.accept(new XQueryDumpConsole(), new ExpressionVisitorContext());
        */

        QizxQueryTextConverter xQueryBuilder = new QizxQueryTextConverter();
        expression.accept(xQueryBuilder, new ExpressionVisitorContext());

        // DEBUG CODE
        /*
        System.out.println("==============================");
        System.out.println("XQUERY QUERY (query text)"); //NON-NLS
        System.out.println("==============================");
        System.out.println(xQueryBuilder.getBuilder().toString());
        */

        try {
            return server.runQuery(select.getRevisionId(), dataContainerName, xQueryBuilder.getBuilder().toString(), new String[0]);
        } catch (XmlServerException e) {
            throw new RuntimeException(e);
        }
    }

}
