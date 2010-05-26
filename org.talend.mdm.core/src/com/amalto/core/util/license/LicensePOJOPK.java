package com.amalto.core.util.license;

import com.amalto.core.ejb.ObjectPOJOPK;

public class LicensePOJOPK extends ObjectPOJOPK {
   public LicensePOJOPK(ObjectPOJOPK pk) {
      super(pk.getIds());
   }
   
   public LicensePOJOPK(String license) {
      super(license);
   }
}
