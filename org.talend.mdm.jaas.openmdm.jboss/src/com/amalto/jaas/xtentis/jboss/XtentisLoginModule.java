package com.amalto.jaas.xtentis.jboss;

import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Map;

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

import com.amalto.jaas.xtentis.jboss.util.Util;
import com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;

/**
 * A loginModule that uses a Form login and the Xtentis backend
 * to fetch authentication and authorizations
 *
 */
public class XtentisLoginModule extends AbstractServerLoginModule {



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

	//Default options values
	private String provisioningCluster  = "PROVISIONING";
	private String userConcept = "User";
	private String passwordPath = "./password";
	private String rolesPath = "./roles/role";
	private String enabledPath = "./enabled";
	private String defaultUniversePath = "./universe";
	private String adminPassword = "1a254116eb5e70714b0680dfd4d8f7d4";
	private String adminPermission = "administration";

	/** */
	private Throwable validateError;

	/**
	 *
	 * @author Bruno Grieder
	 *
	 * In server/default/cong/login-config.xml you must add a new domain
	 *
	 *
	    <application-policy name="xtentisSecurity">
	      <authentication>
	        <login-module code="com.amalto.jaas.xtentis.jboss.XtentisLoginModule" flag="required">
	          <module-option name="password-stacking">useFirstPass</module-option>
			  <module-option name="unauthenticatedIdentity">anonymous</module-option>
			  <module-option name="provisioningCluster">PROVISIONING</module-option>
			  <module-option name="userConcept">User</module-option>
			  <module-option name="passwordPath">./password</module-option>
			  <module-option name="rolesPath">./roles/role</module-option>
			  <module-option name="enabledPath">./enabled</module-option>
			  <module-option name="defaultUniversePath">./universe</module-option>
			  <module-option name="adminPassword">1a254116eb5e70714b0680dfd4d8f7d4</module-option>
	        </login-module>
	       <!-- Add this line to your login-config.xml to include the ClientLoginModule propogation -->
	        <login-module code="org.jboss.security.ClientLoginModule" flag="required" />
	      </authentication>
	    </application-policy>
	 *
	 * The Jar should go in sever/default/lib
	 */
	@SuppressWarnings("unchecked")
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);

		Object tmp = null;
		if ((tmp=options.get("provisioningCluster"))!=null) provisioningCluster = tmp.toString();
		if ((tmp=options.get("userConcept"))!=null) userConcept = tmp.toString();
		if ((tmp=options.get("passwordPath"))!=null) passwordPath = tmp.toString();
		if ((tmp=options.get("permissionsPath"))!=null) rolesPath = tmp.toString();
		if ((tmp=options.get("enabledPath"))!=null) enabledPath = tmp.toString();
		if ((tmp=options.get("adminPassword"))!=null) adminPassword = tmp.toString();
		//if ((tmp=map.get("adminPermission"))!=null) adminPermission = tmp.toString();


		//get the DB implementation class
		String serverClass = MDMConfiguration.getConfiguration().getProperty("xmlserver.class");
		if ((serverClass==null) || "".equals(serverClass)) serverClass = "com.amalto.xmldb.XmldbSLWrapper";

		//instantiate the DB implementation class
		//we cannot user ObjectPOJO.load since it will try to check our authentication
		try {
	        server = (IXmlServerSLWrapper) Class.forName(serverClass).newInstance();
	        if (server instanceof IXmlServerEBJLifeCycle) {
	        	((IXmlServerEBJLifeCycle)server).doCreate();
	        }
        } catch (Throwable t) {
        	String err = "Unable to start the XMLDB driver "+serverClass+": "+t.getMessage();
        	org.apache.log4j.Logger.getLogger(this.getClass()).error(err,t);
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

		// See if shared credentials exist (from a previous login for instance) and an universe has been defined
		if ((super.login() == true) && (sharedState.get("javax.security.auth.login.universe") != null)) {

			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
				"super.login() is true: \n" +
				"   universe: "+sharedState.get("javax.security.auth.login.universe")+"\n"+
				"   name: "+sharedState.get("javax.security.auth.login.name")+"\n"
			);

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
			//Parse the password
			Object password = sharedState.get("javax.security.auth.login.password");
			if (password instanceof char[])
				credential = (char[]) password;
			else if (password != null) {
				String tmp = password.toString();
				credential = tmp.toCharArray();
			}
			//Parse the Universe
			universe = (String) sharedState.get("javax.security.auth.login.universe");
			return true;
		}

		//No shared or previous login

		super.loginOk = false;
		String[] info = getUsernameAndPassword();
		String universeID = info[0];
		String username = info[1];
		String password = info[2];

		//Check if we are logging with the unauthenticated identity
		if (username == null && password == null) {
			identity = unauthenticatedIdentity;
			universe = null;
			credential = "dummy".toCharArray();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("login() Authenticating as unauthenticatedIdentity=" + identity);
		}

		//If we do not have locally cached values,
		//perform password validation and universe determination
		if (identity == null) {

		    //recover saved password hash for the user
		    String hashedPassword = getSavedUsersPassword(username);
		    //hash and validate the entered password
		    if (password == null)
		    	throw new LoginException("Please enter a password for user '"+username+"'");
		    if (! Util.md5AsHexString(password, "utf-8").equals(hashedPassword))
		    	throw new LoginException("Please enter a valid password for user '"+username+"'");

		    //password validated, set the credential
		    credential = password.toCharArray();

		    //get the universe
		    universe = universeID;
		    if (universe == null) {
		    	//fetch the default user universe
		    	universe = getUserDefaultUniverse(username);
		    }

            //we are done, set the identity
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
			"login() User '"+identity.getName()+"' successfully logged in Universe '"+(universe == null ? "[HEAD]" : universe)+"'"
		);
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
	    org.apache.log4j.Logger.getLogger(this.getClass()).info(
	    	"Logged Out User '"+identity.getName()+"' " +
	    	"in Universe "+(universe==null ? "[HEAD]" : "'"+universe+"'")
	    );
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

		//Prepare standard callbacks for [universe/]username and password
		NameCallback nc = new NameCallback("User name: ", "guest");
		PasswordCallback pc = new PasswordCallback("Password: ", false);
		Callback[] callbacks = { nc, pc };

		String universeID = null;
		String username = null;
		String password = null;
		try {
			//call the handlers to fill in the [universe/]username and password
			callbackHandler.handle(callbacks);

			//read the entered usename and password
			String universeAndUsername = nc.getName();
			org.apache.log4j.Logger.getLogger(this.getClass()).trace("getUsernameAndPassword() Username callback returns '"+universeAndUsername+"'");

			if (universeAndUsername == null) {
				//timeout, startup process --> this will map as the unauthenticated identity
				return new String[] {null, null, null };
			}

			//get universeID and username
		    String[] vals = universeAndUsername.split("/");
		    if (vals.length>1) {
		    	universeID = "".equals(vals[0]) ? null : vals[0];
		    	username = vals[1];
		    } else {
		    	universeID = null;
		    	username = vals[0];
		    }

		    //get the password
			char[] tmpPassword = pc.getPassword();
			if (tmpPassword != null) {
				char[] credential = new char[tmpPassword.length];
				System.arraycopy(tmpPassword, 0, credential, 0, tmpPassword.length);
				pc.clearPassword();
				password = new String(credential);
			}

		} catch (IOException e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("getUsernameAndPassword() ERROR ",e);
			LoginException le = new LoginException("Failed to get universe/username/password");
			le.initCause(e);
			throw le;
		} catch (UnsupportedCallbackException e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("getUsernameAndPassword() ERROR ",e);
			LoginException le = new LoginException("CallbackHandler does not support: " + e.getCallback());
			le.initCause(e);
			throw le;
		}

		//"people" cannot login using the ejbTimeout user
		if (unauthenticatedIdentity.getName().equals(username)) {
			throw new LoginException("The anonymous user is a reserved user.");
		}

		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getUsernameAndPassword() Universe: '"+universeID + "'  - user: '"+username+"'");
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
	 * @return
	 */
	protected Element getSavedUserDetails(String username) throws LoginException{
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getSavedUserDetails() "+username);

		try {

			if (username.equals("admin")) {
				throw new LoginException("Administrator information should not be fetched");
			}

			if (user == null) {
				String userString = server.getDocumentAsString(
					null, //head
					provisioningCluster,
					provisioningCluster+"."+userConcept+"."+username
				);
				user  = (Element)Util.getNodeList(Util.parse(userString), "//"+userConcept).item(0);
			}
			return user;
		} catch (Exception e) {
			throw new LoginException("Failed to fetch user \"" +username+"\": "+e.getLocalizedMessage());
		}
	}

	/**
	 * Fetches the saved user form the xtentis DB password hashed using MD5
	 *
	 * @return the valid password String
	 */
	protected String getSavedUsersPassword(String username) throws LoginException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getSavedUsersPassword() "+username);

		if (username.equals("admin")) return adminPassword;
		if (username.equals(unauthenticatedIdentity.getName())) return Util.md5AsHexString("dummy", "utf-8");

		String hashedPassword = null;
		try {
			hashedPassword =  Util.getFirstTextNode(getSavedUserDetails(username), passwordPath);
		} catch (Exception e) {
			throw new LoginException("No password found for "+getUsername()+": "+e.getLocalizedMessage());
		}

		if (hashedPassword == null) throw new LoginException("Password is null for User '"+username+"'");

		return hashedPassword;
	}

	/**
	 * Fetches the saved user form the xtentis DB password hashed using MD5
	 *
	 * @return the valid password String
	 */
	protected String getUserDefaultUniverse(String username) throws LoginException {

		if (username.equals("admin")) return null;
		if (username.equals(unauthenticatedIdentity.getName())) return null;

		String defaultUniverseID = null;
		try {
			defaultUniverseID =  Util.getFirstTextNode(getSavedUserDetails(username), defaultUniversePath);
		} catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("getDefaultUniverse() No default universe found for '"+username+"'");
			return null;
		}

		return defaultUniverseID;
	}



	@Override
	protected Group[] getRoleSets() throws LoginException {

		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getRoleSets() for user '"+getUsername()+"'");

		//The username group maintains the username
		Group usernameGroup = new SimpleGroup("Username");
		usernameGroup.addMember(new SimplePrincipal(getUsername()));

		//The Universe Group maintains the Universe name
		Group universeGroup = new SimpleGroup("Universe");
		if (universe != null) universeGroup.addMember(new SimplePrincipal(universe.toString()));


		//JBoss expects the Roles to be set in a group called Roles
		Group rolesGroup = new SimpleGroup("Roles");

		// the user authenticated correctly - we add the authenticated role
		rolesGroup.addMember(new SimplePrincipal("authenticated"));

		//getRoleSets is called by the InitialContext.lookup with user anonymous when internal calls are made.
		if (getUsername().equals(unauthenticatedIdentity.getName())) {
			rolesGroup.addMember(new SimplePrincipal(adminPermission));
			return new Group[]{usernameGroup, universeGroup, rolesGroup};
		}

		//super admin
		if (getUsername().equals("admin")) {
			rolesGroup.addMember(new SimplePrincipal(adminPermission));
			return new Group[]{usernameGroup, universeGroup, rolesGroup};
		}

		//Fetch the xtentis based saved User Details
		Element user = getSavedUserDetails(getUsername());

		//The Xtentis User Group maintains the user in xml serialized form and the universePOJO in serialized form
		Group xtentisUserGroup = new SimpleGroup("XtentisUser");
		try {
			if (user != null) xtentisUserGroup.addMember(new SimplePrincipal(Util.nodeToString(user)));
		} catch (Exception e) {
			throw new LoginException("Unable to parse the user XML: "+e.getMessage());
		}

		//reset roles
		String[] roles = new String[0];

		//Determine if user is enabled
		boolean userEnabled = true;
		try {

			String enabled = Util.getFirstTextNode(user, enabledPath);
			if (enabled!=null) {
				//previous versions do not have the enabled stuff
				if (!"yes".equals(enabled.trim().toLowerCase())) {
					rolesGroup.addMember(new SimplePrincipal("disabled"));
					userEnabled = false;
				}
			}

			if (userEnabled) {
				roles =  Util.getTextNodes(user, rolesPath);
				for (int i = 0; i < roles.length; i++) {
					rolesGroup.addMember(new SimplePrincipal(roles[i]));
				}
			} else {

			}
		} catch (Exception e) {
			throw new LoginException("No appropriate permission found for "+getUsername()+": "+e.getLocalizedMessage());
		}

		return new Group[]{
			usernameGroup,
			universeGroup,
			rolesGroup,
			xtentisUserGroup
		};

	}

}
