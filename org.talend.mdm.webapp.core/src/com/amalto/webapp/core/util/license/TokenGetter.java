package com.amalto.webapp.core.util.license;

import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONException;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSLicense;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class TokenGetter {
   
   private static TokenGetter instance;
   
   private TokenGetter() {
      initTokenGetter();
   }
   
   private void initTokenGetter() {
      
   }

   protected static JSONObject getData() throws JSONException {
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
           WSLicense licensep = Util.getPort().getLicense();
           
//           License license = LicenseHelper.getInstance().getLicense();
           object.put("license", new String(Base64.encodeBase64(licensep.getLicense().getBytes())));
       } 
       catch (Exception e) {
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
       /*JSONArray users = new JSONArray();
       List<User> listUsers = UserHelper.getInstance().listUsers(false);
       for (User current : listUsers) {
           users.put(userToJSon(current));
       }
       object.put("users", users);*/

       return object;
   }

   /*protected JSONObject userToJSon(User user) throws JSONException {
       JSONObject toReturn = new JSONObject();
       toReturn.put("login", user.getLogin());
       toReturn.put("role", user.getRole().getName());
       toReturn.put("active", !user.isDeleted());
       return toReturn;
   }*/

   private String getVersion() {
       return "";//BrandingHelper.getInstance().getVersionDotRelease();
   }

   protected static byte[] encodeData(JSONObject o) throws GeneralSecurityException {
       return B64.encode(Hsifwolb.encrypt(o.toString().getBytes(), YekEht.dk));
   }

   public static String getValidationRequest() throws GeneralSecurityException, JSONException {
       return new String(encodeData(getData()));
   }

   /*public String sendData() throws Exception {
       final URL url = new URL("http://www.talend.com/api/get_tis_validation_token.php");

       URLConnection httpURLConnection = url.openConnection();

       httpURLConnection.setDoOutput(true);
       OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());

       final String x = URLEncoder.encode(getValidationRequest(), "UTF8");
       writer.write("msg=" + x);
       writer.flush();

       BufferedReader r = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

       return r.readLine();
   }*/
}
