package org.talend.mdm.jaas.jboss.open;

import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.AbstractServerLoginModule;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.jaas.jboss.open.util.Util;
import org.w3c.dom.Element;

import com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;

/**
 * A loginModule that uses a Form login and the Xtentis backend to fetch authentication and authorizations
 * 
 */
public class SimpleLoginModule extends AbstractServerLoginModule {

    private String adminPermission = "administration";

    /** The login identity */
    private Principal identity;

    /** The proof of login identity */
    private char[] credential;

    /** The universe of the user */
    private String universe;

    /** The xml server DB manager */
    private IXmlServerSLWrapper server;

    /**
     * 
     * 
     * In server/default/cong/login-config.xml you must add a new domain
     * 
     * 
     * <application-policy name="xtentisSecurity"> <authentication> <login-module
     * code="com.amalto.jaas.xtentis.jboss.XtentisLoginModule" flag="required"> <module-option
     * name="logins">administrator,user,john,jack,james, admin, UnauthorizedLoginForTest</module-option> <module-option
     * name="passwords">administrator,user,bpm, bpm, bpm, bpm, UnauthorizedPasswordForTest</module-option>
     * </login-module>
     * 
     * <!-- Add this line to your login-config.xml to include the ClientLoginModule propogation --> <login-module
     * code="org.jboss.security.ClientLoginModule" flag="required" />
     * 
     * </authentication> </application-policy>
     * 
     * The Jar should go in sever/default/lib
     */
    // initial state
    private Subject subject;

    private CallbackHandler callbackHandler;

    private Map sharedState;

    private Map options;

    // the authentication status
    private boolean succeeded = false;

    private boolean commitSucceeded = false;

    // username and password
    private String username;

    private char[] password;

    /** The User object */
    private Element user;

    private Map<String, String> availableUsers = new HashMap<String, String>();

    /**
     * Initialize this <code>LoginModule</code>.
     * 
     * <p>
     * 
     * @param subject the <code>Subject</code> to be authenticated.
     * <p>
     * 
     * @param callbackHandler a <code>CallbackHandler</code> for communicating with the end user (prompting for user
     * names and passwords, for example).
     * <p>
     * 
     * @param sharedState shared <code>LoginModule</code> state.
     * <p>
     * 
     * @param options options specified in the login <code>Configuration</code> for this particular
     * <code>LoginModule</code>.
     */
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        super.initialize(subject, callbackHandler, sharedState, options);

        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;

        // initialize any configured options
        String logins = (String) options.get("logins");
        String passwords = (String) options.get("passwords");
        String[] users = logins.split(",");
        String[] pws = passwords.split(",");
        for (int i = 0; i < users.length; i++) {
            availableUsers.put(users[i], pws[i]);
        }
        // get the DB implementation class
        String serverClass = MDMConfiguration.getConfiguration().getProperty("xmlserver.class");
        if ((serverClass == null) || "".equals(serverClass)) {
            serverClass = "com.amalto.core.storage.SQLWrapper";
        }

        // instantiate the DB implementation class
        // we cannot user ObjectPOJO.load since it will try to check our authentication
        try {
            server = (IXmlServerSLWrapper) Class.forName(serverClass).newInstance();
            if (server instanceof IXmlServerEBJLifeCycle) {
                ((IXmlServerEBJLifeCycle) server).doCreate();
            }
        } catch (Throwable t) {
            String err = "Unable to start the DB driver " + serverClass + ": " + t.getMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err, t);
            throw new IllegalArgumentException(err);
        }
    }

    /**
     * Authenticate the user by prompting for a user name and password.
     * 
     * <p>
     * 
     * @return true in all cases since this <code>LoginModule</code> should not be ignored.
     * 
     * @exception FailedLoginException if the authentication fails.
     * <p>
     * 
     * @exception LoginException if this <code>LoginModule</code> is unable to perform the authentication.
     */
    @Override
    public boolean login() throws LoginException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("login() ");

        identity = null;
        credential = null;
        universe = null;

        // See if shared credentials exist (from a previous login for instance) and an universe has been defined
        if ((super.login() == true) && (sharedState.get("javax.security.auth.login.universe") != null)) {

            org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                    "super.login() is true: \n" + "   universe: " + sharedState.get("javax.security.auth.login.universe") + "\n"
                            + "   name: " + sharedState.get("javax.security.auth.login.name") + "\n");

            // Parse the username
            Object username = sharedState.get("javax.security.auth.login.name");
            if (username instanceof Principal) {
                identity = (Principal) username;
            } else {
                String name = username.toString();
                try {
                    identity = createIdentity(name);
                } catch (Exception e) {
                    log.debug("Failed to create principal", e);
                    throw new LoginException("Failed to create principal: " + e.getMessage());
                }
            }
            // Parse the password
            Object password = sharedState.get("javax.security.auth.login.password");
            if (password instanceof char[]) {
                credential = (char[]) password;
            } else if (password != null) {
                String tmp = password.toString();
                credential = tmp.toCharArray();
            }
            // Parse the Universe
            universe = (String) sharedState.get("javax.security.auth.login.universe");
            return true;
        }

        // No shared or previous login

        super.loginOk = false;
        String[] info = getUsernameAndPassword();
        String universeID = info[0];
        String username = info[1];
        String password = info[2];

        // Check if we are logging with the unauthenticated identity
        if (username == null && password == null) {
            identity = unauthenticatedIdentity;
            universe = null;
            credential = "dummy".toCharArray();
            org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                    "login() Authenticating as unauthenticatedIdentity=" + identity);
        }

        // If we do not have locally cached values,
        // perform password validation and universe determination
        if (identity == null) {

            // recover saved password hash for the user
            String hashedPassword = availableUsers.get(username);
            // hash and validate the entered password
            if (password == null) {
                throw new LoginException("Please enter a password for user '" + username + "'");
            }
            if (!password.equals(hashedPassword)) {
                throw new LoginException("Please enter a valid password for user '" + username + "'");
            }

            // password validated, set the credential
            credential = password.toCharArray();

            // get the universe
            universe = universeID;

            // we are done, set the identity
            try {
                identity = createIdentity(username);
            } catch (Exception e) {
                log.debug("Failed to create principal", e);
                throw new LoginException("Failed to create principal: " + e.getMessage());
            }
        }

        // Add the username, password and universe to the
        // shared state map
        if (getUseFirstPass() == true) {
            sharedState.put("javax.security.auth.login.name", identity);
            sharedState.put("javax.security.auth.login.password", credential);
            sharedState.put("javax.security.auth.login.universe", universe);
        }
        super.loginOk = true;
        org.apache.log4j.Logger.getLogger(this.getClass()).info(
                "login() User '" + identity.getName() + "' successfully logged in Universe '"
                        + (universe == null ? "[HEAD]" : universe) + "'");
        return true;
    }

    /**
     * Logout the user.
     * 
     * <p>
     * This method removes the <code>SamplePrincipal</code> that was added by the <code>commit</code> method.
     * 
     * <p>
     * 
     * @exception LoginException if the logout fails.
     * 
     * @return true in all cases since this <code>LoginModule</code> should not be ignored.
     */
    @Override
    public boolean logout() throws LoginException {

        org.apache.log4j.Logger.getLogger(this.getClass()).info(
                "Logged Out User '" + identity.getName() + "' " + "in Universe "
                        + (universe == null ? "[HEAD]" : "'" + universe + "'"));
        return super.logout();

    }

    @Override
    protected Group[] getRoleSets() throws LoginException {

        org.apache.log4j.Logger.getLogger(this.getClass()).trace("getRoleSets() for user '" + username + "'");

        // The username group maintains the username
        Group usernameGroup = new SimpleGroup("Username");
        usernameGroup.addMember(new SimplePrincipal(getUsername()));

        Group passwordGroup = new SimpleGroup("Password");
        passwordGroup.addMember(new SimplePrincipal(String.valueOf(getPassword())));

        // The Universe Group maintains the Universe name
        Group universeGroup = new SimpleGroup("Universe");
        if (universe != null) {
            universeGroup.addMember(new SimplePrincipal(universe.toString()));
        }

        // JBoss expects the Roles to be set in a group called Roles
        Group rolesGroup = new SimpleGroup("Roles");
        // the user authenticated correctly - we add the authenticated role
        rolesGroup.addMember(new SimplePrincipal("authenticated"));

        // getRoleSets is called by the InitialContext.lookup with user anonymous when internal calls are made.
        if (getUsername().equals(unauthenticatedIdentity.getName())) {
            rolesGroup.addMember(new SimplePrincipal(adminPermission));
            return new Group[] { usernameGroup, universeGroup, rolesGroup };
        }

        // super admin
        if (getUsername().equals(MDMConfiguration.getAdminUser())) {
            rolesGroup.addMember(new SimplePrincipal(adminPermission));
            return new Group[] { usernameGroup, passwordGroup, universeGroup, rolesGroup };
        }
        // add an 'openTest' role
        rolesGroup.addMember(new SimplePrincipal(ICoreConstants.SYSTEM_INTERACTIVE_ROLE));
        // Fetch the xtentis based saved User Details
        Element user = getSavedUserDetails(getUsername(), getPassword());

        // The Xtentis User Group maintains the user in xml serialized form and the universePOJO in serialized form
        Group xtentisUserGroup = new SimpleGroup("XtentisUser");
        try {
            if (user != null) {
                xtentisUserGroup.addMember(new SimplePrincipal(Util.nodeToString(user)));
            }
        } catch (Exception e) {
            throw new LoginException("Unable to parse the user XML: " + e.getMessage());
        }

        return new Group[] { usernameGroup, passwordGroup, universeGroup, rolesGroup, xtentisUserGroup };
    }

    protected Element getSavedUserDetails(String username, String password) throws LoginException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("getSavedUserDetails() " + username);

        try {

            if (username.equals(MDMConfiguration.getAdminUser())) {
                throw new LoginException("Administrator information should not be fetched");
            }

            if (user == null) {
                String userString = server.getDocumentAsString(null, // head
                        "PROVISIONING", "PROVISIONING" + "." + "User" + "." + username);
                if (userString == null) {
                    String md5pwd = Util.md5AsHexString(password, "utf-8");
                    userString = generateNewUser(username, md5pwd).toString();
                }
                user = (Element) Util.getNodeList(Util.parse(userString), "//" + "User").item(0);
            }
            return user;
        } catch (Exception e) {
            throw new LoginException("Failed to fetch user \"" + username + "\": " + e.getLocalizedMessage());
        }
    }

    private StringBuffer generateNewUser(String username, String md5pwd) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-16\"?> ");
        sb.append("<ii> ");
        sb.append("    <c>PROVISIONING</c> ");
        sb.append("    <n>User</n> ");
        sb.append("    <dmn>PROVISIONING</dmn> ");
        sb.append("    <dmr/> ");
        sb.append("    <sp/> ");
        sb.append("    <i>").append(username).append("</i> ");
        sb.append("    <t>").append(System.currentTimeMillis()).append("</t> ");
        sb.append("    <p> ");
        sb.append("        <User> ");
        sb.append("            <username>").append(username).append("</username> ");
        sb.append("            <password>").append(md5pwd).append("</password> ");
        sb.append("            <givenname></givenname> ");
        sb.append("            <familyname></familyname> ");
        sb.append("            <phonenumber/> ");
        sb.append("            <company></company> ");
        sb.append("            <id></id> ");
        sb.append("            <signature/> ");
        sb.append("            <realemail></realemail> ");
        sb.append("            <fakeemail/> ");
        sb.append("            <viewrealemail>no</viewrealemail> ");
        sb.append("            <registrationdate></registrationdate> ");
        sb.append("            <lastvisitdate>0</lastvisitdate> ");
        sb.append("            <enabled>yes</enabled> ");
        sb.append("            <homepage></homepage> ");
        sb.append("            <universe/> ");
        sb.append("            <roles> ");
        sb.append("                <role>administration</role> ");
        sb.append("                <role>Default_User</role> ");
        sb.append("            </roles> ");
        sb.append("            <properties></properties> ");
        sb.append("            <applications></applications> ");
        sb.append("        </User> ");
        sb.append("    </p> ");
        sb.append("</ii> ");
        return sb;
    }

    /**
     * Called by login() to acquire the username and password strings for authentication. This method does no validation
     * of any of this data.<br/>
     * 
     * @return String[], [0] = universe, [1] = username, [2] = password
     * @exception LoginException thrown if CallbackHandler is not set or fails.
     */
    protected String[] getUsernameAndPassword() throws LoginException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("getUsernameAndPassword() ");

        String[] info = { null, null, null };

        // prompt for a [universe/]username and password
        if (callbackHandler == null) {
            throw new LoginException("Error: no CallbackHandler available to collect authentication information");
        }

        // Prepare standard callbacks for [universe/]username and password
        NameCallback nc = new NameCallback("User name: ", "guest");
        PasswordCallback pc = new PasswordCallback("Password: ", false);
        Callback[] callbacks = { nc, pc };

        String universeID = null;
        String username = null;
        String password = null;
        try {
            // call the handlers to fill in the [universe/]username and password
            callbackHandler.handle(callbacks);

            // read the entered usename and password
            String universeAndUsername = nc.getName();
            org.apache.log4j.Logger.getLogger(this.getClass()).trace(
                    "getUsernameAndPassword() Username callback returns '" + universeAndUsername + "'");

            if (universeAndUsername == null) {
                // timeout, startup process --> this will map as the unauthenticated identity
                return new String[] { null, null, null };
            }

            // get universeID and username
            String[] vals = universeAndUsername.split("/");
            if (vals.length > 1) {
                universeID = "".equals(vals[0]) ? null : vals[0];
                username = vals[1];
            } else {
                universeID = null;
                username = vals[0];
            }

            // get the password
            char[] tmpPassword = pc.getPassword();
            if (tmpPassword != null) {
                char[] credential = new char[tmpPassword.length];
                System.arraycopy(tmpPassword, 0, credential, 0, tmpPassword.length);
                pc.clearPassword();
                password = new String(credential);
            }

        } catch (IOException e) {
            org.apache.log4j.Logger.getLogger(this.getClass()).debug("getUsernameAndPassword() ERROR ", e);
            LoginException le = new LoginException("Failed to get universe/username/password");
            le.initCause(e);
            throw le;
        } catch (UnsupportedCallbackException e) {
            org.apache.log4j.Logger.getLogger(this.getClass()).debug("getUsernameAndPassword() ERROR ", e);
            LoginException le = new LoginException("CallbackHandler does not support: " + e.getCallback());
            le.initCause(e);
            throw le;
        }

        org.apache.log4j.Logger.getLogger(this.getClass()).trace(
                "getUsernameAndPassword() Universe: '" + universeID + "'  - user: '" + username + "'");
        info[0] = universeID;
        info[1] = username;
        info[2] = password;
        return info;
    }

    @Override
    protected Principal getIdentity() {
        return identity;
    }

    @Override
    protected Principal getUnauthenticatedIdentity() {
        return unauthenticatedIdentity;
    }

    protected Object getCredentials() {
        return credential;
    }

    protected String getUsername() {
        String username = null;
        if (getIdentity() != null) {
            username = getIdentity().getName();
        }
        return username;
    }

    protected String getPassword() {
        String password = null;
        if (getCredentials() != null) {
            password = new String((char[]) getCredentials());
        }
        return password;
    }

    protected String getUniverse() {
        return universe;
    }

}
