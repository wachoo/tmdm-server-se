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

import com.amalto.core.history.Document;
import org.apache.commons.lang.StringUtils;

/**
*
*/
class EmptyDocument implements Document {
    public String getAsString() {
        return StringUtils.EMPTY;
    }

    public boolean isCreated() {
        return false;
    }

    public boolean isDeleted() {
        return false;
    }

    public void restore() {
    }
}
