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
package org.talend.mdm.webapp.itemsbrowser2.client.util;

import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService;
import org.talend.mdm.webapp.itemsbrowser2.client.mockup.ClientFakeData;
import org.talend.mdm.webapp.itemsbrowser2.shared.AppHeader;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class Locale {
    
    
    /**
     * DOC HSHU Comment method "getUsingLanguage".
     */
    public static String getLanguage(AppHeader appHeader) {
        
        if(appHeader.isStandAloneMode()){
            return ClientFakeData.DEFAULT_LANGUAUE;
        }else {
            return GetService.getLanguage();
        }

    }

    public static boolean isLetter(char c) {
        boolean result = ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');

        return result;
    }

    /**
     * Parse a multiple language string and return the message corresponding to the current language.
     * 
     * s is expected to be in the following format:
     * 
     * [en:...][fr:...][zh:...]
     * 
     * Characters ] and \ can be escaped in these using backslash escapes, for example
     * 
     * [en: a message with a \] character in the middle]
     * 
     * A message for a language can also be embedded anywhere in the string, for example
     * 
     * abcd[en:...]abcd[fr:...]abcd
     * 
     * If a message for the current language exists, then it is returned.
     * 
     * If a message for the current language doesn't exist, but an english message exists, then it is returned.
     * 
     * If neither of the above are true, then no parsing takes place, and s string is returned.
     * 
     * @param language Two character language code
     * @param s Multiple language message string to be parsed
     * @return Message corresponding to the specified language
     */
    public static String getExceptionMessageByLanguage(String language, String s) {

        String result = s;

        if (s != null && language != null) {

            // Map between languages and messages
            Map<String, String> m = new HashMap<String, String>();

            // Parse states
            final byte PARSE_ERROR = 0;
            final byte LOOKING_FOR_OPENING_BRACKET = 1;
            final byte LOOKING_FOR_COUNTRY_CODE_FIRST_CHAR = 2;
            final byte LOOKING_FOR_COUNTRY_CODE_SECOND_CHAR = 3;
            final byte LOOKING_FOR_COLON = 4;
            final byte LOOKING_FOR_CLOSING_BRACKET = 5;
            final byte ENCOUNTERED_FIRST_BACKSLASH = 6;

            byte parseState = LOOKING_FOR_OPENING_BRACKET;
            StringBuffer countryCodeBuffer = new StringBuffer(); // string buffer for constructing current country code
            StringBuffer messageBuffer = new StringBuffer(); // string buffer for constructing current error message

            for (int i = 0, l = s.length(); i < l && parseState != PARSE_ERROR; ++i) {
                char c = s.charAt(i);

                switch (parseState) {
                case LOOKING_FOR_OPENING_BRACKET:
                    if (c == '[') {
                        parseState = LOOKING_FOR_COUNTRY_CODE_FIRST_CHAR;
                    }
                    break;
                case LOOKING_FOR_COUNTRY_CODE_FIRST_CHAR:
                    if (isLetter(c)) {
                        countryCodeBuffer.append(c);
                        parseState = LOOKING_FOR_COUNTRY_CODE_SECOND_CHAR;
                    } else {
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    }
                    break;
                case LOOKING_FOR_COUNTRY_CODE_SECOND_CHAR:
                    if (isLetter(c)) {
                        countryCodeBuffer.append(c);
                        parseState = LOOKING_FOR_COLON;
                    } else {
                        countryCodeBuffer = new StringBuffer();
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    }
                    break;
                case LOOKING_FOR_COLON:
                    if (c == ':') {
                        parseState = LOOKING_FOR_CLOSING_BRACKET;
                    } else {
                        countryCodeBuffer = new StringBuffer();
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    }
                    break;
                case LOOKING_FOR_CLOSING_BRACKET:
                    if (c == ']') {
                        String countryCode = countryCodeBuffer.toString().toLowerCase();
                        m.put(countryCode, messageBuffer.toString());
                        countryCodeBuffer = new StringBuffer();
                        messageBuffer = new StringBuffer();
                        parseState = LOOKING_FOR_OPENING_BRACKET;
                    } else if (c == '\\') {
                        parseState = ENCOUNTERED_FIRST_BACKSLASH;
                    } else {
                        messageBuffer.append(c);
                    }
                    break;
                case ENCOUNTERED_FIRST_BACKSLASH:
                    if (c == '\\' || c == ']') {
                        messageBuffer.append(c);
                    }
                    parseState = LOOKING_FOR_CLOSING_BRACKET;
                    break;
                default:
                    parseState = PARSE_ERROR;
                }
            }

            String langCode = language.toLowerCase();
            if (m.containsKey(langCode)) {
                result = m.get(langCode);
            } else {
                if (m.containsKey("en")) { //$NON-NLS-1$
                    result = m.get("en"); //$NON-NLS-1$
                } else {
                    result = s;
                }
            }
        }

        return result;
    }
}
