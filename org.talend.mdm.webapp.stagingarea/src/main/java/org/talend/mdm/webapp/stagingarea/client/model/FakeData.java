// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.stagingarea.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;

public class FakeData {

    static DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

    static Date getDate(String dateText) {
        return format.parse(dateText);
    }

    static final StagingAreaExecutionModel[] tasks = new StagingAreaExecutionModel[] {
            new StagingAreaExecutionModel("111", getDate("2009-07-08 04:04:04"), getDate("2009-10-01 04:04:04"), 100000, 400, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("112", getDate("2009-08-08 04:04:04"), getDate("2009-11-01 04:04:04"), 100000, 1000, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("113", getDate("2010-03-08 04:04:04"), getDate("2010-08-01 04:04:04"), 100000, 1200, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("114", getDate("2010-05-08 04:04:04"), getDate("2010-11-01 04:04:04"), 100000, 300, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("115", getDate("2010-07-08 04:04:04"), getDate("2010-10-01 04:04:04"), 100000, 20, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("116", getDate("2010-08-08 04:04:04"), getDate("2010-09-01 04:04:04"), 100000, 125, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("117", getDate("2010-09-08 04:04:04"), getDate("2010-10-01 04:04:04"), 100000, 1298, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("118", getDate("2011-02-08 04:04:04"), getDate("2011-03-01 04:04:04"), 100000, 21, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("119", getDate("2011-02-08 04:04:04"), getDate("2011-05-01 04:04:04"), 100000, 128, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("120", getDate("2011-03-08 04:04:04"), getDate("2011-10-01 04:04:04"), 100000, 3600, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("121", getDate("2011-04-08 04:04:04"), getDate("2011-05-01 04:04:04"), 100000, 30, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("122", getDate("2011-05-08 04:04:04"), getDate("2011-08-01 04:04:04"), 100000, 128, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("123", getDate("2011-05-08 04:04:04"), getDate("2011-07-01 04:04:04"), 100000, 2500, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("124", getDate("2011-07-08 04:04:04"), getDate("2011-10-01 04:04:04"), 100000, 50, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("125", getDate("2011-08-08 04:04:04"), getDate("2011-11-01 04:04:04"), 100000, 128, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("126", getDate("2011-09-08 04:04:04"), getDate("2011-11-01 04:04:04"), 100000, 3000, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("127", getDate("2012-01-08 04:04:04"), getDate("2012-05-01 04:04:04"), 100000, 20000, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("128", getDate("2012-01-08 04:04:04"), getDate("2012-10-01 04:04:04"), 100000, 11234, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("129", getDate("2012-04-08 04:04:04"), getDate("2012-06-01 04:04:04"), 100000, 1280, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("130", getDate("2012-06-08 04:04:04"), getDate("2012-07-01 04:04:04"), 100000, 128, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("131", getDate("2012-07-08 04:04:04"), getDate("2012-10-01 04:04:04"), 100000, 3214, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("132", getDate("2012-05-08 04:04:04"), getDate("2012-10-01 04:04:04"), 100000, 3214, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("133", getDate("2012-08-08 04:04:04"), getDate("2012-10-01 04:04:04"), 100000, 3214, 200, getDate("2009-10-01 04:04:04")), //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new StagingAreaExecutionModel("134", getDate("2012-01-08 04:04:04"), getDate("2012-10-01 04:04:04"), 100000, 3214, 200, getDate("2009-10-01 04:04:04")) //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    };

    public static int getTotal() {
        return tasks.length;
    }

    public static List<StagingAreaExecutionModel> getTasks(Date beforeDate, int offset, int limit) {
        List<StagingAreaExecutionModel> results = new ArrayList<StagingAreaExecutionModel>();
        for (StagingAreaExecutionModel task : tasks) {
            if (beforeDate == null) {
                results.add(task);
            } else if (task.getStartDate().before(beforeDate)) {
                results.add(task);
            }
        }
        int size = offset + limit < results.size() ? limit : results.size() - offset;
        return getSubList(results, offset, offset + size);
    }

    public static List<StagingAreaExecutionModel> getSubList(List<StagingAreaExecutionModel> list, int offset, int to) {
        try {
            List<StagingAreaExecutionModel> sublist = new ArrayList<StagingAreaExecutionModel>();
            for (int i = offset; i < to; i++) {
                sublist.add(list.get(i));
            }
            return sublist;
        } catch (Exception e){
            return null;
        }
    }

}
