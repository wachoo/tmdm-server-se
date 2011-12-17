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

package org.talend.mdm.webapp.browserecords.client.widget.ForeignKey;

import java.util.Collection;

import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;

import com.extjs.gxt.ui.client.widget.form.PropertyEditor;

public class FKPropertyEditor implements PropertyEditor<ForeignKeyBean> {

    private ForeignKeyBean foreignKeyBean;

    /**
     * Creates a new date time property editor.
     */
    public FKPropertyEditor() {

    }

    public ForeignKeyBean convertStringValue(String value) {
        return this.foreignKeyBean;
    }

    public String getStringValue(ForeignKeyBean foreignKeyBean) {
        this.foreignKeyBean = foreignKeyBean;
        if (foreignKeyBean.getForeignKeyInfo() != null && foreignKeyBean.getForeignKeyInfo().size() > 0) {
            StringBuilder sb = new StringBuilder();
            Collection<String> fkInfoValues = foreignKeyBean.getForeignKeyInfo().values();
            int i = 0;
            for (String fkInfoValue : fkInfoValues) {
                if (i > 0)
                    sb.append('-');
                sb.append(fkInfoValue);
                i++;
            }
            return sb.toString();
        } else {
            return foreignKeyBean.getId();
        }
    }

}
