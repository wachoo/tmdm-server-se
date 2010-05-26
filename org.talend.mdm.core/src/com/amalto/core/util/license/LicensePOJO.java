package com.amalto.core.util.license;

import org.w3c.dom.Element;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.util.Util;

public class LicensePOJO extends ObjectPOJO {
   private String license;
   private String customerCompany;
   private String token;
   
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
      if (getLicense()==null) return null;
      return new LicensePOJOPK(getLicense());
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
      }
      catch(Exception e) {
         e.printStackTrace();
      }
   }
}
