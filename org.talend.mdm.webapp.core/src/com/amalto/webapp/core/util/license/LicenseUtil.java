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
package com.amalto.webapp.core.util.license;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.talend.commons.model.ProductsMapping;
import org.talend.commons.model.TalendObject;

import com.amalto.core.util.UserHelper;
import com.amalto.core.util.license.ElapsedTime;
import com.amalto.core.util.license.Token;
import com.amalto.core.util.license.TokenGetter;
import com.amalto.core.util.license.TokenReader;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSLicense;
import com.amalto.webapp.util.webservices.WSPutLicense;


/**
 * DOC mhirt class global comment. Detailled comment <br/>
 * 
 */
public final class LicenseUtil {
    public enum LicenseZone {
        US(1),
        EMEA(0);

        Integer value;

        private LicenseZone(int value) {
            this.value = value;
        }

        public static LicenseZone getZone(int zone) {
            for (LicenseZone lz : LicenseZone.values()) {
                if (lz.value == zone)
                    return lz;
            }
            return null;
        }
    }

    public enum LicenseMode {
        NAMED(0),
        SIMULTANEOUS(1);

        Integer value;

        private LicenseMode(int value) {
            this.value = value;
        }

        public static LicenseMode getMode(int mode) {
            for (LicenseMode lm : LicenseMode.values()) {
                if (lm.value == mode)
                    return lm;
            }
            return null;
        }
    }

    private LicenseUtil() {
        initLicenseUtil();
    }

    private static LicenseUtil instance;

    public static LicenseUtil getInstance() {
        if (instance == null || "".equals(instance.companyName) || instance.getLicenseDate() == null) {
            instance = new LicenseUtil();
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    private void initLicenseUtil() {
       try {
          WSLicense licensep = Util.getPort().getLicense();
          license = licensep.getLicense();
          
          if(license != null) {
	          String customerName = licensep.getCustomerCompany();
	          byte[] licenseb = Base64.decodeBase64(license.getBytes());
	          tokenStr = licensep.getToken();
	          
	          if(tokenStr != null) {
	              token = new TokenReader().readToken(tokenStr.getBytes());
	          }
	          
	          this.init(licenseb, customerName);
          }
          else {
              resetFields();
          }
       }
       catch(Exception e) {
          e.printStackTrace();
       }
    }

    private String companyName = "";

    private Date licenseDate;

    private String licenseType;

    private ProductsMapping productMapping;

    private LicenseZone licenseZone;

    private LicenseMode licenseMode;
    
    private String tokenStr;
    
    private int adminUsers;
    
    private int installations;
    
    private String license;
    
    private Token token;
    
    public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}
	
    public int getInstallations() {
        return installations;
    }

    public void setInstallations(int installations) {
        this.installations = installations;
    }

    public int getAdminUsers() {
        return adminUsers;
    }

    public void setAdminUsers(int adminUsers) {
        this.adminUsers = adminUsers;
    }

    public int getInteractiveUsers() {
        return interactiveUsers;
    }

    public void setInteractiveUsers(int interactiveUsers) {
       this.interactiveUsers = interactiveUsers;
   }

   public int getViewers() {
      return viewers;
   }

   public void setViewers(int viewers) {
      this.viewers = viewers;
   }

   private int interactiveUsers;
    
   private int viewers;
    
   public String getTokenStr() {
      return tokenStr;
   }

   public void setTokenStr(String tokenStr) {
      this.tokenStr = tokenStr;
   }

   public boolean init(byte[] licenseb, String newCompanyName) {
        if (licenseb == null) {
            return false;
        }
        
        String license = new String(Base64.encodeBase64(licenseb));
        boolean result = false;
        
        try {
            TalendObject t = new TalendObject(license, newCompanyName);
            t.isAvailable("mdm");
            companyName = t.companyName;
            licenseDate = t.licenseDate;
            productMapping = t.productMapping;
            licenseZone = LicenseZone.getZone(t.licenseZone);
            licenseMode = LicenseMode.getMode(t.licenseMode);
            licenseType = t.licenseType.toString();
            adminUsers = t.adminUsers;
            interactiveUsers = t.interactiveUsers;
            viewers = t.viewers;
            installations = t.installations;
            
            result = true;
        } 
        catch (Throwable e) {
            resetFields();
        }
        
        return result;
    }

    private void resetFields() {
        companyName = "";
        licenseDate = null;
        productMapping = null;
        licenseZone = null;
        licenseMode = null;
        licenseType = null;
        adminUsers = 0;
        interactiveUsers = 0;
        viewers = 0;
        token = null;
        installations = 0;
    }
    
    /**
     * Set new license.
     * @param licenseKey
     * @throws Exception
     */
    public void setNewLicense(String licenseKey) throws Exception {
        if (licenseKey == null || licenseKey.length() == 0) {
            throw new Exception("license.error.invalid");
        }
        String customerName;
        String license;
        
        try {
            customerName = licenseKey.split("_")[0];
            license = licenseKey.split("_")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("license.error.invalid");
        }
        
        boolean result = init(Base64.decodeBase64(license.getBytes()), customerName);
        
        if (!result) {
           throw new Exception("license.error.invalid");
        }
        
        WSPutLicense putLicense = new WSPutLicense();
        WSLicense wsLicense = new WSLicense();
        wsLicense.setLicense(license);
        wsLicense.setCustomerCompany(customerName);
        putLicense.setWsLicense(wsLicense);
        Util.getPort().putLicense(putLicense);
        //auto get new token
        checkLicense();
        setNewToken(getNewTokenStr(license));
    }
    
    /**
     * the date is validated.
     * @return
     */
    public boolean isLicenseDateValid() {
        if (licenseDate == null)
            return false;
        else
            return (Calendar.getInstance().getTime().compareTo(licenseDate) < 0);
    }
    
    /**
     * the token is validated.
     * @return
     */
    public boolean isTokenDateValid() {
        if (token == null)
            return false;
        else
            return (Calendar.getInstance().getTime().compareTo(token.getEnd()) < 0);
    }
    
    /**
     * check is registry license.
     * @return
     */
    public boolean isRegistriedLicense() {
       boolean registriedLicense = false;
       
       if(getLicenseDate() != null) {
          registriedLicense = true;
       }
       
       return registriedLicense;
    }
    
    /**
     * get expired days.
     * @return
     */
    public long getSoonExpired() {
        // License date:
        final long diff = ElapsedTime.getNbDays(new Date(), getLicenseDate());
       
        return diff;
    }
    
    /**
     * get token expired days.
     * @return
     */
    public long getTokenSoonExpired() {
        // License date:
        if(getToken() == null)
            return 0;
        
        final long diff = ElapsedTime.getNbDays(new Date(), getToken().getEnd());
       
        return diff;
    }

    /**
     * Getter for licenseDate.
     * 
     * @return the licenseDate
     */
    public Date getLicenseDate() {
        return licenseDate;
        // return new Date(109, 3, 3);
    }

    /**
     * Getter for companyName.
     * 
     * @return the companyName
     */
    public String getCompanyName() {
        return companyName;
    }

    public LicenseZone getLicenseZone() {
        return licenseZone;
    }

    public LicenseMode getLicenseMode() {
        return licenseMode;
    }

    public String getLicenseType() {
        return licenseType;
    }
    
    /**
     * Get the number of all available users.
     * @return
     */
    public int getAvailableUsers() {
       return 1;
    }
    
    /**
     * get viewer users.
     * @return
     */
    public int getViewerUsers() {
       return UserHelper.getInstance().getViewerUsers();
    }
    
    /**
     * get the number of normal users.
     * @return
     */
    public int getNormalUsers() {
       return UserHelper.getInstance().getNormalUsers();
    }
    
    /**
     * Get the number of admin users.
     * @return
     */
    public int getNBAdminUsers() {
       return UserHelper.getInstance().getNBAdminUsers();
    }
    
    /**
     * Get the number of active users.
     * @return
     */
    public int getActiveUsers() {
       return UserHelper.getInstance().getActiveUsers();
    }
    
    /**
     * Get the number of active users.
     * @return
     */
    public int getActiveAdmins() {
       return UserHelper.getInstance().getNBAdminUsers();
    }
    
    /**
     * Get the number of active users.
     * @return
     */
    public int getActiveNormals() {
       return UserHelper.getInstance().getNormalUsers();
    }
    
    /**
     * Get the number of active users.
     * @return
     */
    public int getActiveViewers() {
       return UserHelper.getInstance().getViewerUsers();
    }
    
    /**
     * DOC stephane Comment method "setNewToken".
     * 
     * @param token
     * @throws Exception
     */
    public void setNewToken(String tokenStr) throws Exception {
        if (tokenStr == null || tokenStr.length() == 0) {
            throw new Exception("license.tokeninvalid");
        }

        try {
            token = new TokenReader().readToken(tokenStr.getBytes());
        } catch (Exception e) {
            throw e;
        }
        
        WSPutLicense putLicense = new WSPutLicense();
        WSLicense licensep = Util.getPort().getLicense();
        licensep.setToken(tokenStr);
        putLicense.setWsLicense(licensep);
        Util.getPort().putLicense(putLicense);
        
        if(token != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(token.getEnd());
            int sec = cal.get(Calendar.SECOND);
            int min = cal.get(Calendar.MINUTE);
            int hour = cal.get(Calendar.HOUR);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            
            //don't care day of week
            resetService(sec + " " + min + " " + hour + " " + day + " " + month + " "  + " ? " + year);
        }
    }
    
    /**
     * check the license.
     * @throws Exception
     */
    public void checkLicense() throws Exception {
       initLicenseUtil();

       if (companyName == null || companyName.length() < 1) {
           throw new Exception("license.noLicenseSetted");
       }
       
       if (!LicenseUtil.getInstance().isLicenseDateValid()) {
           throw new Exception("license.invalidLicenseDate");
       }
       
       isLicenseNumberUserValid();
   }
    
   /**
    * number of users validate for license.
    * @throws Exception
    */
   public void isLicenseNumberUserValid() throws Exception {
	   //@temp check three field of license 
	   final int activeAdminUsers = getActiveAdmins();
       final int activeNormalUsers = getActiveNormals();
       final int activeViewerUsers = getActiveViewers();
       final int nbAdminUsers = getAdminUsers();
       final int nbNormalUsers = getInteractiveUsers();
       final int nbViewers = getViewers();
       
       if (getLicenseMode() == LicenseMode.NAMED) {
    	   if(nbAdminUsers - activeAdminUsers < 0)
    		   throw new Exception("invalid license admin number");
    	   if(nbNormalUsers - activeNormalUsers < 0)
    		   throw new Exception("invalid license interactive number");
    	   if(nbViewers - activeViewerUsers < 0)
    		   throw new Exception("invalid license viewer number");
       }
   } 
   
   /**
    * reset the schedule of service.
    * @param time new time.
    * @throws Exception
    */
   public void resetService(String time) throws Exception {
       String ip = "127.0.0.1";
       String port = "8080";
       String uri = "http://" + ip + ":" + port + "/SrvSchedule/SrvScheduleServlet?" +
           "action=reschedule&planId=Job.autovalidation.fetchFromOutbound.1272441219265&mode=" + 
           URLEncoder.encode(time);

       HttpClient client = new HttpClient();
       GetMethod get = new GetMethod(uri);
       client.setConnectionTimeout(30000);
       int status = client.executeMethod(get);
       
       if(status != 200)
           org.apache.log4j.Logger.getLogger(this.getClass()).warn("Start up service schedule engine failed! ");
   }
   
   /**
    * get new token string.
    * @return
    */
   public String getNewTokenStr(String licenseStr) {
       try {
           final URL url = new URL("http://www.talend.com/api/get_tis_validation_token.php?msg=");
           
           String newCompany = LicenseUtil.getInstance().getCompanyName();
           URLConnection httpURLConnection = url.openConnection();
           httpURLConnection.setDoOutput(true);
           OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
      
           final String x = URLEncoder.encode(new TokenGetter().getValidationRequest(licenseStr, newCompany), "UTF8");
           writer.write("msg=" + x);
           writer.flush();
           InputStream in = httpURLConnection.getInputStream();
           BufferedReader r = new BufferedReader(new InputStreamReader(in));
           
           return r.readLine();
       }
       catch(Exception e) {
           org.apache.log4j.Logger.getLogger(this.getClass()).warn(e.getLocalizedMessage());
           return null;
       }
   }
   
   /**
    * get token string.
    * @return
    */
   public String getNewTokenStr() {
       String licenseStr = LicenseUtil.getInstance().getLicense();
       return getNewTokenStr(licenseStr);
   }
   
   /**
    * get token data.
    * @return
    * @throws Exception
    */
   public String getTokenData() throws Exception {
       return new TokenGetter().getValidationRequest(license, companyName);
   }
   
   /**
    * check if is set token.
    * @return
    */
   public boolean isSetToken() {
       return getToken() != null;
   }
}
