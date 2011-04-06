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

package com.amalto.core.load;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 *
 */
public interface Constants {
    Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
    String PAYLOAD_ELEMENT = "p";
    String WRAPPER_ELEMENT = "ii";
    String CONTAINER_ELEMENT = "c";
    String NAME_ELEMENT = "n";
    String DMN_ELEMENT = "dmn";
    String DMR_ELEMENT = "dmr";
    String SP_ELEMENT = "sp";
    String VERSION_ELEMENT = "i";
    String TASK_ID_ELEMENT = "taskId";
    String ID_ELEMENT = "t";
    boolean NON_FINAL_STATE = false;
    boolean FINAL_STATE = true;
    int DEFAULT_PARSER_LIMIT = -1; // Means no limit
}
