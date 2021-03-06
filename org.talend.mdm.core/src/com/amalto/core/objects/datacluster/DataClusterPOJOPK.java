/*
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package com.amalto.core.objects.datacluster;

import com.amalto.core.objects.ObjectPOJOPK;


public class DataClusterPOJOPK extends ObjectPOJOPK {

    public DataClusterPOJOPK(ObjectPOJOPK pk) {
        super(pk.getIds());
    }

    /**
     * For Marshaling purposes only
     */
    public DataClusterPOJOPK() {
        super();
    }

    public DataClusterPOJOPK(String name) {
        super(new String[]{name});
    }

}
