package com.amalto.jaas.ldap.jboss;

import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.AbstractServerLoginModule;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Element;

import com.amalto.jaas.ldap.jboss.util.Util;
import com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;

/**
 * This login module performs JAAS authentication using an LDAP
 * and authorization using a User concept holding roles in a cluster<br/>
 * <br/>
 * The LDAP can be queried using a direct or indirect query; see below<br/>
 * <br/>
 * In server/default/cong/login-config.xml the xtentisSecurity entry must be replaced with
 * <pre>
    &lt;application-policy name="xtentisSecurity"&gt;
      &lt;authentication&gt;
        &lt;login-module code="com.amalto.jaas.ldap.jboss.XtentisLoginModule" flag="required"&gt;

			&lt;!--
				Generic JAAS options
			--&gt;
			&lt;!-- cache passwords --&gt;
			&lt;module-option name="password-stacking"&gt;useFirstPass&lt;/module-option&gt;
			&lt;!-- unauthenticated user for background processes - do not change --&gt;
		  	&lt;module-option name="unauthenticatedIdentity"&gt;anonymous&lt;/module-option&gt;
		  	&lt;!-- MD5 hash of the password for the special "admin" user --&gt;
		  	&lt;module-option name="adminPassword"&gt;1a254116eb5e70714b0680dfd4d8f7d4&lt;/module-option&gt;

		  	&lt;!--
		  		ROLES
		  		Location and Paths on the User Concept holding roles information
		  	--&gt;
		  	&lt;!-- name of the cluster --&gt;
		  	&lt;module-option name="provisioningCluster"&gt;PROVISIONING&lt;/module-option&gt;
		  	&lt;-- name of the concept --&gt;
		  	&lt;module-option name="userConcept"&gt;User&lt;/module-option&gt;
		  	&lt;!-- xpath to the elements holding the role names --&gt;
		  	&lt;module-option name="rolesPath"&gt;./roles/role&lt;/module-option&gt;
		  	&lt;!-- xpath to the element holding the enabled state of the user - true or false --&gt;
		  	&lt;module-option name="enabledPath"&gt;./enabled&lt;/module-option&gt;
		  	&lt;!-- xpath to the element holding the name of the universe --&gt;
		  	&lt;module-option name="defaultUniversePath"&gt;./universe&lt;/module-option&gt;

			&lt;!--
				Authentication using LDAP
			--&gt;
			&lt;!-- The LDAP library/factory to use --&gt;
		  	&lt;module-option name="java.naming.factory.initial"&gt;com.sun.jndi.ldap.LdapCtxFactory&lt;/module-option&gt;
			&lt;!-- The LDAP authentication scheme - one of none, simple or strong --&gt;
		  	&lt;module-option name="java.naming.security.authentication"&gt;simple&lt;/module-option&gt;
			&lt;!-- The URL - use port 636 for ssl, 10389 otherwise --&gt;
		  	&lt;module-option name="java.naming.provider.url"&gt;ldap://localhost:636&lt;/module-option&gt;

			&lt;!-- The method used to perform authentication on the ldap
			 		direct: a direct attempt is made using the username to build the DN of the user
			 		indirect: an admin is used to browse the LDAP and find the DN for the given username
			 --&gt;
		  	&lt;module-option name="LdapDirect"&gt;true&lt;/module-option&gt;

			&lt;!--
				LDAP Direct
			--&gt;
			&lt;!-- The optional prefix to add to the username to build the subject DN in the direct method --&gt;
		  	&lt;module-option name="principalDNPrefix"&gt;cn=&lt;/module-option&gt;
		  	&lt;!-- The optional suffix to add to the username to build the subject DN in the direct method--&gt;
		  	&lt;module-option name="principalDNSuffix"&gt;,ou=people,o=sevenSeas&lt;/module-option&gt;

			&lt;!--
				LDAP Indirect
			--&gt;
		  	&lt;!-- The DN of the admin used to browse the LDAP in the indirect method --&gt;
		  	&lt;module-option name="LdapAdminDN"&gt;&lt;/module-option&gt;
		  	&lt;!-- The password of the admin used to browse the LDAP in the indirect method --&gt;
		  	&lt;module-option name="LdapAdminPassword"&gt;&lt;/module-option&gt;
		  	&lt;!-- The base context to search n the LDAP --&gt;
		  	&lt;module-option name="searchBase"&gt;o=sevenSeas&lt;/module-option&gt;
		  	&lt;!-- A filter expression to perform the search - {0} is replaced with the username --&gt;
		  	&lt;module-option name="searchFilter"&gt;(&(objectClass=inetOrgPerson)(uid={0}))&lt;/module-option&gt;

        &lt;/login-module&gt;

       &lt;!-- This is required to include the ClientLoginModule propagation --&gt;
        &lt;login-module code="org.jboss.security.ClientLoginModule" flag="required" /&gt;

      &lt;/authentication&gt;
    &lt;/application-policy&gt;
 * </pre>
 * The Jar should go in sever/default/lib
 *
 * @author Bruno Grieder
 */
public class XtentisLoginModule extends AbstractServerLoginModule {

//	private static final String PRINCIPAL_DN_PREFIX_OPT = "principalDNPrefix";
//	private static final String PRINCIPAL_DN_SUFFIX_OPT = "principalDNSuffix";


	/** The login identity */
	private Principal identity;

	/** The proof of login identity */
	private char[] credential;

	/** The universe of the user */
	private String universe;

	/** The User object */
	private Element user;

	/** The xml server DB manager */
	private IXmlServerSLWrapper server;

	// Default options values
	private String provisioningCluster = "PROVISIONING";

	private String userConcept = "User";

	private String rolesPath = "./roles/role";

	private String enabledPath = "./enabled";

	private String defaultUniversePath = "./universe";

	private String adminPassword = "1a254116eb5e70714b0680dfd4d8f7d4";

	private String adminPermission = "administration";

	/** */
	private Throwable validateError;


	@SuppressWarnings("unchecked")
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);

		Object tmp = null;
		if ((tmp = options.get("provisioningCluster")) != null)
			provisioningCluster = tmp.toString();
		if ((tmp = options.get("userConcept")) != null)
			userConcept = tmp.toString();
		if ((tmp = options.get("permissionsPath")) != null)
			rolesPath = tmp.toString();
		if ((tmp = options.get("enabledPath")) != null)
			enabledPath = tmp.toString();
		if ((tmp = options.get("adminPassword")) != null)
			adminPassword = tmp.toString();
		// if ((tmp=map.get("adminPermission"))!=null) adminPermission =
		// tmp.toString();

		// get the DB implementation class
		String serverClass = MDMConfiguration.getConfiguration().getProperty("xmlserver.class");
		if ((serverClass == null) || "".equals(serverClass))
			serverClass = "com.amalto.xmldb.XmldbSLWrapper";

		// instantiate the DB implementation class
		// we cannot user ObjectPOJO.load since it will try to check our
		// authentication
		try {
			server = (IXmlServerSLWrapper) Class.forName(serverClass).newInstance();
			if (server instanceof IXmlServerEBJLifeCycle) {
				((IXmlServerEBJLifeCycle) server).doCreate();
			}
		} catch (Throwable t) {
			String err = "Unable to start the XMLDB driver " + serverClass + ": " + t.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err, t);
			throw new IllegalArgumentException(err);
		}
	}

	/**
	 * Perform the authentication of the username and password.
	 */
	@SuppressWarnings("unchecked")
	public boolean login() throws LoginException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("login() ");

		identity = null;
		credential = null;
		universe = null;

		// See if shared credentials exist (from a previous login for instance)
		// and an universe has been defined
		if ((super.login() == true) && (sharedState.get("javax.security.auth.login.universe") != null)) {

			org.apache.log4j.Logger.getLogger(this.getClass()).debug("super.login() is true: \n" + "   universe: " + sharedState.get("javax.security.auth.login.universe") + "\n" + "   name: "
			    + sharedState.get("javax.security.auth.login.name") + "\n");

			// Parse the username
			Object username = sharedState.get("javax.security.auth.login.name");
			if (username instanceof Principal)
				identity = (Principal) username;
			else {
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
			if (password instanceof char[])
				credential = (char[]) password;
			else if (password != null) {
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
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("login() Authenticating as unauthenticatedIdentity=" + identity);
		}

		// If we do not have locally cached values,
		// perform password validation and universe determination
		if (identity == null) {

			if (password == null)
				throw new LoginException("Please enter a password for user '" + username + "'");

			//admin does not use the LDAP
			if ("admin".equals(username)) {
				if (! Util.md5AsHexString(password, "utf-8").equals(adminPassword))
			    	throw new LoginException("Please enter a valid password for the admin");
			} else {
    			//Determine the LDAP login strategy, direct or indirect
    			// Check for an allowEmptyPasswords option
                boolean ldapDirect = true;
                String flag = (String) options.get("LdapDirect");
                if (flag != null) ldapDirect = Boolean.valueOf(flag).booleanValue();

                boolean isValid = false;
    	        try {
    	        	// Validate the password by trying to create an initial context
    	        	if (ldapDirect)
    	        		directBind(username, password);
    	        	else
    	        		indirectBind(username, password);
    	        	isValid = true;
    	        } catch (NamingException e) {
    	        	org.apache.log4j.Logger.getLogger(this.getClass()).warn("validatePassword() failed for '"+username+"'");
    	        }
    			if (! isValid)
    				throw new LoginException("Please enter a valid password for user '" + username + "'");
			}

			// password validated, set the credential
			credential = password.toCharArray();

			// get the universe
			universe = universeID;
			if (universe == null) {
				// fetch the default user universe
				universe = getUserDefaultUniverse(username);
			}

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
		org.apache.log4j.Logger.getLogger(this.getClass()).info("login() User '" + identity.getName() + "' successfully logged in Universe '" + (universe == null ? "[HEAD]" : universe) + "'");
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		org.apache.log4j.Logger.getLogger(this.getClass()).info("Logged Out User '" + identity.getName() + "' " + "in Universe " + (universe == null ? "[HEAD]" : "'" + universe + "'"));
		return super.logout();
	}

	protected Principal getIdentity() {
		return identity;
	}

	protected Principal getUnauthenticatedIdentity() {
		return unauthenticatedIdentity;
	}

	protected Object getCredentials() {
		return credential;
	}

	protected String getUsername() {
		String username = null;
		if (getIdentity() != null)
			username = getIdentity().getName();
		return username;
	}

	protected String getUniverse() {
		return universe;
	}

	/**
	 * Called by login() to acquire the username and password strings for
	 * authentication. This method does no validation of any of this data.<br/>
	 *
	 * @return String[], [0] = universe, [1] = username, [2] = password
	 * @exception LoginException
	 *                thrown if CallbackHandler is not set or fails.
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
			org.apache.log4j.Logger.getLogger(this.getClass()).trace("getUsernameAndPassword() Username callback returns '" + universeAndUsername + "'");

			if (universeAndUsername == null) {
				// timeout, startup process --> this will map as the
				// unauthenticated identity
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

		// "people" cannot login using the ejbTimeout user
		if (unauthenticatedIdentity.getName().equals(username)) {
			throw new LoginException("The anonymous user is a reserved user.");
		}

		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getUsernameAndPassword() Universe: '" + universeID + "'  - user: '" + username + "'");
		info[0] = universeID;
		info[1] = username;
		info[2] = password;
		return info;
	}

	/**
	 * Get the error associated with the validatePassword failure
	 *
	 * @return the Throwable seen during validatePassword, null if no error
	 *         occurred.
	 */
	protected Throwable getValidateError() {
		return validateError;
	}

	/**
	 * Set the error associated with the validatePassword failure
	 *
	 * @param validateError
	 */
	protected void setValidateError(Throwable validateError) {
		this.validateError = validateError;
	}

	/**
	 * Read the User "object" from the xtentis DB
	 *
	 * @return
	 */
	protected Element getSavedUserDetails(String username) throws LoginException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getSavedUserDetails() " + username);

		try {

			if (username.equals("admin")) {
				throw new LoginException("Administrator information should not be fetched");
			}

			if (user == null) {
				String userString = server.getDocumentAsString(null, // head
				provisioningCluster, provisioningCluster + "." + userConcept + "." + username);
				user = (Element) Util.getNodeList(Util.parse(userString), "//" + userConcept).item(0);
			}
			return user;
		} catch (Exception e) {
			throw new LoginException("Failed to fetch user \"" + username + "\": " + e.getLocalizedMessage());
		}
	}


	/**
	 * Fetches the saved user form the xtentis DB password hashed using MD5
	 *
	 * @return the valid password String
	 */
	protected String getUserDefaultUniverse(String username) throws LoginException {

		if (username.equals("admin"))
			return null;
		if (username.equals(unauthenticatedIdentity.getName()))
			return null;

		String defaultUniverseID = null;
		try {
			defaultUniverseID = Util.getFirstTextNode(getSavedUserDetails(username), defaultUniversePath);
		} catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("getDefaultUniverse() No default universe found for '" + username + "'");
			return null;
		}

		return defaultUniverseID;
	}

	@Override
	protected Group[] getRoleSets() throws LoginException {

		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getRoleSets() for user '" + getUsername() + "'");

		// The username group maintains the username
		Group usernameGroup = new SimpleGroup("Username");
		usernameGroup.addMember(new SimplePrincipal(getUsername()));

		// The Universe Group maintains the Universe name
		Group universeGroup = new SimpleGroup("Universe");
		if (universe != null)
			universeGroup.addMember(new SimplePrincipal(universe.toString()));

		// JBoss expects the Roles to be set in a group called Roles
		Group rolesGroup = new SimpleGroup("Roles");

		// the user authenticated correctly - we add the authenticated role
		rolesGroup.addMember(new SimplePrincipal("authenticated"));

		// getRoleSets is called by the InitialContext.lookup with user
		// anonymous when internal calls are made.
		if (getUsername().equals(unauthenticatedIdentity.getName())) {
			rolesGroup.addMember(new SimplePrincipal(adminPermission));
			return new Group[] { usernameGroup, universeGroup, rolesGroup };
		}

		// super admin
		if (getUsername().equals("admin")) {
			rolesGroup.addMember(new SimplePrincipal(adminPermission));
			return new Group[] { usernameGroup, universeGroup, rolesGroup };
		}

		// Fetch the xtentis based saved User Details
		Element user = getSavedUserDetails(getUsername());

		// The Xtentis User Group maintains the user in xml serialized form and
		// the universePOJO in serialized form
		Group xtentisUserGroup = new SimpleGroup("XtentisUser");
		try {
			if (user != null)
				xtentisUserGroup.addMember(new SimplePrincipal(Util.nodeToString(user)));
		} catch (Exception e) {
			throw new LoginException("Unable to parse the user XML: " + e.getMessage());
		}

		// reset roles
		String[] roles = new String[0];

		// Determine if user is enabled
		boolean userEnabled = true;
		try {

			String enabled = Util.getFirstTextNode(user, enabledPath);
			if (enabled != null) {
				// previous versions do not have the enabled stuff
				if (!"yes".equals(enabled.trim().toLowerCase())) {
					rolesGroup.addMember(new SimplePrincipal("disabled"));
					userEnabled = false;
				}
			}

			if (userEnabled) {
				roles = Util.getTextNodes(user, rolesPath);
				for (int i = 0; i < roles.length; i++) {
					rolesGroup.addMember(new SimplePrincipal(roles[i]));
				}
			} else {

			}
		} catch (Exception e) {
			throw new LoginException("No appropriate permission found for " + getUsername() + ": " + e.getLocalizedMessage());
		}

		return new Group[] { usernameGroup, universeGroup, rolesGroup, xtentisUserGroup };

	}

	/***********************************************************************************************************************************************************
	 * LDAP Stuff
	 ***********************************************************************************************************************************************************/

	@SuppressWarnings("unchecked")
    private InitialLdapContext directBind(String username, Object credential) throws NamingException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("directBind() "+username);

		Properties env = new Properties();
		// Map all option into the JNDI InitialLdapContext env
		Iterator<Entry<String,String>> iter = options.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String,String> entry = (Entry<String,String>) iter.next();
			env.put(entry.getKey(), entry.getValue());
		}

		// Set defaults for key values if they are missing
		String factoryName = env.getProperty(Context.INITIAL_CONTEXT_FACTORY);
		if (factoryName == null) {
			factoryName = "com.sun.jndi.ldap.LdapCtxFactory";
			env.setProperty(Context.INITIAL_CONTEXT_FACTORY, factoryName);
		}
		String authType = env.getProperty(Context.SECURITY_AUTHENTICATION);
		if (authType == null) env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");

		String protocol = env.getProperty(Context.SECURITY_PROTOCOL);
		String providerURL = (String) options.get(Context.PROVIDER_URL);
		if (providerURL == null)
			providerURL = "ldap://localhost:" + ((protocol != null && protocol.equals("ssl")) ? "636" : "389");

		String principalDNPrefix = (String) options.get("principalDNPrefix");
		if (principalDNPrefix == null) principalDNPrefix = "";

		String principalDNSuffix = (String) options.get("principalDNSuffix");
		if (principalDNSuffix == null) principalDNSuffix = "";

		String userDN = principalDNPrefix + username + principalDNSuffix;
		env.setProperty(Context.PROVIDER_URL, providerURL);
		env.setProperty(Context.SECURITY_PRINCIPAL, userDN);
		env.put(Context.SECURITY_CREDENTIALS, credential);

		org.apache.log4j.Logger.getLogger(this.getClass()).debug("directLogin() Logging into LDAP server, env=" + env);
		InitialLdapContext ctx = new InitialLdapContext(env, null);
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("directLogin() Logged into LDAP server");
		return ctx;
	}


	@SuppressWarnings("unchecked")
	private InitialLdapContext indirectBind(String username, Object credential) throws NamingException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("indirectBind() "+username);

		Properties env = new Properties();
		// Map all option into the JNDI InitialLdapContext env
		Iterator<Entry<String,String>> iter = options.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String,String> entry = (Entry<String,String>) iter.next();
			env.put(entry.getKey(), entry.getValue());
		}

		// Set defaults for key values if they are missing
		String factoryName = env.getProperty(Context.INITIAL_CONTEXT_FACTORY);
		if (factoryName == null) {
			factoryName = "com.sun.jndi.ldap.LdapCtxFactory";
			env.setProperty(Context.INITIAL_CONTEXT_FACTORY, factoryName);
		}
		String authType = env.getProperty(Context.SECURITY_AUTHENTICATION);
		if (authType == null) env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");

		String protocol = env.getProperty(Context.SECURITY_PROTOCOL);
		String providerURL = (String) options.get(Context.PROVIDER_URL);
		if (providerURL == null)
			providerURL = "ldap://localhost:" + ((protocol != null && protocol.equals("ssl")) ? "636" : "389");

		String principalDNPrefix = (String) options.get("principalDNPrefix");
		if (principalDNPrefix == null) principalDNPrefix = "";

		String principalDNSuffix = (String) options.get("principalDNSuffix");
		if (principalDNSuffix == null) principalDNSuffix = "";

		env.put(Context.SECURITY_PRINCIPAL, (String) options.get("LdapAdminDN"));
		env.put(Context.SECURITY_CREDENTIALS, (String) options.get("LdapAdminPassword"));

		InitialLdapContext ctx = null;
		// Step 1: Bind anonymously
		ctx = new InitialLdapContext(env, null);
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("indirectBind() Admin bind OK");

		// Step 2: Search the directory
		String base = (String) options.get("searchBase");
		String filter = (String) options.get("searchFilter");
		SearchControls ctls = new SearchControls();
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		ctls.setReturningAttributes(new String[0]);
		ctls.setReturningObjFlag(true);
		NamingEnumeration<SearchResult> enm = ctx.search(base, filter, new String[] { username }, ctls);
		String dn = null;
		if (enm.hasMore()) {
			SearchResult result = (SearchResult) enm.next();
			dn = result.getNameInNamespace();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("indirectBind() Found DN '"+dn+"'");
		}

		if (dn == null || enm.hasMore()) {
			// uid not found or not unique
			throw new NamingException("Authentication failed");
		}

		// Step 3: Bind with found DN and given password
		ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
		ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, credential);
		// Perform a lookup in order to force a bind operation with JNDI
		ctx.lookup(dn);

		return ctx;

	}

}
