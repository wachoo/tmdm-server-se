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

package com.amalto.core.servlet;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

@javax.ws.rs.ext.Provider
public class StringListWriter implements MessageBodyWriter<List> {

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return List.class.isAssignableFrom(aClass)
                && (mediaType.equals(MediaType.TEXT_PLAIN_TYPE) |
                mediaType.equals(MediaType.TEXT_XML_TYPE) |
                mediaType.equals(MediaType.APPLICATION_JSON_TYPE));
    }

    @Override
    public long getSize(List strings, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return strings.size();
    }

    @Override
    public void writeTo(List strings, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
        for (Object string : strings) {
            bw.write(String.valueOf(string));
        }
        bw.flush();
    }
}
