// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.core.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.talend.commons.model.ProductsMapping;
import org.talend.commons.model.TalendObject;

import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItems;
import com.amalto.webapp.util.webservices.WSLicense;
import com.amalto.webapp.util.webservices.WSPutLicense;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;


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
          String license = licensep.getLicense();
          String customerName = licensep.getCustomerCompany();
          byte[] licenseb = Base64.decodeBase64(license.getBytes());
          
          this.init(licenseb, customerName);
       }
       catch(Exception e) {
          e.printStackTrace();
       }
    }

    private String companyName = "";

    private int nbUser = 1;

    private Date licenseDate;

    private String licenseType;

    private ProductsMapping productMapping;

    private LicenseZone licenseZone;

    private LicenseMode licenseMode;

    public boolean init(byte[] licenseb, String newCompanyName) {
        if (licenseb == null) {
            return false;
        }
        String license = new String(Base64.encodeBase64(licenseb));
        boolean result = false;
        try {
            TalendObject t = new TalendObject(license, newCompanyName);

            companyName = t.companyName;
            nbUser = t.nbUser;
            licenseDate = t.licenseDate;
            productMapping = t.productMapping;
            licenseZone = LicenseZone.getZone(t.licenseZone);
            licenseMode = LicenseMode.getMode(t.licenseMode);
            licenseType = t.licenseType.toString();
            result = true;
        } catch (Throwable e) {
            resetFields();
        }
        return result;
    }

    private void resetFields() {
        companyName = "";
        nbUser = 1;
        licenseDate = null;
        productMapping = null;
        licenseZone = null;
        licenseMode = null;
        licenseType = null;
    }

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
    }

    public boolean isLicenseDateValid() {
        if (licenseDate == null)
            return false;
        else
            return (Calendar.getInstance().getTime().compareTo(licenseDate) < 0);
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
     * Getter for licenseDate.
     * 
     * @return the licenseDate
     */
    public Date getLicenseDate() {
        return licenseDate;
        // return new Date(109, 3, 3);
    }

    /**
     * Getter for nbUser.
     * 
     * @return the nbUser
     */
    public int getNbUser() {
        return nbUser;
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
    
    public int getAvailableUsers() {
       final int countActiveUsers = getActiveUsers();
       final int nbUser2 = getNbUser();
       if (getLicenseMode() == LicenseMode.NAMED) {
           return nbUser2 - countActiveUsers;
       } else {
           return 1;
       }
   }
    
   /**
    * Get the active users.
    */
    public static int getActiveUsers() {
       String dataclusterPK = "PROVISIONING";
       String[] results = new String[0];
       WSWhereCondition condition = new WSWhereCondition("User/enabled", 
             WSWhereOperator.EQUALS, "yes", WSStringPredicate.NONE, false);
       WSWhereItem whereItem = new WSWhereItem(condition, null, null);
       
       try {
          results = Util.getPort().getItems(new WSGetItems(
             new WSDataClusterPK(dataclusterPK), "User", whereItem, 0, 0, Integer.MAX_VALUE)).getStrings();
       }
       catch(Exception e) {
          e.printStackTrace();
       }
       
       return results.length;
    }
}
