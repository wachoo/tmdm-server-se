package com.amalto.core.util;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GroovyUser {

    Integer id;

    String username;

    String givenname;

    String familyname;

    String phonenumber;

    String realemail;

    String fakeemail;

    String company;

    long registrationdate;

    long lastvisitdate;

    String password;

    boolean enabled;

    boolean viewrealemail;

    String signature;

    String homepage;

    String universe;

    String language;

    Set<Role> roles;

    Map<String, String> properties;

    Map<String, String> applications;

    Set<String> rolenames;

    public GroovyUser() {

    }

    public static GroovyUser parse(String xml) throws Exception {
        GroovyUser user = new GroovyUser();
        parse(xml, user);
        return user;
    }

    public static void parse(String xml, GroovyUser user) throws Exception {

        try {
            Element result = Util.parse(xml).getDocumentElement();
            user.setUsername(Util.getFirstTextNode(result, "//username")); //$NON-NLS-1$
            user.setPassword(Util.getFirstTextNode(result, "//password")); //$NON-NLS-1$
            user.setGivenname(Util.getFirstTextNode(result, "//givenname")); //$NON-NLS-1$
            user.setFamilyname(Util.getFirstTextNode(result, "//familyname")); //$NON-NLS-1$
            user.setPhonenumber(Util.getFirstTextNode(result, "//phonenumber")); //$NON-NLS-1$
            user.setCompany(Util.getFirstTextNode(result, "//company")); //$NON-NLS-1$
            //user.setId(new Integer(Util.getFirstTextNode(result, "id"))); //$NON-NLS-1$
            user.setSignature(Util.getFirstTextNode(result, "//signature")); //$NON-NLS-1$
            user.setRealemail(Util.getFirstTextNode(result, "//realemail")); //$NON-NLS-1$
            user.setFakeemail(Util.getFirstTextNode(result, "//fakeemail")); //$NON-NLS-1$
            user.setViewrealemail("yes".equals(Util.getFirstTextNode(result, "//viewrealemail")));  //$NON-NLS-1$//$NON-NLS-2$

            try {
                user.setRegistrationdate(new Date(Long.parseLong(Util.getFirstTextNode(result, "//registrationdate")))); //$NON-NLS-1$
            } catch (Exception nfe) {
                user.setRegistrationdate(null);
            }

            try {
                user.setLastvisitdate(new Date(Long.parseLong(Util.getFirstTextNode(result, "//lastvisitdate")))); //$NON-NLS-1$
            } catch (Exception nfe) {
                user.setLastvisitdate(null);
            }
            user.setEnabled("yes".equals(Util.getFirstTextNode(result, "//enabled")));  //$NON-NLS-1$//$NON-NLS-2$
            user.setHomepage(Util.getFirstTextNode(result, "//homepage")); //$NON-NLS-1$
            user.setUniverse(Util.getFirstTextNode(result, "//universe")); //$NON-NLS-1$
            user.setLanguage(Util.getFirstTextNode(result, "//language")); //$NON-NLS-1$

            String[] roles = Util.getTextNodes(result, "//roles/role"); //$NON-NLS-1$
            HashSet<String> rs = new HashSet<String>();
            if (roles != null) {
                for (String role : roles) {
                    rs.add(role);
                }
            }
            user.setRoleNames(rs);

            HashMap<String, String> props = new HashMap<String, String>();
            NodeList properties = Util.getNodeList(result, "//properties/property"); //$NON-NLS-1$
            if (properties != null) {
                for (int i = 0; i < properties.getLength(); i++) {
                    Element property = (Element) properties.item(i);
                    String name = Util.getFirstTextNode(property, "name"); //$NON-NLS-1$
                    String value = Util.getFirstTextNode(property, "value"); //$NON-NLS-1$
                    props.put(name, value);
                }
            }
            user.setProperties(props);

            HashMap<String, String> apps = new HashMap<String, String>();
            NodeList appNodes = Util.getNodeList(result, "//applications/application"); //$NON-NLS-1$
            if (appNodes != null) {
                for (int i = 0; i < appNodes.getLength(); i++) {
                    Element application = (Element) appNodes.item(i);
                    String name = Util.getFirstTextNode(application, "name"); //$NON-NLS-1$
                    String value = Util.getFirstTextNode(application, "value"); //$NON-NLS-1$
                    apps.put(name, value);
                }
            }
            user.setApplications(apps);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to parse user: " + ": " + e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }

    public Map<String, String> getApplications() {
        return applications;
    }

    public void setApplications(Map<String, String> applications) {
        this.applications = applications;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGivenname() {
        return this.givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public String getFamilyname() {
        return this.familyname;
    }

    public void setFamilyname(String familyname) {
        this.familyname = familyname;
    }

    public String getPhonenumber() {
        return this.phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getRealemail() {
        return this.realemail;
    }

    public void setRealemail(String realemail) {
        this.realemail = realemail;
    }

    public String getFakeemail() {
        return this.fakeemail;
    }

    public void setFakeemail(String fakeemail) {
        this.fakeemail = fakeemail;
    }

    public String getCompany() {
        return this.company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public long getRegistrationdate() {
        return this.registrationdate;
    }

    public void setRegistrationdate(Date  registrationdate) {
        this.registrationdate = (registrationdate == null ? 0 : registrationdate.getTime());
    }

    public long getLastvisitdate() {
        return this.lastvisitdate;
    }

    public void setLastvisitdate(Date lastvisitdate) {
        this.lastvisitdate = (lastvisitdate == null ? 0 : lastvisitdate.getTime());
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isViewrealemail() {
        return this.viewrealemail;
    }

    public void setViewrealemail(boolean viewrealemail) {
        this.viewrealemail = viewrealemail;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getHomepage() {
        return this.homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getUniverse() {
        return this.universe;
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

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Set<String> getRolenames() {
        return this.rolenames;
    }

    public void setRoleNames(Set<String> rolenames) {
        this.rolenames = rolenames;
    }

}
