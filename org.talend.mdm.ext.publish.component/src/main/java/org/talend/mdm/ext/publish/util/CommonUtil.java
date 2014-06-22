// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.mdm.ext.publish.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class CommonUtil
{

    public CommonUtil()
    {
    }

    public static String urlEncode(Object value)
    {
        if(value == null)
            return null;
        try
        {
            String result = URLEncoder.encode(value.toString(), "UTF-8");
            result = result.replaceAll("\\+", "%20");
            return result;
        }
        catch(UnsupportedEncodingException e)
        {
            return null;
        }
    }

    public static String urlDecode(Object value)
    {
        if(value == null)
            return null;
        try
        {
            return URLDecoder.decode(value.toString(), "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            return null;
        }
    }
}
