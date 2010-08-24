package com.amalto.core.util.license;

import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;

import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class LicenseHelper {
    private static LicenseHelper instance;
    
    private LicenseHelper() {
        super();
    }
    
    public static synchronized LicenseHelper getInstance() {
        if(instance == null) {
            return instance  = new LicenseHelper();
        }
        
        return instance;
    }
    
    /**
     * listLicenses.
     * 
     * @param loadActiveLicensesOnly param to load only active licenses
     * @return the licenses found
     */
    public List<String> listLicenses() {
        String query = "for $pivot0 in collection('amaltoOBJECTSLicense')/license-pOJO return if ($pivot0) then $pivot0 else <license/>";
        XmlServerSLWrapperLocal server = null;

        try {
           server = ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
           return server.runQuery(null, "amaltoOBJECTSLicense", query, null);
        } 
        catch(Exception e) {
           e.printStackTrace();
           return new ArrayList<String>();
        }
        
    }
    
    public LicensePOJO getLicense() throws Exception {
        List<String> listLicenses = listLicenses();
        LicensePOJO licensePOJO = null;
        
        if (listLicenses.size() == 1) {
            String license = listLicenses.get(0);
            licensePOJO = new LicensePOJO();
            licensePOJO.parseXML(license);
        }

        return licensePOJO;
    }
    
    /**
     * saveOrUpdate.
     * 
     * @param license the license to save
     * @return true if license has been saved
     */
    public boolean saveOrUpdate(LicensePOJO license) throws XtentisException {
        try {
            LicensePOJO licensePOJO = getLicense();
            
            if(licensePOJO != null && licensePOJO.getLicense() != null) {
                deleteLicenseCluster();
//                licensePOJO.remove(LicensePOJO.class, new LicensePOJOPK(licensePOJO.getPK().getUniqueId()));
            }
            
            license.setPK(new LicensePOJOPK("License"));
            license.store();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * delete license cluster.
     * @return
     */
    private boolean deleteLicenseCluster() throws XtentisException {
        XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
        long result = server.deleteCluster("", LicensePOJO.getCluster(LicensePOJO.class));
        
        return result != -1;
    }
}
