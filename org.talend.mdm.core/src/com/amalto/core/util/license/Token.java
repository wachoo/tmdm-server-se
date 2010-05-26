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

import java.text.MessageFormat;
import java.util.Date;

import com.amalto.core.util.Util;
/**
 * DOC Administrator class global comment. Detailled comment
 */
public final class Token {

    private static final int DAYSOFWARNING = 10;

    private Date start;

    private Date end;

    public void isValid() throws Exception {
        isValid(new Date());
    }

    public String getWarning(String language) {
        return getWarning(new Date(), language);
    }

    protected void isValid(Date now) throws Exception {
        boolean fullValid = now.equals(start) || now.equals(end) || (now.after(start) && now.before(end));
        if (fullValid)
            return;
        else
            throw new Exception("license invalid validation Token");
    }

    protected boolean isAlreadyExpired() {
        Date now = new Date();
        return now.after(end);
    }

    public boolean isNearToExpire() {
        return isNearToExpire(new Date());
    }

    protected boolean isNearToExpire(Date now) {
        // Not before:
        if (now.before(start))
            return false;

        // Not after:
        if (now.after(end))
            return false;

        final long daysBeforeExpire = getDaysBeforeExpire(now);
        return daysBeforeExpire >= 0 && daysBeforeExpire <= 10;
    }

    protected String getWarning(Date now, String language) {
        if (isNearToExpire(now)) {
            String msg;
        
            if("fr".equals(language)) {
                msg = "Le jeton expirera dans {0} jours.";
            }
            else {
                msg = "The token will expire in {0} days";
            }
            
            return Util.getMessage(msg, getDaysBeforeExpire(now));
//            return PropertiesLoaderProvider.getMessages().getString("license.error.expire", getDaysBeforeExpire(now));
        }
        else
            return null;
    }

    protected long getDaysBeforeExpire(Date now) {
        return ElapsedTime.getNbDays(now, end) + 1;
    }

    public Date getStart() {
        return this.start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return this.end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

}
