package com.amalto.core.util.license;

import java.io.BufferedReader;
import java.io.InputStream;
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
import org.talend.commons.model.TalendObject;


import com.amalto.core.util.User;
import com.amalto.core.util.UserHelper;
import com.amalto.core.util.json.JSONArray;
import com.amalto.core.util.json.JSONException;
import com.amalto.core.util.json.JSONObject;


/**
 * DOC stephane class global comment. Detailled comment
 */
public class TokenGetter {
   protected JSONObject getData(String license, String newCompanyName) throws JSONException {
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
//           object.put("version", getVersion());
       } catch (Exception e) {
       }

       try {
           TalendObject t = new TalendObject(license, newCompanyName);
           object.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
           object.put("license_type", t.licenseType);
           object.put("customer_name", t.companyName);
           object.put("license", new String(Base64.encodeBase64(license.getBytes())));
       } 
       catch (Exception e) {
       }

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
//       toReturn.put("login", user);
       toReturn.put("role", user.getRoleNames());
       toReturn.put("active", !user.isEnabled());
       return toReturn;
   }

   private String getVersion() {
       return "";//BrandingHelper.getInstance().getVersionDotRelease();
   }

   protected static byte[] encodeData(JSONObject o) throws GeneralSecurityException {
       return B64.encode(Hsifwolb.encrypt(o.toString().getBytes(), YekEht.dk));
   }

   public String getValidationRequest(String license, String newCompanyName) throws GeneralSecurityException, JSONException {
       return new String(encodeData(getData(license, newCompanyName)));
   }

   public String sendData(String license, String newCompanyName) throws Exception {
       final URL url = new URL("http://www.talend.com/api/get_tis_validation_token.php?msg=");

       URLConnection httpURLConnection = url.openConnection();
       httpURLConnection.setDoOutput(true);
       OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());

       final String x = URLEncoder.encode(getValidationRequest(license, newCompanyName), "UTF8");
       writer.write("msg=" + x);
       writer.flush();
       InputStream in = httpURLConnection.getInputStream();
       BufferedReader r = new BufferedReader(new InputStreamReader(in));
       
       while(r.readLine() != null)
    	   System.out.println(r.readLine());
       return r.readLine();
   }
}
