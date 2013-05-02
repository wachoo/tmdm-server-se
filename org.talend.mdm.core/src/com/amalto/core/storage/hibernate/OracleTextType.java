/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.type.TextType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleTextType extends TextType {

    public static final Logger LOGGER = Logger.getLogger(OracleTextType.class);

    @Override
    public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
        // Retrieve the value of the designated column in the current row of this
        // ResultSet object as a java.io.Reader object
        InputStream charInputStream = rs.getBinaryStream(name);
        // if the corresponding SQL value is NULL, the reader we got is NULL as well
        if (charInputStream == null) {
            return null;
        }
        // Fetch Reader content up to the end - and put characters in a StringBuffer
        StringBuilder sb = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(charInputStream);
        try {
            char[] buffer = new char[2048];
            while (true) {
                int amountRead = reader.read(buffer, 0, buffer.length);
                if (amountRead == -1) {
                    break;
                }
                sb.append(buffer, 0, amountRead);
            }
        } catch (IOException ioe) {
            throw new HibernateException("IOException occurred reading text", ioe);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Exception during close.", e);
                }
            }
        }
        return sb.toString();
    }
}
