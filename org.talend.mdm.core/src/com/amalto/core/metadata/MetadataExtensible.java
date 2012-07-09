/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */
package com.amalto.core.metadata;

import java.util.HashMap;
import java.util.Map;

class MetadataExtensible {

    private Map<String, Object> dataMap;

    MetadataExtensible() {
    }

    /**
     * Sets the defined property with the given name.
     * 
     * @param key the name of the property
     * @param data the new value for the property
     */
    public synchronized void setData(String key, Object data) {
        if (dataMap == null) {
            dataMap = new HashMap<String, Object>() {
                private static final int THRESHOLD = 10;

                @Override
                public Object put(String s, Object o) {
                    if(size() > THRESHOLD) {
                        throw new IllegalStateException("Map is not aimed to contain more than " + THRESHOLD + " elements.");
                    }
                    return super.put(s, o);
                }
            };
        }
        dataMap.put(key, data);
    }

    /**
     * Returns the defined property for the given name, or <code>null</code> if it has not been set.
     * 
     * @param key the name of the property
     * @return the value or <code>null</code> if it has not been set
     */
    public <X> X getData(String key) {
        if (dataMap == null) {
            return null;
        }
        return (X) dataMap.get(key);
    }

}
