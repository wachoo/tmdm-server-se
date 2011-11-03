package org.talend.mdm.webapp.general.client;

import com.extjs.gxt.ui.client.state.CookieProvider;

public class Cookies {

    private static CookieProvider provider = new CookieProvider(null, null, null, false);

    public static void setValue(String key, Object value) {
        provider.set(key, value);
    }

    public static Object getValue(String key) {
        return provider.get(key);
    }
}
