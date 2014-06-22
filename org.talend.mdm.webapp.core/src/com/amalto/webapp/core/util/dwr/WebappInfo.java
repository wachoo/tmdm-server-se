package com.amalto.webapp.core.util.dwr;


public class WebappInfo {
   private String licenseModel;
   private int nbUser;
   private String licenseExpired;
   private String company;
   private String licenseType;
   private String license;
   private int activeAdmins;
   
   private int activeNormals;
   private int activeViewers;
   private long soonExpired;
   private boolean tokenSet;
   private int adminUsers;
   private int normalUsers;
   private int viewers;
   private int installations;
   private long tokenSoonExpired;
   private String warning;
   private boolean licenseValid;
  
   public boolean isLicenseValid() {
       return licenseValid;
   }
   public void setLicenseValid(boolean licenseValid) {
        this.licenseValid = licenseValid;
    }
    public String getWarning() {
       return warning;
   }
   public void setWarning(String warning) {
        this.warning = warning;
   }
   public long getTokenSoonExpired() {
	  return tokenSoonExpired;
   }
   public void setTokenSoonExpired(long tokenSoonExpired) {
	   this.tokenSoonExpired = tokenSoonExpired;
   }
   public int getActiveAdmins() {
       return activeAdmins;
   }
   public void setActiveAdmins(int activeAdmins) {
       this.activeAdmins = activeAdmins;
   }
   public int getActiveNormals() {
       return activeNormals;
   }
   public void setActiveNormals(int activeNormals) {
       this.activeNormals = activeNormals;
   }
   public int getActiveViewers() {
       return activeViewers;
   }
   public void setActiveViewers(int activeViewers) {
       this.activeViewers = activeViewers;
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
 
   public int getNormalUsers() {
      return normalUsers;
   }
   public void setNormalUsers(int normalUsers) {
      this.normalUsers = normalUsers;
   }
   public int getViewers() {
      return viewers;
   }
   public void setViewers(int viewers) {
      this.viewers = viewers;
   }
 
   public boolean isTokenSet() {
      return tokenSet;
   }
   public void setTokenSet(boolean tokenSet) {
      this.tokenSet = tokenSet;
   }
   public long getSoonExpired() {
      return soonExpired;
   }
   public void setSoonExpired(long soonExpired) {
      this.soonExpired = soonExpired;
   }
   
   public String getLicenseModel() {
      return licenseModel;
   }
   public void setLicenseModel(String licenseModel) {
      this.licenseModel = licenseModel;
   }
   public int getNbUser() {
      return nbUser;
   }
   public void setNbUser(int nbUser) {
      this.nbUser = nbUser;
   }
   public String getLicenseExpired() {
      return licenseExpired;
   }
   public void setLicenseExpired(String licenseExpired) {
      this.licenseExpired = licenseExpired;
   }
   public String getCompany() {
      return company;
   }
   public void setCompany(String company) {
      this.company = company;
   }
   public String getLicenseType() {
      return licenseType;
   }
   public void setLicenseType(String licenseType) {
      this.licenseType = licenseType;
   }
   public String getLicense() {
      return license;
   }
   public void setLicense(String license) {
      this.license = license;
   }
   
}
