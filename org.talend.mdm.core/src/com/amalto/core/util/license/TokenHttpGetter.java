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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.amalto.core.util.User;
import com.amalto.core.util.UserHelper;
import com.amalto.core.util.json.JSONArray;
import com.amalto.core.util.json.JSONException;
import com.amalto.core.util.json.JSONObject;


/**
 * DOC stephane class global comment. Detailled comment
 */
public class TokenHttpGetter {

    protected JSONObject getData() throws JSONException {
        JSONObject object = new JSONObject();

        // Mac addresses:
        JSONArray macs = new JSONArray();
        try {
            for (String mac : InetUtil.getMacAddress()) {
                macs.put(mac);
            }
            object.put("macadress", macs);
        } catch (Exception e) {
        }

        try {
            object.put("version", getVersion());
        } catch (Exception e) {
        }

        try {
            LicensePOJO license = LicenseHelper.getInstance().getLicense();
            object.put("license", new String(Base64.encodeBase64(license.getLicense().getBytes())));
        } catch (Exception e) {
        }

        object.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        object.put("license_type", LicenseUtil.getInstance().getLicenseType());

        object.put("customer_name", LicenseUtil.getInstance().getCompanyName());

        String[] props = new String[] { "os.name", "java.vm.version", "java.vm.vendor", "user.country", "java.runtime.version",
                "os.arch", "os.version", "sun.arch.data.model" };
        JSONObject propsObj = new JSONObject();

        for (String s : props)
            propsObj.put(s, System.getProperty(s));

        object.put("props", propsObj);

        // Users:
        JSONArray users = new JSONArray();
        List<User> listUsers = UserHelper.getInstance().listUsers();
        for (User current : listUsers) {
            users.put(userToJSon(current));
        }
        object.put("users", users);

        return object;
    }

    protected JSONObject userToJSon(User user) throws JSONException {
        JSONObject toReturn = new JSONObject();
//        toReturn.put("login", user.getLogin());
        toReturn.put("role", user.getRoleNames());
        toReturn.put("active", !user.isEnabled());
        return toReturn;
    }

    private String getVersion() {
        return "";//BrandingHelper.getInstance().getVersionDotRelease();
    }

    protected byte[] encodeData(JSONObject o) throws GeneralSecurityException {
        return B64.encode(Hsifwolb.encrypt(o.toString().getBytes(), YekEht.DK));
    }

    public String getValidationRequest() throws GeneralSecurityException, JSONException {
        return new String(encodeData(getData()));
    }

    public String sendData() throws Exception {
        final URL url = new URL("http://www.talend.com/api/get_tis_validation_token.php");

        URLConnection httpURLConnection = url.openConnection();

        httpURLConnection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());

        final String x = URLEncoder.encode(getValidationRequest(), "UTF8");
        writer.write("msg=" + x);
        writer.flush();

        BufferedReader r = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

        return r.readLine();
    }

}
