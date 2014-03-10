// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.base.client.rest.convertor;

import java.io.IOException;
import java.util.Set;

import org.restlet.client.Response;
import org.restlet.client.ext.json.JsonRepresentation;
import org.restlet.client.representation.EmptyRepresentation;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class JsonConvertor {

    final static String ROOT_NAME = "groups"; //$NON-NLS-1$

    final static String DISPLAY_NAME = "name"; //$NON-NLS-1$

    final static String VALUE_SEPARATOR = " : "; //$NON-NLS-1$

    public static BaseTreeModel responseToTreeModel(Response response) throws IOException {

        if (response == null || response.getEntity() == null || response.getEntity() instanceof EmptyRepresentation) {
            return null;
        }

        BaseTreeModel rootModel = new BaseTreeModel();
        rootModel.set(DISPLAY_NAME, ROOT_NAME);

        JsonRepresentation representation = new JsonRepresentation(response.getEntity());
        JSONObject jsonObject = representation.getJsonObject();
        JSONValue jsonValue = jsonObject.get(ROOT_NAME);
        retriveNode(jsonValue, rootModel);
        return rootModel;
    }

    private static void retriveNode(JSONValue jsonValue, BaseTreeModel parent) {
        if (jsonValue.isArray() != null) {
            JSONArray array = jsonValue.isArray();
            for (int i = 0; i < array.size(); i++) {
                JSONValue value = array.get(i);
                String content = getValue(value);
                if (content != null) {
                    addChildNode(content, parent);
                }
                retriveNode(value, parent);
            }
        } else if (jsonValue.isObject() != null) {
            JSONObject object = jsonValue.isObject();
            Set<String> keySet = object.keySet();
            for (String key : keySet) {
                JSONValue value = object.get(key);
                String content = getValue(value);
                if (content != null) {
                    addChildNode(key + VALUE_SEPARATOR + content, parent);
                } else {
                    BaseTreeModel treeModel = addChildNode(key, parent);
                    retriveNode(value, treeModel);
                }
            }
        }
    }

    private static BaseTreeModel addChildNode(String content, BaseTreeModel parent) {
        BaseTreeModel treeModel = new BaseTreeModel();
        treeModel.set(DISPLAY_NAME, content);
        parent.add(treeModel);
        return treeModel;
    }

    private static String getValue(JSONValue jsonValue) {
        String value = null;
        if (jsonValue.isString() != null) {
            value = jsonValue.isString().toString().replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
        } else if (jsonValue.isBoolean() != null) {
            value = jsonValue.isBoolean().toString();
        } else if (jsonValue.isNumber() != null) {
            value = jsonValue.isNumber().toString();
        }
        return value;
    }
}