// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.util;

import java.util.*;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class User implements Cloneable {
    
    private static final Logger LOG = Logger.getLogger(User.class);

    Integer ID;

    String userName;

    String givenName;

    String familyName;

    String phoneNumber;

    String realEmail;

    String fakeEmail;

    String company;

    long registrationDateAsLong;

    long lastVisitDateAsLong;

    String password;

    boolean enabled;

    boolean viewRealEmail;

    String signature;

    String homePage;

    String universe;

    String language;

    Set<Role> roles;

    Map<String, String> properties;

    Map<String, String> applications;

    Set<String> roleNames;

    public User() {

    }

    /**
     * User format
     * 
     * <user> <username/> <password/> <givename/> <familyname/> <portalid/> <signature/> <realemail/> <fakeemail/>
     * <viewrealemail/> <compagny/> <registrationdate/> <lastvisitdate/> <enabled/> <homePage> <roles> <role/> </roles>
     * <properties> <property> <name/> <value/> </property> </properties> <applications> <application> <name/> <value/>
     * </application> </applications> </user>
     */

    public String serialize() {
        String user = "<User>" + "    <username>" + userName + "</username>" + "    <password>" + password + "</password>"
                + "    <givenname>" + (givenName == null ? "" : givenName) + "</givenname>" + "    <familyname>"
                + (familyName == null ? "" : familyName) + "</familyname>" + "    <phonenumber>"
                + (phoneNumber == null ? "" : phoneNumber) + "</phonenumber>" + "    <company>"
                + (company == null ? "" : company) + "</company>" + "    <id>" + ID + "</id>" + "    <signature>"
                + (signature == null ? "" : signature) + "</signature>" + "    <realemail>"
                + (realEmail == null ? "" : realEmail) + "</realemail>" + "    <fakeemail>"
                + (fakeEmail == null ? "" : fakeEmail) + "</fakeemail>" + "    <viewrealemail>" + (viewRealEmail ? "yes" : "no")
                + "</viewrealemail>" + "    <registrationdate>"
                + (registrationDateAsLong == 0 ? System.currentTimeMillis() : registrationDateAsLong) + "</registrationdate>"
                + "    <lastvisitdate>" + lastVisitDateAsLong + "</lastvisitdate>" + "    <enabled>" + (enabled ? "yes" : "no")
                + "</enabled>" + "    <homepage>" + homePage + "</homepage>" + "    <universe>"
                + (universe == null ? "" : universe) + "</universe>" + "    <language>" + (language == null ? "" : language)
                + "</language>";
        user += "    <roles>";
        Iterator<String> iter = roleNames.iterator();
        while (iter.hasNext()) {
            user += "<role>" + iter.next() + "</role>";
        }
        user += "    </roles>";
        user += "    <properties>";
        if (properties != null) {
            for (iter = properties.keySet().iterator(); iter.hasNext();) {
                String key = iter.next();
                String value = properties.get(key);
                user += "        <property>" + "            <name>" + key + "</name>" + "            <value>" + value
                        + "</value>" + "        </property>";
            }
        }
        user += "    </properties>";

        user += "    <applications>";
        if (applications != null) {
            for (iter = applications.keySet().iterator(); iter.hasNext();) {
                String key = iter.next();
                String value = applications.get(key);
                user += "        <application>" + "            <name>" + key + "</name>" + "            <value>" + value
                        + "</value>" + "        </application>";
            }
        }
        user += "    </applications>";
        user += "</User>";

        return user;

    }

    public static User parse(String xml) throws Exception {
        User user = new User();
        parse(xml, user);
        return user;
    }

    public static void parse(String xml, User user) throws Exception {

        try {
            Element result = Util.parse(xml).getDocumentElement();
            user.setUserName(Util.getFirstTextNode(result, "//username"));
            user.setPassword(Util.getFirstTextNode(result, "//password"));
            user.setGivenName(Util.getFirstTextNode(result, "//givenname"));
            user.setFamilyName(Util.getFirstTextNode(result, "//familyname"));
            user.setPhoneNumber(Util.getFirstTextNode(result, "//phonenumber"));
            user.setCompany(Util.getFirstTextNode(result, "//company"));
            // user.setID(new Integer(Util.getFirstTextNode(result, "id")));
            user.setSignature(Util.getFirstTextNode(result, "//signature"));
            user.setRealEmail(Util.getFirstTextNode(result, "//realemail"));
            user.setFakeEmail(Util.getFirstTextNode(result, "//fakeemail"));
            user.setViewRealEmail("yes".equals(Util.getFirstTextNode(result, "//viewrealemail")));

            try {
                user.setRegistrationDate(new Date(Long.parseLong(Util.getFirstTextNode(result, "//registrationdate")))); //$NON-NLS-1$
            } catch (Exception nfe) {
                user.setRegistrationDate(null);
            }

            try {
                user.setLastVisitDate(new Date(Long.parseLong(Util.getFirstTextNode(result, "//lastvisitdate")))); //$NON-NLS-1$
            } catch (Exception nfe) {
                user.setLastVisitDate(null);
            }
            user.setEnabled("yes".equals(Util.getFirstTextNode(result, "//enabled")));
            user.setHomePage(Util.getFirstTextNode(result, "//homepage"));
            user.setUniverse(Util.getFirstTextNode(result, "//universe"));
            user.setLanguage(Util.getFirstTextNode(result, "//language"));

            String[] roles = Util.getTextNodes(result, "//roles/role");
            HashSet<String> rs = new HashSet<String>();
            if (roles != null) {
                Collections.addAll(rs, roles);
            }
            user.setRoleNames(rs);

            // XtentisPropertyMap propertyMap = new XtentisPropertyMap(user);
            HashMap<String, String> props = new HashMap<String, String>();
            NodeList properties = Util.getNodeList(result, "//properties/property");
            if (properties != null) {
                for (int i = 0; i < properties.getLength(); i++) {
                    Element property = (Element) properties.item(i);
                    String name = Util.getFirstTextNode(property, "name");
                    String value = Util.getFirstTextNode(property, "value");
                    props.put(name, value);
                }
            }
            user.setProperties(props);

            HashMap<String, String> apps = new HashMap<String, String>();
            NodeList appNodes = Util.getNodeList(result, "//applications/application");
            if (appNodes != null) {
                for (int i = 0; i < appNodes.getLength(); i++) {
                    Element application = (Element) appNodes.item(i);
                    String name = Util.getFirstTextNode(application, "name");
                    String value = Util.getFirstTextNode(application, "value");
                    apps.put(name, value);
                }
            }
            user.setApplications(apps);
            if(LOG.isDebugEnabled()){
                LOG.debug("parsing xml complete."); //$NON-NLS-1$
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new Exception("Failed to parse user: " + ": " + e.getLocalizedMessage());  //$NON-NLS-1$//$NON-NLS-2$
        }

    }

    public Map<String, String> getApplications() {
        return applications;
    }

    public void setApplications(Map<String, String> applications) {
        this.applications = applications;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // for groovy
    public String getFakeemail() {
        return getFakeEmail();
    }
    
    public String getFakeEmail() {
        return fakeEmail;
    }
    
    public void setFakeEmail(String fakeEmail) {
        this.fakeEmail = fakeEmail;
    }

    // for groovy
    public String getFamilyname() {
        return getFamilyName();
    }
    
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    // for groovy
    public String getGivenname() {
        return getGivenName();
    }

    public String getGivenName() {
        return givenName;
    }
    
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    // for groovy
    public String getHomepage() {
        return getHomePage();
    }
    
    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer id) {
        ID = id;
    }

    // for groovy
    public long getLastvisitdate() {
        return getLastVisitDateAsLong();
    }
    
    public long getLastVisitDateAsLong() {
        return lastVisitDateAsLong;
    }

    public void setLastVisitDate(Date lastVisitDate) {
        this.lastVisitDateAsLong = (lastVisitDate == null ? 0 : lastVisitDate.getTime());
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // for groovy
    public String getPhonenumber() {
        return getPhoneNumber();
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
    
    // for groovy
    public String getRealemail() {
        return getRealEmail();
    }
    
    public String getRealEmail() {
        return realEmail;
    }

    public void setRealEmail(String realEmail) {
        this.realEmail = realEmail;
    }

    // for groovy
    public long getRegistrationdate() {
        return getRegistrationDate();
    }
    
    public long getRegistrationDate() {
        return registrationDateAsLong;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDateAsLong = (registrationDate == null ? 0 : registrationDate.getTime());
    }

    // for groovy
    public Set<String> getRolenames() {
        return getRoleNames();
    }
    
    public Set<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(Set<String> roleNames) {
        this.roleNames = roleNames;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    // for groovy
    public String getUsername() {
        return getUserName();
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    // for groovy
    public boolean isViewrealemail() {
        return isViewRealEmail();
    }

    public boolean isViewRealEmail() {
        return viewRealEmail;
    }

    public void setViewRealEmail(boolean viewRealEmail) {
        this.viewRealEmail = viewRealEmail;
    }

    public String getUniverse() {
        return universe;
    }

    public void setUniverse(String universe) {
        this.universe = universe;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }

}
