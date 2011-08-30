package org.talend.mdm.webapp.browserecords.client.util;

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
        return getDateTime(aDate);
    }
    
    public static final String getDate(Date aDate) {
        if (aDate != null){
            DateTimeFormat df = DateTimeFormat.getFormat(datePattern);
            return df.format(aDate);//$NON-NLS-1$
        }
        return "";//$NON-NLS-1$
    }
    
    public static final String getDateTime(Date aDate) {
        if (aDate != null){
            DateTimeFormat df = DateTimeFormat.getFormat(datePattern);
            DateTimeFormat tf = DateTimeFormat.getFormat(timePattern);
            return df.format(aDate) + "T" + tf.format(aDate);//$NON-NLS-1$
        }
        return "";//$NON-NLS-1$
    }
}
