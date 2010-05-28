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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import org.talend.commons.model.ProductsMapping;
import org.talend.commons.model.TalendObject;

import com.amalto.core.util.UserHelper;
import com.amalto.core.util.Util;


/**
 * DOC mhirt class global comment. Detailled comment <br/>
 * 
 */
public final class LicenseUtil {

    private static Logger log = Logger.getLogger(LicenseUtil.class);

    /**
     * DOC Administrator LicenseUtil class global comment. Detailled comment
     */
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

    /**
     * DOC Administrator LicenseUtil class global comment. Detailled comment
     */
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
        if (instance == null || "".equals(instance.companyName)) {
            instance = new LicenseUtil();
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    private void initLicenseUtil() {
        // resetFields();
        List<String> licenses = LicenseHelper.getInstance().listLicenses();
        LicensePOJO licenseProxy = null;
        
        if ((licenses != null) && (licenses.size() == 1)) {
            licenseProxy = new LicensePOJO();
            licenseProxy.parseXML(licenses.get(0));
        }
        
        if (licenseProxy != null) {
            this.init(Base64.decodeBase64(licenseProxy.getLicense().getBytes()), licenseProxy.getCustomerCompany());
        }
        else {
            resetFields();
        }
    }
    
    private String companyName = "";

    private int nbUser = 1;

    private Date licenseDate;

    private String licenseType;

    private ProductsMapping productMapping;

    private LicenseZone licenseZone;

    private LicenseMode licenseMode;
    
    private int adminUsers;
    
    private int interactiveUsers;
    
    private int viewers;
    
    private int installations;

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
            throw new Exception("License invalid");
        }
        String customerName;
        String license;
        try {
            customerName = licenseKey.split("_")[0];
            license = licenseKey.split("_")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("License invalid");
        }

        LicensePOJO licenseProxy = LicenseHelper.getInstance().getLicense();
        if (licenseProxy == null) {
            licenseProxy = new LicensePOJO();
        }

        byte[] encryptedBytes = Base64.decodeBase64(license.getBytes());
        licenseProxy.setLicense(license);
        licenseProxy.setCustomerCompany(customerName);

        // Delegate insert of the proxy to the facade :
        boolean result = this.init(encryptedBytes, customerName);

        if (!result) {
            throw new Exception("License invalid");
        }
        
        LicenseHelper.getInstance().saveOrUpdate(licenseProxy);

        licenseProxy = LicenseHelper.getInstance().getLicense();
        this.init(Base64.decodeBase64(licenseProxy.getLicense().getBytes()), customerName);
//        this.updateAdminData();
    }

    /**
     * DOC stephane Comment method "setNewToken".
     * 
     * @param token
     * @param update
     * @throws Exception
     */
    public void setNewToken(String token) throws Exception {
        if (token == null || token.length() == 0) {
            throw new Exception("token invalid");
        }

        new TokenReader().readToken(token.getBytes());

        LicensePOJO license = LicenseHelper.getInstance().getLicense();
        license.setToken(token);
        LicenseHelper.getInstance().saveOrUpdate(license);
    }

    /**
     * 
     * Update data function of current new license.
     */
    public void updateAdminData() {/*
        if (!isLicenseForFileTrigger()) {
            try {
                Scheduler scheduler = SchedulerInitializer.getInstance().getScheduler();
                String[] triggerNames = scheduler.getTriggerNames(QuartzConstants.GROUP_JOB_CONDUCTOR);
                for (int i = 0; i < triggerNames.length; i++) {
                    String triggerName = triggerNames[i];
                    if (triggerName.startsWith(TRIGGER_TYPE.getFileTriggerPrefix())) {
                        scheduler.pauseTrigger(triggerName, QuartzConstants.GROUP_JOB_CONDUCTOR);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    */}

    public boolean isLicenseDateValid() {
        if (licenseDate == null)
            return false;
        else
            return (Calendar.getInstance().getTime().compareTo(licenseDate) < 0);
    }

    public void isLicenseNumberUserValid() throws Exception {
      //@temp check three field of license 
        final int activeAdminUsers = UserHelper.getInstance().getNBAdminUsers();
        final int activeNormalUsers = UserHelper.getInstance().getNormalUsers();
        final int activeViewerUsers = UserHelper.getInstance().getViewerUsers();
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

    /*public int getAvailableUsers() {
        final int countActiveUsers = UserHelper.getInstance().countActiveUsers();
        final int nbUser2 = getNbUser();
        if (getLicenseMode() == LicenseMode.NAMED) {
            return nbUser2 - countActiveUsers;
        } else {
            return 1;
        }
    }*/

    public int getAdminUsers() {
        return adminUsers;
    }

    public int getInteractiveUsers() {
        return interactiveUsers;
    }

    public int getViewers() {
        return viewers;
    }

    public int getInstallations() {
        return installations;
    }

    public void checkToken() throws Exception {
        Token token = getToken(true);
        if (token == null) {
            throw new Exception("No token set");
        }
        token.isValid();
    }

    public Token getToken(boolean tryGettingNewToken) throws Exception {
        LicensePOJO license = LicenseHelper.getInstance().getLicense();
        String tokenKey = license.getToken();
        if (tokenKey == null) {
            if (tryGettingNewToken) {
                getNewToken();
                return getToken(false);
            }
            throw new Exception("No token set");
        }

        try {
            final Token token = new TokenReader().readToken(tokenKey.getBytes());
            if (token.isNearToExpire() || token.isAlreadyExpired())
                getNewToken();
            return token;
        } catch (Throwable e) {
            throw new Exception("Malformed token");
        }
    }
    
    public void checkLicense() throws Exception {
        checkLicense(true);
    }
    
    public void checkLicense(boolean reGet) throws Exception {
        if(instance == null || reGet) {
            initLicenseUtil();
        }
        
        if (companyName == null || companyName.length() < 1) {
            throw new Exception("No license available, please input your valid license");
        }
        if (!LicenseUtil.getInstance().isLicenseDateValid()) {
            throw new Exception("License Expired");
        }
        isLicenseNumberUserValid();
        checkToken();
    }

    public List<String> getLicenseWarning(String language) {
        List<String> toReturn = new ArrayList<String>();

        if (getLicenseDate() == null)
            return toReturn;

        // License date:
        final long diff = ElapsedTime.getNbDays(new Date(), getLicenseDate());
        // final long diff = licenseDate.getTime() - new Date().getTime() + 24 * 60 * 60 * 1000;
        if (diff < 20 && diff > 0) {
            long nbDays = diff;
            //@temp write message at here.
            String msg;
            if("fr".equals(language)) {
                msg = "La license expirera dans {0} jours. Veuillez contacter votre commercial Talend.";
            }
            else {
                msg = "The License will expire in {0} days. Please contact your Talend account manager.";
            }
            
            toReturn.add(Util.getMessage(msg, nbDays));
//            toReturn.add(MessageHandler.getMessage("license.soonExpire", nbDays));
        }

        // Token date:
        String warning;
        try {
            warning = getToken(true).getWarning(language);
            if (warning != null) {
                toReturn.add(warning);
                // getNewToken();
            }
        } catch (Exception e) {
            // getNewToken();
        }
        return toReturn;
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

    /**
     * "isLicenseForFileTrigger".
     * 
     * @return true if license is valid for FileTrigger else false.
     */
    public boolean isLicenseForFileTrigger() {
        /*if (productMapping != null && productMapping.isAvailable(ProductsConstants.FILETRIGGER)) {
            return true;
        }*/
        return false;
    }

    /**
     * "isLicenseForFileTrigger".
     * 
     * @return true if license is valid for Virtal Servers else false.
     */
    public boolean isLicenseForVirtualServers() {
        /*if (productMapping != null && productMapping.isAvailable(ProductsConstants.VIRTUALSERVER)) {
            return true;
        }*/
        return false;
    }

    /**
     * "isLicenseForResuming".
     * 
     * @return true if license is valid for Resuming else false.
     */
    public boolean isLicenseForResuming() {
        // if (productMapping != null && productMapping.isAvailable(ProductsConstants.RESUMING)) {
        // return true;
        // }
        // return false;
        return isLicenseForFileTrigger();
    }

    /**
     * "isLicenseForDashboard".
     * 
     * @return true if license is valid for Dashboard else false.
     */
    public boolean isLicenseForDashboard() {
        /*if (productMapping != null && productMapping.isAvailable(ProductsConstants.DASHBOARD)) {
            return true;
        }*/
        return false;
    }

    /**
     * "isLicenseForAudit".
     * 
     * @return true if license is valid for Audit else false.
     */
    public boolean isLicenseForAudit() {
        /*if (productMapping != null && productMapping.isAvailable(ProductsConstants.AUDIT)) {
            return true;
        }*/
        return false;
    }

    private void getNewToken() throws Exception {
        log.info("Retrieving token");
        try {
            String token = new TokenHttpGetter().sendData();
            try {
                new TokenReader().readToken(token.getBytes());
                setNewToken(token);
                log.info("New token set");
            } catch (Throwable t) {
                log.warn("Unreadable token key");
                // setNewToken(null);
            }
        } catch (Exception e) {
            log.info("Cannot automatically get retrieve", e);
            throw new Exception("failed to retreive the token, please set token  on manual.");
        }
    }
    
    /**
     * check if is license parsed.
     * @return
     * @throws Exception 
     */
    public static boolean isAlreadyParsed() throws Exception {
        //two party are most waste time.
        //1. init license. get data from database.
        //2. get new token. get token from www.talend.com
        
        if(instance != null) {
            if(instance.getLicenseDate() == null) {
                return true;
            }
            
            Token token = instance.getToken(false);
            
            if(token == null) {
                return false;
            }
            else if(token != null) {
                if(token.isNearToExpire() || token.isAlreadyExpired()) {
                    return false;
                }
                else {
                    return true;
                }
            }
        }
        else {
            return false;
        }
        
        return true;
    }
}
