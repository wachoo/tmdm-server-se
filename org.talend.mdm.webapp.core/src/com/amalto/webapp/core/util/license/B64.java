// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.core.util.license;

import org.apache.commons.codec.binary.Base64;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class B64 {

    public static byte[] decode(byte[] bytes) {
        return Base64.decodeBase64(bytes);
    }

    public static byte[] encode(byte[] bytes) {
        return Base64.encodeBase64(bytes);
    }
}
