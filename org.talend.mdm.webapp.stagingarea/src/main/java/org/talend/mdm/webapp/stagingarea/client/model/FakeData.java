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
    //            new StagingAreaExecutionModel(getDate("2009-07-08 04:04:04"), getDate("2009-10-01 04:04:04"), 100000, 400), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2009-08-08 04:04:04"), getDate("2009-11-01 04:04:04"), 100000, 1000), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2010-03-08 04:04:04"), getDate("2010-08-01 04:04:04"), 100000, 1200), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2010-05-08 04:04:04"), getDate("2010-11-01 04:04:04"), 100000, 300), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2010-07-08 04:04:04"), getDate("2010-10-01 04:04:04"), 100000, 20), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2010-08-08 04:04:04"), getDate("2010-09-01 04:04:04"), 100000, 125), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2010-09-08 04:04:04"), getDate("2010-10-01 04:04:04"), 100000, 1298), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2011-02-08 04:04:04"), getDate("2011-03-01 04:04:04"), 100000, 21), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2011-02-08 04:04:04"), getDate("2011-05-01 04:04:04"), 100000, 128), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2011-03-08 04:04:04"), getDate("2011-10-01 04:04:04"), 100000, 3600), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2011-04-08 04:04:04"), getDate("2011-05-01 04:04:04"), 100000, 30), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2011-05-08 04:04:04"), getDate("2011-08-01 04:04:04"), 100000, 128), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2011-05-08 04:04:04"), getDate("2011-07-01 04:04:04"), 100000, 2500), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2011-07-08 04:04:04"), getDate("2011-10-01 04:04:04"), 100000, 50), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2011-08-08 04:04:04"), getDate("2011-11-01 04:04:04"), 100000, 128), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2011-09-08 04:04:04"), getDate("2011-11-01 04:04:04"), 100000, 3000), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2012-01-08 04:04:04"), getDate("2012-05-01 04:04:04"), 100000, 20000), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2012-01-08 04:04:04"), getDate("2012-10-01 04:04:04"), 100000, 11234), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2012-04-08 04:04:04"), getDate("2012-06-01 04:04:04"), 100000, 1280), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2012-06-08 04:04:04"), getDate("2012-07-01 04:04:04"), 100000, 128), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2012-07-08 04:04:04"), getDate("2012-10-01 04:04:04"), 100000, 3214), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2012-05-08 04:04:04"), getDate("2012-10-01 04:04:04"), 100000, 3214), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2012-08-08 04:04:04"), getDate("2012-10-01 04:04:04"), 100000, 3214), //$NON-NLS-1$//$NON-NLS-2$
    //            new StagingAreaExecutionModel(getDate("2012-01-08 04:04:04"), getDate("2012-10-01 04:04:04"), 100000, 3214) //$NON-NLS-1$//$NON-NLS-2$
    };

    public static int getTotal() {
        return tasks.length;
    }

    public static List<StagingAreaExecutionModel> getTasks(Date beforeDate, int offset, int limit) {
        List<StagingAreaExecutionModel> results = new ArrayList<StagingAreaExecutionModel>();
        for (StagingAreaExecutionModel task : tasks) {
            if (beforeDate == null) {
                results.add(task);
            } else if (task.getStart_date().before(beforeDate)) {
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
