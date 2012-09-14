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
package org.talend.mdm.webapp.stagingareabrowser.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FakeData {

    static List<ResultItem> items = new ArrayList<ResultItem>();
    static {
        for (int i = 0; i < 100; i++) {
            ResultItem item = new ResultItem();
            item.setIds("id" + i);
            item.setKey("id" + i);
            item.setEntity("Product");
            item.setError("adsfjkas;fjfasfasf");
            item.setSource("aa");
            item.setStatus(i % 2 == 0 ? 403 : 203);
            item.setDateTime(new Date());
            items.add(item);
        }
    }

    public static int getTotal() {
        return items.size();
    }

    public static List<ResultItem> getResults(int from, int to) {
        List<ResultItem> subItems = new ArrayList<ResultItem>();
        for (int i = from; i < to && i < items.size(); i++) {
            subItems.add(items.get(i));
        }
        return subItems;
    }
}
