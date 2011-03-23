package org.talend.mdm.webapp.itemsbrowser2.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;


public class DateUtil {

    private static String datePattern = "yyyy-MM-dd";//$NON-NLS-1$

    public static String timePattern = "HH:mm:ss";//$NON-NLS-1$
    
    public static String dateTimePattern = "yyyy-MM-ddTHH:mm:ss";//$NON-NLS-1$
    
    public static final Date convertStringToDate(String aMask, String strDate)  {
        if (strDate != null && strDate.trim().length() != 0){
            DateTimeFormat df = DateTimeFormat.getFormat(aMask);
            return df.parse(strDate);
        } 
        return null;
    }
    
    public static Date convertStringToDate(String strDate) {
        return convertStringToDate(datePattern, strDate);
    }
    
    public static final String convertDateToString(Date aDate) {
        return getDateTime(datePattern, aDate);
    }
    
    public static final String getDateTime(String aMask, Date aDate) {
        if (aDate != null){
            DateTimeFormat df = DateTimeFormat.getFormat(aMask);
            return df.format(aDate);
        }
        return "";//$NON-NLS-1$
    }
}
