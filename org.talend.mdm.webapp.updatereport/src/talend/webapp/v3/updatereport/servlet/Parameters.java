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

package talend.webapp.v3.updatereport.servlet;

import java.util.Arrays;

/**
*
*/
class Parameters {
    private long date;
    private String dataClusterName;
    private String dataModelName;
    private String conceptName;
    private String[] id;
    private String revisionId;
    private String action;

    Parameters(long date, String dataClusterName, String dataModelName, String conceptName, String[] id, String revisionId, String action) {
        this.date = date;
        this.dataClusterName = dataClusterName;
        this.dataModelName = dataModelName;
        this.conceptName = conceptName;
        this.id = id;
        this.revisionId = revisionId;
        this.action = action;
    }

    public long getDate() {
        return date;
    }

    public String getDataClusterName() {
        return dataClusterName;
    }

    public String getDataModelName() {
        return dataModelName;
    }

    public String getConceptName() {
        return conceptName;
    }

    public String[] getId() {
        return id;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public String getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parameters)) return false;

        Parameters that = (Parameters) o;

        if (!conceptName.equals(that.conceptName)) return false;
        if (!dataClusterName.equals(that.dataClusterName)) return false;
        if (!dataModelName.equals(that.dataModelName)) return false;
        if (!Arrays.equals(id, that.id)) return false;
        if (revisionId != null ? !revisionId.equals(that.revisionId) : that.revisionId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dataClusterName.hashCode();
        result = 31 * result + dataModelName.hashCode();
        result = 31 * result + conceptName.hashCode();
        result = 31 * result + Arrays.hashCode(id);
        result = 31 * result + (revisionId != null ? revisionId.hashCode() : 0);
        return result;
    }
}
