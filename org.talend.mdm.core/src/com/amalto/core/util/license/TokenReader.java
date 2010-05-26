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
package com.amalto.core.util.license;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amalto.core.util.json.JSONObject;




/**
 * DOC stephane class global comment. Detailled comment
 */
public final class TokenReader {

    protected Token readToken(JSONObject token) throws Exception {
        try {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date from = simpleDateFormat.parse(token.getString("from"));
            Date to = simpleDateFormat.parse(token.getString("to"));
            Token toReturn = new Token();
            toReturn.setStart(from);
            toReturn.setEnd(to);
            return toReturn;
        } catch (Exception e) {
            throw new Exception("Cannot instantiate token", e);
        }
    }

    public Token readToken(byte[] bytes) throws Exception {
        byte[] hash = B64.decode(bytes);
        try {
            byte[] decrypted = Hsifwolb.decrypt(hash, YekEht.DK);
            return readToken(new JSONObject(new String(decrypted)));
        } catch (Exception e) {
            throw new Exception("Cannot instantiate token", e);
        }
    }
}
