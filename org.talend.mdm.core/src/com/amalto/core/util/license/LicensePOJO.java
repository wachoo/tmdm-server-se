package com.amalto.core.util.license;

import org.w3c.dom.Element;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.util.Util;

public class LicensePOJO extends ObjectPOJO {
   private String license;
   private String customerCompany;
   private String token;
   private LicensePOJOPK PK;
   
   public String getToken() {
      return token;
   }
   public void setToken(String token) {
      this.token = token;
   }
   public String getCustomerCompany() {
      return customerCompany;
   }
   public void setCustomerCompany(String customerCompany) {
      this.customerCompany = customerCompany;
   }
   public LicensePOJO() {
   }
   public LicensePOJO(String license) {
      this.license = license;
   }
   
   public String getLicense() {
      return license;
   }
   public void setLicense(String license) {
      this.license = license;
   }
   
   @Override
   public LicensePOJOPK getPK() {
      if (getLicense() !=null && PK != null) 
          return PK;
      
      return null;
   }
   
   public void setPK(LicensePOJOPK PK) {
       this.PK = PK;
   }
   
   /**
    * parse xml to object.
    * @param xml
    */
   public void parseXML(String xml) {
      try {
         Element result = Util.parse(xml).getDocumentElement();
         setLicense(Util.getFirstTextNode(result, "//license"));
         setCustomerCompany(Util.getFirstTextNode(result, "//customer-company"));
         setToken(Util.getFirstTextNode(result, "//token"));
         setPK(new LicensePOJOPK(Util.getFirstTextNode(result, "//unique-id")));
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
}
