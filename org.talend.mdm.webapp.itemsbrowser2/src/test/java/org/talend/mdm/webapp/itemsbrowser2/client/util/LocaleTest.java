package org.talend.mdm.webapp.itemsbrowser2.client.util;

import junit.framework.TestCase;

public class LocaleTest extends TestCase {

    public void testGetExceptionMessageByLanguage() {
        // Sanity check
        String s = "[fr:f][en:e][zh:c]"; //$NON-NLS-1$
        assertTrue(Locale.getExceptionMessageByLanguage("en", s).equals("e")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(Locale.getExceptionMessageByLanguage("fr", s).equals("f")); //$NON-NLS-1$//$NON-NLS-2$
        assertTrue(Locale.getExceptionMessageByLanguage("zh", s).equals("c")); //$NON-NLS-1$ //$NON-NLS-2$

        // Test backslash escaped ] and \ characters
        s = "[fr:f\\]f][en:e\\\\e][zh:c\\]c\\]]"; //$NON-NLS-1$
        assertTrue(Locale.getExceptionMessageByLanguage("en", s).equals("e\\e")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(Locale.getExceptionMessageByLanguage("fr", s).equals("f]f")); //$NON-NLS-1$//$NON-NLS-2$
        assertTrue(Locale.getExceptionMessageByLanguage("zh", s).equals("c]c]")); //$NON-NLS-1$ //$NON-NLS-2$

        // Test default to English if language code not present and english is
        assertTrue(Locale.getExceptionMessageByLanguage("sp", s).equals("e\\e")); //$NON-NLS-1$//$NON-NLS-2$

        // Test default to whole string when no English
        s = "[fr:f\\]f][zh:c\\]c\\]]"; //$NON-NLS-1$
        assertTrue(Locale.getExceptionMessageByLanguage("sp", s).equals("[fr:f\\]f][zh:c\\]c\\]]")); //$NON-NLS-1$ //$NON-NLS-2$

        // Testing being able to pick out language strings
        s = "dddd[fr:f\\]f]dddd[en:e\\\\e]ddd[zh:c\\]c\\]]dddd"; //$NON-NLS-1$
        assertTrue(Locale.getExceptionMessageByLanguage("en", s).equals("e\\e")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(Locale.getExceptionMessageByLanguage("fr", s).equals("f]f")); //$NON-NLS-1$//$NON-NLS-2$
        assertTrue(Locale.getExceptionMessageByLanguage("zh", s).equals("c]c]")); //$NON-NLS-1$ //$NON-NLS-2$

        // Testing being able to skip malformed country codes
        s = "dddd[french:f\\]f]dddd[en:e\\\\e]ddd[zh:c\\]c\\]]dddd"; //$NON-NLS-1$
        assertTrue(Locale.getExceptionMessageByLanguage("en", s).equals("e\\e")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(Locale.getExceptionMessageByLanguage("fr", s).equals("e\\e")); //$NON-NLS-1$//$NON-NLS-2$
        assertTrue(Locale.getExceptionMessageByLanguage("zh", s).equals("c]c]")); //$NON-NLS-1$ //$NON-NLS-2$

        // Testing special characters outside of language specific messages
        s = "dd\\\\dd[fr:f\\]f]dd\\[ddd[en:e\\\\e]dd[[d[zh:c\\]c\\]]dddd"; //$NON-NLS-1$
        assertTrue(Locale.getExceptionMessageByLanguage("en", s).equals("e\\e")); //$NON-NLS-1$//$NON-NLS-2$
        assertTrue(Locale.getExceptionMessageByLanguage("fr", s).equals("f]f")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(Locale.getExceptionMessageByLanguage("zh", s).equals("c]c]")); //$NON-NLS-1$//$NON-NLS-2$
    }
}
