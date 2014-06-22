// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.v3.xtentismdm.dwr;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSGetUniversePKs;
import com.amalto.webapp.util.webservices.WSUniversePK;
import com.amalto.webapp.util.webservices.WSUniversePKArray;
import com.amalto.webapp.util.webservices.XtentisPort;

public class LoginDWR {
    
    /**
     * if the session of login page invalidate
     */
    public boolean isTimeOut() {
       boolean timeout = false;
       WebContext ctx = WebContextFactory.get();
       
       if(ctx.getSession(false) == null) {
          return true;
       }
       
       return timeout;
    }
    
    public String[] getUniverseNames() throws XtentisWebappException {

        List<String> universeNames = new ArrayList<String>();
        universeNames.add("HEAD"); //$NON-NLS-1$
        try {
            if (com.amalto.core.util.Util.isEnterprise()) {
                XtentisPort port = Util.getPort(null, null);
                WSUniversePKArray pks = port.getUniversePKs(new WSGetUniversePKs(".*")); //$NON-NLS-1$
                if (pks != null) {
                    WSUniversePK[] wsUniversePKs = pks.getWsUniversePK();
                    if (wsUniversePKs != null && wsUniversePKs.length > 0) {
                        for (int i = 0; i < wsUniversePKs.length; i++) {
                            universeNames.add(wsUniversePKs[i].getPk());
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new XtentisWebappException(e);
        }
        WebContext ctx = WebContextFactory.get();
        HttpSession session = ctx.getSession(false);
        if (session != null) {
            session.setAttribute("sessionTimeOut", session.getMaxInactiveInterval()); //$NON-NLS-1$
            session.setMaxInactiveInterval(-1);
        }

        return universeNames.toArray(new String[universeNames.size()]);

    }
}
